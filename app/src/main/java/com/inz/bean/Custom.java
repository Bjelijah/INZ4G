package com.inz.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/2/23.
 */

public class Custom implements Serializable {
    boolean isCustom;
    String customIP;
    int customPort;
    boolean isSSL;

    public boolean isCustom() {
        return isCustom;
    }

    public void setCustom(boolean custom) {
        isCustom = custom;
    }

    public String getCustomIP() {
        return customIP;
    }

    public void setCustomIP(String customIP) {
        if (customIP==null){
            this.customIP = "";
            return;
        }
        this.customIP = customIP;
    }

    public int getCustomPort() {
        return customPort;
    }

    public void setCustomPort(int customPort) {
        this.customPort = customPort;
    }

    public boolean isSSL() {
        return isSSL;
    }

    public void setSSL(boolean SSL) {
        isSSL = SSL;
    }

    @Override
    public String toString() {
        return "Custom{" +
                "isCustom=" + isCustom +
                ", customIP='" + customIP + '\'' +
                ", customPort=" + customPort +
                ", isSSL=" + isSSL +
                '}';
    }
}
