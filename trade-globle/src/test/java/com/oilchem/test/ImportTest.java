package com.oilchem.test;

import com.oilchem.trade.domain.detail.ImpTradeDetail;
import com.oilchem.trade.domain.Log;
import com.oilchem.trade.service.CommonService;
import com.oilchem.trade.service.TaskService;
import com.oilchem.trade.service.TradeDetailService;
import com.oilchem.trade.service.TradeSumService;
import com.oilchem.trade.view.controller.CommonController;
import com.oilchem.trade.bean.CommonDto;
import com.oilchem.trade.bean.YearMonthDto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

import static com.oilchem.trade.bean.DocBean.Config.unzip_detail_dir;
import static com.oilchem.trade.bean.DocBean.ImpExpType.import_type;
import static com.oilchem.trade.bean.DocBean.TableType.*;
import static junit.framework.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 12-11-13
 * Time: 上午9:42
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test/applicationContext-root.xml"})
public class ImportTest {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    TaskService taskService;

    @Autowired
    CommonService commonService;

    @Autowired
    TradeDetailService tradeDetailService;

    @Autowired
    TradeSumService tradeSumService;

    YearMonthDto yearMonth;
    MockMultipartFile file;


    @Before
    public void setUp() throws Exception {
        yearMonth = new YearMonthDto();
        yearMonth.setImpExpType(import_type.ordinal());
//        yearMonth.setImpExpType(ImpExpType.出口.getCode());
        yearMonth.setImportType(import_type.getValue());
        yearMonth.setProductType("有机化工");
        yearMonth.setTableType(detail.name());
        yearMonth.setYear(2012);
        yearMonth.setMonth(11);

//        file = new MockMultipartFile("idb_20121113143200.zip",
//                new FileInputStream("D:/aaaa/upload/detailzip/idb_20121113143200.zip"));
    }

    @After
    public void tearDown() throws Exception {

    }

    /**
     * 上传测试
     *
     * @throws Exception
     */
    @Test
    public void testUploadFile() throws Exception {
        assertNotNull(tradeDetailService.uploadFile(file, yearMonth));
    }

    /**
     * 解压测试
     *
     * @throws Exception
     */
    @Test
//    @Test(expected = Exception.class)
    public void testUnpackageFile() throws Exception {
        try {
            Map<Long, Log> unExtractMap = commonService.getUnExtractPackage(detail.getValue());
            for (Map.Entry<Long, Log> entry : unExtractMap.entrySet()) {
                commonService.unpackageFile(
                        entry, unzip_detail_dir.value());
            }
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    /**
     * 导入Access测试
     *
     * @throws Exception
     */
    @Test
    public void testImportAccess() throws Exception {
        Boolean isSuccess = false;
        try {
            Map<Long, Log> unImportMap = commonService.getUnImportFile(detail.getValue());
            if (unImportMap != null)
                for (Map.Entry<Long, Log> entry : unImportMap.entrySet()) {
                    tradeDetailService.importAccess(entry, yearMonth);
                }
            assertTrue(isSuccess);
        } catch (Exception e) {
            e.printStackTrace();
            assertFalse(isSuccess);
        }

    }

    /**
     * 导入excel
     *
     * @throws Exception
     */
    @Test
    public void testImportExcel() throws Exception {

        Boolean isSuccess = false;
        try {
            Map<Long, Log> unImportMap = commonService.getUnImportFile(sum.getValue());
            if (unImportMap != null)
                for (Map.Entry<Long, Log> logEntry : unImportMap.entrySet()) {
                    isSuccess = tradeSumService.importExcel(logEntry, yearMonth);
                }
            assertTrue(isSuccess);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(isSuccess);
        }

    }

    /**
     * 测试查询明细表
     * @throws Exception
     */
    @Test
    public void testFindDetailListWithCriteria() throws Exception {

        ImpTradeDetail impTradeDetail = new ImpTradeDetail();
        impTradeDetail.setProductName("拉伯胶") ;
//        impTradeDetail.setProductCode("13012");
        impTradeDetail.setCity("广东广州市");
//        impTradeDetail.setTradeType("一般贸易");
//        impTradeDetail.setCompanyType("私人企业");

        CommonDto commonDto = new CommonDto();
        commonDto.setPageNumber(0).setPageSize(20).setSort("city:asc");
//        commonDto .setLowValue("2003-9").setHighValue("2012-12");


        Page<ImpTradeDetail> tradeDetails = tradeDetailService
                .findImpWithCriteria(impTradeDetail, commonDto,
                        yearMonth, new CommonController().getPageRequest(commonDto));

        assertEquals(true,tradeDetails.getContent().size() > 0);

    }

    /**
     * 测试查询明细表
     * @throws Exception
     */
    @Test
    public void testFindSumListWithCriteria() throws Exception {

    }

}
