package com.uxm.embeddedproject.data;

import java.io.*;

public class GroupUser implements Serializable {

	private static final long serialVersionUID = 1L;
	private int GUId;
	private int gId;
	private int uId;
	
	public int getGUId() {
		return GUId;
	}
	public void setGUId(int gUId) {
		GUId = gUId;
	}
	public int getgId() {
		return gId;
	}
	public void setgId(int gId) {
		this.gId = gId;
	}
	public int getuId() {
		return uId;
	}
	public void setuId(int uId) {
		this.uId = uId;
	}
}
