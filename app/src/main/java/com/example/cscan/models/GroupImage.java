package com.example.cscan.models;


public class GroupImage {
    private int groupId;

    private String groupName;

    private String groupDate;

    private int userId;

    public GroupImage() {
    }

    public GroupImage(int groupId, String groupName, String groupDate, int userId) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupDate = groupDate;
        this.userId = userId;
    }

    public GroupImage(int groupid, String groupname, String groupdate) {
        this.groupId = groupid;
        this.groupName = groupname;
        this.groupDate = groupdate;
    }

    public GroupImage(String groupName, String groupDate, int userId) {
        this.groupName = groupName;
        this.groupDate = groupDate;
        this.userId = userId;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "GroupImage{" +
                "groupId=" + groupId +
                ", groupName='" + groupName + '\'' +
                ", groupDate='" + groupDate + '\'' +
                ", userId=" + userId +
                '}';
    }


}
