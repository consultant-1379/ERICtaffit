package com.ericsson.cifwk.taf.taffit.test.cases.netsim;

import com.ericsson.cifwk.taf.annotations.DataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulationDataSourceContainer {

    public static class Simulations {
        @DataSource
        public List<Map<String, Object>> records() throws Exception {
            Map<String, String[]> simsWith2 = NetsimInformation.getSimsWith2NEsAndNodes();
            Map<String, String[]> simsWith4 = NetsimInformation.getSimsWith4NEsAndNodes();
            List<Map<String, Object>> records = new ArrayList<>();
            if (simsWith2.isEmpty() || simsWith4.isEmpty())
                System.out.println("Map does not contain any entries ");
            for (Map.Entry<String, String[]> entry : simsWith2.entrySet()) {
                Map<String, Object> record = new HashMap<>();
                record.put("Simulation", entry.getKey());
                record.put("NEcount", 2);
                records.add(record);
                break;
            }
            for (Map.Entry<String, String[]> entry : simsWith4.entrySet()) {
                Map<String, Object> record = new HashMap<>();
                record.put("Simulation", entry.getKey());
                record.put("NEcount", 4);
                records.add(record);
                break;
            }
            return records;
        }
    }

    public static class Simulations2 {
        @DataSource
        public static List<Map<String, Object>> records() throws Exception {
            Map<String, String[]> simsWith2 = NetsimInformation.getSimsWith2NEsAndNodes();
            List<Map<String, Object>> records = new ArrayList<>();
            if (simsWith2.isEmpty())
                //System.out.println("Map does not contain any entries ");
                for (Map.Entry<String, String[]> entry : simsWith2.entrySet()) {
                    Map<String, Object> record = new HashMap<>();
                    record.put("Simulation", entry.getKey());
                    record.put("NEList", entry.getValue()[0] + ";" + entry.getValue()[1]);
                    records.add(record);
                    break;
                }
            return records;
        }
    }

    public static class SimulationsList {
        @DataSource
        public static List<Map<String, Object>> records() throws Exception {
            Map<String, String[]> simsWith2 = NetsimInformation.getSimsWith2NEsAndNodes();
            List<Map<String, Object>> records = new ArrayList<>();
            Map<String, Object> record = new HashMap<>();
            String sim1 = null, sim2 = null;
            for (Map.Entry<String, String[]> entry : simsWith2.entrySet()) {
                if (sim1 == null)
                    sim1 = entry.getKey();
                else if (sim2 == null) {
                    sim2 = entry.getKey();
                    break;
                }
            }
            record.put("Simulations", sim1 + ";" + sim2);
            record.put("NodeCount", 2 + ";" + 2);
            record.put("TotalNodes", 4);
            records.add(record);
            return records;
        }
    }
}
