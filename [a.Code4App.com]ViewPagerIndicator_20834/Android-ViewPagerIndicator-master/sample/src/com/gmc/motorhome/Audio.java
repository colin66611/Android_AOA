package com.gmc.motorhome;

public class Audio {

	private Ipcl mIpcl = null;
	private byte mDVDVolume;
	
	private AudioSrc mAudioSrc;
	
	
	public enum AudioSrc {DVD, DTV};
	
	/* AUDIO API definition */
	/* request to PLC */
	private static final byte AUDIO_API_GET_CURR_SRC		= 0x01;
	private static final byte AUDIO_API_SET_CURR_SRC		= 0x02;	
	private static final byte AUDIO_API_GET_DVD_VOL		= 0x03;	
	private static final byte AUDIO_API_SET_DVD_VOL		= 0x04;
	/* response from AUDIO */
	private static final byte AUDIO_API_RESP_CURR_SRC	= 0x05;	
	private static final byte AUDIO_API_RESP_DVD_VOL	= 0x06;	
	
	private static final byte SRC_DVD = 0x00;
	private static final byte SRC_DTV = 0x01;
	
	public Audio(Ipcl ipcl)
	{
		mIpcl = ipcl;
		mAudioSrc = AudioSrc.DVD;
	}
	
	public AudioSrc getAudioSrc()
	{
		return mAudioSrc;
	}
	
	/**
	 * 
	 * @param targetSrc
	 */
	public void setAudioSrc(AudioSrc targetSrc)
	{
		mAudioSrc = targetSrc;
		
		switch (mAudioSrc)
		{
		case DVD:
			setRemoteSrc(SRC_DVD);
			break;
		case DTV:
			setRemoteSrc(SRC_DTV);			
			break;
		default:
			mAudioSrc = AudioSrc.DVD;
			setRemoteSrc(SRC_DVD);
			break;
		}
	}
	
	/**
	 * Prepare IPCL API call to switch source
	 * @param src
	 * @return
	 */
	private boolean setRemoteSrc(byte src)
	{
		boolean bRes = false;
		
		if ( (mIpcl != null) 
			&& (src >= SRC_DVD) 
			&& (src <= SRC_DTV))
		{
			byte[] payload = new byte[2];
			
			payload[0] = AUDIO_API_SET_CURR_SRC;
			payload[1] = src;
			
			mIpcl.sendMsg(mIpcl.SS_AUDIO, payload);
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
			
			mIpcl.sendMsg(mIpcl.SS_AUDIO, payload);
			bRes = true;
		}
		
		return bRes;
	}
	
	public boolean notificationCallback(byte[] msg)
	{
		boolean bRes = false;
		
		if (msg.length > 1)
		{
			switch (msg[0])
			{
			case AUDIO_API_RESP_CURR_SRC:
				if (msg.length == 2)
				{
					if ((msg[1] == SRC_DVD) && (mAudioSrc != AudioSrc.DVD)){
						mAudioSrc = AudioSrc.DVD;
						bRes = true;
					}
					
					if ((msg[1] == SRC_DTV) && (mAudioSrc != AudioSrc.DTV)){
						mAudioSrc = AudioSrc.DTV;
						bRes = true;
					}					

				}
				break;
				


			default:
				
				break;
			}
		}
		
		return bRes;
	}
	
}
