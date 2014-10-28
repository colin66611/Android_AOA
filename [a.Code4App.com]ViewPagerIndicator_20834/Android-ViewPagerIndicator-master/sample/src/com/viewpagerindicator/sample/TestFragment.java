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
import android.widget.ImageView;
import android.widget.ToggleButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.gmc.motorhome.*;
import com.gmc.motorhome.Audio.AudioSrc;


public final class TestFragment extends Fragment {
    private static final String KEY_CONTENT = "TestFragment:Content";
    
    static final int imgCurtainOpen = R.drawable.curtain_open;
    static final int imgCurtainClose = R.drawable.curtain_close;
    static final int imgSunroofOpen = R.drawable.sunroof_open;
    static final int imgSunroofClose = R.drawable.sunroof_close;
    static final int imgPCOn = R.drawable.pc_on;
    static final int imgPCOff = R.drawable.pc_off;
    static final int imgDTVOn = R.drawable.dtv_on;
    static final int imgDTVOff = R.drawable.dtv_off;
    static final int imgDVDOn = R.drawable.dvd_on;
    static final int imgDVDOff = R.drawable.dvd_off;
    
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
    
    
    /*new plc agent*/

    public static Ipcl mIpclServer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("colin", "inside onCreate.");
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
        
    	/* get ipcl instance */
    	mIpclServer = Ipcl.getInstance();
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
    		Log.d("colin", "page 0.");
    		break;
    	case 1:
    		fragmentView = inflater.inflate(R.layout.page_1, container, false);
    		fragmentPage_1(fragmentView);
    		Log.d("colin", "page 1.");
    		break;
    	case 2:
    		fragmentView = inflater.inflate(R.layout.page_2, container, false);
    		fragmentPage_2(fragmentView);
    		Log.d("colin", "page 2.");
    		break;
    	case 3:
    		fragmentView = inflater.inflate(R.layout.page_3, container, false);
    		fragmentPage_3(fragmentView);
    		Log.d("colin", "page 3.");
    		break;
    	case 4:
    		fragmentView = inflater.inflate(R.layout.page_4, container, false);
    		fragmentPage_4(fragmentView);
    		Log.d("colin", "page 4.");
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
    public void onResume(){
		super.onResume();	
		Index = getArguments().getInt("index", 0);
		Log.d("colin", "fragment onResume page." + Index);
			
    }
    

