package com.example.cscan.models;

public class DataTypes {

    private int dataTypeId;

    private String dataTypeName;

    private int documentId;

    public DataTypes() {
    }

    public DataTypes(String dataTypeName, int documentId) {
        this.dataTypeName = dataTypeName;
        this.documentId = documentId;
    }

    public int getDataTypeId() {
        return dataTypeId;
    }

    public void setDataTypeId(int dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    public String getDataTypeName() {
        return dataTypeName;
    }

    public void setDataTypeName(String dataTypeName) {
        this.dataTypeName = dataTypeName;
    }

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    @Override
    public String toString() {
        return "DataTypes{" +
                "dataTypeId=" + dataTypeId +
                ", dataTypeName='" + dataTypeName + '\'' +
                ", documentId=" + documentId +
                '}';
    }
}
