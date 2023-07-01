package com.example.cscan.models;

public class Images {
    private int imageId;
    private String imageData;
    private int groupId;

    public Images() {
    }

    public Images(int imageid, String imagename) {
        this.imageId = imageid;
        this.imageData = imagename;
    }

    public Images(String imageData, int groupId) {
        this.imageData = imageData;
        this.groupId = groupId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "Images{" +
                "imageId=" + imageId +
                ", imageData='" + imageData + '\'' +
                ", groupId=" + groupId +
                '}';
    }
}