    @Override
    public void onSaveInstanceState(Bundle outState) {
    	Index = getArguments().getInt("index", 0);
    	Log.d("colin", "inside onSaveInstanceState page." + Index);
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	Index = getArguments().getInt("index", 0);
    	Log.d("colin", "fragment onStop." + Index);
    }
    
    
    public void fragmentPage_0(View view) {
    	Button btnLanguage = (Button) view.findViewById(R.id.btn_language);
    	Button btnTvUp = (Button) view.findViewById(R.id.btn_TV_up);
    	Button btnGlassUp = (Button) view.findViewById(R.id.btn_glass_up);
    	ToggleButton btnTvSwtich = (ToggleButton) view.findViewById(R.id.btn_TV_swtich);
    	ToggleButton btnGlassChange = (ToggleButton) view.findViewById(R.id.btn_glass_change);
    	Button btnTvDown = (Button) view.findViewById(R.id.btn_TV_down);
    	Button btnGlassDown = (Button) view.findViewById(R.id.btn_glass_down);
    	
    	if(mIpclServer.mPlc.getTVUp() == true) {
    		btnTvUp.setEnabled(false);
    	}
    	
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
    	
    	final ImageSwitcher curtain_switcher_1 = (ImageSwitcher) view.findViewById(R.id.img_curtain_1);
    	final ImageSwitcher curtain_switcher_2 = (ImageSwitcher) view.findViewById(R.id.img_curtain_2);
    	final ImageSwitcher curtain_switcher_3 = (ImageSwitcher) view.findViewById(R.id.img_curtain_3);
    	final ImageSwitcher curtain_switcher_4 = (ImageSwitcher) view.findViewById(R.id.img_curtain_4);
    	
    	class ViewFactory_1 implements ViewFactory {

    		@Override
    		public View makeView() {
    			// TODO Auto-generated method stub
    			ImageView curtain_image_1 = (ImageView)LayoutInflater.from(getActivity()).inflate(R.layout.switch_view, curtain_switcher_1, false);
    			return curtain_image_1;
    		}
        }
    	
    	class ViewFactory_2 implements ViewFactory {

    		@Override
    		public View makeView() {
    			// TODO Auto-generated method stub
    			ImageView curtain_image_2 = (ImageView)LayoutInflater.from(getActivity()).inflate(R.layout.switch_view, curtain_switcher_2, false);
    			return curtain_image_2;
    		}
        }
    	
    	class ViewFactory_3 implements ViewFactory {

    		@Override
    		public View makeView() {
    			// TODO Auto-generated method stub
    			ImageView curtain_image_3 = (ImageView)LayoutInflater.from(getActivity()).inflate(R.layout.switch_view, curtain_switcher_3, false);
    			return curtain_image_3;
    		}
        }
    	
    	class ViewFactory_4 implements ViewFactory {

    		@Override
    		public View makeView() {
    			// TODO Auto-generated method stub
    			ImageView curtain_image_4 = (ImageView)LayoutInflater.from(getActivity()).inflate(R.layout.switch_view, curtain_switcher_4, false);
    			return curtain_image_4;
    		}
        }
    	
    	curtain_switcher_1.setFactory(new ViewFactory_1());
    	curtain_switcher_2.setFactory(new ViewFactory_2());
    	curtain_switcher_3.setFactory(new ViewFactory_3());
    	curtain_switcher_4.setFactory(new ViewFactory_4());
    	
    	if(mIpclServer.mPlc.getShadeOpenState_1()) {
    		curtain_switcher_1.setImageResource(imgCurtainOpen);  
    	}
    	else {
    		curtain_switcher_1.setImageResource(imgCurtainClose);  
    	}
    	
    	if(mIpclServer.mPlc.getShadeOpenState_2()) {
    		curtain_switcher_2.setImageResource(imgCurtainOpen);  
    	}
    	else {
    		curtain_switcher_2.setImageResource(imgCurtainClose);  
    	}
    	
    	if(mIpclServer.mPlc.getShadeOpenState_3()) {
    		curtain_switcher_3.setImageResource(imgCurtainOpen);  
    	}
    	else {
    		curtain_switcher_3.setImageResource(imgCurtainClose);  
    	}
    	
    	if(mIpclServer.mPlc.getShadeOpenState_4()) {
    		curtain_switcher_4.setImageResource(imgCurtainOpen);  
    	}
    	else {
    		curtain_switcher_4.setImageResource(imgCurtainClose);  
    	}
    	
    	btnCurtain1Up.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!mIpclServer.mPlc.getShadeOpenState_1()) {
					mIpclServer.mPlc.openShade_1();
					curtain_switcher_1.setImageResource(imgCurtainOpen);
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
					curtain_switcher_1.setImageResource(imgCurtainClose);
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
					curtain_switcher_2.setImageResource(imgCurtainOpen);
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
					curtain_switcher_2.setImageResource(imgCurtainClose);
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
					curtain_switcher_3.setImageResource(imgCurtainOpen);
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
					curtain_switcher_3.setImageResource(imgCurtainClose);
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
					curtain_switcher_4.setImageResource(imgCurtainOpen);
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
					curtain_switcher_4.setImageResource(imgCurtainClose);
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
    	
    	final ImageSwitcher Sunroof_switcher 	= (ImageSwitcher) view.findViewById(R.id.img_sunroof);
    	final ImageSwitcher PC_switcher 		= (ImageSwitcher) view.findViewById(R.id.img_PC);
    	final ImageSwitcher DTV_switcher 		= (ImageSwitcher) view.findViewById(R.id.img_DTV);
    	final ImageSwitcher DVD_switcher 		= (ImageSwitcher) view.findViewById(R.id.img_DVD);
    	
    	class ViewFactorySunroof implements ViewFactory {

    		@Override
    		public View makeView() {
    			// TODO Auto-generated method stub
    			ImageView sunroof_image = (ImageView)LayoutInflater.from(getActivity()).inflate(R.layout.switch_view, Sunroof_switcher, false);
    			return sunroof_image;
    		}
        }
    	
    	class ViewFactoryPC implements ViewFactory {

    		@Override
    		public View makeView() {
    			// TODO Auto-generated method stub
    			ImageView pc_image = (ImageView)LayoutInflater.from(getActivity()).inflate(R.layout.switch_view, PC_switcher, false);
    			return pc_image;
    		}
        }
    	
    	class ViewFactoryDTV implements ViewFactory {

    		@Override
    		public View makeView() {
    			// TODO Auto-generated method stub
    			ImageView dtv_image = (ImageView)LayoutInflater.from(getActivity()).inflate(R.layout.switch_view, DTV_switcher, false);
    			return dtv_image;
    		}
        }
    	
    	class ViewFactoryDVD implements ViewFactory {

    		@Override
    		public View makeView() {
    			// TODO Auto-generated method stub
    			ImageView dvd_image = (ImageView)LayoutInflater.from(getActivity()).inflate(R.layout.switch_view, DVD_switcher, false);
    			return dvd_image;
    		}
        }
    	
    	Sunroof_switcher.setFactory(new ViewFactorySunroof());
    	PC_switcher.setFactory(new ViewFactoryPC());
    	DTV_switcher.setFactory(new ViewFactoryDTV());
    	DVD_switcher.setFactory(new ViewFactoryDVD());
    	
    	mIpclServer.mPlc.openSunroof();
    	
    	if(mIpclServer.mPlc.getSunroofOpenState()) {
    		Sunroof_switcher.setImageResource(imgSunroofOpen);  
    	}
    	else {
    		Sunroof_switcher.setImageResource(imgSunroofClose);  
    	}
    	
    	if(mIpclServer.mPlc.getPCPwrState()) {
    		PC_switcher.setImageResource(imgPCOn);  
    	}
    	else {
    		PC_switcher.setImageResource(imgPCOff);  
    	}
    	
    	if(mIpclServer.mPlc.getSunroofOpenState()) {
    		DTV_switcher.setImageResource(imgDTVOn);  
    	}
    	else {
    		DTV_switcher.setImageResource(imgDTVOff);  
    	}
    	
    	if(mIpclServer.mPlc.getSunroofOpenState()) {
    		DVD_switcher.setImageResource(imgDVDOn);  
    	}
    	else {
    		DVD_switcher.setImageResource(imgDVDOff);  
    	}

//    	if(mIpclServer.mPlc.getShadeOpenState_1()) {
//    		Sunroof_switcher.setImageResource(imgSunroofOpen);  
//    	}
//    	else {
//    		Sunroof_switcher.setImageResource(imgSunroofClose);  
//    	}
//    	
//    	if(mIpclServer.mPlc.getShadeOpenState_2()) {
//    		PC_switcher.setImageResource(imgPCOn);  
//    	}
//    	else {
//    		PC_switcher.setImageResource(imgPCOff);  
//    	}
//    	
//    	if(mIpclServer.mPlc.getShadeOpenState_3()) {
//    		DTV_switcher.setImageResource(imgDTVOn);  
//    	}
//    	else {
//    		DTV_switcher.setImageResource(imgDTVOff);  
//    	}
//    	
//    	if(mIpclServer.mPlc.getShadeOpenState_4()) {
//    		DVD_switcher.setImageResource(imgDVDOn);  
//    	}
//    	else {
//    		DVD_switcher.setImageResource(imgDVDOff);  
//    	}
    	
    	
    	btnSunroofOn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnCurtain1Up.", Toast.LENGTH_SHORT).show();
				Sunroof_switcher.setImageResource(imgSunroofOpen); 
				mIpclServer.mPlc.openSunroof();
			}
		});
    	
    	btnSunroofOff.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnCurtain1Down.", Toast.LENGTH_SHORT).show();
				Sunroof_switcher.setImageResource(imgSunroofClose);  
				mIpclServer.mPlc.closeSunroof();
			}
		});
    	
    	btnPCOn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnCurtain2Up.", Toast.LENGTH_SHORT).show();
				PC_switcher.setImageResource(imgPCOn);  
				mIpclServer.mPlc.setPCPwr(true);
			}
		});
    	
    	btnPCOff.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnCurtain2Down.", Toast.LENGTH_SHORT).show();
				PC_switcher.setImageResource(imgPCOff);  
				mIpclServer.mPlc.setPCPwr(false);
			}
		});
    	
    	btnDTVOn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnCurtain3Up.", Toast.LENGTH_SHORT).show();
				DTV_switcher.setImageResource(imgDTVOn);  
				//if(false != mIpclServer.mAudio.srcIsDTV())
				{
					mIpclServer.mAudio.setAudioSrc(AudioSrc.DTV);;
				}
			}
		});
    	
    	btnDTVOff.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnCurtain3Down.", Toast.LENGTH_SHORT).show();
				DTV_switcher.setImageResource(imgDTVOff); 
				//TO DO
			}
		});
    	
    	btnDVDOn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnCurtain4Up.", Toast.LENGTH_SHORT).show();
				DVD_switcher.setImageResource(imgDVDOn);  
				//if(false != mIpclServer.mAudio.srcIsDVD())
				{
					mIpclServer.mAudio.setAudioSrc(AudioSrc.DVD);;
				}
			}
		});
    	
    	btnDVDOff.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnCurtain4Down.", Toast.LENGTH_SHORT).show();
				DVD_switcher.setImageResource(imgDVDOff);  
				//TO DO
			}
		});
    	
    }

    public void fragmentPage_4(View view) {
    	Button btnDvdHome = (Button) view.findViewById(R.id.btn_dvd_main);
    	Button btnEject = (Button) view.findViewById(R.id.btn_eject);
    	Button btnDvdPlay = (Button) view.findViewById(R.id.btn_dvd_display);
    	Button btnDvdPause = (Button) view.findViewById(R.id.btn_dvd_pause);
    	Button btnFF = (Button) view.findViewById(R.id.btn_forward);
    	Button btnFB = (Button) view.findViewById(R.id.btn_rewind);
    	Button btnNext = (Button) view.findViewById(R.id.btn_next_song);
    	Button btnPrev = (Button) view.findViewById(R.id.btn_last_song);
    	Button btnRepeat = (Button) view.findViewById(R.id.btn_dvd_repeat);
    	Button btnMix = (Button) view.findViewById(R.id.btn_dvd_random);
    	Button btnDVDUp = (Button) view.findViewById(R.id.btn_up);
    	Button btnDVDLeft = (Button) view.findViewById(R.id.btn_left);
    	Button btnDVDRight = (Button) view.findViewById(R.id.btn_right);
    	Button btnDVDDown = (Button) view.findViewById(R.id.btn_down);
    	Button btnEnter = (Button) view.findViewById(R.id.btn_yes);
    	Button btnDVDAudio = (Button) view.findViewById(R.id.btn_sound);
    	Button btnSubtitle = (Button) view.findViewById(R.id.btn_subtitle);
    	Button btnDVD = (Button) view.findViewById(R.id.btn_DVD);
    	Button btnSD = (Button) view.findViewById(R.id.btn_SD);
    	Button btnUSB = (Button) view.findViewById(R.id.btn_USB);


    	
    	btnDvdPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnDvdPlay.", Toast.LENGTH_SHORT).show();
				Log.v("colin", "btnDvdPlay");
				mIpclServer.mDvd.setDVDPlay();
			}
		});
    	
    	btnDvdPause.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnDvdPause.", Toast.LENGTH_SHORT).show();
				mIpclServer.mDvd.setDVDPause();
			}
		});
    	
    	btnEject.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnEject.", Toast.LENGTH_SHORT).show();
				mIpclServer.mDvd.setDVDEject();
			}
		});
    	
    	btnFF.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnFF.", Toast.LENGTH_SHORT).show();
				mIpclServer.mDvd.setDVDFF();
			}
		});
    	
    	btnFB.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnFB.", Toast.LENGTH_SHORT).show();
				mIpclServer.mDvd.setDVDFB();
			}
		});
    	
    	btnNext.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnNext.", Toast.LENGTH_SHORT).show();
				mIpclServer.mDvd.setDVDNext();
			}
		});
    	
    	btnPrev.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnPrev.", Toast.LENGTH_SHORT).show();
				mIpclServer.mDvd.setDVDPrev();
			}
		});
    	
    	btnRepeat.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnRepeat.", Toast.LENGTH_SHORT).show();
				mIpclServer.mDvd.setDVDRpt();
			}
		});
    	
    	btnMix.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnMix.", Toast.LENGTH_SHORT).show();
				mIpclServer.mDvd.setDVDMix();
			}
		});
    	
    	btnDVDUp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnDVDUp.", Toast.LENGTH_SHORT).show();
				mIpclServer.mDvd.setDVDUp();
			}
		});
    	
    	btnDVDLeft.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnDVDLeft.", Toast.LENGTH_SHORT).show();
				mIpclServer.mDvd.setDVDLeft();
			}
		});
    	
    	btnDVDRight.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnDVDRight.", Toast.LENGTH_SHORT).show();
				mIpclServer.mDvd.setDVDRight();
			}
		});
    	
    	btnDVDDown.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnDVDDown.", Toast.LENGTH_SHORT).show();
				mIpclServer.mDvd.setDVDDn();
			}
		});
    	
    	btnEnter.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnEnter.", Toast.LENGTH_SHORT).show();
				mIpclServer.mDvd.setDVDEnter();
			}
		});
    	
    	btnDvdHome.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnDvdHome.", Toast.LENGTH_SHORT).show();
				mIpclServer.mDvd.setDVDMenu();
			}
		});
    	
    	btnDVDAudio.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnDVDAudio.", Toast.LENGTH_SHORT).show();
				mIpclServer.mDvd.setDVDAudio();
			}
		});
    	
    	btnSubtitle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnSubtitle.", Toast.LENGTH_SHORT).show();
				mIpclServer.mDvd.setDVDSubTitle();
			}
		});
    	
    	btnDVD.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnDVD.", Toast.LENGTH_SHORT).show();
				Log.v("colin", "btnDVD");
				mIpclServer.mDvd.setDVDPlay();
			}
		});

		btnSD.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnSD.", Toast.LENGTH_SHORT).show();
				Log.v("colin", "btnSD");
				mIpclServer.mDvd.setDVDPlay();
			}
		});

		btnUSB.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "btnUSB.", Toast.LENGTH_SHORT).show();
				Log.v("colin", "btnUSB");
				mIpclServer.mDvd.setDVDPlay();
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
	
	private void sync_status( int page) {
		switch(page)
		{
		case 0:
			
		case 1:
			
		case 2:
			
		case 3:
			
		case 4:
			
		default:
			Log.e("colin", "sync status: wrong page num.");
			break;
		
		}
	}
	    

}
