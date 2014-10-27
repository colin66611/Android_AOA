package com.gmc.motorhome;

public class MSystem {
	
	private String mVersion;
	private int mHealth;
	
	/* SYSTEM API definition */
	/* request to SYSTEM */
	private final byte SYS_API_GET_VERSION		= 0x01;
	private final byte SYS_API_GET_HEALTH		= 0x02;	

	/* response from SYSTEM */
	private final byte SYS_API_RESP_VERSION		= 0x11;	
	private final byte SYS_API_RESP_HEALTH		= 0x12;	
	
	
	public MSystem(){
		mVersion = null;
	}
	
	/**
	 * get control board version
	 * the version string is 10 characters like 2014102601
	 * @return
	 */
	public String getVersion(){
		if (mVersion == null){
			requestRemoteVersion();
		}
		return mVersion;
	}
	
	public int getHealth(){
		return mHealth;
	}
	
	/**
	 * Request Remote control board version
	 */
	private void requestRemoteVersion() {

		byte[] payload = new byte[1];
			
		payload[0] = SYS_API_GET_VERSION;
		
		Ipcl.getInstance().sendMsg(Ipcl.SS_SYSTEM, payload);	
	}
	
	/**
	 * 
	 * @param msg
	 * @return
	 * 	返回true通知UI有状态更新；返回false表示无状态更新
	 */
	
	public boolean notificationCallback(byte[] msg){
		boolean bRes = false;
		
		if (msg.length > 1)
		{
			switch (msg[0])
			{
			case SYS_API_RESP_VERSION:
				if (msg.length == 11)
				{
					if (mVersion == null)
					{
						char[] version = new char[10];
						for(int i = 0; i < 10; i++)
						{
							version[i] = (char)msg[i + 1];
						}
						
						mVersion = new String(version, 0, 10);
						bRes = true;
					}
				}
				break;
				
			case SYS_API_RESP_HEALTH:
				if (msg.length == 3)
				{
					mHealth = (int)msg[1];
					//TODO
					bRes = true;
				}
				break;

			default:
				
				break;
			}
		}
		
		return bRes;
	}
}
