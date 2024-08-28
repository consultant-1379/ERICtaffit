package com.ericsson.cifwk.taf.taffit.test.cases.netsim;

import com.ericsson.cifwk.taf.annotations.DataSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeDataDataSourceContainer {

    public static class NodeData {

        @DataSource
        public static List<Map<String, Object>> NodeData() throws Exception {
            Map<String, String[]> sims = NetsimInformation.getSimsWith2NEsAndNodes();
            List<Map<String, Object>> records = new ArrayList<>();
            if (sims.isEmpty())
                System.out.println("Map does not contain any entries ");
            for (Map.Entry<String, String[]> entry : sims.entrySet()) {
                String simulation = entry.getKey();
                for (String ne : entry.getValue()) {
                    Map<String, Object> record = new HashMap<>();
                    record.put("Simulation", simulation);
                    record.put("NE", ne);
                    records.add(record);
                }
                break;
            }
            return records;
        }
    }

    public static class NodeData2 {
        @DataSource
        public List<Map<String, Object>> records() throws IOException {
            Map<String, String[]> sims = NetsimInformation.getSimsWith4NEsAndNodes();
            List<Map<String, Object>> records = new ArrayList<>();
            if (sims.isEmpty())
                System.out.println("Map does not contain any entries ");
            for (Map.Entry<String, String[]> entry : sims.entrySet()) {
                Map<String, Object> record = new HashMap<>();
                record.put("Simulation", entry.getKey());
                record.put("NE1", entry.getValue()[0]);
                record.put("NE2", entry.getValue()[1]);
                record.put("NE3", entry.getValue()[2]);
                records.add(record);
                break;
            }
            return records;
        }
    }

    public static class NodeData3 {
        @DataSource
        public List<Map<String, Object>> records() throws IOException {
            List<Map<String, Object>> records = new ArrayList<>();
            Map<String, String[]> sims = NetsimInformation.getSimsWith2NEsAndNodes();
            if (sims.isEmpty())
                System.out.println("Map does not contain any entries ");
            for (Map.Entry<String, String[]> entry : sims.entrySet()) {
                Map<String, Object> record = new HashMap<>();
                record.put("Simulation", entry.getKey());
                record.put("NE", entry.getValue()[0]);
                records.add(record);
                break;
            }
            return records;
        }
    }
}
