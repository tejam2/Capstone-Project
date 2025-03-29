package com.rabeet.spring.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GCMLipid {
    private String name;
    private String fraction;
    private double E18_1;
    private double E18_2;
    private double E18_3;
    private double E18_4;
    private double E18_5;
    private double E18_6;
    private double P0_1;
    private double P0_2;
    private double P0_3;
    private double P0_4;
    private double P0_5;
    private double P0_6;
    private double P3_1;
    private double P3_2;
    private double P3_3;
    private double P3_4;
    private double P3_5;
    private double P3_6;
    private double P6_1;
    private double P6_2;
    private double P6_3;
    private double P6_4;
    private double P6_5;
    private double P6_6;
    private double P9_1;
    private double P9_2;
    private double P9_3;
    private double P9_4;
    private double P9_5;
    private double P9_6;

    private List<Double> data = new ArrayList<>();
    private List<Double> E_18_data = new ArrayList<>();
    private List<Double> P_0_data = new ArrayList<>();
    private List<Double> P_3_data = new ArrayList<>();
    private List<Double> P_6_data = new ArrayList<>();
    private List<Double> P_9_data = new ArrayList<>();

    public GCMLipid(String[] elems) {
        this.name = elems[0].replace("\"", "");
        this.E18_1 = new Double(elems[1].replace("\"","")); data.add(E18_1); E_18_data.add(E18_1);
        this.E18_2 = new Double(elems[2].replace("\"","")); data.add(E18_2); E_18_data.add(E18_2);
        this.E18_3 = new Double(elems[3].replace("\"","")); data.add(E18_3); E_18_data.add(E18_3);
        this.E18_4 = new Double(elems[4].replace("\"","")); data.add(E18_4); E_18_data.add(E18_4);
        this.E18_5 = new Double(elems[5].replace("\"","")); data.add(E18_5); E_18_data.add(E18_5);
        this.E18_6 = new Double(elems[6].replace("\"","")); data.add(E18_6); E_18_data.add(E18_6);
        this.P0_1 = new  Double(elems[7].replace("\"","")); data.add(P0_1);  P_0_data.add(P0_1);
        this.P0_2 = new  Double(elems[8].replace("\"","")); data.add(P0_2);  P_0_data.add(P0_2);
        this.P0_3 = new  Double(elems[9].replace("\"","")); data.add(P0_3);  P_0_data.add(P0_3);
        this.P0_4 = new  Double(elems[10].replace("\"","")); data.add(P0_4); P_0_data.add(P0_4);
        this.P0_5 = new  Double(elems[11].replace("\"","")); data.add(P0_5); P_0_data.add(P0_5);
        this.P0_6 = new  Double(elems[12].replace("\"","")); data.add(P0_6); P_0_data.add(P0_6);
        this.P3_1 = new  Double(elems[13].replace("\"","")); data.add(P3_1); P_3_data.add(P3_1);
        this.P3_2 = new  Double(elems[14].replace("\"","")); data.add(P3_2); P_3_data.add(P3_2);
        this.P3_3 = new  Double(elems[15].replace("\"","")); data.add(P3_3); P_3_data.add(P3_3);
        this.P3_4 = new  Double(elems[16].replace("\"","")); data.add(P3_4); P_3_data.add(P3_4);
        this.P3_5 = new  Double(elems[17].replace("\"","")); data.add(P3_5); P_3_data.add(P3_5);
        this.P3_6 = new  Double(elems[18].replace("\"","")); data.add(P3_6); P_3_data.add(P3_6);
        this.P6_1 = new  Double(elems[19].replace("\"","")); data.add(P6_1); P_6_data.add(P6_1);
        this.P6_2 = new  Double(elems[20].replace("\"","")); data.add(P6_2); P_6_data.add(P6_2);
        this.P6_3 = new  Double(elems[21].replace("\"","")); data.add(P6_3); P_6_data.add(P6_3);
        this.P6_4 = new  Double(elems[22].replace("\"","")); data.add(P6_4); P_6_data.add(P6_4);
        this.P6_5 = new  Double(elems[23].replace("\"","")); data.add(P6_5); P_6_data.add(P6_5);
        this.P6_6 = new  Double(elems[24].replace("\"","")); data.add(P6_6); P_6_data.add(P6_6);
        this.P9_1 = new  Double(elems[25].replace("\"","")); data.add(P9_1); P_9_data.add(P9_1);
        this.P9_2 = new  Double(elems[26].replace("\"","")); data.add(P9_2); P_9_data.add(P9_2);
        this.P9_3 = new  Double(elems[27].replace("\"","")); data.add(P9_3); P_9_data.add(P9_3);
        this.P9_4 = new  Double(elems[28].replace("\"","")); data.add(P9_4); P_9_data.add(P9_4);
        this.P9_5 = new  Double(elems[29].replace("\"","")); data.add(P9_5); P_9_data.add(P9_5);
        this.P9_6 = new  Double(elems[30].replace("\"","")); data.add(P9_6); P_9_data.add(P9_6);

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

    public double getE18_1() {
        return E18_1;
    }

    public void setE18_1(double e18_1) {
        E18_1 = e18_1;
    }

    public double getE18_2() {
        return E18_2;
    }

    public void setE18_2(double e18_2) {
        E18_2 = e18_2;
    }

    public double getE18_3() {
        return E18_3;
    }

    public void setE18_3(double e18_3) {
        E18_3 = e18_3;
    }

    public double getE18_4() {
        return E18_4;
    }

    public void setE18_4(double e18_4) {
        E18_4 = e18_4;
    }

    public double getE18_5() {
        return E18_5;
    }

    public void setE18_5(double e18_5) {
        E18_5 = e18_5;
    }

    public double getE18_6() {
        return E18_6;
    }

    public void setE18_6(double e18_6) {
        E18_6 = e18_6;
    }

    public double getP0_1() {
        return P0_1;
    }

    public void setP0_1(double p0_1) {
        P0_1 = p0_1;
    }

    public double getP0_2() {
        return P0_2;
    }

    public void setP0_2(double p0_2) {
        P0_2 = p0_2;
    }

    public double getP0_3() {
        return P0_3;
    }

    public void setP0_3(double p0_3) {
        P0_3 = p0_3;
    }

    public double getP0_4() {
        return P0_4;
    }

    public void setP0_4(double p0_4) {
        P0_4 = p0_4;
    }

    public double getP0_5() {
        return P0_5;
    }

    public void setP0_5(double p0_5) {
        P0_5 = p0_5;
    }

    public double getP0_6() {
        return P0_6;
    }

    public void setP0_6(double p0_6) {
        P0_6 = p0_6;
    }

    public double getP3_1() {
        return P3_1;
    }

    public void setP3_1(double p3_1) {
        P3_1 = p3_1;
    }

    public double getP3_2() {
        return P3_2;
    }

    public void setP3_2(double p3_2) {
        P3_2 = p3_2;
    }

    public double getP3_3() {
        return P3_3;
    }

    public void setP3_3(double p3_3) {
        P3_3 = p3_3;
    }

    public double getP3_4() {
        return P3_4;
    }

    public void setP3_4(double p3_4) {
        P3_4 = p3_4;
    }

    public double getP3_5() {
        return P3_5;
    }

    public void setP3_5(double p3_5) {
        P3_5 = p3_5;
    }

    public double getP3_6() {
        return P3_6;
    }

    public void setP3_6(double p3_6) {
        P3_6 = p3_6;
    }

    public double getP6_1() {
        return P6_1;
    }

    public void setP6_1(double p6_1) {
        P6_1 = p6_1;
    }

    public double getP6_2() {
        return P6_2;
    }

    public void setP6_2(double p6_2) {
        P6_2 = p6_2;
    }

    public double getP6_3() {
        return P6_3;
    }

    public void setP6_3(double p6_3) {
        P6_3 = p6_3;
    }

    public double getP6_4() {
        return P6_4;
    }

    public void setP6_4(double p6_4) {
        P6_4 = p6_4;
    }

    public double getP6_5() {
        return P6_5;
    }

    public void setP6_5(double p6_5) {
        P6_5 = p6_5;
    }

    public double getP6_6() {
        return P6_6;
    }

    public void setP6_6(double p6_6) {
        P6_6 = p6_6;
    }

    public double getP9_1() {
        return P9_1;
    }

    public void setP9_1(double p9_1) {
        P9_1 = p9_1;
    }

    public double getP9_2() {
        return P9_2;
    }

    public void setP9_2(double p9_2) {
        P9_2 = p9_2;
    }

    public double getP9_3() {
        return P9_3;
    }

    public void setP9_3(double p9_3) {
        P9_3 = p9_3;
    }

    public double getP9_4() {
        return P9_4;
    }

    public void setP9_4(double p9_4) {
        P9_4 = p9_4;
    }

    public double getP9_5() {
        return P9_5;
    }

    public void setP9_5(double p9_5) {
        P9_5 = p9_5;
    }

    public double getP9_6() {
        return P9_6;
    }

    public void setP9_6(double p9_6) {
        P9_6 = p9_6;
    }

    public List<Double> getData() {
        return data;
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

    public List<Double> getData(String age) {
        switch (age) {
            case "E18": return getE_18_data();
            case "P0": return getP_0_data();
            case "P3": return getP_3_data();
            case "P6": return getP_6_data();
            case "P9": return getP_9_data();
            default: return getData();
        }
    }

    public List<List<Double>> getData(Set<String> age) {
        List<List<Double>> list = new ArrayList<>(age.size());
        for (String a : age) {
            list.add(getData(a));
        }
        return list;
    }
}
