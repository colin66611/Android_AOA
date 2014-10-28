package com.viewpagerindicator.sample;

public interface MessageListener {
	
	int SS_SYSTEM = 0x01;
	int SS_PLC = 0x02;
	int SS_DVD = 0x03;
	int SS_AUDIO = 0x04;
	
	public void syncView(int type);
}
