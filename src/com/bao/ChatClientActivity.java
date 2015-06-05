package com.bao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChatClientActivity extends Activity {
	EditText showAll;
	EditText input;
	Button send;
	Button exitButton;
	String serverIp = "192.168.0.107";
//	String serverIp = "192.168.43.231";//192.168.0.107
	int port = 9998;
	Socket socket = null;
	OutputStream os;
	BufferedWriter bw;
	InputStream is;
	BufferedReader br;
	String Msg = "";
	String msg="";
	int temp = 0;
//	List clientMsgList;
	
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		showAll = (EditText) findViewById(R.id.showall);
		showAll.setCursorVisible(false);//��겻�ɼ�
		showAll.setFocusable(false);//�������ȡ����
		input = (EditText) findViewById(R.id.input);
		send = (Button) findViewById(R.id.send);
		exitButton = (Button) findViewById(R.id.exit);
		
		exitButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				msg = "end" +"\n";
				Thread sendThread = new Thread(new sendMessage());
				sendThread.start();
				input.setText("");
			}
		});

		
		//������Ϣ
		send.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//��showAll����ʾ�Լ���������Ϣ
				//sendMsg(input.getText().toString() + "\n");
				temp = 1;
				//showAll.setText(showAll.getText() + "\n" + input.getText().toString() + "\n");
				msg = (input.getText().toString() + "\n");
				Thread sendThread = new Thread(new sendMessage());
				sendThread.start();
				input.setText("");

			}
		});
		showAlert();
	}
	
	//��showAll����ʾ��������
	MyHandler handler = new MyHandler();
	class MyHandler extends Handler {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			showAll.append(Msg);    //��showAll�в���Msg
		}
	}

	//��¼Alert
	public void showAlert() {
		LinearLayout ll = new LinearLayout(ChatClientActivity.this);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		TextView tv = new TextView(ChatClientActivity.this);
		tv.setText("�������ǳ�: ");
		final EditText et = new EditText(ChatClientActivity.this);
		et.setWidth(300);
		ll.addView(tv);
		ll.addView(et);
		AlertDialog alertDialog = new AlertDialog.Builder(
				ChatClientActivity.this)
		// ���ñ���ʹ�ø�������ԴID,app_about
				.setTitle("��������")
				// ����һ������ʱҪ���öԻ���Ļ�����ť������
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						//sendMsg("start:" + et.getText().toString() + "\n");
						showAll.setText(showAll.getText() + "\n" + "start:" + et.getText().toString() + "\n");
						msg = ("start:" + et.getText().toString() + "\n");
						
						Thread sendThread = new Thread(new sendMessage());
						sendThread.start();

						
					}
					// ����һ���ṩ���˽����ߺ���ʾ������AlertDialog���ĶԻ�
				}).create();
		alertDialog.setView(ll);
		alertDialog.show();
	}

	class sendMessage implements Runnable {
		public void run() {
			try {
				if (socket == null) {
					socket = new Socket(serverIp, port);
					is = socket.getInputStream();
					os = socket.getOutputStream();
					bw = new BufferedWriter(new OutputStreamWriter(os));
					
				} else {
					Thread getThread = new Thread(new getMessage());
					getThread.start();
					/*while (temp == 1) {
						temp = 0;
						try {
							byte[] b = new byte[is.available()];//available��ȡ is �Ĵ�С
							if (is.read(b) != -1) {
								if (b.length != 0) {
									Msg = new String(b, "utf-8") + "\n";//����ȡ��������������Msg
									handler.sendEmptyMessage(0);
								}
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
					}*/
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			try {
				bw.write(msg);
				bw.flush();
			} catch (Exception e) {
				// TODO: handle exception
			}
			
		}
	}
	
	class getMessage implements Runnable {
		public void run() {
			while (true) {
				temp = 0;
				try {
					byte[] b = new byte[is.available()];//available��ȡ is �Ĵ�С
					if (is.read(b) != -1) {
						if (b.length != 0) {
							Msg = new String(b, "utf-8") + "\n";//����ȡ��������������Msg
							handler.sendEmptyMessage(0);
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}
	

}