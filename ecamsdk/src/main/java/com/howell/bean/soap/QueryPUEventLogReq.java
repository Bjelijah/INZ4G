package com.howell.bean.soap;

/**
 * Created by Administrator on 2017/6/16.
 */

public class QueryPUEventLogReq {
    String account;
    String session;
    String devID;
    String eventType;
    String eventState;
    String startTime;
    String endTime;
    int pageNo;
    String searchID;
    int pageSize;

    @Override
    public String toString() {
        return "QueryPUEventLogReq{" +
                "account='" + account + '\'' +
                ", session='" + session + '\'' +
                ", devID='" + devID + '\'' +
                ", eventType='" + eventType + '\'' +
                ", eventState='" + eventState + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", pageNo=" + pageNo +
                ", searchID='" + searchID + '\'' +
                ", pageSize=" + pageSize +
                '}';
    }

    public QueryPUEventLogReq() {
    }

    public QueryPUEventLogReq(String account, String session, String devID, String eventType, String eventState, String startTime, String endTime, int pageNo, String searchID, int pageSize) {

        this.account = account;
        this.session = session;
        this.devID = devID;
        this.eventType = eventType;
        this.eventState = eventState;
        this.startTime = startTime;
        this.endTime = endTime;
        this.pageNo = pageNo;
        this.searchID = searchID;
        this.pageSize = pageSize;
    }

    public String getAccount() {

        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getDevID() {
        return devID;
    }

    public void setDevID(String devID) {
        this.devID = devID;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventState() {
        return eventState;
    }

    public void setEventState(String eventState) {
        this.eventState = eventState;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public String getSearchID() {
        return searchID;
    }

    public void setSearchID(String searchID) {
        this.searchID = searchID;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
