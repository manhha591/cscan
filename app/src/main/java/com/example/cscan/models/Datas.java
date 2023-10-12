package com.example.cscan.models;

public class Datas {

    private int dataId;

    private String dataName;

    private String dataValue;

    private int dataTypeId;

    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Datas(String dataName, String dataValue, int dataTypeId, String date) {
        this.dataName = dataName;
        this.dataValue = dataValue;
        this.dataTypeId = dataTypeId;
        this.date = date;
    }

    public Datas() {
    }

    public Datas(String dataName, String dataValue, int dataTypeId) {
        this.dataName = dataName;
        this.dataValue = dataValue;
        this.dataTypeId = dataTypeId;
    }

    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
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
