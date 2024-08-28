package com.ericsson.cifwk.taf.taffit.test.cases.datadriven;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.ericsson.cifwk.taf.annotations.DataSource;

public class DataSourceContainer {

    public static class NameAndSignum {
        @DataSource
        public List<Map<String, Object>> records() {
            List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
            records.add(nameAndSignumRecord("Paul", "epaudoy"));
            records.add(nameAndSignumRecord("Carol", "ecarheg"));
            records.add(nameAndSignumRecord("Sarah", "esardoh"));
            records.add(nameAndSignumRecord("Carol", "ecarbro"));
            records.add(nameAndSignumRecord("Carol", "ecarden"));
            records.add(nameAndSignumRecord("Stephen", "estehug"));
            records.add(nameAndSignumRecord("Carol", "ecarmar"));
            records.add(nameAndSignumRecord("Anne", "eanngib"));
            records.add(nameAndSignumRecord("Thomas", "ethodur"));
            records.add(nameAndSignumRecord("Carol", "ecarlyn"));
            return records;
        }
    }

    public static Map<String, Object> nameAndSignumRecord(String name, String signum) {
        Map<String, Object> record = new HashMap<String, Object>();
        record.put("name", name);
        record.put("signum", signum);
        return record;
    }

    public static class CountyAndTown {
        @DataSource
        public List<Map<String, Object>> records() {
            List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
            records.add(countyAndTownRecord("Mayo", "Ballina"));
            records.add(countyAndTownRecord("Westmeath", "Mullingar"));
            records.add(countyAndTownRecord("Dublin", "Swords"));
            records.add(countyAndTownRecord("Sligo", "Dromore West"));
            records.add(countyAndTownRecord("Leitrim", "Manorhamilton"));
            records.add(countyAndTownRecord("Westmeath", "Athlone"));
            records.add(countyAndTownRecord("Longford", "BEdgeworthstownallina"));
            records.add(countyAndTownRecord("Donegal", "Letterkenny"));
            records.add(countyAndTownRecord("Clare", "Ennis"));
            records.add(countyAndTownRecord("Cork", "Milltown"));
            return records;
        }
    }

    public static Map<String, Object> countyAndTownRecord(String county, String town) {
        Map<String, Object> record = new HashMap<String, Object>();
        record.put("county", county);
        record.put("town", town);
        return record;
    }

    public static class Animals {
        @DataSource
        public List<Map<String, Object>> records() {
            List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
            records.add(animalRecord("dog"));
            records.add(animalRecord("cat"));
            records.add(animalRecord("horse"));
            records.add(animalRecord("sheep"));
            records.add(animalRecord("fox"));
            records.add(animalRecord("bear"));
            records.add(animalRecord("wolf"));
            records.add(animalRecord("tiger"));
            records.add(animalRecord("lion"));
            records.add(animalRecord("rabbit"));
            records.add(animalRecord("elephant"));
            records.add(animalRecord("kangaroo"));
            records.add(animalRecord("leopard"));
            records.add(animalRecord("ferret"));
            records.add(animalRecord("rat"));
            records.add(animalRecord("mouse"));
            records.add(animalRecord("deer"));
            records.add(animalRecord("raccoon"));
            records.add(animalRecord("hyena"));
            records.add(animalRecord("otter"));
            return records;
        }
    }

    public static Map<String, Object> animalRecord(String animalName) {
        Map<String, Object> record = new HashMap<String, Object>();
        record.put("animal", animalName);
        return record;
    }
}
