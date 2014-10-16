package com.example.plctest;

public class Audio {

	private Ipcl mIpcl = null;
	private byte mCurrSrc;
	private byte mDVDVolume;
	
	public final byte SRC_DVD = 0x00;
	public final byte SRC_DTV = 0x01;	
	
	/* AUDIO API definition */
	/* request to PLC */
	private final byte AUDIO_API_GET_CURR_SRC		= 0x01;
	private final byte AUDIO_API_SET_CURR_SRC		= 0x02;	
	private final byte AUDIO_API_GET_DVD_VOL		= 0x03;	
	private final byte AUDIO_API_SET_DVD_VOL		= 0x04;
	/* response from AUDIO */
	private final byte AUDIO_API_RESP_CURR_SRC	= 0x05;	
	private final byte AUDIO_API_RESP_DVD_VOL	= 0x06;	


	
	public Audio()
	{
		mIpcl = Ipcl.getInstance();
	}
	
	public byte getCurrSrc()
	{
		return mCurrSrc;
	}
	
	public boolean srcIsDTV()
	{
		boolean bRes = false;
		
		if(SRC_DTV == getCurrSrc())
		{
			bRes = true;
		}
		else
		{
			bRes = false;
		}
		
		return bRes;
			
	}
	
	public boolean srcIsDVD()
	{
		boolean bRes = false;
		
		if(SRC_DVD == getCurrSrc())
		{
			bRes = true;
		}
		else
		{
			bRes = false;
		}
		
		return bRes;
			
	}
	
	public boolean setSrcToDTV()
	{
		return setCurrSrc(SRC_DTV);
	}
	
	public boolean setSrcToDVD()
	{
		return setCurrSrc(SRC_DVD);
	}
	
	public boolean setCurrSrc(byte src)
	{
		boolean bRes = false;
		
		if ( (mIpcl != null) 
			&& (src >= SRC_DVD) 
			&& (src <= SRC_DTV))
		{
			byte[] payload = new byte[2];
			
			payload[0] = AUDIO_API_SET_CURR_SRC;
			payload[1] = src;
			
			mIpcl.sendMsg(mIpcl.SS_DVD, payload);
			bRes = true;
		}
		
		return bRes;
	}
	
	public byte getDvdVolume()
	{
		return mDVDVolume;
	}
	
	public boolean setDvdVolume(byte volume)
	{
		boolean bRes = false;
		
		if ( (mIpcl != null) 
			&& (volume < 40))
		{
			byte[] payload = new byte[2];
			
			payload[0] = AUDIO_API_SET_DVD_VOL;
			payload[1] = volume;
			
			mIpcl.sendMsg(mIpcl.SS_DVD, payload);
			bRes = true;
		}
		
		return bRes;
	}
	
}
