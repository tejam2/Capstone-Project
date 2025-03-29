package com.rabeet.spring.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// One instance - record - of protein
public class GCPProtein {
    private String name;
    private String fraction;
    private Double M_E18_1;
    private Double M_E18_2;
    private Double M_E18_3;
    private Double M_E18_4;
    private Double M_E18_5;
    private Double M_E18_6;
    private Double M_P0_1;
    private Double M_P0_2;
    private Double M_P0_3;
    private Double M_P0_4;
    private Double M_P0_5;
    private Double M_P0_6;
    private Double M_P3_1;
    private Double M_P3_2;
    private Double M_P3_3;
    private Double M_P3_4;
    private Double M_P3_5;
    private Double M_P3_6;
    private Double M_P6_1;
    private Double M_P6_2;
    private Double M_P6_3;
    private Double M_P6_4;
    private Double M_P6_5;
    private Double M_P6_6;
    private Double M_P9_2;
    private Double M_P9_3;
    private Double M_P9_4;
    private Double M_P9_5;
    private Double M_P9_6;
    private List<Double> data = new ArrayList<>();
    private List<Double> E_18_data = new ArrayList<>();
    private List<Double> P_0_data = new ArrayList<>();
    private List<Double> P_3_data = new ArrayList<>();
    private List<Double> P_6_data = new ArrayList<>();
    private List<Double> P_9_data = new ArrayList<>();

