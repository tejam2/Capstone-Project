package com.rabeet.spring.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// One instance - record - of protein
public class GCMProtein {
    private String name;
    private String fraction;
    private Double GC_E18_2;
    private Double GC_E18_3;
    private Double GC_E18_4;
    private Double GC_E18_5;
    private Double GC_E18_6;
    private Double GC_P0_1;
    private Double GC_P0_2;
    private Double GC_P0_3;
    private Double GC_P0_4;
    private Double GC_P0_5;
    private Double GC_P0_6;
    private Double GC_P3_1;
    private Double GC_P3_2;
    private Double GC_P3_3;
    private Double GC_P3_4;
    private Double GC_P3_5;
    private Double GC_P3_6;
    private Double GC_P6_1;
    private Double GC_P6_2;
    private Double GC_P6_3;
    private Double GC_P6_4;
    private Double GC_P6_5;
    private Double GC_P6_6;
    private Double GC_P9_1;
    private Double GC_P9_2;
    private Double GC_P9_3;
    private Double GC_P9_4;
    private Double GC_P9_5;
    private Double GC_P9_6;

    private List<Double> data = new ArrayList<>();
    private List<Double> E_18_data = new ArrayList<>();
    private List<Double> P_0_data = new ArrayList<>();
    private List<Double> P_3_data = new ArrayList<>();
    private List<Double> P_6_data = new ArrayList<>();
    private List<Double> P_9_data = new ArrayList<>();

