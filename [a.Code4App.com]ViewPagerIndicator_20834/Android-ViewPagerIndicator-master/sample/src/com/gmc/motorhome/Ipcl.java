package com.gmc.motorhome;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.gmc.motorhome.FT311UARTInterface;

public class Ipcl extends Thread{
	

	private Handler mTargetHandler;
	private Context mContex;
	private FT311UARTInterface mUartAgent;
	private static Ipcl mIpclServer;	
	
	/* Definition of IPCL protocol */
	private final byte IPCL_MSG_HEADER 			= 0x02;
	private final byte IPCL_MSG_FOOTER			= 0x03;
	private final int IPCL_RX_POOL_SIZE			= 1000;
	
	
	private class IPCL_RX_MSG_t
	{
		public int head;
		public int tail;
		public byte[] msg;
		
		IPCL_RX_MSG_t(int pool_size)
		{
			msg = new byte[pool_size];
			head = 0;
			tail = 0;
		}
	};
	
	
	private IPCL_RX_MSG_t m_rxPool;
	
	public Plc mPlc;
	public Dvd mDvd;
	public Audio mAudio;
	public MSystem mSystem;
	
	/* Define Subsystem group channel */
	public static final byte SS_SYSTEM = 0x11;	
	public static final byte SS_PLC = 0x21;
	public static final byte SS_DVD = 0x22;
	public static final byte SS_AUDIO = 0x23;


	public Ipcl(Context ctx, Handler h)
	{
		m_rxPool = new IPCL_RX_MSG_t(IPCL_RX_POOL_SIZE);
		
		mTargetHandler = h;
		mContex = ctx;
		mUartAgent = new FT311UARTInterface(ctx, 115200, (byte)8, (byte)1, (byte)0, (byte)(0));
		
		/* Subsystem proxy */
		mPlc = new Plc(this);
		mDvd = new Dvd(this);
		mAudio = new Audio(this);
		mSystem = new MSystem();
		
		
		mIpclServer = this;
	}

	

	
	public static Ipcl getInstance()
	{
		return mIpclServer;
	}

	/**
	 * 
	 * @param SS_chan
	 * @param payload
	 * @return
	 */
	public int sendMsg(byte SS_chan, byte[] payload)
	{
		int		iMsgLen	= payload.length;/*payload.length;*/
		int		iFramelen = iMsgLen + 9;
		byte[]	ipclFrame = new byte[iFramelen];
		int		iIndex = 0;
		byte	EncryCode = (byte)(Math.random() * 256);
		
		
		/* Frame Header */
		ipclFrame[iIndex++] = IPCL_MSG_HEADER;
		
		/* Frame Length */
		ipclFrame[iIndex++] = (byte) ((iFramelen >> 8) & 0xFF);
		ipclFrame[iIndex++] = (byte) (iFramelen & 0xFF);		
		
		/* Encrypt Code */
		ipclFrame[iIndex++] = EncryCode;
		
		/* Subsystem channel */
		ipclFrame[iIndex++] = SS_chan;
		
		/* Message Length */
		ipclFrame[iIndex++] = (byte) ((iMsgLen >> 8) & 0xFF);
		ipclFrame[iIndex++] = (byte) (iMsgLen & 0xFF);
		
		/* payload */		
		for (int i = 0; i < iMsgLen; i++)
		{
			ipclFrame[iIndex++] = payload[i];
		}
		
		/* Checksum */
		byte checksum = calChecksum(ipclFrame, 3, iFramelen - 3);
		
		ipclFrame[iIndex++] = checksum;
		
		/* Frame end */
		ipclFrame[iIndex] = IPCL_MSG_FOOTER;
		
		//mUartAgent.SendData(iMsgLen, payload);
		mUartAgent.SendData(iFramelen, ipclFrame);
		
		return 1;
	}
	
	/**
	 * 
	 * @param SS
	 * @param payload
	 * @param expectLen
	 * @return
	 */
	public int sendMsg(int SS, byte[] payload, int expectLen)
	{
		byte[] msgBuff = new byte[expectLen];
		
		mUartAgent.SendData(expectLen, payload);
		
		return 1;
	}
	
