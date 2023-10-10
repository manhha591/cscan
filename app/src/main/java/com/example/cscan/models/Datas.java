package com.example.cscan.models;

public class Datas {

    private int dataId;

    private String dataname;

    private String dataValue;

    private int dataTypeId;

    public Datas() {
    }

    public Datas(String dataname, String dataValue, int dataTypeId) {
        this.dataname = dataname;
        this.dataValue = dataValue;
        this.dataTypeId = dataTypeId;
    }

    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    public String getDataname() {
        return dataname;
    }

    public void setDataname(String dataname) {
        this.dataname = dataname;
    }

    public String getDataValue() {
        return dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }

    public int getDataTypeId() {
        return dataTypeId;
    }

    public void setDataTypeId(int dataTypeId) {
        this.dataTypeId = dataTypeId;
    }
}
