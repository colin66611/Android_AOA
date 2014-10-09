//User must modify the below package with their package name
package com.example.plctest; 
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

	/*read data*/
	public byte ReadData(int numBytes,byte[] buffer, int [] actualNumBytes)
	{
		status = 0x00; /*success by default*/

		/*should be at least one byte to read*/
		if((numBytes < 1) || (totalBytes == 0)){
			actualNumBytes[0] = 0;
			status = 0x01;
			return status;
		}

		/*check for max limit*/
		if(numBytes > totalBytes)
			numBytes = totalBytes;

		/*update the number of bytes available*/
		totalBytes -= numBytes;

		actualNumBytes[0] = numBytes;	

		/*copy to the user buffer*/	
		for(int count = 0; count<numBytes;count++)
		{
			buffer[count] = readBuffer[readIndex];
			readIndex++;
			/*shouldnt read more than what is there in the buffer,
			 * 	so no need to check the overflow
			 */
			readIndex %= maxnumbytes;
		}
		return status;
	}

	public int readDataBlocked1(byte[] buff, int iExpectNum)
	{
		int iReadNum = 0;
				
		if (iExpectNum <= 0)
		{
			iReadNum = 0;
		}
		else
		{
			if (iExpectNum > buff.length)
			{
				iExpectNum = buff.length;
			}
			
			while (iExpectNum > 0)
			{
				if (iExpectNum <= totalBytes)
				{
					//synchronized(buffLock)
					{
						for (int i = 0; i < iExpectNum; i++)
						{
							buff[iReadNum + i] = readBuffer[readIndex];
							readIndex++;
							readIndex %= maxnumbytes;
						}
						iReadNum = iReadNum + iExpectNum;
						totalBytes = totalBytes - iExpectNum;
						iExpectNum = 0;
					}
				}
				else
				{
					//synchronized(buffLock)
					{
						for (int i = 0; i < totalBytes; i++)
						{
							buff[iReadNum + i] = readBuffer[readIndex];
							readIndex++;
							readIndex %= maxnumbytes;
						}
						iReadNum = iReadNum + totalBytes;
						totalBytes = 0;
						iExpectNum = iExpectNum - totalBytes;
					}
					
					try
					{
						Thread.sleep(100);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		
		
		return (iReadNum);
	}
	
	/**
	 * 
	 * @param iExpectNum
	 * @param buff
	 * @return error < 0; Actual read byte number; 
	 * 
	 */
	public int readRataBlocked(byte[] buff, int iExpectNum)
	{
		int iRes = 0;
		int iActualNum = 0;
		int iReadNum = 0;
		byte[] tempBuff = new byte[iExpectNum];
		
		
		/* judge if AOA is established */
		if(inputstream != null)
		{
			while (iReadNum < iExpectNum)
			{
				try
				{
					iActualNum = inputstream.read(tempBuff, 0, iExpectNum - iReadNum);
					
					for (int i = 0; i < iActualNum; i++)
					{
						buff[iReadNum + i] = tempBuff[i];
					}
					iReadNum = iReadNum + iActualNum;
					if (iActualNum == 0)
					{/* no data, sleep for a while */
						try
						{
							Thread.sleep(50);
						}
						catch (InterruptedException e)
						{/* interrupted, continue */
							e.printStackTrace();
						}
					}
				}
				catch(IOException e)
				{/* AOA read failure, quit read with error */
					e.printStackTrace();
					iRes = -1;
					break;
				}				
			}
		}
		else
		{
			iRes = -2;
		}
		
		if (iRes < 0)
		{
			return (iRes);
		}
		else
		{
			return (iExpectNum);
		}
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
			usbaccessory = accessory;

			FileDescriptor fd = filedescriptor.getFileDescriptor();

			inputstream = new FileInputStream(fd);
			outputstream = new FileOutputStream(fd);
			
			Config(mBaud, mDataBits, mStopBits, mParity, mFlowControl);
			
			/*check if any of them are null*/
			if(inputstream == null || outputstream==null){
				return;
			}

			if(READ_ENABLE == false){
				READ_ENABLE = true;
				readThread = new read_thread(inputstream);
				readThread.start();
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
				while(totalBytes > (maxnumbytes - 1024))
				{
					try 
					{
						Thread.sleep(50);
					}
					catch (InterruptedException e) {e.printStackTrace();}
				}

				try
				{
					if(instream != null)
					{	
						//synchronized(buffLock)
						{
							readcount = instream.read(usbdata,0, 1024);
							if(readcount > 0)
							{
								for(int count = 0;count<readcount;count++)
								{					    			
									readBuffer[writeIndex] = usbdata[count];
									writeIndex++;
									writeIndex %= maxnumbytes;
								}
	
								if(writeIndex >= readIndex)
									totalBytes = writeIndex-readIndex;
								else
									totalBytes = (maxnumbytes-readIndex)+writeIndex;
	
	//					    		Log.e(">>@@","totalBytes:"+totalBytes);
							}
						}
					}
				}
				catch (IOException e){e.printStackTrace();}
			}
		}
	}
}