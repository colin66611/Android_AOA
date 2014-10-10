package com.viewpagerindicator.sample;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageSwitcher;
import android.widget.ToggleButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.example.plctest.Ipcl;


public final class TestFragment extends Fragment {
    private static final String KEY_CONTENT = "TestFragment:Content";
    
    static final int imgCurtainOpen = R.drawable.curtain_open;
    static final int imgCurtainClose = R.drawable.curtain_close;
/*
    public static TestFragment newInstance(String content) {
        TestFragment fragment = new TestFragment();

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            builder.append(content).append(" ");
        }
        builder.deleteCharAt(builder.length() - 1);
        fragment.mContent = builder.toString();
        Log.d("colin", "inside newInstance.");
        return fragment;
    }
*/
    public static TestFragment newInstance(int index) {
    	TestFragment fragment = new TestFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", index);
    	fragment.setArguments(args);
		return fragment;
    	
    }
    private Toast mToast;
    private String mContent = "???";
    private static int Index = -1;
    
    //private PLCActivity plc_server;
    
    
    /*new plc agent*/
    private PlcAgent plcAgent;
    public static Ipcl mIpclServer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("colin", "inside onCreate.");
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
        
    	plcAgent = new PlcAgent(); 
    	/*new and start ipcl thread*/
		mIpclServer = new Ipcl(getActivity(), handler);
		mIpclServer.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("colin", "inside onCreateView.");
        View fragmentView = null;
        
        TextView text = new TextView(getActivity());
        text.setGravity(Gravity.CENTER);
        text.setText(mContent);
        text.setTextSize(20 * getResources().getDisplayMetrics().density);
        text.setPadding(20, 20, 20, 20);
        
        Index = getArguments().getInt("index", 0);
        Log.d("colin", "get index " + Index + " inside onCreateView");
    	switch(Index)
    	{
    	case 0:
    		fragmentView = inflater.inflate(R.layout.page_0, container, false);
    		fragmentPage_0(fragmentView);
    		Log.d("colin", "page 1.");
    		break;
    	case 1:
    		fragmentView = inflater.inflate(R.layout.page_1, container, false);
    		fragmentPage_1(fragmentView);
    		Log.d("colin", "page 2.");
    		break;
    	case 2:
    		fragmentView = inflater.inflate(R.layout.page_2, container, false);
    		fragmentPage_2(fragmentView);
    		Log.d("colin", "page 3.");
    		break;
    	case 3:
    		fragmentView = inflater.inflate(R.layout.page_3, container, false);
    		//fragmentPage_3(fragmentView);
    		Log.d("colin", "page 4.");
    		break;
    	case 4:
    		fragmentView = inflater.inflate(R.layout.page_4, container, false);
    		//fragmentPage_4(fragmentView);
    		Log.d("colin", "page 5.");
    		break;    		
    	default:
    		Log.e("colin", "index is wrong." + Index);
    	
    	}

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        layout.setGravity(Gravity.CENTER);
        layout.addView(text);

