package com.example.plctest;

public class Dvd {
	private Ipcl mIpcl = null;
	
	public final byte CMD_ID_PLAY = (0x00);     /*payload none*/
	public final byte CMD_ID_PAUSE = (0x01);     /*payload none*/
	public final byte CMD_ID_FF = (0x02);     /*payload none*/
	public final byte CMD_ID_FB = (0x03);     /*payload none*/
	public final byte CMD_ID_POS_PLAY = (0x04);     /*payload 3 uint8_t: 1st: hour; 2nd: min; 3rd: sec*/

	public final byte CMD_ID_NEXT = (0x10);     /*payload none*/
	public final byte CMD_ID_PREV = (0x11);     /*payload none*/

	public final byte CMD_ID_RPT = (0x20);     /*payload none*/
	public final byte CMD_ID_MIX = (0x21);     /*payload none*/

	public final byte CMD_ID_NUM = (0x30);     /*payload 1 uint8_t: from 0~10, 10 means +10 per press*/
	public final byte CMD_ID_ARROW = (0x31);     /*payload 1 uint8_t: UP 0x0Du, LEFT 0x0Eu, RIGHT 0x0Fu, DOWN 0x10u, ENTER 0x11u*/

	public final byte CMD_ID_MENU = (0x40);     /*payload none*/
	public final byte CMD_ID_AUDIO = (0x41);     /*payload none*/
	public final byte CMD_ID_SUBTITLE = (0x42);     /*payload none*/

	public final byte CMD_ID_EJECT = (byte)(0xf0);     /*payload none*/

	public boolean setDVDState(byte cmd_id, byte payload_1, byte payload_2, byte payload_3)
	{
		boolean bRes = false;
		
		if (mIpcl != null)
		{
			byte[] payload = new byte[4];
			
			payload[0] = cmd_id;
			payload[1] = payload_1;
			payload[2] = payload_2;
			payload[3] = payload_3;
			
			mIpcl.sendMsg(mIpcl.SS_DVD, payload);
			bRes = true;
		}
		
		return bRes;
	}
	
public

	Dvd(Ipcl ipcl)
	{
		mIpcl = ipcl;
	}
	
	public boolean setDVDEject()
	{
		return setDVDState(CMD_ID_EJECT, (byte)0, (byte)0, (byte)0);
	}
	
	public boolean setDVDPlay()
	{
		return setDVDState(CMD_ID_PLAY, (byte)0, (byte)0, (byte)0);
	}
	
	public boolean setDVDPause()
	{
		return setDVDState(CMD_ID_PAUSE, (byte)0, (byte)0, (byte)0);
	}
	public boolean setDVDFF()
	{
		return setDVDState(CMD_ID_FF, (byte)0, (byte)0, (byte)0);
	}
	
	public boolean setDVDFB()
	{
		return setDVDState(CMD_ID_FB, (byte)0, (byte)0, (byte)0);
	}
	
	public boolean setDVDPosPlay(byte hour, byte min, byte sec)
	{
		return setDVDState(CMD_ID_POS_PLAY,hour,min,sec);
	}
	
	public boolean setDVDNext()
	{
		return setDVDState(CMD_ID_NEXT, (byte)0, (byte)0, (byte)0);
	}
	
	public boolean setDVDPrev()
	{
		return setDVDState(CMD_ID_PREV, (byte)0, (byte)0, (byte)0);
	}
	
	public boolean setDVDRpt()
	{
		return setDVDState(CMD_ID_RPT, (byte)0, (byte)0, (byte)0);
	}
	
	public boolean setDVDMix()
	{
		return setDVDState(CMD_ID_MIX, (byte)0, (byte)0, (byte)0);
	}
	
	public boolean setDVDNum(byte number)
	{
		return setDVDState(CMD_ID_NUM,number, (byte)0, (byte)0);
	}
	
	public boolean setDVDUp()
	{
		return setDVDState(CMD_ID_ARROW,(byte)0x0D, (byte)0, (byte)0);
	}
	
	public boolean setDVDLeft()
	{
		return setDVDState(CMD_ID_ARROW,(byte)0x0E, (byte)0, (byte)0);
	}
	
	public boolean setDVDRight()
	{
		return setDVDState(CMD_ID_ARROW,(byte)0x0F, (byte)0, (byte)0);
	}
	
	public boolean setDVDDn()
	{
		return setDVDState(CMD_ID_ARROW,(byte)0x10, (byte)0, (byte)0);
	}
	
	public boolean setDVDEnter()
	{
		return setDVDState(CMD_ID_ARROW,(byte)0x11, (byte)0, (byte)0);
	}
	
	public boolean setDVDMenu()
	{
		return setDVDState(CMD_ID_MENU, (byte)0, (byte)0, (byte)0);
	}
	
	public boolean setDVDAudio()
	{
		return setDVDState(CMD_ID_AUDIO, (byte)0, (byte)0, (byte)0);
	}
	
	public boolean setDVDSubTitle()
	{
		return setDVDState(CMD_ID_SUBTITLE, (byte)0, (byte)0, (byte)0);
	}
}
