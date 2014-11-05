//User must modify the below package with their package name
package com.gmc.motorhome; 
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;



/******************************FT311 GPIO interface class******************************************/
public class FT311UARTInterface extends Activity
{

	private int		mBaud;
	private byte	mDataBits;
	private byte	mStopBits;
	private byte	mParity;
	private byte	mFlowControl;
	
	private static final String ACTION_USB_PERMISSION =    "com.UARTLoopback.USB_PERMISSION";
	public UsbManager usbmanager;
	public UsbAccessory usbaccessory;
	public PendingIntent mPermissionIntent;
	public ParcelFileDescriptor filedescriptor = null;
	public FileInputStream inputstream = null;
	public FileOutputStream outputstream = null;
	public boolean mPermissionRequestPending = false;
	public read_thread readThread;

	private byte [] usbdata; 
	private byte []	writeusbdata;
	private byte  [] readBuffer; /*circular buffer*/
	private int readcount;
	private int totalBytes;
	private int writeIndex;
	private int readIndex;
	private byte status;
	final int  maxnumbytes = 65536;
	private int[] buffLock = new int[0];

	public boolean datareceived = false;
	public boolean READ_ENABLE = false;
	public boolean accessory_attached = false;

	public Context global_context;

	public static String ManufacturerString = "mManufacturer=FTDI";
	public static String ModelString1 = "mModel=FTDIUARTDemo";
	public static String ModelString2 = "mModel=Android Accessory FT312D";
	public static String VersionString = "mVersion=1.0";


	/*constructor*/
	public FT311UARTInterface(Context context, int baud, byte data_bits, byte stop_bits, byte parity, byte flow_control ){
		super();
		global_context = context;
		
		mBaud = baud;
		mDataBits = data_bits;
		mStopBits = stop_bits;
		mParity = parity;
		mFlowControl = flow_control;
		
		/*shall we start a thread here or what*/
		usbdata = new byte[1024]; 
		writeusbdata = new byte[256];
		/*128(make it 256, but looks like bytes should be enough)*/
		readBuffer = new byte [maxnumbytes];


		readIndex = 0;
		writeIndex = 0;
		/***********************USB handling******************************************/

		usbmanager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
		// Log.d("LED", "usbmanager" +usbmanager);
		mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		context.registerReceiver(mUsbReceiver, filter);

		inputstream = null;
		outputstream = null;
	}

	public int getBaud()
	{
		return mBaud;
	}
	
	public byte getDataBits()
	{
		return mDataBits;
	}
	
	public byte getStopBits()
	{
		return mStopBits;
	}
	
	public byte getParity()
	{
		return mParity;
	}
	
	public byte getFlowControl()
	{
		return mFlowControl;
	}
	
	
	public boolean Config(int baud, byte data_bits, byte stop_bits, byte parity, byte flow_control) {
		
		if ((baud > 0) 
				&& ((data_bits > 6) && (data_bits < 9))
				&& ((stop_bits > 0) && (stop_bits < 3))
				&& ((parity >= 0) && (parity < 3))
				&& (flow_control >= 0))
		{
			byte[] buff;
			
			mBaud			= baud;
			mDataBits		= data_bits;
			mStopBits		= stop_bits;
			mParity			= parity;
			mFlowControl	= flow_control;
			
			buff = new byte[8];
			
			/*prepare the baud rate buffer*/
			buff[0] = (byte)mBaud;
			buff[1] = (byte)(mBaud >> 8);
			buff[2] = (byte)(mBaud >> 16);
			buff[3] = (byte)(mBaud >> 24);

			/*data bits*/
			buff[4] = mDataBits;
			/*stop bits*/
			buff[5] = mStopBits;
			/*parity*/
			buff[6] = mParity;
			/*flow control*/
			buff[7] = mFlowControl;		
			
			return (SendPacket(buff));
		}
		else
		{
			return false;
		}
	}

