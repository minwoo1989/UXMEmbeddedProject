package com.uxm.embeddedproject.data;

import java.io.*;

public class GroupBoard implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private int gId;
	private int uId;
	private int gCreateY, gCreateM, gCreateD, gCreateH, gCreateMI;
	private int gMeetY, gMeetM, gMeetD, gMeetH, gMeetMI;
	private String gTitle;
	private String gContent;
	private double gLatitude, gLongtitude;

	public int getgCreateY() {
		return gCreateY;
	}
	public void setgCreateY(int gCreateY) {
		this.gCreateY = gCreateY;
	}
	public int getgCreateM() {
		return gCreateM;
	}
	public void setgCreateM(int gCreateM) {
		this.gCreateM = gCreateM;
	}
	public int getgCreateD() {
		return gCreateD;
	}
	public void setgCreateD(int gCreateD) {
		this.gCreateD = gCreateD;
	}
	public int getgCreateH() {
		return gCreateH;
	}
	public void setgCreateH(int gCreateH) {
		this.gCreateH = gCreateH;
	}
	public int getgCreateMI() {
		return gCreateMI;
	}
	public void setgCreateMI(int gCreateMI) {
		this.gCreateMI = gCreateMI;
	}
	public int getgMeetY() {
		return gMeetY;
	}
	public void setgMeetY(int gMeetY) {
		this.gMeetY = gMeetY;
	}
	public int getgMeetM() {
		return gMeetM;
	}
	public void setgMeetM(int gMeetM) {
		this.gMeetM = gMeetM;
	}
	public int getgMeetD() {
		return gMeetD;
	}
	public void setgMeetD(int gMeetD) {
		this.gMeetD = gMeetD;
	}
	public int getgMeetH() {
		return gMeetH;
	}
	public void setgMeetH(int gMeetH) {
		this.gMeetH = gMeetH;
	}
	public int getgMeetMI() {
		return gMeetMI;
	}
	public void setgMeetMI(int gMeetMI) {
		this.gMeetMI = gMeetMI;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public String getgTitle() {
		return gTitle;
	}
	public void setgTitle(String gTitle) {
		this.gTitle = gTitle;
	}
	public String getgContent() {
		return gContent;
	}
	public void setgContent(String gContent) {
		this.gContent = gContent;
	}

	public double getgLatitude() {
		return gLatitude;
	}
	public void setgLatitude(double gLatitude) {
		this.gLatitude = gLatitude;
	}
	public double getgLongtitude() {
		return gLongtitude;
	}
	public void setgLongtitude(double gLongtitude) {
		this.gLongtitude = gLongtitude;
	}
}
