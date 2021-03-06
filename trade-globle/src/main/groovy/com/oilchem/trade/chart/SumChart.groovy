package com.oilchem.trade.chart

import ofc4j.model.Chart
import com.oilchem.trade.domain.abstrac.TradeSum
import com.oilchem.trade.bean.ChartData
import ofc4j.model.Text

import ofc4j.model.elements.LineChart

import static com.oilchem.trade.bean.DocBean.ExcelFiled.excel_num_month
import static com.oilchem.trade.bean.DocBean.ExcelFiled.excel_num_sum
import static com.oilchem.trade.bean.DocBean.ExcelFiled.excel_money_month
import static com.oilchem.trade.bean.DocBean.ExcelFiled.excel_money_sum
import static com.oilchem.trade.bean.DocBean.ExcelFiled.excel_avg_price_month
import static com.oilchem.trade.bean.DocBean.ExcelFiled.excel_avg_price_sum
import static com.oilchem.trade.bean.DocBean.ExcelFiled.excel_pm
import static com.oilchem.trade.bean.DocBean.ExcelFiled.excel_py
import static com.oilchem.trade.bean.DocBean.ExcelFiled.excel_pq
import ofc4j.model.elements.BarChart

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 12-12-4
 * Time: 上午9:20
 * To change this template use File | Settings | File Templates.
 */
class SumChart extends Common{

    def List<Chart> getSumChart(Map<String, ChartData<TradeSum>> chartDataMap, String chartType) {

        //--------------tradeSum----------------------------
        List<Chart> sumChartList = new ArrayList<Chart>();
        Chart nummonthChat = new Chart()
                .setTitle(new Text(excel_num_month.getValue())).setYLegend(new Text(excel_num_month.getValue(), style));
        Chart numSumChat = new Chart()
                .setTitle(new Text(excel_num_sum.getValue())).setYLegend(new Text(excel_num_sum.getValue(), style))
        Chart moneyMonthChat = new Chart()
                .setTitle(new Text(excel_money_month.getValue())).setYLegend(new Text(excel_money_month.getValue(), style))
        Chart moneySumChat = new Chart()
                .setTitle(new Text(excel_money_sum.getValue())).setYLegend(new Text(excel_money_sum.getValue(), style));
        Chart avgPriceMonthChat = new Chart()
                .setTitle(new Text(excel_avg_price_month.getValue())).setYLegend(new Text(excel_avg_price_month.getValue(), style));
        Chart avgPriceSumChat = new Chart()
                .setTitle(new Text(excel_avg_price_sum.getValue())).setYLegend(new Text(excel_avg_price_sum.getValue(), style));
        Chart pmChart = new Chart()
                .setTitle(new Text(excel_pm.getValue())).setYLegend(new Text(excel_pm.getValue(), style));
        Chart pyChart = new Chart()
                .setTitle(new Text(excel_py.getValue())).setYLegend(new Text(excel_py.getValue(), style));
        Chart pqChart = new Chart()
                .setTitle(new Text(excel_pq.getValue())).setYLegend(new Text(excel_pq.getValue(), style));

        Map<String,BigDecimal> minRangMap =  new TreeMap<String, BigDecimal>();
        Map<String,BigDecimal> maxRangMap = new TreeMap<String, BigDecimal>();
        Map<String,BigDecimal> stepMap = new TreeMap<String, BigDecimal>();

        for(Map.Entry<String,ChartData<TradeSum>> it:chartDataMap) {
            String code = it.key
            ChartData<TradeSum> chartData = it.value

            List<LineChart> sumElementList = null
            if ("barChart".equals(chartType)) {
                sumElementList = getSumBarList(code, chartData.elementList)
            } else {
                sumElementList = getSumLineList(code, chartData.elementList)
            }

            nummonthChat = newChart(nummonthChat, chartData, excel_num_month.getValue(),minRangMap,maxRangMap,stepMap)
                    .addElements(sumElementList.get(0))

            numSumChat = newChart(numSumChat, chartData, excel_num_sum.getValue(),minRangMap,maxRangMap,stepMap)
                    .addElements(sumElementList.get(1))

            moneyMonthChat = newChart(moneyMonthChat, chartData, excel_money_month.getValue(),minRangMap,maxRangMap,stepMap)
                    .addElements(sumElementList.get(2))

            moneySumChat = newChart(moneySumChat, chartData, excel_money_sum.getValue(),minRangMap,maxRangMap,stepMap)
                    .addElements(sumElementList.get(3))

            avgPriceMonthChat = newChart(avgPriceMonthChat, chartData, excel_avg_price_month.getValue(),minRangMap,maxRangMap,stepMap)
                    .addElements(sumElementList.get(4))

            avgPriceSumChat = newChart(avgPriceSumChat, chartData, excel_avg_price_sum.getValue(),minRangMap,maxRangMap,stepMap)
                    .addElements(sumElementList.get(5))

            pmChart = newChart(pmChart, chartData, excel_pm.getValue(),minRangMap,maxRangMap,stepMap)
                    .addElements(sumElementList.get(6))

            pyChart = newChart(pyChart, chartData, excel_py.getValue(),minRangMap,maxRangMap,stepMap)
                    .addElements(sumElementList.get(7))

            pqChart = newChart(pqChart, chartData, excel_pq.getValue(),minRangMap,maxRangMap,stepMap)
                    .addElements(sumElementList.get(8))

        }
        [nummonthChat,numSumChat,moneyMonthChat,moneySumChat,avgPriceMonthChat,avgPriceSumChat,pmChart,pyChart,pqChart]
    }


