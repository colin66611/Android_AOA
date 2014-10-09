package com.example.plctest;

public class Plc {

	private int mDevStatus;
	private Ipcl mIpcl = null;
	
	/* PLC API definition */
	/* request to PLC */
	private final byte PLC_API_GET_DEV_STATE		= 0x01;
	private final byte PLC_API_GET_BATCH_STATE		= 0x02;	
	private final byte PLC_API_SET_DEV_STATE		= 0x03;	
	private final byte PLC_API_SET_BATCH_STATE		= 0x04;
	/* response from PLC */
	private final byte PLC_API_RESP_DEV_STATE	= 0x05;	
	private final byte PLC_API_RESP_BATCH_STATE	= 0x06;	
	
	
	/* Device name definition */
	public final byte DEV_NAME_MIN			= 0;
	public final byte DEV_TV_UP			= 1;
	public final byte DEV_TV_DOWN			= 2;
	public final byte DEV_TV_PWR			= 3;
	public final byte DEV_GLASS_PWR		= 4;
	public final byte DEV_GLASS_UP			= 5;
	public final byte DEV_GLASS_DOWN		= 6;
	public final byte DEV_SUNROOF_OPEN		= 7;
	public final byte DEV_SUNROOF_CLOSE	= 8;
	public final byte DEV_PC_PWR			= 9;
	public final byte DEV_MOODLIGHT_1_PWR	= 10;
	public final byte DEV_MOODLIGHT_2_PWR	= 11;
	public final byte DEV_MOODLIGHT_3_PWR	= 12;
	public final byte DEV_BARLIGHT_PWR		= 13;
	public final byte DEV_TOPLIGHT_PWR		= 14;
	public final byte DEV_READLIGHT_1_PWR	= 15;
	public final byte DEV_READLIGHT_2_PWR	= 16;
	public final byte DEV_READLIGHT_3_PWR	= 17;
	public final byte DEV_READLIGHT_4_PWR	= 18;
	public final byte DEV_SHADE_1_OPEN		= 19;
	public final byte DEV_SHADE_1_CLOSE	= 20;
	public final byte DEV_SHADE_2_OPEN		= 21;
	public final byte DEV_SHADE_2_CLOSE	= 22;
	public final byte DEV_SHADE_3_OPEN		= 23;
	public final byte DEV_SHADE_3_CLOSE	= 24;
	public final byte DEV_SHADE_4_OPEN		= 25;
	public final byte DEV_SHADE_4_CLOSE	= 26;
	public final byte DEV_NAME_MAX			= 27;
	


	public boolean setDevState(byte devName, boolean devState)
	{
		if ((devName > DEV_NAME_MIN) && (devName < DEV_NAME_MAX))
		{
			int bitMask = 1;
			
			bitMask = bitMask << (devName - 1);
			if (devState == true)
			{
				mDevStatus = mDevStatus | bitMask;
			}
			else
			{
				mDevStatus = mDevStatus & (~bitMask);	
			}
			return (setBatchState(mDevStatus));	
		}
		else
		{
			return false;
		}
	}
	
	public boolean getDevState(byte devName)
	{
		boolean bRes = false;
		
		if ((devName >= DEV_TV_UP) && (devName <= DEV_SUNROOF_CLOSE))
		{
			if ((mDevStatus & ((int)1 << devName)) != 0)
			{
				bRes = true;
			}
		}
		
		return bRes;
	}
	
	public boolean setBatchState(int batch_state)
	{
		boolean bRes = false;
		
		if (mIpcl != null)
		{
			byte[] payload = new byte[5];
			
			payload[0] = PLC_API_SET_BATCH_STATE;
			payload[1] = (byte)((batch_state >> 24) & 0xFF);
			payload[2] = (byte)((batch_state >> 16) & 0xFF);
			payload[3] = (byte)((batch_state >> 8) & 0xFF);
			payload[4] = (byte)(batch_state & 0xFF);
			
			mIpcl.sendMsg(mIpcl.SS_PLC, payload);
			bRes = true;
		}
		
		return bRes;
	}
	
	public int getBatchState()
	{
		return mDevStatus;
	}
	
	public boolean setPlcDev(byte dev_name, boolean bEn)
	{
		boolean bRes = false;
		
		if ((dev_name > DEV_NAME_MIN) && (dev_name < DEV_NAME_MAX))
		{
			if (bEn == true)
			{
				mDevStatus |= (int)1 << dev_name;
			}
			else
			{
				mDevStatus &= ~((int)1 << dev_name);
			}
			setBatchState(mDevStatus);
		}
		
		return bRes;
	}
	
	public boolean getPlcDev(byte dev_name)
	{
		boolean bRes = false;
		
		if ((dev_name > DEV_NAME_MIN) && (dev_name < DEV_NAME_MAX))
		{
			if ((mDevStatus & ((int)1 << dev_name)) != 0)
			{
				bRes = true;
			}
		}
		
		return bRes;
	}
	

public

	Plc(Ipcl ipcl)
	{
		mIpcl = ipcl;
		mDevStatus = 0x00000000;
	}

	
	/* TV up */
	public boolean setTVUp()
	{
		return (setDevState(DEV_TV_UP, true));	
	}
	
