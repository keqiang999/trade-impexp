package com.oilchem.trade.service.impl;

import com.oilchem.trade.dao.LogDao;
import com.oilchem.trade.domain.Log;
import com.oilchem.trade.service.LogService;
import com.oilchem.trade.bean.YearMonthDto;
import com.oilchem.trade.service.TradeDetailService;
import com.oilchem.trade.util.DynamicSpecifications;
import com.oilchem.trade.util.QueryUtils;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.oilchem.trade.bean.DocBean.Flag.*;
import static com.oilchem.trade.bean.DocBean.ImpExpType.export_type;
import static com.oilchem.trade.bean.DocBean.ImpExpType.import_type;
import static com.oilchem.trade.bean.DocBean.OptType.import_opt;
import static com.oilchem.trade.util.QueryUtils.PropertyFilter;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 12-11-11
 * Time: 下午6:45
 * To change this template use File | Settings | File Templates.
 */
@Aspect
@Service("logService")
public class LogServiceImpl implements LogService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    LogDao logDao;

    Log log;

    /**
     * 上传文件切入点
     *
     * @param file
     * @param realDir
     * @param yearMonthDto
     */
    @Pointcut(value = "execution(String com.oilchem.trade.service.impl.CommonServiceImpl.uploadFile(" +
            "org.springframework.web.multipart.MultipartFile," +
            "java.lang.String," +
            "com.oilchem.trade.bean.YearMonthDto)) " +
            "&& args(file,realDir,yearMonthDto)",
            argNames = "file,realDir,yearMonthDto")
    void cutUploadFile(MultipartFile file, String realDir, YearMonthDto yearMonthDto) {
    }

    /**
     * 上传前更新日志
     *
     * @param file
     * @param readDir
     */
    @Before("cutUploadFile(file,readDir,yearMonthDto)")
    void logUploadingFile(MultipartFile file, String readDir, YearMonthDto yearMonthDto) {
        log = new Log();
        log.setLogType(import_opt.getValue());
        log.setTableType(yearMonthDto.getTableType());

        if (yearMonthDto.getImpExpType().equals(import_type.ordinal())) {
            log.setTradeType(import_type.getValue());
        } else if (yearMonthDto.getImpExpType().equals(export_type.ordinal())) {
            log.setTradeType(export_type.getValue());
        }

        log.setYear(yearMonthDto.getYear());
        log.setMonth(yearMonthDto.getMonth());
        log.setUploadFlg(uploading_flag.getValue());
        log.setLogTime(new Date());
        logDao.save(log);
    }

    /**
     * 上传后更新日志
     *
     * @param file
     * @param readDir
     * @param uploadUrl
     */
    @AfterReturning(pointcut = "cutUploadFile(file,readDir,yearMonthDto)",
            returning = "uploadUrl")
    void logUploadedFile(MultipartFile file, String readDir,
                         YearMonthDto yearMonthDto, String uploadUrl) {
        log.setUploadPath(uploadUrl);
        log.setUploadPath(readDir + uploadUrl.substring(uploadUrl.lastIndexOf("/")));

        log.setUploadFlg(uploaded_flag.getValue());
        log.setExtractFlag(unextract_flag.getValue());
        log.setLogTime(new Date());
        logDao.save(log);
        //更新日志 .. 上传完毕
    }

    /**
     * 发生错误更新日志
     *
     * @param file
     * @param readDir
     */
    @AfterThrowing("cutUploadFile(file,readDir,yearMonthDto)")
    void logUploadFileThrowing(MultipartFile file, String readDir,
                               YearMonthDto yearMonthDto) {

        log.setUploadFlg(upload_faild.getValue());
        log.setErrorOccur(upload_faild.getValue());
        log.setLogTime(new Date());
        logDao.save(log);
    }

    /**
     * 解压文件切入点
     *
     * @param logEntry
     * @param unPackageDir
     */
    @Pointcut(value = "execution(" +
            "String com.oilchem.trade.service.impl.CommonServiceImpl.unpackageFile(" +
            "java.util.Map.Entry<Long, com.oilchem.trade.domain.Log>," +
            "java.lang.String))" +
            "&& args(logEntry,unPackageDir)",
            argNames = "logEntry,unPackageDir")
    void cutUnpackageFile(Map.Entry<Long, Log> logEntry, String unPackageDir) {
    }

    /**
     * 解压前更新日志
     *
     * @param logEntry
     * @param unPackageDir
     */
    @Before("cutUnpackageFile(logEntry,unPackageDir)")
    void logUnpackagingFile(Map.Entry<Long, Log> logEntry, String unPackageDir) {

        log = logDao.findOne(logEntry.getKey());
        log.setExtractFlag(extracting_flag.getValue());
        log.setLogTime(new Date());
        logDao.save(log);
    }

    /**
     * 解压后更新日志
     *
     * @param logEntry
     * @param unPackagePath
     */
    @AfterReturning(pointcut = "cutUnpackageFile(logEntry,unPackageDir)",
            returning = "unPackagePath")
    void logUnpackagedFile(Map.Entry<Long, Log> logEntry,
                           String unPackageDir, String unPackagePath) {

        log = logDao.findOne(logEntry.getKey());
        log.setExtractFlag(extracted_flag.getValue());
        log.setExtractPath(unPackagePath);
        log.setImportFlag(unimport_flag.getValue());
        log.setLogTime(new Date());
        logDao.save(log);
    }

    /**
     * 解压发生异常
     *
     * @param logEntry
     * @param unPackageDir
     */
    @AfterThrowing("cutUnpackageFile(logEntry,unPackageDir)")
    void logUnpackageFileThrowing(Map.Entry<Long, Log> logEntry,
                                  String unPackageDir) {

        log = logDao.findOne(logEntry.getKey());
        log.setExtractFlag(extract_faild.getValue());
        log.setImportFlag(unimport_flag.getValue());
        log.setErrorOccur(extract_faild.getValue());
        log.setLogTime(new Date());
        logDao.save(log);
    }


    /**
     * 导入详细表日志记录切入点
     *
     * @param logEntry     logEntry
     * @param yearMonthDto yearMonthDto
     */
    @Pointcut(value = "execution(" +
            "Boolean com.oilchem.trade.service.impl.TradeDetailServiceImpl.importAccess(" +
            "java.util.Map.Entry<Long, com.oilchem.trade.domain.Log>," +
            "com.oilchem.trade.bean.YearMonthDto))" +
            "&& args(logEntry,yearMonthDto)",
            argNames = "logEntry,yearMonthDto")
    void cutImportTradeDetail(Map.Entry<Long, Log> logEntry,
                              YearMonthDto yearMonthDto) {
    }

    /**
     * 导入详细表数据时更新日志
     *
     * @param logEntry
     * @param yearMonthDto
     */
    @Before("cutImportTradeDetail(logEntry,yearMonthDto)")
    void logImportingTradeDetail(Map.Entry<Long, Log> logEntry,
                                 YearMonthDto yearMonthDto) {

        log = logDao.findOne(logEntry.getKey());
        log.setImportFlag(importing_flag.getValue());
        log.setLogTime(new Date());
        logDao.save(log);

    }

    /**
     * 成功导入详细表后更新日志
     *
     * @param logEntry
     * @param yearMonthDto
     * @param isSuccess
     */
    @AfterReturning(pointcut = "cutImportTradeDetail(logEntry,yearMonthDto)",
            returning = "isSuccess")
    void logImportedTradeDetail(Map.Entry<Long, Log> logEntry,
                                YearMonthDto yearMonthDto,
                                Boolean isSuccess) {

        log = logDao.findOne(logEntry.getKey());
        log.setImportFlag(imported_flag.getValue());
        log.setErrorOccur(imported_flag.getValue());
        log.setLogTime(new Date());
        logDao.save(log);

        //更新明细产品类型
        tradeDetailService.updateDetailType(yearMonthDto);
        //更新产品表
        //.....
        //更新产品总统计表
        //.....
    }

    @Autowired
    TradeDetailService tradeDetailService;


    @AfterThrowing("cutImportTradeDetail(logEntry,yearMonthDto)")
    void logImportTradeDetailThrowing(Map.Entry<Long, Log> logEntry,
                                      YearMonthDto yearMonthDto) {
        log = logDao.findOne(logEntry.getKey());
        log.setImportFlag(import_faild.getValue());
        log.setErrorOccur(import_faild.getValue());
        log.setLogTime(new Date());
        logDao.save(log);
    }

    /**
     * 导入excel时记录日志
     *
     * @param logEntry
     * @param yearMonthDto
     * @return
     */
    @Pointcut(value = "execution(Boolean com.oilchem.trade.service.impl.TradeSumServiceImpl.importExcel(" +
            "java.util.Map.Entry<Long, com.oilchem.trade.domain.Log>," +
            "com.oilchem.trade.bean.YearMonthDto))" +
            "&& args(logEntry,yearMonthDto)"
            , argNames = "logEntry,yearMonthDto")
    void cutImportTradeSum(Map.Entry<Long, Log> logEntry, YearMonthDto yearMonthDto) {
    }

    /**
     * 导入Excel前更新日志
     *
     * @param logEntry
     * @param yearMonthDto
     */
    @Before("cutImportTradeSum(logEntry,yearMonthDto)")
    void logImportingTradeSum(Map.Entry<Long, Log> logEntry,
                              YearMonthDto yearMonthDto) {
        log = logDao.findOne(logEntry.getKey());
        log.setImportFlag(importing_flag.getValue());
        log.setLogTime(new Date());
        logDao.save(log);

    }

    /**
     * 导入Excel后更新报日志
     *
     * @param logEntry
     * @param yearMonthDto
     * @param isSuccess
     */
    @AfterReturning(pointcut = "cutImportTradeSum(logEntry,yearMonthDto)",
            returning = "isSuccess")
    void logImportedTradeSum(Map.Entry<Long, Log> logEntry,
                             YearMonthDto yearMonthDto, Boolean isSuccess) {

        log = logDao.findOne(logEntry.getKey());
        log.setImportFlag(imported_flag.getValue());
        log.setErrorOccur(imported_flag.getValue());
        log.setLogTime(new Date());
        logDao.save(log);
    }

    /**
     * 导入Excel发生异常更新日志
     *
     * @param logEntry
     * @param yearMonthDto
     */
    @AfterThrowing("cutImportTradeSum(logEntry,yearMonthDto)")
    void logImportTradeSumThrowing(Map.Entry<Long, Log> logEntry, YearMonthDto yearMonthDto) {

        log = logDao.findOne(logEntry.getKey());
        log.setImportFlag(import_faild.getValue());
        log.setErrorOccur(import_faild.getValue());
        log.setLogTime(new Date());
        logDao.save(log);
    }


    /**
     * 列出日志
     *
     * @param log
     * @param pageable
     * @return
     */
    public Page<Log> findAll(Log log, Pageable pageable) {
        if (pageable == null)
            return null;

        final List<QueryUtils.PropertyFilter> filterList = getLogQueryProps(log);
        Specification<Log> spec = DynamicSpecifications.<Log>byPropertyFilter(
                filterList, Log.class);

        return logDao.findAll(spec,pageable);
    }

    /**
     * 获得Log的查询属性
     * @param log
     * @return
     */
    private List<PropertyFilter> getLogQueryProps(Log log) {
        List<PropertyFilter> filterList = new ArrayList<PropertyFilter>();

        if (isNotBlank(log.getTableType())) {
            String tableType = "%";
            if(log.getTableType().equals("0"))
                tableType = "明细表";
            if(log.getTableType().equals("1"))
                tableType = "总表";
            filterList.add(new PropertyFilter("tableType", tableType));
        }
        return filterList;
    }


}
