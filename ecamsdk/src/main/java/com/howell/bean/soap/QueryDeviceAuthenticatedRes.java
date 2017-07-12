package com.howell.bean.soap;

/**
 * query result if this phone has been authenticated to server<br/>
 * @author howell
 */
public class QueryDeviceAuthenticatedRes {
	String Result;
	boolean Authenticated;
	public String getResult() {
		return Result;
	}
	public void setResult(String result) {
		Result = result;
	}
	public boolean isAuthenticated() {
		return Authenticated;
	}
	public void setAuthenticated(boolean authenticated) {
		Authenticated = authenticated;
	}
	public QueryDeviceAuthenticatedRes(String result, boolean authenticated) {
		super();
		Result = result;
		Authenticated = authenticated;
	}
	public QueryDeviceAuthenticatedRes() {
		super();
	}
	@Override
	public String toString() {
		return "QueryDeviceAuthenticatedRes [Result=" + Result + ", Authenticated=" + Authenticated + "]";
	}
	
}
