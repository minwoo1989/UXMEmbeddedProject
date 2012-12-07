package com.uxm.embeddedproject;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.uxm.embeddedproject.data.EmbedMessage;
import com.uxm.embeddedproject.net.NetConnector;

public class ProjectManager extends AsyncTask<Void, Void, Void> {

	private NetConnector client;
	private Message message;
	private EmbedMessage embedMessage;
	private Handler handler;

	public ProjectManager(String host, int port, Handler handler,
			EmbedMessage embedMessage) {
		client = new NetConnector(host, port);
		this.handler = handler;
		this.embedMessage = embedMessage;
	}

	@Override
	protected void onPostExecute(Void result) {
		handler.sendMessage(message);
		super.onPostExecute(result);
	}

	@Override
	protected Void doInBackground(Void... params) {
		Log.i("Msg", embedMessage.toString());
		client.sendData(embedMessage);
		message = new android.os.Message();
		message.obj = ((EmbedMessage) client.recieveData()).getObj();
		return null;
	}

}
