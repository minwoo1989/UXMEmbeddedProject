package com.uxm.embeddedproject.net;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.uxm.embeddedproject.data.EmbedMessage;

public class NetConnector {
	private final String DISCONNECT_MSG = "DISCONNECT_MSG";
	private Socket sock;
	private ObjectOutputStream objOutStream;
	private ObjectInputStream objInStream;

	public NetConnector(String host, int port) {
		try {
			this.sock = new Socket(host, port);
			this.objOutStream = new ObjectOutputStream(sock.getOutputStream());
			this.objInStream = new ObjectInputStream(sock.getInputStream());

		} catch (Exception ex) {
			this.disConnect();
			ex.printStackTrace();
		}
	}

	public NetConnector(Socket sock) {
		this.sock = sock;
		try {
			this.objOutStream = new ObjectOutputStream(sock.getOutputStream());
			this.objInStream = new ObjectInputStream(sock.getInputStream());
		} catch (Exception ex) {
			this.disConnect();
			ex.printStackTrace();
		}
	}

	public void disConnect() {
		try {
			if (objOutStream != null)
				objOutStream.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			if (objInStream != null)
				objInStream.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			if (sock != null)
				sock.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void sendDisconnectMessage() {
		try {
			this.objOutStream.writeObject(DISCONNECT_MSG);
			this.objOutStream.flush();
			this.disConnect();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void sendData(EmbedMessage data) {
		try {
			objOutStream.writeObject(data);
			objOutStream.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public EmbedMessage recieveData() {
		EmbedMessage msg = null;
		try {
			msg = (EmbedMessage) objInStream.readObject();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (msg != null) {
			if (msg.getMsg().equals(DISCONNECT_MSG)) {
				disConnect();
			}
		}
		return msg;
	}
}