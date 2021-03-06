package com.howell.bean.soap;

/**
 * Created by Administrator on 2017/6/20.
 */

public class SetVideoBasicReq {
    String account;
    String session;
    String devID;
    int channelNo;
    String DNMode;
    int DNSensitivity;
    String AGCMode;
    int AGC;
    String eShutterMode;
    String eShutter;
    String infraredMode;

    @Override
    public String toString() {
        return "SetVideoBasicReq{" +
                "account='" + account + '\'' +
                ", session='" + session + '\'' +
                ", devID='" + devID + '\'' +
                ", channelNo=" + channelNo +
                ", DNMode='" + DNMode + '\'' +
                ", DNSensitivity=" + DNSensitivity +
                ", AGCMode='" + AGCMode + '\'' +
                ", AGC=" + AGC +
                ", eShutterMode='" + eShutterMode + '\'' +
                ", eShutter='" + eShutter + '\'' +
                ", infraredMode='" + infraredMode + '\'' +
                '}';
    }

    public SetVideoBasicReq() {
    }

    public SetVideoBasicReq(String account, String session, String devID, int channelNo, String DNMode, int DNSensitivity, String AGCMode, int AGC, String eShutterMode, String eShutter, String infraredMode) {

        this.account = account;
        this.session = session;
        this.devID = devID;
        this.channelNo = channelNo;
        this.DNMode = DNMode;
        this.DNSensitivity = DNSensitivity;
        this.AGCMode = AGCMode;
        this.AGC = AGC;
        this.eShutterMode = eShutterMode;
        this.eShutter = eShutter;
        this.infraredMode = infraredMode;
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

    public int getChannelNo() {
        return channelNo;
    }

    public void setChannelNo(int channelNo) {
        this.channelNo = channelNo;
    }

    public String getDNMode() {
        return DNMode;
    }

    public void setDNMode(String DNMode) {
        this.DNMode = DNMode;
    }

    public int getDNSensitivity() {
        return DNSensitivity;
    }

    public void setDNSensitivity(int DNSensitivity) {
        this.DNSensitivity = DNSensitivity;
    }

    public String getAGCMode() {
        return AGCMode;
    }

    public void setAGCMode(String AGCMode) {
        this.AGCMode = AGCMode;
    }

    public int getAGC() {
        return AGC;
    }

    public void setAGC(int AGC) {
        this.AGC = AGC;
    }

    public String geteShutterMode() {
        return eShutterMode;
    }

    public void seteShutterMode(String eShutterMode) {
        this.eShutterMode = eShutterMode;
    }

    public String geteShutter() {
        return eShutter;
    }

    public void seteShutter(String eShutter) {
        this.eShutter = eShutter;
    }

    public String getInfraredMode() {
        return infraredMode;
    }

    public void setInfraredMode(String infraredMode) {
        this.infraredMode = infraredMode;
    }
}