    public GCPProtein(String[] elems) {
        this.name = elems[0].replace("\"", "");
        this.M_E18_1 = new Double(elems[1].replace("\"","")); data.add(M_E18_1); E_18_data.add(M_E18_1);
        this.M_E18_2 = new Double(elems[2].replace("\"","")); data.add(M_E18_2); E_18_data.add(M_E18_2);
        this.M_E18_3 = new Double(elems[3].replace("\"","")); data.add(M_E18_3); E_18_data.add(M_E18_3);
        this.M_E18_4 = new Double(elems[4].replace("\"","")); data.add(M_E18_4); E_18_data.add(M_E18_4);
        this.M_E18_5 = new Double(elems[5].replace("\"","")); data.add(M_E18_5); E_18_data.add(M_E18_5);
        this.M_E18_6 = new Double(elems[6].replace("\"","")); data.add(M_E18_6); E_18_data.add(M_E18_6);
        this.M_P0_1 = new  Double(elems[7].replace("\"","")); data.add(M_P0_1);  P_0_data.add(M_P0_1);
        this.M_P0_2 = new  Double(elems[8].replace("\"","")); data.add(M_P0_2);  P_0_data.add(M_P0_2);
        this.M_P0_3 = new  Double(elems[9].replace("\"","")); data.add(M_P0_3);  P_0_data.add(M_P0_3);
        this.M_P0_4 = new  Double(elems[10].replace("\"","")); data.add(M_P0_4); P_0_data.add(M_P0_4);
        this.M_P0_5 = new  Double(elems[11].replace("\"","")); data.add(M_P0_5); P_0_data.add(M_P0_5);
        this.M_P0_6 = new  Double(elems[12].replace("\"","")); data.add(M_P0_6); P_0_data.add(M_P0_6);
        this.M_P3_1 = new  Double(elems[13].replace("\"","")); data.add(M_P3_1); P_3_data.add(M_P3_1);
        this.M_P3_2 = new  Double(elems[14].replace("\"","")); data.add(M_P3_2); P_3_data.add(M_P3_2);
        this.M_P3_3 = new  Double(elems[15].replace("\"","")); data.add(M_P3_3); P_3_data.add(M_P3_3);
        this.M_P3_4 = new  Double(elems[16].replace("\"","")); data.add(M_P3_4); P_3_data.add(M_P3_4);
        this.M_P3_5 = new  Double(elems[17].replace("\"","")); data.add(M_P3_5); P_3_data.add(M_P3_5);
        this.M_P3_6 = new  Double(elems[18].replace("\"","")); data.add(M_P3_6); P_3_data.add(M_P3_6);
        this.M_P6_1 = new  Double(elems[19].replace("\"","")); data.add(M_P6_1); P_6_data.add(M_P6_1);
        this.M_P6_2 = new  Double(elems[20].replace("\"","")); data.add(M_P6_2); P_6_data.add(M_P6_2);
        this.M_P6_3 = new  Double(elems[21].replace("\"","")); data.add(M_P6_3); P_6_data.add(M_P6_3);
        this.M_P6_4 = new  Double(elems[22].replace("\"","")); data.add(M_P6_4); P_6_data.add(M_P6_4);
        this.M_P6_5 = new  Double(elems[23].replace("\"","")); data.add(M_P6_5); P_6_data.add(M_P6_5);
        this.M_P6_6 = new  Double(elems[24].replace("\"","")); data.add(M_P6_6); P_6_data.add(M_P6_6);
        this.M_P9_2 = new  Double(elems[25].replace("\"","")); data.add(M_P9_2); P_9_data.add(M_P9_2);
        this.M_P9_3 = new  Double(elems[26].replace("\"","")); data.add(M_P9_3); P_9_data.add(M_P9_3);
        this.M_P9_4 = new  Double(elems[27].replace("\"","")); data.add(M_P9_4); P_9_data.add(M_P9_4);
        this.M_P9_5 = new  Double(elems[28].replace("\"","")); data.add(M_P9_5); P_9_data.add(M_P9_5);
        this.M_P9_6 = new  Double(elems[29].replace("\"","")); data.add(M_P9_6); P_9_data.add(M_P9_6);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public double getM_E18_1() {
        return M_E18_1;
    }

    public void setM_E18_1(double m_E18_1) {
        M_E18_1 = m_E18_1;
    }

    public double getM_E18_2() {
        return M_E18_2;
    }

    public void setM_E18_2(double m_E18_2) {
        M_E18_2 = m_E18_2;
    }

    public double getM_E18_3() {
        return M_E18_3;
    }

    public void setM_E18_3(double m_E18_3) {
        M_E18_3 = m_E18_3;
    }

    public double getM_E18_4() {
        return M_E18_4;
    }

    public void setM_E18_4(double m_E18_4) {
        M_E18_4 = m_E18_4;
    }

    public double getM_E18_5() {
        return M_E18_5;
    }

    public void setM_E18_5(double m_E18_5) {
        M_E18_5 = m_E18_5;
    }

    public double getM_E18_6() {
        return M_E18_6;
    }

    public void setM_E18_6(double m_E18_6) {
        M_E18_6 = m_E18_6;
    }

    public double getM_P0_1() {
        return M_P0_1;
    }

    public void setM_P0_1(double m_P0_1) {
        M_P0_1 = m_P0_1;
    }

    public double getM_P0_2() {
        return M_P0_2;
    }

    public void setM_P0_2(double m_P0_2) {
        M_P0_2 = m_P0_2;
    }

    public double getM_P0_3() {
        return M_P0_3;
    }

    public void setM_P0_3(double m_P0_3) {
        M_P0_3 = m_P0_3;
    }

    public double getM_P0_4() {
        return M_P0_4;
    }

    public void setM_P0_4(double m_P0_4) {
        M_P0_4 = m_P0_4;
    }

    public double getM_P0_5() {
        return M_P0_5;
    }

    public void setM_P0_5(double m_P0_5) {
        M_P0_5 = m_P0_5;
    }

    public double getM_P0_6() {
        return M_P0_6;
    }

    public void setM_P0_6(double m_P0_6) {
        M_P0_6 = m_P0_6;
    }

    public double getM_P3_1() {
        return M_P3_1;
    }

    public void setM_P3_1(double m_P3_1) {
        M_P3_1 = m_P3_1;
    }

    public double getM_P3_2() {
        return M_P3_2;
    }

    public void setM_P3_2(double m_P3_2) {
        M_P3_2 = m_P3_2;
    }

    public double getM_P3_3() {
        return M_P3_3;
    }

    public void setM_P3_3(double m_P3_3) {
        M_P3_3 = m_P3_3;
    }

    public double getM_P3_4() {
        return M_P3_4;
    }

    public void setM_P3_4(double m_P3_4) {
        M_P3_4 = m_P3_4;
    }

    public double getM_P3_5() {
        return M_P3_5;
    }

    public void setM_P3_5(double m_P3_5) {
        M_P3_5 = m_P3_5;
    }

    public double getM_P3_6() {
        return M_P3_6;
    }

    public void setM_P3_6(double m_P3_6) {
        M_P3_6 = m_P3_6;
    }

    public double getM_P6_1() {
        return M_P6_1;
    }

    public void setM_P6_1(double m_P6_1) {
        M_P6_1 = m_P6_1;
    }

    public double getM_P6_2() {
        return M_P6_2;
    }

    public void setM_P6_2(double m_P6_2) {
        M_P6_2 = m_P6_2;
    }

    public double getM_P6_3() {
        return M_P6_3;
    }

    public void setM_P6_3(double m_P6_3) {
        M_P6_3 = m_P6_3;
    }

    public double getM_P6_4() {
        return M_P6_4;
    }

    public void setM_P6_4(double m_P6_4) {
        M_P6_4 = m_P6_4;
    }

    public double getM_P6_5() {
        return M_P6_5;
    }

    public void setM_P6_5(double m_P6_5) {
        M_P6_5 = m_P6_5;
    }

    public double getM_P6_6() {
        return M_P6_6;
    }

    public void setM_P6_6(double m_P6_6) {
        M_P6_6 = m_P6_6;
    }

    public double getM_P9_2() {
        return M_P9_2;
    }

    public void setM_P9_2(double m_P9_2) {
        M_P9_2 = m_P9_2;
    }

    public double getM_P9_3() {
        return M_P9_3;
    }

    public void setM_P9_3(double m_P9_3) {
        M_P9_3 = m_P9_3;
    }

    public double getM_P9_4() {
        return M_P9_4;
    }

    public void setM_P9_4(double m_P9_4) {
        M_P9_4 = m_P9_4;
    }

    public double getM_P9_5() {
        return M_P9_5;
    }

    public void setM_P9_5(double m_P9_5) {
        M_P9_5 = m_P9_5;
    }

    public double getM_P9_6() {
        return M_P9_6;
    }

    public void setM_P9_6(double m_P9_6) {
        M_P9_6 = m_P9_6;
    }

    public List<Double> getData() { return this.data; }

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

    public String getFraction() {
        return fraction;
    }

    public void setFraction(String fraction) {
        this.fraction = fraction;
    }

    public List<List<Double>> getData(Set<String> age) {
        List<List<Double>> list = new ArrayList<>(age.size());
        for (String a : age) {
            list.add(getData(a));
        }
        return list;
    }
}
