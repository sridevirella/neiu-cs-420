package computedata;

import model.MonthlyData;
import model.SectorName;
import model.ComputeData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;


public class StockGainData implements ComputeData {

    private static List<Integer> stockGainPercentageList;
    private static Map<String, Integer> stockGainPercentageMap;
    private static String fromDate, toDate;

    public StockGainData(String fromDateAndYear, String toDateAndYear) {

        fromDate = fromDateAndYear;
        toDate = toDateAndYear;
        stockGainPercentageList = new ArrayList<>();
        stockGainPercentageMap = new HashMap<>();
    }

    public Map<String, Integer> stockGainPercentageData() {

        computeDataForChart();
        createMapForChart();
        return stockGainPercentageMap;
    }

    @Override
    public void computeDataForChart() {

        ComputeData.getAllSectorMonthlyData().forEach(sector -> {

            List<Integer> priceGrowthPercentage = sector.stream()
                    .filter(monthlyData -> monthlyData.getDate().contains(toDate) || monthlyData.getDate().contains(fromDate))
                    .mapToDouble(MonthlyData::getClosePrice)
                    .mapToObj(BigDecimal::valueOf)
                    .reduce((t, d) -> t.subtract(d).divide(d, 2, RoundingMode.CEILING).multiply(BigDecimal.valueOf(100)))
                    .stream()
                    .map(BigDecimal::intValue)
                    .limit(1)
                    .collect(Collectors.toList());
            stockGainPercentageList.add(priceGrowthPercentage.isEmpty() ? 0 : priceGrowthPercentage.get(0));
        });
    }

    @Override
    public void createMapForChart() {

        Arrays.stream(SectorName.values()).forEach(sectorName -> {
            stockGainPercentageMap.put(sectorName.name(), stockGainPercentageList.get(sectorName.getOrder()-1));
        });
    }
}
