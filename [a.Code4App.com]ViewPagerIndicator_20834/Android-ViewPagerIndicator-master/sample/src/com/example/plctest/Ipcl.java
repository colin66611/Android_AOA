package com.example.plctest;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.example.plctest.FT311UARTInterface;

public class Ipcl extends Thread{
	

	private Handler mTargetHandler;
	private FT311UARTInterface uartAgent;
	
	/* Definition of IPCL protocol */
	private final byte IPCL_MSG_HEADER 			= 0x02;
	private final byte IPCL_MSG_FOOTER			= 0x03;
	private final byte IPCL_RX_POOL_SIZE		= 100;
	
	
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
	
	/* Define Subsystem group channel */
	public final byte SS_SYSTEM = 0x11;	
	public final byte SS_PLC = 0x21;
	public final byte SS_DVD = 0x22;
	public final byte SS_AUDIO = 0x23;


	public Ipcl(Context ctx, Handler h)
	{
		mTargetHandler = h;
		m_rxPool = new IPCL_RX_MSG_t(IPCL_RX_POOL_SIZE);
		
		/* Subsystem proxy */
		mPlc = new Plc(this);
		
		uartAgent = new FT311UARTInterface(ctx, 9600, (byte)8, (byte)1, (byte)0, (byte)(0));
		
	}
	

	public Plc mPlc;
	

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
		
		//uartAgent.SendData(iMsgLen, payload);
		uartAgent.SendData(iFramelen, ipclFrame);
		
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
		
		uartAgent.SendData(expectLen, payload);
		
		return 1;
	}
	
	public int sendRawMsg(byte[] payload)
	{
		uartAgent.SendData(payload.length, payload);
		
		return 1;
	}
	
	/**
	 * Receiving thread
	 */
	public void run() {
		byte[]	tempBuff	= new byte[1];
		
		while (true) {
		
			if (uartAgent.readDataBlocked1(tempBuff, 1) == 1)
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
		}
	}
	
	public void destroyIpcl()
	{
		uartAgent.DestroyAccessory(true);		
	}

	public void resumeIpcl()
	{
		if( 2 == uartAgent.ResumeAccessory() )
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
				t_expect_len = (int)m_rxPool.msg[t_cur] << 8;
				t_step = 2;
			}
			else if (t_step == 2)
			{/* 3. search for frame length LSB*/
				t_step = 3;
				
				t_expect_len = t_expect_len + (int)m_rxPool.msg[t_cur];
				if (t_expect_len < IPCL_RX_POOL_SIZE)
				{
					t_frame_len = 3;
					t_step = 3;			
				}
				else
				{/* invalid length */
					t_cur = (m_rxPool.head + 1) % IPCL_RX_POOL_SIZE;
					m_rxPool.head = t_cur;
					t_step = 0;			
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
							for (int i= 0; i < t_cur - t_start + 1; i++)
							{
								ipcl_msg[i] = m_rxPool.msg[i + t_start];
							}
						}
						else
						{
							int t_part1_len, t_part2_len;
							
							t_part1_len	= IPCL_RX_POOL_SIZE - t_start; 
							for (int i= 0; i < t_part1_len; i++)
							{
								ipcl_msg[i] = m_rxPool.msg[i + t_start];
							}							
							
							t_part2_len	= t_cur + 1;
							for (int i= 0; i < t_part2_len; i++)
							{
								ipcl_msg[i + t_part1_len] = m_rxPool.msg[i];
							}
						}
						
						checksum = calChecksum(ipcl_msg, 3, t_frame_len - 3);
						if (checksum == m_rxPool.msg[t_cur -1])
						{
							Message msg;
						
							msg = mTargetHandler.obtainMessage(0x0001, t_frame_len, 0, ipcl_msg);
							dispatchMsg(ipcl_msg);
							mTargetHandler.sendMessage(msg);
							
							t_step = 0;
							m_rxPool.head = (t_cur + 1) % IPCL_RX_POOL_SIZE;
						}
						else
						{
							t_cur = (m_rxPool.head + 1) % IPCL_RX_POOL_SIZE;
							m_rxPool.head = t_cur;
							t_step = 0;				
						}
					}
					else
					{/* incorrect IPCL frame, drop 1st byte header and rescan */
						t_cur = (m_rxPool.head + 1) % IPCL_RX_POOL_SIZE;
						m_rxPool.head = t_cur;
						t_step = 0;
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
		byte[] msg = new byte[ipcl_frame.length - 9];
		
		System.arraycopy(ipcl_frame, 7, msg, 0, ipcl_frame.length - 9);
		
		switch (ipcl_frame[4])
		{
		case SS_SYSTEM:
			
			break;
			
		case SS_PLC:
			mPlc.notificationCallback(msg);
			break;
			
		case SS_AUDIO:
			
			break;
			
		case SS_DVD:
			
			break;
			
		default:
			
			break;
		}
	}
}
