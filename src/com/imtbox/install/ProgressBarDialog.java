package com.imtbox.install;



import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Switch;
import android.widget.TextView;

public class ProgressBarDialog extends Activity {

	private String TAG = "ProgressBarDialog";
	private TextView status;
	private AutoInstallUtility mAutoInstallUtility;
	
 	public static final String KEY_AUTO_INSTALLED = "auto_installed";
 	public static final boolean INSTALLED = true;
 	public static final boolean NO_INSTALLED = false;
	public static final String KEY_APK_PATH = "key_apk_path";
	
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
            String value = (String)msg.obj;
            if(value.equals("finished"))
    			finish();
    		else {
    			status.setText(value);
    		}
		}
		
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 
		setContentView(R.layout.warning_main);
		
		mAutoInstallUtility = AutoInstallUtility.getAutoInstallUtility();
        status = (TextView)findViewById(R.id.status);
        
		
		if(null != getIntent()){
			String apk_path = getIntent().getStringExtra(KEY_APK_PATH);
			if(null != apk_path && !getInstalled()){
				Log.d(TAG, "apk_path." + apk_path);
				mAutoInstallUtility.scanPreInstallApkPacket(apk_path, this,mHandler);
			}else {
				finish();
			}
		}
	}

	public boolean getInstalled(){
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);        	
        return preferences.getBoolean(KEY_AUTO_INSTALLED, NO_INSTALLED);
    }

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
 
	 
}
