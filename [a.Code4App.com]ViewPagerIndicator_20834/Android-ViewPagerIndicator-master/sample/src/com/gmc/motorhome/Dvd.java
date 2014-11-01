package com.gmc.motorhome;

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

	public final byte CMD_ID_MODE = (0x50);     /*payload 1 uint8_t: DISC 0x1Cu, USB 0x1Du, SD 0x1Eu*/
	
	public final byte CMD_ID_EJECT = (byte)(0xf0);     /*payload none*/

	
	public final byte MSG_ID_DISC_STAT = (0x00);
	public final byte MSG_ID_PLAY_STAT = (0x01);
	public final byte MSG_ID_TRACK_INFO = (0x02);
	public final byte MSG_ID_TIME_INFO = (0x03);
	public final byte MSG_ID_FILENAME_ID3_INFO = (0x04);
	
	/*disc status*/
	public final byte DISC_NOT_IN = (0x00);
	public final byte DISC_EJECTING = (0x01);
	public final byte DISC_LOADING = (0x02);
	public final byte DISC_EJECTED = (0x03);
	public final byte DISC_LOADED = (0x04);
	public final byte DISC_STATE_ERROR = (0x05);
	
	/*play status*/
	public final byte PLAY_ST_IDLE = (0x00);
	public final byte PLAY_ST_READING = (0x01);
	public final byte PLAY_ST_PLAY = (0x02);
	public final byte PLAY_ST_FWD = (0x03);
	public final byte PLAY_ST_BWD = (0x04);
	public final byte PLAY_ST_PAUSE = (0x05);
	public final byte PLAY_ST_STOP = (0x0D);

	public final byte DVD_RPT_IDLE = (0x00);
	public final byte DVD_RPT_FILE = (0x10);
	public final byte DVD_RPT_DIR = (0x20);
	public final byte DVD_RPT_ALL = (0x30);
	public final byte DVD_RPT_DVD_DISC = (0x40);

	public final byte DVD_SHF_IDLE = (0x00);
	public final byte DVD_SHF_FOLDER = (0x04);
	public final byte DVD_SHF_ALL = (0x08);

	public final byte DISC_TYPE_UNKNOWN = (0x00);
	public final byte DISC_TYPE_CDDA = (0x10);
	public final byte DISC_TYPE_CDCA = (0x20);
	public final byte DISC_TYPE_VCD1 = (0x30);
	public final byte DISC_TYPE_VCD2 = (0x40);
	public final byte DISC_TYPE_SVCD = (0x50);
	public final byte DISC_TYPE_DVD = (0x60);

	public final byte TYPE_FILENAME = (0x01);
	public final byte TYPE_ID3_TITLE = (0x02);
	public final byte TYPE_ID3_ALBUM = (0x03);
	public final byte TYPE_ID3_ARTIST = (0x04);

	public final byte CODE_TYPE_8859 = (0x00);
	public final byte CODE_TYPE_UTF16LE = (0x01);
	public final byte CODE_TYPE_UTF16BE = (0x02);
	public final byte CODE_TYPE_UTF8 = (0x03);
	
	private byte mDISC_st = DISC_TYPE_UNKNOWN;
	
	private byte mPlay_st = PLAY_ST_IDLE;
	private byte mRPT_st = DVD_RPT_IDLE;
	private byte mSHF_st = DVD_SHF_IDLE;
	private byte mDISC_type = DISC_TYPE_UNKNOWN;
	
	private int mCur_folder_nr = 0;
	private int mCur_file_nr = 0;
	private int mTotal_folder_nr = 0;
	private int mTotal_file_nr = 0;
	
	private byte mHour = 0;
	private byte mMin = 0;
	private byte mSec = 0;
	private byte mT_Hour = 0;
	private byte mT_Min = 0;
	private byte mT_Sec = 0;
	
	private byte mFilename_code = CODE_TYPE_UTF16BE;
	private String mFilename = ""; 
	private byte mTitle_code = CODE_TYPE_UTF16BE;
	private String mTitle = ""; 
	private byte mAlbum_code = CODE_TYPE_UTF16BE;
	private String mAlbum = ""; 
	private byte mArtist_code = CODE_TYPE_UTF16BE;
	private String mArtist = ""; 
	
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
	
	public boolean updateDiscStat(byte disc_st)
	{
		boolean bRes = false;
		if(disc_st == mDISC_st)
		{
			
		}
		else
		{
			mDISC_st = disc_st;
			bRes = true;
		}
		return bRes;
	}
	
	public boolean updatePlayStat(byte play_st, byte rpt_st, byte shf_st, byte disc_type)
	{
		boolean bRes = false;
		if((play_st == mPlay_st)&&(rpt_st == mRPT_st)&&(shf_st == mSHF_st)&&(disc_type == mDISC_type))
		{
			
		}
		else
		{
			mPlay_st = play_st;
			mRPT_st = rpt_st;
			mSHF_st = shf_st;
			mDISC_type = disc_type;
			bRes = true;
		}
		return bRes;
	}
	
	public boolean updateTrackInfo(int cur_folder, int cur_file, int total_folder, int total_file)
	{
		boolean bRes = false;
		if((cur_folder == mCur_folder_nr)&&(cur_file == mCur_file_nr)&&(total_folder == mTotal_folder_nr)&&(total_file == mTotal_file_nr))
		{
			
		}
		else
		{
			mCur_folder_nr = cur_folder;
			mCur_file_nr = cur_file;
			mTotal_folder_nr = total_folder;
			mTotal_file_nr = total_file;
			bRes = true;
		}
		return bRes;
	}
	
	public boolean updateTimeInfo(byte hour, byte min, byte sec, byte t_hour, byte t_min, byte t_sec)
	{
		boolean bRes = false;
		if((hour == mHour)&&(min == mMin)&&(sec == mSec)&&(t_hour == mT_Hour)&&(t_min == mT_Min)&&(t_sec == mT_Sec))
		{
			
		}
		else
		{
			mHour = hour;
			mMin = min;
			mSec = sec;
			mT_Hour = t_hour;
			mT_Min = t_min;
			mT_Sec = t_sec;
			bRes = true;
		}
		return bRes;
	}
	
	public boolean updateTextInfo(byte type, byte code, byte[] text)
	{
		boolean bRes = false;
		switch(type)
		{
			case TYPE_FILENAME:
				mFilename_code = code;
				mFilename = text.toString();
				bRes = true;
				break;
				
			case TYPE_ID3_TITLE:
				mTitle_code = code;
				mTitle = text.toString();
				bRes = true;
				break;
				
			case TYPE_ID3_ALBUM:
				mAlbum_code = code;
				mAlbum = text.toString();
				bRes = true;
				break;
				
			case TYPE_ID3_ARTIST:
				mArtist_code = code;
				mArtist = text.toString();
				bRes = true;
				break;
				
			default:
				break;
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
	
	public boolean setDVDMode()
	{
		return setDVDState(CMD_ID_MODE, (byte)0x1C, (byte)0, (byte)0);
	}
	
	public boolean setUSBMode()
	{
		return setDVDState(CMD_ID_MODE, (byte)0x1D, (byte)0, (byte)0);
	}
	
	public boolean setSDMode()
	{
		return setDVDState(CMD_ID_MODE, (byte)0x1E, (byte)0, (byte)0);
	}
	
	public boolean notificationCallback(byte[] msg)
	{
		boolean bRes = false;
		if (msg.length > 1)
		{
			switch(msg[0])
			{
				case MSG_ID_DISC_STAT:
					if(msg.length >= 2)
					{
						bRes = updateDiscStat(msg[1]);
					}
					break;
					
				case MSG_ID_PLAY_STAT:
					if(msg.length >= 5)
					{
						bRes = updatePlayStat(msg[1],msg[2],msg[3],msg[4]);
					}
					break;
					
				case MSG_ID_TRACK_INFO:
					if(msg.length >= 9)
					{
						int cur_folder = ((int)msg[1])*256 + ((int)msg[2]);
						int cur_file = ((int)msg[3])*256 + ((int)msg[4]);
						int total_folder = ((int)msg[5])*256 + ((int)msg[6]);
						int total_file =  ((int)msg[7])*256 + ((int)msg[8]);
						
						bRes = updateTrackInfo(cur_folder,cur_file,total_folder,total_file);
					}
					break;
					
				case MSG_ID_TIME_INFO:
					if(msg.length >= 7)
					{
						bRes = updateTimeInfo(msg[1],msg[2],msg[3],msg[4],msg[5],msg[6]);
					}
					break;
					
				case MSG_ID_FILENAME_ID3_INFO:
					if(msg.length >= 5)
					{
						byte[] text = new byte[msg[3]];
						System.arraycopy(msg, 4, text, 0, (int)msg[3]);
						bRes = updateTextInfo(msg[1],msg[2],text);
					}
					break;
					
				default:
					break;
			}
		}
		return bRes;
	}

	public byte getDiscStatus()
	{
		return mDISC_st;
	}
	
	public byte getPlayStatus()
	{
		return mPlay_st;
	}
	
	public byte getRPTStatus()
	{
		return mRPT_st;
	}
	
	public byte getSHFStatus()
	{
		return mSHF_st;
	}
	
	public byte getDiscType()
	{
		return mDISC_type;
	}
	
	public int getCurFolderNr()
	{
		return mCur_folder_nr;
	}
	
	public int getCurFileNr()
	{
		return mCur_file_nr;
	}
	
	public int getTotalFolderNr()
	{
		return mTotal_folder_nr;
	}
	
	public int getTotalFileNr()
	{
		return mTotal_file_nr;
	}
	
	public byte getHour()
	{
		return mHour;
	}
	
	public byte getMin()
	{
		return mMin;
	}
	
	public byte getSec()
	{
		return mSec;
	}
	
	public byte getTotalHour()
	{
		return mT_Hour;
	}
	
	public byte getTotalMin()
	{
		return mT_Min;
	}
	
	public byte getTotalSec()
	{
		return mT_Sec;
	}
	
	public byte getTextType(byte text_type)
	{
		switch(text_type)
		{
			case TYPE_ID3_TITLE:
				return mTitle_code;
				
			case TYPE_ID3_ALBUM:
				return mAlbum_code;
				
			case TYPE_ID3_ARTIST:
				return mArtist_code;
				
			case TYPE_FILENAME:
			default:
				return mFilename_code;
		}
	}
	
	public String getText(byte text_type)
	{
		switch(text_type)
		{
			case TYPE_ID3_TITLE:
				return mTitle;
				
			case TYPE_ID3_ALBUM:
				return mAlbum;
				
			case TYPE_ID3_ARTIST:
				return mArtist;
				
			case TYPE_FILENAME:
			default:
				return mFilename;
		}
	}
}
