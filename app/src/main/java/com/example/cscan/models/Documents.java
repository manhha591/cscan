package com.example.cscan.models;

public class Documents {

    private int documentId;

    private String documentName;

    private int userId;

    public Documents(String documentName, int userId, String date) {
        this.documentName = documentName;
        this.userId = userId;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;
    public Documents() {
    }

    public Documents(String documentName, int userId) {
        this.documentName = documentName;
        this.userId = userId;
    }

    public Documents(int documentId, String documentName, int userId) {
        this.documentId = documentId;
        this.documentName = documentName;
        this.userId = userId;
    }

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Documents{" +
                "documentId=" + documentId +
                ", documentName='" + documentName + '\'' +
                ", userId=" + userId +
                '}';
    }
}
