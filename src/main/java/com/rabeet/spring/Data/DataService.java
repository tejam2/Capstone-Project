package com.rabeet.spring.Data;

import com.orsonpdf.PDFDocument;
import com.orsonpdf.PDFGraphics2D;
import com.orsonpdf.Page;
import com.rabeet.spring.Utilities;
import org.jfree.chart.JFreeChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class DataService {

    private static final Logger logger = LoggerFactory.getLogger(DataService.class);

    public static final String[] AGES = {"E18", "P0", "P3", "P6", "P9"};
    public static final String PROTEIN = "Proteins";
    public static final String LIPID = "Lipids";
    public static final String GCP_FRACTION = "Growth Cone Particulate (GCP)";
    public static final String GCM_FRACTION = "Growth Cone Membrane (GCM)";
    public static final String[] ALGORITHMS = {"MKM: Mini Batch K Means", "AFP: Affinity Propagation", "MSH: MeanShift", "SPC: Spectral Clustering", "WRD: Ward", "AGC: Agglomerative Clustering", "DBS: BSCAN", "OPT: OPTICS", "BRC: Birch", "GMX: Gaussian Mixture"};

    public static final String GCP_Protein_File = "data//GCP_GeneNames_Normalized.csv";
    public static final String GCM_Protein_File = "data//GCM_GeneNames_Normalized.csv";
    public static final String GCP_Lipid_File = "data//GCP_LipidClass_Normalized.csv";
    public static final String GCM_Lipid_File = "data//GCM_LipidClass_Normalized.csv";

    private List<GCPProtein> GCPProteinList;
    private List<GCMProtein> GCMProteinList;
    private List<GCPLipid> GCPLipidList;
    private List<GCMLipid> GCMLipidList;

    private Set<String> names;
    private Set<String> proteinNames;
    private Set<String> lipidNames;

    private Map<String, Object> data;

    public static void main(String[] args) {
        DataService dataService = new DataService();
        Set<String> set = new HashSet<>();
        set.add("GCM_E18");
    }

    public DataService() {
        logger.info("Initializing files");
        try {
            data = new HashMap<>();

            GCPProteinList = new ArrayList<>();
            BufferedReader reader = Utilities.getFileReader(GCP_Protein_File);
            reader.readLine(); reader.readLine();
            String line;
            while((line = reader.readLine()) != null ) {
                String[] elems = line.split(",");
                GCPProtein protein = new GCPProtein(elems);
                protein.setFraction(GCP_FRACTION);
                GCPProteinList.add(protein);
                data.put(makeKey(PROTEIN, GCP_FRACTION, protein.getName()), protein);
            }

            GCMProteinList = new ArrayList<>();
            reader = Utilities.getFileReader(GCM_Protein_File);
            reader.readLine(); reader.readLine();
            while((line = reader.readLine()) != null ) {
                String[] elems = line.split(",");
                GCMProtein protein = new GCMProtein(elems);
                protein.setFraction(GCM_FRACTION);
                GCMProteinList.add(protein);
                data.put(makeKey(PROTEIN, GCM_FRACTION, protein.getName()), protein);
            }

            GCPLipidList = new ArrayList<>();
            reader = Utilities.getFileReader(GCP_Lipid_File);
            reader.readLine(); reader.readLine();
            while((line = reader.readLine()) != null ) {
                String[] elems = line.split(",");
                GCPLipid lipid = new GCPLipid(elems);
                lipid.setFraction(GCP_FRACTION);
                GCPLipidList.add(lipid);
                data.put(makeKey(LIPID, GCP_FRACTION, lipid.getName()), lipid);
            }

            GCMLipidList = new ArrayList<>();
            reader = Utilities.getFileReader(GCM_Lipid_File);
            reader.readLine(); reader.readLine();
            while((line = reader.readLine()) != null ) {
                String[] elems = line.split(",");
                GCMLipid lipid = new GCMLipid(elems);
                lipid.setFraction(GCM_FRACTION);
                GCMLipidList.add(lipid);
                data.put(makeKey(LIPID, GCM_FRACTION, lipid.getName()), lipid);
            }
        } catch (IOException e) {
            logger.error("Unable to read file!", e.getMessage());
        }
    }

    public static String makeKey(String omics, String fraction, String name) {
        return omics+","+fraction+","+name;
    }

    private Map<String, List<Double>> getLipidData(String omics, String geneName, Set<String> ageGroups) {
        Map<String, List<Double>> map = new LinkedHashMap<>();

        return map;
    }

    public List<GCPProtein> getGCPProteinList() {
        return this.GCPProteinList;
    }

    public List<GCMProtein> getGCMProteinList() {
        return this.GCMProteinList;
    }

    public List<GCPLipid> getGCPLipidList() {
        return GCPLipidList;
    }

    public List<GCMLipid> getGCMLipidList() {
        return GCMLipidList;
    }

    public Set<String> getAllNames() {
        if (names != null) return names;

        names = new HashSet<>();
        names.addAll(GCPProteinList.stream().map(GCPProtein::getName).collect(Collectors.toList()));
        names.addAll(GCMProteinList.stream().map(GCMProtein::getName).collect(Collectors.toList()));
        names.addAll(GCPLipidList.stream().map(GCPLipid::getName).collect(Collectors.toList()));
        names.addAll(GCMLipidList.stream().map(GCMLipid::getName).collect(Collectors.toList()));
        return names;
    }

    public Map<String, Object> getMainData() {
        return this.data;
    }

    public Set<String> getAllNames(String selection) {
        if (selection.equals(PROTEIN)) {
            if (proteinNames != null) return proteinNames;
            proteinNames = new HashSet<>();
            proteinNames.addAll(GCPProteinList.stream().map(GCPProtein::getName).collect(Collectors.toList()));
            proteinNames.addAll(GCMProteinList.stream().map(GCMProtein::getName).collect(Collectors.toList()));
            return proteinNames;
        } else if (selection.equals(LIPID)){
            if (lipidNames != null) return lipidNames;
            lipidNames = new HashSet<>();
            lipidNames.addAll(GCPLipidList.stream().map(GCPLipid::getName).collect(Collectors.toList()));
            lipidNames.addAll(GCMLipidList.stream().map(GCMLipid::getName).collect(Collectors.toList()));
            return lipidNames;
        }
        return null;
    }

    public String[][] getTransposedData(String omics, List<String> geneName, String fraction, List<String> age) {
        String csvData = getCSVData(omics, fraction, age,  geneName);
        String[] temp = csvData.split("\n");
        int rows = temp.length, cols;
        String[][] base = new String[rows][];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = temp[i].substring(0, temp[i].length()-1);
            base[i] = temp[i].split(",");
        }
        cols = base[0].length;
        String[][] ans = new String[cols][rows];

        for (int i = 0 ; i < base.length; i++) {
            for (int j = 0; j < base[0].length; j++) {
                ans[j][i] = base[i][j];
            }
        }
        return ans;
    }

    public Map<String, Map<String, List<Double>>> getData(String omics, List<String> geneName, String fraction, List<String> age) {
        Map<String, Map<String, List<Double>>> map = new LinkedHashMap<>();

        logger.info("Received - {}", geneName);

        List list = new ArrayList<>();
        for (String gene : geneName) {
            String key = makeKey(omics, fraction, gene);
            list.add(data.get(key));
        }

        if (omics.equals(PROTEIN)) {
            if (fraction.equals(GCP_FRACTION)) {
                for (Object obj : list) {
                    GCPProtein protein = (GCPProtein) obj;
                    Map<String, List<Double>> temp = new LinkedHashMap<>();
                    age.forEach(e -> temp.put(e, protein.getData(e)));
                    map.put(protein.getName(), temp);
                }

            } else {
                for (Object obj : list) {
                    GCMProtein protein = (GCMProtein) obj;
                    Map<String, List<Double>> temp = new LinkedHashMap<>();
                    age.forEach(e -> temp.put(e, protein.getData(e)));
                    map.put(protein.getName(), temp);
                }
            }
        } else  if (omics.equals(LIPID)) {
            if (fraction.equals(GCP_FRACTION)) {
                for (Object obj : list) {
                    GCPLipid lipid = (GCPLipid) obj;
                    Map<String, List<Double>> temp = new LinkedHashMap<>();
                    age.forEach(e -> temp.put(e, lipid.getData(e)));
                    map.put(lipid.getName(), temp);
                }
            } else {
                for (Object obj : list) {
                    GCMLipid lipid = (GCMLipid) obj;
                    Map<String, List<Double>> temp = new LinkedHashMap<>();
                    age.forEach(e -> temp.put(e, lipid.getData(e)));
                    map.put(lipid.getName(), temp);
                }
            }
        }
        return map;
    }

    public String getCSVData(String omics, String fraction, List<String> age, List<String> names) {
        StringBuilder built = new StringBuilder();
        Map<String, Map<String, List<Double>>> data = getData(omics, names, fraction, age);

        built.append("Label,");
        Map<String, List<Double>> temp = data.values().iterator().next();
        for (Map.Entry<String, List<Double>> entry : temp.entrySet()) {
            for (Double d : entry.getValue()) {
                built.append(entry.getKey()).append(",");
            }
        }
        built.append("\n");

        for (String name : names) {
            built.append(name).append(",");
            Map<String, List<Double>> vMap = data.get(name);
            for (String a : age) {
                for (Double d : vMap.get(a))
                {
                    built.append(d).append(",");
                }
            }

            built.append("\n");
        }

        return built.toString();
    }

}