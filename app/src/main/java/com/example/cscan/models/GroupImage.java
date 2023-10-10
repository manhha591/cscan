package com.example.cscan.models;


public class GroupImage {
    private int groupId;

    private String groupName;

    private String groupDate;

    private int dataTypeId;

    public GroupImage() {
    }

    public GroupImage(int groupId, String groupName, String groupDate, int dataTypeId) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupDate = groupDate;
        this.dataTypeId = dataTypeId;
    }

    public GroupImage(int groupid, String groupname, String groupdate) {
        this.groupId = groupid;
        this.groupName = groupname;
        this.groupDate = groupdate;
    }

    public GroupImage(String groupName, String groupDate, int dataTypeId) {
        this.groupName = groupName;
        this.groupDate = groupDate;
        this.dataTypeId = dataTypeId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDate() {
        return groupDate;
    }

    public void setGroupDate(String groupDate) {
        this.groupDate = groupDate;
    }

    public int getDataTypeId() {
        return dataTypeId;
    }

    public void setDataTypeId(int dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    @Override
    public String toString() {
        return "GroupImage{" +
                "groupId=" + groupId +
                ", groupName='" + groupName + '\'' +
                ", groupDate='" + groupDate + '\'' +
                ", dataTypeId=" + dataTypeId +
                '}';
    }


}
