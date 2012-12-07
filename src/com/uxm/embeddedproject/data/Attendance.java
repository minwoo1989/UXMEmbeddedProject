package com.uxm.embeddedproject.data;

import java.io.*;

public class Attendance implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private int uid;
	private int gbid;
	private boolean attendance;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public int getGbid() {
		return gbid;
	}
	public void setGbid(int gbid) {
		this.gbid = gbid;
	}
	public boolean isAttendance() {
		return attendance;
	}
	public void setAttendance(boolean attendance) {
		this.attendance = attendance;
	}

	
}
