package com.gmc.motorhome;

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
	private final byte PLC_API_RESP_DEV_STATE		= 0x05;	
	private final byte PLC_API_RESP_BATCH_STATE		= 0x06;	
	
	
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
			return true;
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
			if ((mDevStatus & ((int)1 << (devName - 1))) != 0)
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
		setDevState(DEV_TV_UP, true);
		setDevState(DEV_TV_DOWN, false);
		
		return (setBatchState(mDevStatus));	
	}
	
	public boolean setTVDown()
	{
		setDevState(DEV_TV_UP, false);
		setDevState(DEV_TV_DOWN, true);
		
		return (setBatchState(mDevStatus));	
	}
	
	public boolean getTVUp()
	{
		return (getDevState(DEV_TV_UP));
	}
	
	public boolean getTVDown()
	{
		return (getDevState(DEV_TV_DOWN));
	}
	
	public boolean setTVPwr(boolean bOn)
	{
		boolean state = false;
		setDevState(DEV_TV_PWR, bOn);
		state = setBatchState(mDevStatus);
		return state;			
	}
	
	public boolean getTVPwr()
	{
		return (getDevState(DEV_TV_PWR));
	}
		
	public boolean setGlassPwr(boolean bOn)
	{
		setDevState(DEV_GLASS_PWR, bOn);
		return (setBatchState(mDevStatus));			
	}
	
	public boolean getGlassPwr()
	{
		return (getDevState(DEV_GLASS_PWR));
	}
	
	public boolean setGlassUp()
	{
		setDevState(DEV_GLASS_UP, true);
		setDevState(DEV_GLASS_DOWN, false);
		
		return (setBatchState(mDevStatus));	
	}
	
	public boolean setGlassDown()
	{
		setDevState(DEV_GLASS_UP, false);
		setDevState(DEV_GLASS_DOWN, true);
		
		return (setBatchState(mDevStatus));		
	}
	
	public boolean openSunroof()
	{
		setDevState(DEV_SUNROOF_OPEN, true);
		setDevState(DEV_SUNROOF_CLOSE, false);	
		
		return (setBatchState(mDevStatus));			
	}
	
	public boolean closeSunroof()
	{
		setDevState(DEV_SUNROOF_OPEN, false);		
		setDevState(DEV_SUNROOF_CLOSE, true);
		return (setBatchState(mDevStatus));		
	}
	
	public boolean getSunroofOpenState()
	{
		return getDevState(DEV_SUNROOF_OPEN);
	}
	
	public boolean setPCPwr(boolean bOn)
	{
		setDevState(DEV_PC_PWR, bOn);	
		return (setBatchState(mDevStatus));			
	}
	
	public boolean getPCPwrState()
	{
		return (getDevState(DEV_PC_PWR));
	}
	
	/* µÆ¹â¿ØÖÆ */
	public boolean setMoodLight_1(boolean bOn)
	{
		setDevState(DEV_MOODLIGHT_1_PWR, bOn);		
		return (setBatchState(mDevStatus));			
	}
	
	public boolean getModdLight_1()
	{
		return (getDevState(DEV_MOODLIGHT_1_PWR));		
	}
	
	public boolean setMoodLight_2(boolean bOn)
	{
		setDevState(DEV_MOODLIGHT_2_PWR, bOn);	
		return (setBatchState(mDevStatus));	
	}
	
	public boolean getModdLight_2()
	{
		return (getDevState(DEV_MOODLIGHT_2_PWR));		
	}
	
	public boolean setMoodLight_3(boolean bOn)
	{
		setDevState(DEV_MOODLIGHT_3_PWR, bOn);	
		return (setBatchState(mDevStatus));			
	}
	
	public boolean getModdLight_3()
	{
		return (getDevState(DEV_MOODLIGHT_3_PWR));		
	}
	
	public boolean setBarLight(boolean bOn)
	{
		setDevState(DEV_BARLIGHT_PWR, bOn);
		return (setBatchState(mDevStatus));	
	}
	
	public boolean getBarLight()
	{
		return (getDevState(DEV_BARLIGHT_PWR));
	}
	
	public boolean setTopLight(boolean bOn)
	{
		setDevState(DEV_TOPLIGHT_PWR, bOn);
		return (setBatchState(mDevStatus));			
	}
	
	public boolean getTopLight()
	{
		return (getDevState(DEV_TOPLIGHT_PWR));
	}
	
	public boolean setReadLight_1(boolean bOn)
	{
		setDevState(DEV_READLIGHT_1_PWR, bOn);
		return (setBatchState(mDevStatus));			
	}
	
	public boolean getReadLight_1()
	{
		return (getDevState(DEV_READLIGHT_1_PWR));		
	}
	
	public boolean setReadLight_2(boolean bOn)
	{
		setDevState(DEV_READLIGHT_2_PWR, bOn);
		return (setBatchState(mDevStatus));	
	}
	
	public boolean getReadLight_2()
	{
		return (getDevState(DEV_READLIGHT_2_PWR));		
	}
	
	public boolean setReadLight_3(boolean bOn)
	{
		setDevState(DEV_READLIGHT_3_PWR, bOn);
		return (setBatchState(mDevStatus));			
	}
	
	public boolean getReadLight_3()
	{
		return (getDevState(DEV_READLIGHT_3_PWR));		
	}
	
	public boolean setReadLight_4(boolean bOn)
	{
		setDevState(DEV_READLIGHT_4_PWR, bOn);	
		return (setBatchState(mDevStatus));			
	}
	
	public boolean getReadLight_4()
	{
		return (getDevState(DEV_READLIGHT_4_PWR));		
	}
	
	/* ´°Á±¿ØÖÆ*/
	public boolean openShade_1()
	{
		setDevState(DEV_SHADE_1_OPEN, true);
		setDevState(DEV_SHADE_1_CLOSE, false);
		
		return (setBatchState(mDevStatus));			
	}
	
	public boolean getShadeOpenState_1()
	{
		return (getDevState(DEV_SHADE_1_OPEN));
	}
	
	public boolean closeShade_1()
	{
		setDevState(DEV_SHADE_1_OPEN, false);		
		setDevState(DEV_SHADE_1_CLOSE, true);
		
		return (setBatchState(mDevStatus));	
	}
	
	public boolean getShadeCloseState_1()
	{
		return (getDevState(DEV_SHADE_1_OPEN));
	}
	
	public boolean openShade_2()
	{
		setDevState(DEV_SHADE_2_OPEN, true);
		setDevState(DEV_SHADE_2_CLOSE, false);
		
		return (setBatchState(mDevStatus));			
	}
	
	public boolean getShadeOpenState_2()
	{
		return (getDevState(DEV_SHADE_2_OPEN));
	}
	
	public boolean closeShade_2()
	{
		setDevState(DEV_SHADE_2_OPEN, false);		
		setDevState(DEV_SHADE_2_CLOSE, true);
		
		return (setBatchState(mDevStatus));	
	}
	
	public boolean getShadeCloseState_2()
	{
		return (getDevState(DEV_SHADE_2_OPEN));
	}
	
	public boolean openShade_3()
	{
		setDevState(DEV_SHADE_3_OPEN, true);
		setDevState(DEV_SHADE_3_CLOSE, false);
		
		return (setBatchState(mDevStatus));			
	}
	
	public boolean getShadeOpenState_3()
	{
		return (getDevState(DEV_SHADE_3_OPEN));
	}
	
	public boolean closeShade_3()
	{
		setDevState(DEV_SHADE_3_OPEN, false);		
		setDevState(DEV_SHADE_3_CLOSE, true);
		
		return (setBatchState(mDevStatus));	
	}
	
	public boolean getShadeCloseState_3()
	{
		return (getDevState(DEV_SHADE_3_OPEN));
	}
	
	public boolean openShade_4()
	{
		setDevState(DEV_SHADE_4_OPEN, true);
		setDevState(DEV_SHADE_4_CLOSE, false);
		
		return (setBatchState(mDevStatus));			
	}
	
	public boolean getShadeOpenState_4()
	{
		return (getDevState(DEV_SHADE_4_OPEN));
	}
	
	public boolean closeShade_4()
	{
		setDevState(DEV_SHADE_4_OPEN, false);		
		setDevState(DEV_SHADE_4_CLOSE, true);
		
		return (setBatchState(mDevStatus));	
	}
	
	public boolean getShadeCloseState_4()
	{
		return (getDevState(DEV_SHADE_4_OPEN));
	}
	

	
	public boolean notificationCallback(byte[] msg)
	{
		boolean bRes = false;
		
		if (msg.length > 1)
		{
			switch (msg[0])
			{
			case PLC_API_RESP_BATCH_STATE:
				if (msg.length == 5)
				{
					int devState;
					devState = (int)(msg[1] & 0xFF) << 24;
					devState |= (int)(msg[2] & 0xFF) << 16;
					devState |= (int)(msg[3] & 0xFF) << 8;
					devState |= (int)(msg[4] & 0xFF);
					
					if (devState != mDevStatus)
					{
						mDevStatus = devState;
						bRes = true;
					}
				}
				bRes = true;
				break;
				
			case PLC_API_RESP_DEV_STATE:
				if (msg.length == 3)
				{
					if ((msg[2] == 0x01) && (getDevState(msg[1]) == false))
					{
						setDevState(msg[1], true);
						bRes = true;
					}
					else if ((msg[2] == 0x00) && (getDevState(msg[1]) == true))
					{
						setDevState(msg[1], false);
						bRes = true;
					}
					else
					{
						
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
