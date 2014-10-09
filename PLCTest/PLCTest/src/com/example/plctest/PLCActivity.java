package com.example.plctest;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class PLCActivity extends Activity{
	
	private Button bnTvUpOn;
	private Button bnTvUpOff;
	private TextView TvUpStatus;
	private Button bnTvDnOn;
	private Button bnTvDnOff;
	private Button bnTvPwrOn;
	private Button bnTvPwrOff;
	private Button bnGlassPwrOn;
	private Button bnGlassPwrOff;
	
	private TextView logWindow;
	
	private PlcAgent plcAgent;

	public Ipcl mIpclServer;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);		
		setContentView(R.layout.main_layout);
		

		
		bnTvUpOn = (Button) findViewById(R.id.bnTvUpOn);
		bnTvUpOff = (Button) findViewById(R.id.bnTvUpOff);
		bnTvDnOn = (Button) findViewById(R.id.bnTvDnOn);
		bnTvDnOff = (Button) findViewById(R.id.bnTvDnOff);
		bnTvPwrOn = (Button) findViewById(R.id.bnTvPwrOn);
		bnTvPwrOff = (Button) findViewById(R.id.bnTvPwrOff);
		bnGlassPwrOn = (Button) findViewById(R.id.bnGlassPwrOn);
		bnGlassPwrOff = (Button) findViewById(R.id.bnGlassPwrOff);
		
		TvUpStatus = (TextView) findViewById(R.id.statusTvUp);
		logWindow = (TextView) findViewById(R.id.logWindow);
		
		plcAgent = new PlcAgent(); 
		
		
		mIpclServer = new Ipcl(this, handler);
		mIpclServer.start();
		
		bnTvUpOn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//plcAgent.setTvUpOn();
				mIpclServer.mPlc.setTVUp();
			}
		});
		
		bnTvUpOff.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mIpclServer.mPlc.setTVUp();
			}
		});
		
		bnTvDnOn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mIpclServer.mPlc.setTVDown();
			}
		});
		
		bnTvDnOff.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mIpclServer.mPlc.setTVDown();
			}
		});	

		bnTvPwrOn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mIpclServer.mPlc.setTVPwr(true);
			}
		});
		
		bnTvPwrOff.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mIpclServer.mPlc.setTVPwr(false);
			}
		});	
		
		bnGlassPwrOn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mIpclServer.mPlc.setGlassPwr(true);
			}
		});
		
		bnGlassPwrOff.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mIpclServer.mPlc.setGlassPwr(false);
			}
		});			
	}
	//@Override
	public void onHomePressed() {
		onBackPressed();
	}	

	public void onBackPressed() {
	    super.onBackPressed();
	}	
	
	@Override
	protected void onResume() {
		// Ideally should implement onResume() and onPause()
		// to take appropriate action when the activity looses focus
		super.onResume();	
		mIpclServer.resumeIpcl();

	}

	@Override
	protected void onPause() {
		// Ideally should implement onResume() and onPause()
		// to take appropriate action when the activity looses focus
		super.onPause();
	}

	@Override
	protected void onStop() {
		// Ideally should implement onResume() and onPause()
		// to take appropriate action when the activity looses focus
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		mIpclServer.destroyIpcl();
		super.onDestroy();
	}	
	
	public class PlcAgent{
		public final byte MSG_FRAME_HEADER = 0x02;
		public final byte MSG_FRAME_FOOTER = 0x03;
		public final int MAX_SWITCH_NO = 25;
		
		public void setTvUpOn(){
			setSwitch(0, true);			
		}
		
		public void setTvUpOff(){
			setSwitch(0, false);
		}
		
		public void setTvDnOn(){
			setSwitch(1, true);			
		}
		
		public void setTvDnOff(){
			setSwitch(1, false);	
		}		
		public void setTvPwrOn(){
			setSwitch(2, true);
		}
		
		public void setTvPwrOff(){
			setSwitch(2, false);
		}
		public void setGlassPwrOn(){
			setSwitch(3, true);
		}
		
		public void setGlassPwrOff(){
			setSwitch(3, false);
		}		
		private int setSwitch(int iSwitchNo, boolean bState){
			
			if (iSwitchNo >= 0 && iSwitchNo <= MAX_SWITCH_NO )
			{
				byte[] plcCmd;
				plcCmd = new byte[14];
				
				plcCmd[0] = (byte) 0x02;
				
				plcCmd[1] = (byte) '0';
				plcCmd[2] = (byte) '1';
				
				plcCmd[3] = (byte) '4';
				plcCmd[4] = (byte) '2';
				
				if (bState == true)
				{
					plcCmd[5] = (byte) '3';
				}
				else
				{
					plcCmd[5] = (byte) '4';					
				}
				
				plcCmd[6] = (byte) 'Y';
				
				String sSwitchNo = Integer.toString(iSwitchNo);
				
				
				plcCmd[7] = (byte) '0';
				plcCmd[8] = (byte) '0';
				plcCmd[9] = (byte) '0';
				plcCmd[10] = (byte) '0'; 
				
				if (sSwitchNo.length() >= 4)
				{
					plcCmd[7] = (byte)sSwitchNo.charAt(3);
				}
				if (sSwitchNo.length() >= 3)
				{
					plcCmd[8] = (byte)sSwitchNo.charAt(2);
				}
				if (sSwitchNo.length() >= 2)
				{
					plcCmd[9] = (byte)sSwitchNo.charAt(1);
				}
				if (sSwitchNo.length() >= 1)
				{
					plcCmd[10] = (byte)sSwitchNo.charAt(0);
				}	
				
				byte lrc = calculateLRC(plcCmd, 11);
				
				plcCmd[11] = bcdToAsc((byte)((lrc >> 4) & 0x0F));				
				plcCmd[12] = bcdToAsc((byte)(lrc & 0x0F));		
				
				plcCmd[13] = (byte) 0x03;
				
				mIpclServer.sendRawMsg(plcCmd);
				
				return 0;
			}
			else
			{
				return -1;
			}
		}
		
		private byte bcdToAsc(byte bcd)
		{
			if ((bcd >= 0x00) && (bcd <= 0x09))
			{
				return (byte)(bcd + (byte)'0');
			}
			else if ((bcd >= 0x0A) && (bcd <= 0x0F))
			{
				return (byte)(bcd + (byte)'A' - (byte)0x0A);
			}
			else
			{
				return (byte)0x00;
			}
			
		}

		private byte calculateLRC(byte[] buff, int byteNum)
		{
			int loop;
			byte res = 0;
			
			for (loop = 0; loop < byteNum; loop ++)
			{
				res = (byte)(buff[loop] + res);
			}
			
			return res;
		}
	}
	
		
	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			if (msg.what == 0x0001)
			{
				byte[] rxMsg = (byte[]) msg.obj;
				String recText = new String();
				
				for (int i = 0; i < msg.arg1 ; i++)
				{
					recText = recText + Integer.toHexString(rxMsg[i]) + " ";
				}
				
				logWindow.append(recText + "\n");
			}
			
			
		}
	};

}