    public GCMProtein(String[] elems) {
        this.name = elems[0].replace("\"", "");
        this.GC_E18_2 = new Double(elems[1].replace("\"","")); data.add(GC_E18_2); E_18_data.add(GC_E18_2);
        this.GC_E18_3 = new Double(elems[2].replace("\"","")); data.add(GC_E18_3); E_18_data.add(GC_E18_3);
        this.GC_E18_4 = new Double(elems[3].replace("\"","")); data.add(GC_E18_4); E_18_data.add(GC_E18_4);
        this.GC_E18_5 = new Double(elems[4].replace("\"","")); data.add(GC_E18_5); E_18_data.add(GC_E18_5);
        this.GC_E18_6 = new Double(elems[5].replace("\"","")); data.add(GC_E18_6); E_18_data.add(GC_E18_6);
        this.GC_P0_1 = new  Double(elems[6].replace("\"","")); data.add(GC_P0_1);  P_0_data.add(GC_P0_1);
        this.GC_P0_2 = new  Double(elems[7].replace("\"","")); data.add(GC_P0_2);  P_0_data.add(GC_P0_2);
        this.GC_P0_3 = new  Double(elems[8].replace("\"","")); data.add(GC_P0_3);  P_0_data.add(GC_P0_3);
        this.GC_P0_4 = new  Double(elems[9].replace("\"","")); data.add(GC_P0_4); P_0_data.add(GC_P0_4);
        this.GC_P0_5 = new  Double(elems[10].replace("\"","")); data.add(GC_P0_5); P_0_data.add(GC_P0_5);
        this.GC_P0_6 = new  Double(elems[11].replace("\"","")); data.add(GC_P0_6); P_0_data.add(GC_P0_6);
        this.GC_P3_1 = new  Double(elems[12].replace("\"","")); data.add(GC_P3_1); P_3_data.add(GC_P3_1);
        this.GC_P3_2 = new  Double(elems[13].replace("\"","")); data.add(GC_P3_2); P_3_data.add(GC_P3_2);
        this.GC_P3_3 = new  Double(elems[14].replace("\"","")); data.add(GC_P3_3); P_3_data.add(GC_P3_3);
        this.GC_P3_4 = new  Double(elems[15].replace("\"","")); data.add(GC_P3_4); P_3_data.add(GC_P3_4);
        this.GC_P3_5 = new  Double(elems[16].replace("\"","")); data.add(GC_P3_5); P_3_data.add(GC_P3_5);
        this.GC_P3_6 = new  Double(elems[17].replace("\"","")); data.add(GC_P3_6); P_3_data.add(GC_P3_6);
        this.GC_P6_1 = new  Double(elems[18].replace("\"","")); data.add(GC_P6_1); P_6_data.add(GC_P6_1);
        this.GC_P6_2 = new  Double(elems[19].replace("\"","")); data.add(GC_P6_2); P_6_data.add(GC_P6_2);
        this.GC_P6_3 = new  Double(elems[20].replace("\"","")); data.add(GC_P6_3); P_6_data.add(GC_P6_3);
        this.GC_P6_4 = new  Double(elems[21].replace("\"","")); data.add(GC_P6_4); P_6_data.add(GC_P6_4);
        this.GC_P6_5 = new  Double(elems[22].replace("\"","")); data.add(GC_P6_5); P_6_data.add(GC_P6_5);
        this.GC_P6_6 = new  Double(elems[23].replace("\"","")); data.add(GC_P6_6); P_6_data.add(GC_P6_6);
        this.GC_P9_1 = new  Double(elems[24].replace("\"","")); data.add(GC_P9_1); P_9_data.add(GC_P9_1);
        this.GC_P9_2 = new  Double(elems[25].replace("\"","")); data.add(GC_P9_2); P_9_data.add(GC_P9_2);
        this.GC_P9_3 = new  Double(elems[26].replace("\"","")); data.add(GC_P9_3); P_9_data.add(GC_P9_3);
        this.GC_P9_4 = new  Double(elems[27].replace("\"","")); data.add(GC_P9_4); P_9_data.add(GC_P9_4);
        this.GC_P9_5 = new  Double(elems[28].replace("\"","")); data.add(GC_P9_5); P_9_data.add(GC_P9_5);
        this.GC_P9_6 = new  Double(elems[29].replace("\"","")); data.add(GC_P9_6); P_9_data.add(GC_P9_6);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFraction() {
        return fraction;
    }

    public void setFraction(String fraction) {
        this.fraction = fraction;
    }

    public Double getGC_E18_2() {
        return GC_E18_2;
    }

    public void setGC_E18_2(Double GC_E18_2) {
        this.GC_E18_2 = GC_E18_2;
    }

    public Double getGC_E18_3() {
        return GC_E18_3;
    }

    public void setGC_E18_3(Double GC_E18_3) {
        this.GC_E18_3 = GC_E18_3;
    }

    public Double getGC_E18_4() {
        return GC_E18_4;
    }

    public void setGC_E18_4(Double GC_E18_4) {
        this.GC_E18_4 = GC_E18_4;
    }

    public Double getGC_E18_5() {
        return GC_E18_5;
    }

    public void setGC_E18_5(Double GC_E18_5) {
        this.GC_E18_5 = GC_E18_5;
    }

    public Double getGC_E18_6() {
        return GC_E18_6;
    }

    public void setGC_E18_6(Double GC_E18_6) {
        this.GC_E18_6 = GC_E18_6;
    }

    public Double getGC_P0_1() {
        return GC_P0_1;
    }

    public void setGC_P0_1(Double GC_P0_1) {
        this.GC_P0_1 = GC_P0_1;
    }

    public Double getGC_P0_2() {
        return GC_P0_2;
    }

    public void setGC_P0_2(Double GC_P0_2) {
        this.GC_P0_2 = GC_P0_2;
    }

    public Double getGC_P0_3() {
        return GC_P0_3;
    }

    public void setGC_P0_3(Double GC_P0_3) {
        this.GC_P0_3 = GC_P0_3;
    }

    public Double getGC_P0_4() {
        return GC_P0_4;
    }

    public void setGC_P0_4(Double GC_P0_4) {
        this.GC_P0_4 = GC_P0_4;
    }

    public Double getGC_P0_5() {
        return GC_P0_5;
    }

    public void setGC_P0_5(Double GC_P0_5) {
        this.GC_P0_5 = GC_P0_5;
    }

    public Double getGC_P0_6() {
        return GC_P0_6;
    }

    public void setGC_P0_6(Double GC_P0_6) {
        this.GC_P0_6 = GC_P0_6;
    }

    public Double getGC_P3_1() {
        return GC_P3_1;
    }

    public void setGC_P3_1(Double GC_P3_1) {
        this.GC_P3_1 = GC_P3_1;
    }

    public Double getGC_P3_2() {
        return GC_P3_2;
    }

    public void setGC_P3_2(Double GC_P3_2) {
        this.GC_P3_2 = GC_P3_2;
    }

    public Double getGC_P3_3() {
        return GC_P3_3;
    }

    public void setGC_P3_3(Double GC_P3_3) {
        this.GC_P3_3 = GC_P3_3;
    }

    public Double getGC_P3_4() {
        return GC_P3_4;
    }

    public void setGC_P3_4(Double GC_P3_4) {
        this.GC_P3_4 = GC_P3_4;
    }

    public Double getGC_P3_5() {
        return GC_P3_5;
    }

    public void setGC_P3_5(Double GC_P3_5) {
        this.GC_P3_5 = GC_P3_5;
    }

    public Double getGC_P3_6() {
        return GC_P3_6;
    }

    public void setGC_P3_6(Double GC_P3_6) {
        this.GC_P3_6 = GC_P3_6;
    }

    public Double getGC_P6_1() {
        return GC_P6_1;
    }

    public void setGC_P6_1(Double GC_P6_1) {
        this.GC_P6_1 = GC_P6_1;
    }

    public Double getGC_P6_2() {
        return GC_P6_2;
    }

    public void setGC_P6_2(Double GC_P6_2) {
        this.GC_P6_2 = GC_P6_2;
    }

    public Double getGC_P6_3() {
        return GC_P6_3;
    }

    public void setGC_P6_3(Double GC_P6_3) {
        this.GC_P6_3 = GC_P6_3;
    }

    public Double getGC_P6_4() {
        return GC_P6_4;
    }

    public void setGC_P6_4(Double GC_P6_4) {
        this.GC_P6_4 = GC_P6_4;
    }

    public Double getGC_P6_5() {
        return GC_P6_5;
    }

    public void setGC_P6_5(Double GC_P6_5) {
        this.GC_P6_5 = GC_P6_5;
    }

    public Double getGC_P6_6() {
        return GC_P6_6;
    }

    public void setGC_P6_6(Double GC_P6_6) {
        this.GC_P6_6 = GC_P6_6;
    }

    public Double getGC_P9_1() {
        return GC_P9_1;
    }

    public void setGC_P9_1(Double GC_P9_1) {
        this.GC_P9_1 = GC_P9_1;
    }

    public Double getGC_P9_2() {
        return GC_P9_2;
    }

    public void setGC_P9_2(Double GC_P9_2) {
        this.GC_P9_2 = GC_P9_2;
    }

    public Double getGC_P9_3() {
        return GC_P9_3;
    }

    public void setGC_P9_3(Double GC_P9_3) {
        this.GC_P9_3 = GC_P9_3;
    }

    public Double getGC_P9_4() {
        return GC_P9_4;
    }

    public void setGC_P9_4(Double GC_P9_4) {
        this.GC_P9_4 = GC_P9_4;
    }

    public Double getGC_P9_5() {
        return GC_P9_5;
    }

    public void setGC_P9_5(Double GC_P9_5) {
        this.GC_P9_5 = GC_P9_5;
    }

    public Double getGC_P9_6() {
        return GC_P9_6;
    }

    public void setGC_P9_6(Double GC_P9_6) {
        this.GC_P9_6 = GC_P9_6;
    }

    public List<Double> getData() {
        return data;
    }

    public List<Double> getData(String fraction) {
        switch (fraction) {
            case "E18": return getE_18_data();
            case "P0": return getP_0_data();
            case "P3": return getP_3_data();
            case "P6": return getP_6_data();
            case "P9": return getP_9_data();
            default: return getData();
        }
    }

    public List<Double> getE_18_data() {
        return E_18_data;
    }

    public List<Double> getP_0_data() {
        return P_0_data;
    }

    public List<Double> getP_3_data() {
        return P_3_data;
    }

    public List<Double> getP_6_data() {
        return P_6_data;
    }

    public List<Double> getP_9_data() {
        return P_9_data;
    }

    public List<List<Double>> getData(Set<String> age) {
        List<List<Double>> list = new ArrayList<>(age.size());
        for (String a : age) {
            list.add(getData(a));
        }
        return list;
    }
}