	/*method to send on USB*/
	private boolean SendPacket(byte[] parcel)
	{	
		boolean bRet = false;
		try {
			if(outputstream != null){
				outputstream.write(parcel, 0, parcel.length);
				bRet = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return bRet;
	}
	
	/*write data*/
	public byte SendData(int numBytes, byte[] buffer) 
	{
		status = 0x00; /*success by default*/
		/*
		 * if num bytes are more than maximum limit
		 */
		if(numBytes < 1){
			/*return the status with the error in the command*/
			return status;
		}

		/*check for maximum limit*/
		if(numBytes > 256){
			numBytes = 256;
		}

		/*prepare the packet to be sent*/
		for(int count = 0;count<numBytes;count++)
		{	
			writeusbdata[count] = buffer[count];
		}

		if(numBytes != 64)
		{
			SendPacket(numBytes);
		}
		else
		{
			byte temp = writeusbdata[63];
			SendPacket(63);
			writeusbdata[0] = temp;
			SendPacket(1);
		}

		return status;
	}

	public int readDataBlocked1(byte[] buff, int iExpectNum)
	{
				
		if ((iExpectNum <= 0) || (buff == null))
		{
			iExpectNum = 0;
		}
		else
		{
			if (iExpectNum > buff.length)
			{
				iExpectNum = buff.length;
			}
			
			// Risk here should mutex protect in the future
			while (iExpectNum > totalBytes)
			{
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			

			//synchronized(buffLock)
			{
				for (int i = 0; i < iExpectNum; i++)
				{
					buff[i] = readBuffer[readIndex];
					readIndex++;
					readIndex %= maxnumbytes;
				}
				totalBytes = totalBytes - iExpectNum;
			}
		}
		
		
		return (iExpectNum);
	}

	public int readDataBlocked(byte[] buff, int iExpectNum)
	{
				
		if ((iExpectNum <= 0) 
			|| (buff == null)
			|| (inputstream == null))
		{
			iExpectNum = 0;
		}
		else
		{
			
			if (iExpectNum > maxnumbytes - 1024)
			{
				iExpectNum = maxnumbytes - 1024;
			}
			
			if (iExpectNum > buff.length)
			{
				iExpectNum = buff.length;
			}
			
			while (iExpectNum > totalBytes)
			{
				try
				{
					readcount = inputstream.read(usbdata, 0, 1024);
				}
				catch(IOException e)
				{/* AOA read failure, quit read with error */
					e.printStackTrace();
					iExpectNum = 0;
					break;
				}	

				if (readcount > 0)
				{
					//if (writeIndex + readcount < maxnumbytes)
					//{
					//	System.arraycopy(usbdata, 0, readBuffer, writeIndex, readcount);
					//}
					//else
					//{
					//	System.arraycopy(usbdata, 0, readBuffer, writeIndex, maxnumbytes - writeIndex);
					//	System.arraycopy(usbdata, maxnumbytes - writeIndex, readBuffer, 0, readcount - maxnumbytes + writeIndex);						
					//}
					
					//writeIndex = (writeIndex + readcount) % maxnumbytes;
					//totalBytes = totalBytes + readcount;
					
					for (int i = 0; i < readcount; i++)
					{
						readBuffer[writeIndex] = usbdata[i];
						writeIndex = (writeIndex + 1) % maxnumbytes;
					}
					totalBytes = totalBytes + readcount;
				}
				else
				{
					try 
					{
						Thread.sleep(50);
					}
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}									
				}
			}
			
			//if (readIndex + iExpectNum < maxnumbytes)
			//{
			//	System.arraycopy(readBuffer, readIndex, buff, 0, iExpectNum);
			//}
			//else
			//{
			//	System.arraycopy(readBuffer, readIndex, buff, 0, maxnumbytes - readIndex);
			//	System.arraycopy(readBuffer, 0, buff, maxnumbytes - readIndex,  iExpectNum - maxnumbytes + readIndex);	
			//}

			//readIndex = (readIndex + iExpectNum) % maxnumbytes;
			//totalBytes = totalBytes - iExpectNum;
			
			for (int i = 0; i < iExpectNum; i++)
			{
				buff[i] = readBuffer[readIndex];
				readIndex = (readIndex + 1) % maxnumbytes;
			}
			totalBytes = totalBytes - iExpectNum;
		}
		
		return (iExpectNum);
	}

	
	/*method to send on USB*/
	private void SendPacket(int numBytes)
	{	
		try {
			if(outputstream != null){
				outputstream.write(writeusbdata, 0,numBytes);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*resume accessory*/
	public int ResumeAccessory()
	{
		// Intent intent = getIntent();
		if (inputstream != null && outputstream != null) {
			return 1;
		}

		UsbAccessory[] accessories = usbmanager.getAccessoryList();
		if(accessories != null)
		{
			Toast.makeText(global_context, "Accessory Attached", Toast.LENGTH_SHORT).show();
		}
		else
		{
			// return 2 for accessory detached case
			//Log.e(">>@@","ResumeAccessory RETURN 2 (accessories == null)");
			accessory_attached = false;
			return 2;
		}

		UsbAccessory accessory = (accessories == null ? null : accessories[0]);
		if (accessory != null) {
			if( -1 == accessory.toString().indexOf(ManufacturerString))
			{
				Toast.makeText(global_context, "Manufacturer is not matched!", Toast.LENGTH_SHORT).show();
				return 1;
			}

			if( -1 == accessory.toString().indexOf(ModelString1) && -1 == accessory.toString().indexOf(ModelString2))
			{
				Toast.makeText(global_context, "Model is not matched!", Toast.LENGTH_SHORT).show();
				return 1;
			}

			if( -1 == accessory.toString().indexOf(VersionString))
			{
				Toast.makeText(global_context, "Version is not matched!", Toast.LENGTH_SHORT).show();
				return 1;
			}

			Toast.makeText(global_context, "Manufacturer, Model & Version are matched!", Toast.LENGTH_SHORT).show();
			accessory_attached = true;

			if (usbmanager.hasPermission(accessory)) {
				Toast.makeText(global_context, "USB permission obtained!", Toast.LENGTH_SHORT).show();				
				OpenAccessory(accessory);
			} 
			else
			{
				synchronized (mUsbReceiver) {
					if (!mPermissionRequestPending) {
						Toast.makeText(global_context, "Request USB Permission", Toast.LENGTH_SHORT).show();
						usbmanager.requestPermission(accessory,
								mPermissionIntent);
						mPermissionRequestPending = true;
					}
				}
			}
		} else {}

		return 0;
	}

	/*destroy accessory*/
	public void DestroyAccessory(boolean bConfiged){

		if(true == bConfiged){
			READ_ENABLE = false;  // set false condition for handler_thread to exit waiting data loop
			writeusbdata[0] = 0;  // send dummy data for instream.read going
			SendPacket(1);
		}
		else
		{
			Config(9600,(byte)8,(byte)1,(byte)0,(byte)0);  // send default setting data for config
			try{Thread.sleep(10);}
			catch(Exception e){}

			READ_ENABLE = false;  // set false condition for handler_thread to exit waiting data loop
			writeusbdata[0] = 0;  // send dummy data for instream.read going
			SendPacket(1);

		}

		try{Thread.sleep(10);}
		catch(Exception e){}			
		CloseAccessory();
	}

	/*********************helper routines*************************************************/		

	public void OpenAccessory(UsbAccessory accessory)
	{
		filedescriptor = usbmanager.openAccessory(accessory);
		if(filedescriptor != null){
			Toast.makeText(global_context, "usbmanager.openAccessory OK!", Toast.LENGTH_SHORT).show();
			
			usbaccessory = accessory;

			FileDescriptor fd = filedescriptor.getFileDescriptor();

			inputstream = new FileInputStream(fd);
			outputstream = new FileOutputStream(fd);
			
			Config(mBaud, mDataBits, mStopBits, mParity, mFlowControl);
			
			/*check if any of them are null*/
			if(inputstream == null || outputstream==null){
				Toast.makeText(global_context, "input or output stream is null!!", Toast.LENGTH_SHORT).show();
				return;
			}

			if(READ_ENABLE == false){
				READ_ENABLE = true;
				//readThread = new read_thread(inputstream);
				//readThread.start();
			}
		}
	}

	private void CloseAccessory()
	{
		try{
			if(filedescriptor != null)
				filedescriptor.close();

		}catch (IOException e){}

		try {
			if(inputstream != null)
				inputstream.close();
		} catch(IOException e){}

		try {
			if(outputstream != null)
				outputstream.close();

		}catch(IOException e){}
		/*FIXME, add the notfication also to close the application*/

		filedescriptor = null;
		inputstream = null;
		outputstream = null;

		System.exit(0);
	}


	/***********USB broadcast receiver*******************************************/
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() 
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) 
			{
				synchronized (this)
				{
					UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
					if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))
					{
						Toast.makeText(global_context, "Allow USB Permission", Toast.LENGTH_SHORT).show();
						OpenAccessory(accessory);
					} 
					else 
					{
						Toast.makeText(global_context, "Deny USB Permission", Toast.LENGTH_SHORT).show();
						Log.d("LED", "permission denied for accessory "+ accessory);

					}
					mPermissionRequestPending = false;
				}
			} 
			else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) 
			{
				DestroyAccessory(true);
				//CloseAccessory();
			}else
			{
				Log.d("LED", "....");
			}
		}	
	};

	/*usb input data handler*/
	private class read_thread  extends Thread 
	{
		FileInputStream instream;

		read_thread(FileInputStream stream ){
			instream = stream;
			this.setPriority(Thread.MAX_PRIORITY);
		}

		public void run()
		{		
			while(READ_ENABLE == true)
			{
					try 
					{
						Thread.sleep(50);
					}
					catch (InterruptedException e) {e.printStackTrace();}

			}
		}
	}
}