	public int sendRawMsg(byte[] payload)
	{
		mUartAgent.SendData(payload.length, payload);
		
		return 1;
	}
	
	/**
	 * Receiving thread
	 */
	public void run() {
		byte[]	tempBuff	= new byte[1];
		
		while (true) {
		
			if (mUartAgent.readDataBlocked(tempBuff, 1) == 1)
			{/* read new byte */
				//logWindow.append(Byte.toString(tempBuff[0]));

				m_rxPool.msg[m_rxPool.tail] = tempBuff[0];
				m_rxPool.tail = (m_rxPool.tail + 1) % IPCL_RX_POOL_SIZE;
				if ( m_rxPool.tail == m_rxPool.head )
				{/* overflow, reject 1st byte */
					m_rxPool.head = (m_rxPool.head + 1) % IPCL_RX_POOL_SIZE;
					//printf("Rx buffer overflow!\n");
				}
				
				MsgUppacket();	
			}
			
			
//			{
//				byte[] ipcl_msg;
//				
//				ipcl_msg = new byte[14];
//				ipcl_msg[4] = Ipcl.SS_PLC;
//				ipcl_msg[7] = (byte)6;
//				
//				dispatchMsg(ipcl_msg);
//			}
			
//			{
//				Message UImsg;
//				
//				UImsg = mTargetHandler.obtainMessage((int)SS_PLC);
//				mTargetHandler.sendMessage(UImsg);				
//			}			
//			
//			try 
//			{
//				Thread.sleep(10000);
//			}
//			catch (InterruptedException e) 
//			{
//				e.printStackTrace();
//			}						
		}
	}
	
	public void destroyIpcl()
	{
		mUartAgent.DestroyAccessory(true);		
	}

	public void resumeIpcl()
	{
		if( 2 == mUartAgent.ResumeAccessory() )
		{
			//cleanPreference();
			//restorePreference();
		}
	}
	