	public boolean setTVDown()
	{
		return (setDevState(DEV_TV_DOWN, true));
	}
	
	public boolean setTVPwr(boolean bOn)
	{
		return (setDevState(DEV_TV_PWR, bOn));		
	}
	
	public boolean getTVPwr()
	{
		return (getDevState(DEV_TV_PWR));
	}
		
	public boolean setGlassPwr(boolean bOn)
	{
		return (setDevState(DEV_GLASS_PWR, bOn));				
	}
	
	public boolean getGlassPwr()
	{
		return (getDevState(DEV_GLASS_PWR));
	}
	
	public boolean setGlassUp()
	{
		return (setDevState(DEV_GLASS_UP, true));						
	}
	
	public boolean setGlassDown()
	{
		return (setDevState(DEV_GLASS_DOWN, true));							
	}
	
	public boolean openSunroof()
	{
		return (setDevState(DEV_SUNROOF_OPEN, true));							
	}
	
	public boolean closeSunroof()
	{
		return (setDevState(DEV_SUNROOF_CLOSE, true));									
	}
	
	public boolean setPCPwr(boolean bOn)
	{
		return (setDevState(DEV_PC_PWR, bOn));		
	}
	
	/* �ƹ���� */
	public boolean setMoodLight_1(boolean bOn)
	{
		return (setDevState(DEV_MOODLIGHT_1_PWR, bOn));		
		
	}
	
	public boolean getModdLight_1()
	{
		return (getDevState(DEV_MOODLIGHT_1_PWR));
	}
	
	public boolean setMoodLight_2(boolean bOn)
	{
		return (setDevState(DEV_MOODLIGHT_2_PWR, bOn));		
	}
	
	public boolean getModdLight_2()
	{
		return (getDevState(DEV_MOODLIGHT_2_PWR));
	}
	
	public boolean setMoodLight_3(boolean bOn)
	{
		return (setDevState(DEV_MOODLIGHT_3_PWR, bOn));		
	}
	
	public boolean getModdLight_3()
	{
		return (getDevState(DEV_MOODLIGHT_3_PWR));
	}
	
	public boolean setBarLight(boolean bOn)
	{
		return (setDevState(DEV_BARLIGHT_PWR, bOn));		
	}
	
	public boolean getBarLight()
	{
		return (getDevState(DEV_BARLIGHT_PWR));
	}
	
	public boolean setTopLight(boolean bOn)
	{
		return (setDevState(DEV_TOPLIGHT_PWR, bOn));		
	}
	
	public boolean getTopLight()
	{
		return (getDevState(DEV_TOPLIGHT_PWR));
	}
	
	public boolean setReadLight_1(boolean bOn)
	{
		return (setDevState(DEV_READLIGHT_1_PWR, bOn));		
	}
	
	public boolean getReadLight_1(boolean bOn)
	{
		return (getDevState(DEV_READLIGHT_1_PWR));		
	}
	
	public boolean setReadLight_2(boolean bOn)
	{
		return (setDevState(DEV_READLIGHT_2_PWR, bOn));		
	}
	
	public boolean getReadLight_2(boolean bOn)
	{
		return (getDevState(DEV_READLIGHT_2_PWR));		
	}
	
	public boolean setReadLight_3(boolean bOn)
	{
		return (setDevState(DEV_READLIGHT_3_PWR, bOn));		
	}
	
	public boolean getReadLight_3(boolean bOn)
	{
		return (getDevState(DEV_READLIGHT_3_PWR));		
	}
	
	public boolean setReadLight_4(boolean bOn)
	{
		return (setDevState(DEV_READLIGHT_4_PWR, bOn));		
	}
	
	public boolean getReadLight_4(boolean bOn)
	{
		return (getDevState(DEV_READLIGHT_4_PWR));		
	}
	
	/* ��������*/
	public boolean openShade_1()
	{
		return (setDevState(DEV_SHADE_1_OPEN, true));	
	}
	
	public boolean closeShade_1()
	{
		return (setDevState(DEV_SHADE_1_CLOSE, true));	
	}
	
	public boolean openShade_2()
	{
		return (setDevState(DEV_SHADE_2_OPEN, true));	
	}
	
	public boolean closeShade_2()
	{
		return (setDevState(DEV_SHADE_2_CLOSE, true));					
	}
	
	public boolean openShade_3()
	{
		return (setDevState(DEV_SHADE_3_OPEN, true));		
	}
	
	public boolean closeShade_3()
	{
		return (setDevState(DEV_SHADE_3_CLOSE, true));			
	}
	public boolean openShade_4()
	{
		return (setDevState(DEV_SHADE_4_OPEN, true));		
	}
	
	public boolean closeShade_4()
	{
		return (setDevState(DEV_SHADE_4_CLOSE, true));	
	}
	

	
	public void notificationCallback(byte[] msg)
	{
		if (msg.length > 1)
		{
			switch (msg[0])
			{
			case PLC_API_RESP_BATCH_STATE:
				int devState;
				devState = (int)msg[1] << 24;
				devState &= (int)msg[2] << 16;
				devState &= (int)msg[3] << 8;
				devState &= (int)msg[4];
				
				mDevStatus = devState;
				break;
				
			default:
				
				break;
			}
		}
	}
}
