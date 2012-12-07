package com.uxm.embeddedproject.data;

import java.io.*;

public class EmbedMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	private String msg;
	private Object obj;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

}