	private byte calChecksum(byte[] Frame, int iStart, int iEnd)
	{
		int iRes = 0;		
		
		if (iEnd > iStart)
		{
			for (int i = iStart; i <= iEnd; i++)
			{
				iRes = iRes + (int)Frame[i];
			}
		}
		
		return (byte)iRes;
	}
	

	
	private  void MsgUppacket()
	{
		int		t_expect_len = 0;
		int		t_frame_len = 0;
		
		int		t_start 	= 0;
		int 	t_cur		= 0;
		int 	t_step 		= 0;

		t_cur = m_rxPool.head;
		t_frame_len = 1;
		
		while (m_rxPool.tail != t_cur)
		{
			if (t_step == 0)
			{/* 1. search for start pos */
				if (m_rxPool.msg[t_cur] == IPCL_MSG_HEADER)
				{
					t_start = t_cur;
					t_step = 1;
				}
				else
				{/* throw data before msg header */
					m_rxPool.head = (t_cur + 1) % IPCL_RX_POOL_SIZE;
				}	
			}
			else if (t_step == 1)
			{/* 2. search for frame length MSB */
				t_expect_len = (int)(m_rxPool.msg[t_cur] & 0xFF) << 8;
				t_step = 2;
			}
			else if (t_step == 2)
			{/* 3. search for frame length LSB*/
				t_step = 3;
				
				t_expect_len = t_expect_len + (int)(m_rxPool.msg[t_cur] & 0xFF);
				if ((t_expect_len < IPCL_RX_POOL_SIZE) && 
					(t_expect_len > 9))
				{
					t_frame_len = 3;
					t_step = 3;			
				}
				else
				{/* invalid length */
					m_rxPool.head = (m_rxPool.head + 1) % IPCL_RX_POOL_SIZE;
					t_cur = m_rxPool.head;
					t_step = 0;
					continue;
				}				
			}
			else if (t_step == 3) 
			{/* 4. search for stop pos */
				t_frame_len ++;
				
				if (t_frame_len == t_expect_len)
				{
					if (m_rxPool.msg[t_cur] == IPCL_MSG_FOOTER)
					{/* IPCL frame is received */
						byte[]	ipcl_msg = new byte[t_frame_len];
						byte	checksum;
						
						if (t_cur > t_start)
						{	
							//for (int i= 0; i < t_cur - t_start + 1; i++)
							//{
							//	ipcl_msg[i] = m_rxPool.msg[i + t_start];
							//}
							System.arraycopy(m_rxPool.msg, t_start, ipcl_msg, 0, t_frame_len);
						}
						else
						{
							int t_part1_len, t_part2_len;
							
							t_part1_len	= IPCL_RX_POOL_SIZE - t_start; 
							//for (int i= 0; i < t_part1_len; i++)
							//{
							//	ipcl_msg[i] = m_rxPool.msg[i + t_start];
							//}							
							System.arraycopy(m_rxPool.msg, t_start, ipcl_msg, 0, t_part1_len);
							
							t_part2_len	= t_cur + 1;
							//for (int i= 0; i < t_part2_len; i++)
							//{
							//	ipcl_msg[i + t_part1_len] = m_rxPool.msg[i];
							//}
							System.arraycopy(m_rxPool.msg, 0, ipcl_msg, t_part1_len, t_part2_len);
						}
						
						checksum = calChecksum(ipcl_msg, 3, t_frame_len - 3);
						if (checksum == ipcl_msg[t_frame_len - 2])
						{
							//Message msg;
						
							//msg = mTargetHandler.obtainMessage(0x0001, t_frame_len, 0, ipcl_msg);
							dispatchMsg(ipcl_msg);
							//mTargetHandler.sendMessage(msg);
							
							t_step = 0;
							m_rxPool.head = (t_cur + 1) % IPCL_RX_POOL_SIZE;
						}
						else
						{
							m_rxPool.head = (m_rxPool.head + 1) % IPCL_RX_POOL_SIZE;
							t_cur = m_rxPool.head;
							t_step = 0;	
							continue;
						}
					}
					else
					{/* incorrect IPCL frame, drop 1st byte header and rescan */
						m_rxPool.head = (m_rxPool.head + 1) % IPCL_RX_POOL_SIZE;
						t_cur = m_rxPool.head;
						t_step = 0;
						continue;
					}
				}

			}
			else
			{/* unexepected condition */
				t_step = 0;
			}

			t_cur = (t_cur + 1) % IPCL_RX_POOL_SIZE;		/* Move cursor forward */
			//t_frame_len ++;
		}
		
	}
	
	private void dispatchMsg(byte[] ipcl_frame)
	{
		if (ipcl_frame.length > 9)
		{
			byte[] msg = new byte[ipcl_frame.length - 9];
			
			System.arraycopy(ipcl_frame, 7, msg, 0, ipcl_frame.length - 9);
			
			switch (ipcl_frame[4])
			{
			case SS_SYSTEM:

				if (mSystem.notificationCallback(msg) == true)
				{
					Message UImsg;
					
					UImsg = mTargetHandler.obtainMessage((int)SS_SYSTEM);
					mTargetHandler.sendMessage(UImsg);				
				}
				
				break;
				
			case SS_PLC:
				synchronized(mSystem)
				{
					if (mPlc.notificationCallback(msg) == true)
					{
						Message UImsg;
						
						UImsg = mTargetHandler.obtainMessage((int)SS_PLC);
						mTargetHandler.sendMessage(UImsg);				
					}
				}
				break;
				
			case SS_AUDIO:
				if (mAudio.notificationCallback(msg) == true)
				{
					Message UImsg;
					
					UImsg = mTargetHandler.obtainMessage((int)SS_AUDIO);
					mTargetHandler.sendMessage(UImsg);	
				}
				break;
				
			case SS_DVD:
				if (mDvd.notificationCallback(msg) == true)
				{
					Message UImsg;
					
					UImsg = mTargetHandler.obtainMessage((int)SS_DVD);
					mTargetHandler.sendMessage(UImsg);	
				}
				break;
				
			default:
				
				break;
			}
		}

	}
}
