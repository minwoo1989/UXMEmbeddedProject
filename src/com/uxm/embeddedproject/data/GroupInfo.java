package com.uxm.embeddedproject.data;

import java.io.*;

public class GroupInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String gName;
	private String gPasswd;
	private String gMaster;
	private String gInfo;
	private String gDate;
	
	public String getgDate() {
		return gDate;
	}
	public void setgDate(String gDate) {
		this.gDate = gDate;
	}
	public String getgInfo() {
		return gInfo;
	}
	public void setgInfo(String gInfo) {
		this.gInfo = gInfo;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getgName() {
		return gName;
	}
	public void setgName(String gName) {
		this.gName = gName;
	}
	public String getgPasswd() {
		return gPasswd;
	}
	public void setgPasswd(String gPasswd) {
		this.gPasswd = gPasswd;
	}
	public String getgMaster() {
		return gMaster;
	}
	public void setgMaster(String gMaster) {
		this.gMaster = gMaster;
	}
	
}
