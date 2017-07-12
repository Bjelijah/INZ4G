package com.inz.bean;

/**
 * Created by howell on 2016/12/2.
 */

public class APDeviceDBBean {
    String userName;
    String deviceName;
    String deviceIP;
    int devicePort;
    boolean isOnLine;


    public APDeviceDBBean(String userName, String deviceName, String deviceIP, int devicePort) {
        this.userName = userName;
        this.deviceName = deviceName;
        this.deviceIP = deviceIP;
        this.devicePort = devicePort;

    }



    public boolean isOnLine(){
        return this.isOnLine;
    }

    public void setOnLine(boolean isOnLine){
        this.isOnLine = isOnLine;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceIP() {
        return deviceIP;
    }

    public void setDeviceIP(String deviceIP) {
        this.deviceIP = deviceIP;
    }

    public int getDevicePort() {
        return devicePort;
    }

    public void setDevicePort(int devicePort) {
        this.devicePort = devicePort;
    }

    @Override
    public String toString() {
        return "APDeviceDBBean{" +
                "userName='" + userName + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceIP='" + deviceIP + '\'' +
                ", devicePort=" + devicePort +
                '}';
    }
}