        return fragmentView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    	Log.d("colin", "inside onSaveInstanceState.");
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }
    
    public void fragmentPage_0(View view) {
    	Button btnLanguage = (Button) view.findViewById(R.id.btn_language);
    	Button btnTvUp = (Button) view.findViewById(R.id.btn_TV_up);
    	Button btnGlassUp = (Button) view.findViewById(R.id.btn_glass_up);
    	ToggleButton btnTvSwtich = (ToggleButton) view.findViewById(R.id.btn_TV_swtich);
    	ToggleButton btnGlassChange = (ToggleButton) view.findViewById(R.id.btn_glass_change);
    	Button btnTvDown = (Button) view.findViewById(R.id.btn_TV_down);
    	Button btnGlassDown = (Button) view.findViewById(R.id.btn_glass_down);
    	
    	btnLanguage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnLanguage.", Toast.LENGTH_SHORT).show();
			}
		});
    	
    	btnTvUp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnTvUp.", Toast.LENGTH_SHORT).show();
				mIpclServer.mPlc.setTVUp();
			}
		});
    	
    	btnGlassUp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnTvUp.", Toast.LENGTH_SHORT).show();
				mIpclServer.mPlc.setGlassUp();
			}
		});
    	
    	btnTvSwtich.setOnCheckedChangeListener(new OnCheckedChangeListener() {
    		@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    			if(isChecked) {
					Toast.makeText(getActivity(), "btnTvOn.", Toast.LENGTH_SHORT).show();
					mIpclServer.mPlc.setTVPwr(true);
				}
				else {
					Toast.makeText(getActivity(), "btnTvOff.", Toast.LENGTH_SHORT).show();
					mIpclServer.mPlc.setTVPwr(false);
				}
    		}
    	});

    	
    	btnGlassChange.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    			if(isChecked) {
					Toast.makeText(getActivity(), "btnGlassChangeOn.", Toast.LENGTH_SHORT).show();
					mIpclServer.mPlc.setGlassPwr(true);
				}
				else {
					Toast.makeText(getActivity(), "btnGlassChangeOff.", Toast.LENGTH_SHORT).show();
					mIpclServer.mPlc.setGlassPwr(false);
				}
			}
		});
    	
    	btnTvDown.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnTvUp.", Toast.LENGTH_SHORT).show();
				mIpclServer.mPlc.setTVDown();
			}
		});
    	
    	btnGlassDown.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnTvUp.", Toast.LENGTH_SHORT).show();
				mIpclServer.mPlc.setGlassDown();
			}
		});
    	
    }

    public void fragmentPage_1(View view) {
    	Button btnCurtain1Up = (Button) view.findViewById(R.id.btn_curtain_1_up);
    	Button btnCurtain1Down = (Button) view.findViewById(R.id.btn_curtain_1_down);
    	Button btnCurtain2Up = (Button) view.findViewById(R.id.btn_curtain_2_up);
    	Button btnCurtain2Down = (Button) view.findViewById(R.id.btn_curtain_2_down);
    	Button btnCurtain3Up = (Button) view.findViewById(R.id.btn_curtain_3_up);
    	Button btnCurtain3Down = (Button) view.findViewById(R.id.btn_curtain_3_down);
    	Button btnCurtain4Up = (Button) view.findViewById(R.id.btn_curtain_4_up);
    	Button btnCurtain4Down = (Button) view.findViewById(R.id.btn_curtain_4_down);
    	
    	final ImageSwitcher curtain_1 = (ImageSwitcher) view.findViewById(R.id.img_curtain_1);
    	final ImageSwitcher curtain_2 = (ImageSwitcher) view.findViewById(R.id.img_curtain_2);
    	final ImageSwitcher curtain_3 = (ImageSwitcher) view.findViewById(R.id.img_curtain_3);
    	final ImageSwitcher curtain_4 = (ImageSwitcher) view.findViewById(R.id.img_curtain_4);
    	
    	curtain_1.setFactory((ViewFactory) view);
    	curtain_2.setFactory((ViewFactory) view);
    	curtain_3.setFactory((ViewFactory) view);
    	curtain_4.setFactory((ViewFactory) view);
    	
    	if(mIpclServer.mPlc.getShadeOpenState_1()) {
    		curtain_1.setImageResource(imgCurtainOpen);  
    	}
    	else {
    		curtain_1.setImageResource(imgCurtainClose);  
    	}
    	
    	if(mIpclServer.mPlc.getShadeOpenState_2()) {
    		curtain_2.setImageResource(imgCurtainOpen);  
    	}
    	else {
    		curtain_2.setImageResource(imgCurtainClose);  
    	}
    	
    	if(mIpclServer.mPlc.getShadeOpenState_3()) {
    		curtain_3.setImageResource(imgCurtainOpen);  
    	}
    	else {
    		curtain_3.setImageResource(imgCurtainClose);  
    	}
    	
    	if(mIpclServer.mPlc.getShadeOpenState_4()) {
    		curtain_4.setImageResource(imgCurtainOpen);  
    	}
    	else {
    		curtain_4.setImageResource(imgCurtainClose);  
    	}
    	
    	btnCurtain1Up.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!mIpclServer.mPlc.getShadeOpenState_1()) {
					mIpclServer.mPlc.openShade_1();
					curtain_1.setImageResource(imgCurtainOpen);
				}
				Toast.makeText(getActivity(), "btnLanguage.", Toast.LENGTH_SHORT).show();
			}
		});
    	
    	btnCurtain1Down.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!mIpclServer.mPlc.getShadeCloseState_1()) {
					mIpclServer.mPlc.closeShade_1();
					curtain_1.setImageResource(imgCurtainClose);
				}
				Toast.makeText(getActivity(), "btnTvUp.", Toast.LENGTH_SHORT).show();
			}
		});
    	
    	btnCurtain2Up.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!mIpclServer.mPlc.getShadeOpenState_2()) {
					mIpclServer.mPlc.openShade_2();
					curtain_2.setImageResource(imgCurtainOpen);
				}
				Toast.makeText(getActivity(), "btnTvUp.", Toast.LENGTH_SHORT).show();
			}
		});
    	
    	btnCurtain2Down.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!mIpclServer.mPlc.getShadeCloseState_2()) {
					mIpclServer.mPlc.closeShade_2();
					curtain_2.setImageResource(imgCurtainClose);
				}
				Toast.makeText(getActivity(), "btnTvUp.", Toast.LENGTH_SHORT).show();
			}
		});
    	
    	btnCurtain3Up.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!mIpclServer.mPlc.getShadeOpenState_3()) {
					mIpclServer.mPlc.openShade_3();
					curtain_3.setImageResource(imgCurtainOpen);
				}
				Toast.makeText(getActivity(), "btnTvUp.", Toast.LENGTH_SHORT).show();
			}
		});
    	
    	btnCurtain3Down.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!mIpclServer.mPlc.getShadeCloseState_3()) {
					mIpclServer.mPlc.closeShade_3();
					curtain_3.setImageResource(imgCurtainClose);
				}
				Toast.makeText(getActivity(), "btnTvUp.", Toast.LENGTH_SHORT).show();
			}
		});
    	
    	btnCurtain4Up.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!mIpclServer.mPlc.getShadeOpenState_4()) {
					mIpclServer.mPlc.openShade_4();
					curtain_4.setImageResource(imgCurtainOpen);
				}
				Toast.makeText(getActivity(), "btnTvUp.", Toast.LENGTH_SHORT).show();
			}
		});
    	
    	btnCurtain4Down.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!mIpclServer.mPlc.getShadeCloseState_4()) {
					mIpclServer.mPlc.closeShade_4();
					curtain_4.setImageResource(imgCurtainClose);
				}
				Toast.makeText(getActivity(), "btnTvUp.", Toast.LENGTH_SHORT).show();
			}
		});
    	
    }
    
    public void fragmentPage_2(View view) {
    	ToggleButton btnCabinetLight = (ToggleButton) view.findViewById(R.id.btn_cabinet_light);
    	ToggleButton btnTopLight = (ToggleButton) view.findViewById(R.id.btn_top_light);
    	ToggleButton btnMoodLight1 = (ToggleButton) view.findViewById(R.id.btn_mood_light_1);
    	ToggleButton btnMoodLight2 = (ToggleButton) view.findViewById(R.id.btn_mood_light_2);
    	ToggleButton btnMoodLight3 = (ToggleButton) view.findViewById(R.id.btn_mood_light_3);
    	ToggleButton btnReadLight1 = (ToggleButton) view.findViewById(R.id.btn_read_light_1);
    	ToggleButton btnReadLight2 = (ToggleButton) view.findViewById(R.id.btn_read_light_2);
    	ToggleButton btnReadLight3 = (ToggleButton) view.findViewById(R.id.btn_read_light_3);
    	ToggleButton btnReadLight4 = (ToggleButton) view.findViewById(R.id.btn_read_light_4);
    	
    	btnCabinetLight.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked) {
					Toast.makeText(getActivity(), "open btnCabinetLight.", Toast.LENGTH_SHORT).show();
					mIpclServer.mPlc.setBarLight(true);
				}
				else {
					Toast.makeText(getActivity(), "close btnCabinetLight.", Toast.LENGTH_SHORT).show();
					mIpclServer.mPlc.setBarLight(false);
				}
			}
		});
    	
    	btnTopLight.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked) {
					Toast.makeText(getActivity(), "open btnTopLight.", Toast.LENGTH_SHORT).show();
					mIpclServer.mPlc.setTopLight(true);
				}
				else {
					Toast.makeText(getActivity(), "close btnTopLight.", Toast.LENGTH_SHORT).show();
					mIpclServer.mPlc.setTopLight(false);
				}
			}
		});
    	
    	btnMoodLight1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked) {
					Toast.makeText(getActivity(), "open btnMoodLight1.", Toast.LENGTH_SHORT).show();
					mIpclServer.mPlc.setMoodLight_1(true);
				}
				else {
					Toast.makeText(getActivity(), "close btnMoodLight1.", Toast.LENGTH_SHORT).show();
					mIpclServer.mPlc.setMoodLight_1(false);
				}
			}
		});
    	
    	btnMoodLight2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked) {
					Toast.makeText(getActivity(), "open btnMoodLight2.", Toast.LENGTH_SHORT).show();
					mIpclServer.mPlc.setMoodLight_2(true);
				}
				else {
					Toast.makeText(getActivity(), "close btnMoodLight2.", Toast.LENGTH_SHORT).show();
					mIpclServer.mPlc.setMoodLight_2(false);
				}
			}
		});
    	
    	btnMoodLight3.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked) {
					Toast.makeText(getActivity(), "open btnMoodLight3.", Toast.LENGTH_SHORT).show();
					mIpclServer.mPlc.setMoodLight_3(true);
				}
				else {
					Toast.makeText(getActivity(), "close btnMoodLight3.", Toast.LENGTH_SHORT).show();
					mIpclServer.mPlc.setMoodLight_3(false);
				}
			}
		});
    	
    	btnReadLight1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked) {
					Toast.makeText(getActivity(), "open btnReadLight1.", Toast.LENGTH_SHORT).show();
					mIpclServer.mPlc.setReadLight_1(true);
				}
				else {
					Toast.makeText(getActivity(), "close btnReadLight1.", Toast.LENGTH_SHORT).show();
					mIpclServer.mPlc.setReadLight_1(false);
				}
			}
		});
    	
    	btnReadLight2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked) {
					Toast.makeText(getActivity(), "open btnReadLight2.", Toast.LENGTH_SHORT).show();
					mIpclServer.mPlc.setReadLight_2(true);
				}
				else {
					Toast.makeText(getActivity(), "close btnReadLight2.", Toast.LENGTH_SHORT).show();
					mIpclServer.mPlc.setReadLight_2(false);
				}
			}
		});
    	
    	btnReadLight3.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked) {
					Toast.makeText(getActivity(), "open btnReadLight3.", Toast.LENGTH_SHORT).show();
					mIpclServer.mPlc.setReadLight_3(true);
				}
				else {
					Toast.makeText(getActivity(), "close btnReadLight3.", Toast.LENGTH_SHORT).show();
					mIpclServer.mPlc.setReadLight_3(false);
				}
			}
		});
    	
    	btnReadLight4.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked) {
					Toast.makeText(getActivity(), "open btnReadLight4.", Toast.LENGTH_SHORT).show();
					mIpclServer.mPlc.setReadLight_4(true);
				}
				else {
					Toast.makeText(getActivity(), "close btnReadLight4.", Toast.LENGTH_SHORT).show();
					mIpclServer.mPlc.setReadLight_4(false);
				}
			}
		});
    	
    }

    public void fragmentPage_3(View view) {
    	Button btnSunroofOn = (Button) view.findViewById(R.id.btn_sun_roof_on);
    	Button btnSunroofOff = (Button) view.findViewById(R.id.btn_sun_roof_off);
    	Button btnPCOn = (Button) view.findViewById(R.id.btn_pc_on);
    	Button btnPCOff = (Button) view.findViewById(R.id.btn_pc_off);
    	Button btnDTVOn = (Button) view.findViewById(R.id.btn_DTV_on);
    	Button btnDTVOff = (Button) view.findViewById(R.id.btn_DTV_off);
    	Button btnDVDOn = (Button) view.findViewById(R.id.btn_DVD_on);
    	Button btnDVDOff = (Button) view.findViewById(R.id.btn_DVD_off);
    	
    	btnSunroofOn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnCurtain1Up.", Toast.LENGTH_SHORT).show();
				mIpclServer.mPlc.openSunroof();
			}
		});
    	
    	btnSunroofOff.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnCurtain1Down.", Toast.LENGTH_SHORT).show();
				mIpclServer.mPlc.closeSunroof();
			}
		});
    	
    	btnPCOn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnCurtain2Up.", Toast.LENGTH_SHORT).show();
				mIpclServer.mPlc.setPCPwr(true);
			}
		});
    	
    	btnPCOff.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnCurtain2Down.", Toast.LENGTH_SHORT).show();
				mIpclServer.mPlc.setPCPwr(false);
			}
		});
    	
    	btnDTVOn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnCurtain3Up.", Toast.LENGTH_SHORT).show();
				// TO DO
			}
		});
    	
    	btnDTVOff.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnCurtain3Down.", Toast.LENGTH_SHORT).show();
				//TO DO
			}
		});
    	
    	btnDVDOn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnCurtain4Up.", Toast.LENGTH_SHORT).show();
				//TO DO
			}
		});
    	
    	btnDVDOff.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnCurtain4Down.", Toast.LENGTH_SHORT).show();
				//TO DO
			}
		});
    	
    }

    public void fragmentPage_4(View view) {
    	Button btnDvdHome = (Button) view.findViewById(R.id.btn_dvd_main);
    	Button btnLastSong = (Button) view.findViewById(R.id.btn_last_song);
    	Button btnDvdPlay = (Button) view.findViewById(R.id.btn_dvd_display);
    	Button btnDvdPause = (Button) view.findViewById(R.id.btn_dvd_pause);
    	Button btnGlassChange = (Button) view.findViewById(R.id.btn_glass_change);
    	Button btnTvDown = (Button) view.findViewById(R.id.btn_TV_down);
    	Button btnGlassDown = (Button) view.findViewById(R.id.btn_glass_down);
    	
    	btnDvdHome.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnDvdHome.", Toast.LENGTH_SHORT).show();
				
			}
		});
    	
    	btnLastSong.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnLastSong.", Toast.LENGTH_SHORT).show();
				
			}
		});
    	
    	btnDvdPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnTvUp.", Toast.LENGTH_SHORT).show();
				//plc_server.onResume();
			}
		});
    	
    	btnDvdPause.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnTvUp.", Toast.LENGTH_SHORT).show();
				//plc_server.onPause();
			}
		});
    	
    	btnGlassChange.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnTvUp.", Toast.LENGTH_SHORT).show();
			}
		});
    	
    	btnTvDown.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnTvUp.", Toast.LENGTH_SHORT).show();
			}
		});
    	
    	btnGlassDown.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnTvUp.", Toast.LENGTH_SHORT).show();
			}
		});
    	
    }
    
	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			if (msg.what == 0x0001)
			{
				byte[] rxMsg = (byte[]) msg.obj;
				String recText = new String();
				
				for (int i = 0; i < msg.arg1 ; i++)
				{
					recText = recText + Integer.toHexString(rxMsg[i]) + " ";
				}
				
				Log.v("colin", "rec is "+recText);
			}
			
			
		}
	};
	
	public class PlcAgent{
		public final byte MSG_FRAME_HEADER = 0x02;
		public final byte MSG_FRAME_FOOTER = 0x03;
		public final int MAX_SWITCH_NO = 25;
		
		public void setTvUpOn(){
			setSwitch(0, true);			
		}
		
		public void setTvUpOff(){
			setSwitch(0, false);
		}
		
		public void setTvDnOn(){
			setSwitch(1, true);			
		}
		
		public void setTvDnOff(){
			setSwitch(1, false);	
		}		
		public void setTvPwrOn(){
			setSwitch(2, true);
		}
		
		public void setTvPwrOff(){
			setSwitch(2, false);
		}
		public void setGlassPwrOn(){
			setSwitch(3, true);
		}
		
		public void setGlassPwrOff(){
			setSwitch(3, false);
		}		
		private int setSwitch(int iSwitchNo, boolean bState){
			
			if (iSwitchNo >= 0 && iSwitchNo <= MAX_SWITCH_NO )
			{
				byte[] plcCmd;
				plcCmd = new byte[14];
				
				plcCmd[0] = (byte) 0x02;
				
				plcCmd[1] = (byte) '0';
				plcCmd[2] = (byte) '1';
				
				plcCmd[3] = (byte) '4';
				plcCmd[4] = (byte) '2';
				
				if (bState == true)
				{
					plcCmd[5] = (byte) '3';
				}
				else
				{
					plcCmd[5] = (byte) '4';					
				}
				
				plcCmd[6] = (byte) 'Y';
				
				String sSwitchNo = Integer.toString(iSwitchNo);
				
				
				plcCmd[7] = (byte) '0';
				plcCmd[8] = (byte) '0';
				plcCmd[9] = (byte) '0';
				plcCmd[10] = (byte) '0'; 
				
				if (sSwitchNo.length() >= 4)
				{
					plcCmd[7] = (byte)sSwitchNo.charAt(3);
				}
				if (sSwitchNo.length() >= 3)
				{
					plcCmd[8] = (byte)sSwitchNo.charAt(2);
				}
				if (sSwitchNo.length() >= 2)
				{
					plcCmd[9] = (byte)sSwitchNo.charAt(1);
				}
				if (sSwitchNo.length() >= 1)
				{
					plcCmd[10] = (byte)sSwitchNo.charAt(0);
				}	
				
				byte lrc = calculateLRC(plcCmd, 11);
				
				plcCmd[11] = bcdToAsc((byte)((lrc >> 4) & 0x0F));				
				plcCmd[12] = bcdToAsc((byte)(lrc & 0x0F));		
				
				plcCmd[13] = (byte) 0x03;
				
				mIpclServer.sendRawMsg(plcCmd);
				
				return 0;
			}
			else
			{
				return -1;
			}
		}
		
		private byte bcdToAsc(byte bcd)
		{
			if ((bcd >= 0x00) && (bcd <= 0x09))
			{
				return (byte)(bcd + (byte)'0');
			}
			else if ((bcd >= 0x0A) && (bcd <= 0x0F))
			{
				return (byte)(bcd + (byte)'A' - (byte)0x0A);
			}
			else
			{
				return (byte)0x00;
			}
			
		}

		private byte calculateLRC(byte[] buff, int byteNum)
		{
			int loop;
			byte res = 0;
			
			for (loop = 0; loop < byteNum; loop ++)
			{
				res = (byte)(buff[loop] + res);
			}
			
			return res;
		}
	}
    

}
