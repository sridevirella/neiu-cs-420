package util;

import domain.MonthlyData;
import domain.SectorName;
import domain.YearName;

import java.io.IOException;
import java.util.*;

import static util.YearlyDataUtil.getYearlyDataMap;

public class SectorDataUtil {

    private SectorDataUtil() {}

    public static Map<SectorName, Map<YearName, List<MonthlyData>>> getSectorsDataMap() throws IOException {

        Map<SectorName, Map<YearName, List<MonthlyData>>> sectorDataMap = new HashMap<>();
        getAllSectorsData(sectorDataMap);
        return sectorDataMap;
    }

    private static void getAllSectorsData(Map<SectorName, Map<YearName, List<MonthlyData>>> sdm) throws IOException {

        for (SectorName sectorName : SectorName.values())
            sdm.put(sectorName, getYearlyDataMap(sectorName.getSector() + ".txt"));
    }
}

