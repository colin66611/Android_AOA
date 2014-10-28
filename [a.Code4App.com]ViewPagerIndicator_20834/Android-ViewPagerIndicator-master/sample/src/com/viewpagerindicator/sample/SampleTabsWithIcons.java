package com.viewpagerindicator.sample;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.gmc.motorhome.*;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TabPageIndicator;

public class SampleTabsWithIcons extends FragmentActivity implements MessageListener{
	
	private MessageListener actMsgListener;
	public Ipcl mIpclServer;
	
    private static final String[] CONTENT = new String[] { "主页", "窗帘", "灯光", "电器", "DVD"};
    private static final int[] ICONS = new int[] {
            R.drawable.perm_group_calendar,
            R.drawable.perm_group_camera,
            R.drawable.perm_group_device_alarms,
            R.drawable.perm_group_location,
            R.drawable.perm_group_location,
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);		
        
        setContentView(R.layout.simple_tabs);
        Log.d("colin", "onCreate activity tabs.");

		mIpclServer = new Ipcl(this, handler);
		mIpclServer.start();  

        FragmentPagerAdapter adapter = new GoogleMusicAdapter(getSupportFragmentManager());

        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);

        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(pager);
    }

	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//System的心跳消息周期为1秒，如果5秒收不到任何消息视为通信中断，显示界面提示
			
			if (msg.what == (int)mIpclServer.SS_PLC)
			{
				actMsgListener.syncView(actMsgListener.SS_PLC);
				Toast.makeText(SampleTabsWithIcons.this, "PLC state updated!", Toast.LENGTH_SHORT).show();	
			}
			
			if (msg.what == (int)mIpclServer.SS_DVD)
			{
				actMsgListener.syncView(actMsgListener.SS_DVD);
				Toast.makeText(SampleTabsWithIcons.this, "DVD state updated!", Toast.LENGTH_SHORT).show();	
			}
			
			if (msg.what == (int)mIpclServer.SS_SYSTEM)
			{
				actMsgListener.syncView(actMsgListener.SS_SYSTEM);
				Toast.makeText(SampleTabsWithIcons.this, "SYSTEM state updated!", Toast.LENGTH_SHORT).show();	
			}		
			
			if (msg.what == (int)mIpclServer.SS_AUDIO)
			{
				actMsgListener.syncView(actMsgListener.SS_AUDIO);
				Toast.makeText(SampleTabsWithIcons.this, "AUDIO state updated!", Toast.LENGTH_SHORT).show();	
			}			
			
		}
	};	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(false);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		// Ideally should implement onResume() and onPause()
		// to take appropriate action when the activity looses focus
		super.onResume();	
		mIpclServer.resumeIpcl();

	}    
	
	@Override
	protected void onDestroy() {
		mIpclServer.destroyIpcl();
		super.onDestroy();
	}	
	
	
	
    @Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
    
    @Override
    public void onAttachFragment(Fragment fragment) {
    	Log.d("colin", "onAttachFragment");
    	try {
    		actMsgListener = (MessageListener)fragment;
    	} catch (Exception e) {
    		Toast.makeText(SampleTabsWithIcons.this, "fragment has to finish the method.", Toast.LENGTH_SHORT).show();
    	}
    	
    	// TODO Auto-generated method stub
    	super.onAttachFragment(fragment); 
    }



	class GoogleMusicAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
        public GoogleMusicAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            //return TestFragment.newInstance(CONTENT[position % CONTENT.length]);
        	return TestFragment.newInstance(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length].toUpperCase();
        }

        @Override public int getIconResId(int index) {
          return ICONS[index];
        }

      @Override
        public int getCount() {
          return CONTENT.length;
        }
    }

	@Override
	public void syncView(int type) {
		// TODO Auto-generated method stub
		
	}
    

	

}