    def getSumLineList(String code, List<TradeSum> sumsList) {

        LineChart numMonthLineChart = new LineChart().setText(code)
        LineChart numSumLineChart = new LineChart().setText(code)
        LineChart moneyMonthLineChart = new LineChart().setText(code)
        LineChart moneySumhLineChart = new LineChart().setText(code)
        LineChart avgPriceMonthhLineChart = new LineChart().setText(code)
        LineChart avgPriceSumLineChart = new LineChart().setText(code)
        LineChart pmLineChart = new LineChart().setText(code)
        LineChart pyLineChart = new LineChart().setText(code)
        LineChart pqLineChart = new LineChart().setText(code)

        sumsList.each {
            numMonthLineChart = newLineElement(numMonthLineChart, it).addValues(it==null ? 0 : it.numMonth.doubleValue())
            numSumLineChart = newLineElement(numSumLineChart, it).addValues(it==null ? 0 : it.numSum.doubleValue())
            moneyMonthLineChart = newLineElement(moneyMonthLineChart, it).addValues(it==null ? 0 :it.moneyMonth.doubleValue())
            moneySumhLineChart = newLineElement(moneySumhLineChart, it).addValues(it==null ? 0 :it.moneySum.doubleValue())
            avgPriceMonthhLineChart = newLineElement(avgPriceMonthhLineChart, it).addValues(it==null ? 0 :it.avgPriceMonth.doubleValue())
            avgPriceSumLineChart = newLineElement(avgPriceSumLineChart, it).addValues(it==null ? 0 :it.avgPriceSum.doubleValue())
            pmLineChart = newLineElement(pmLineChart, it).addValues(it==null ? 0 :it.pm.doubleValue())
            pyLineChart = newLineElement(pyLineChart, it).addValues(it==null ? 0 :it.py.doubleValue())
            pqLineChart = newLineElement(pqLineChart, it).addValues(it==null ? 0 :it.pq.doubleValue())
        }
        [numMonthLineChart,numSumLineChart,moneyMonthLineChart,moneySumhLineChart,
                avgPriceMonthhLineChart,avgPriceSumLineChart,pmLineChart,pyLineChart,pqLineChart]
    }

    def getSumBarList(String code, List<TradeSum> sumsList) {

        BarChart numMonthBarChart = new BarChart(BarChart.Style.GLASS).setText(code)
        BarChart numSumBarChart = new BarChart(BarChart.Style.GLASS).setText(code)
        BarChart moneyMonthBarChart = new BarChart(BarChart.Style.GLASS).setText(code)
        BarChart moneySumhBarChart = new BarChart(BarChart.Style.GLASS).setText(code)
        BarChart avgPriceMonthhBarChart = new BarChart(BarChart.Style.GLASS).setText(code)
        BarChart avgPriceSumBarChart = new BarChart(BarChart.Style.GLASS).setText(code)
        BarChart pmBarChart = new BarChart(BarChart.Style.GLASS).setText(code)
        BarChart pyBarChart = new BarChart(BarChart.Style.GLASS).setText(code)
        BarChart pqBarChart = new BarChart(BarChart.Style.GLASS).setText(code)

        sumsList.each {
            numMonthBarChart = newBarElement(numMonthBarChart).addValues(it==null ? 0 : it.numMonth.doubleValue())
            numSumBarChart = newBarElement(numSumBarChart).addValues(it==null ? 0 : it.numSum.doubleValue())
            moneyMonthBarChart = newBarElement(moneyMonthBarChart).addValues(it==null ? 0 :it.moneyMonth.doubleValue())
            moneySumhBarChart = newBarElement(moneySumhBarChart).addValues(it==null ? 0 :it.moneySum.doubleValue())
            avgPriceMonthhBarChart = newBarElement(avgPriceMonthhBarChart).addValues(it==null ? 0 :it.avgPriceMonth.doubleValue())
            avgPriceSumBarChart = newBarElement(avgPriceSumBarChart).addValues(it==null ? 0 :it.avgPriceSum.doubleValue())
            pmBarChart = newBarElement(pmBarChart).addValues(it==null ? 0 :it.pm.doubleValue())
            pyBarChart = newBarElement(pyBarChart).addValues(it==null ? 0 :it.py.doubleValue())
            pqBarChart = newBarElement(pqBarChart).addValues(it==null ? 0 :it.pq.doubleValue())
        }
        [numMonthBarChart,numSumBarChart,moneyMonthBarChart,moneySumhBarChart,
                avgPriceMonthhBarChart,avgPriceSumBarChart,pmBarChart,pyBarChart,pqBarChart]
    }

}
