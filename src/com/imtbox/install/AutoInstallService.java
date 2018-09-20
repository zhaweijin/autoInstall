package com.imtbox.install;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class AutoInstallService extends Service {
	public static final String TAG = "AutoInstallService";
	public static final boolean DEBUG = false;
	
	public static final String KEY_APK_PATH = "key_apk_path";
	
	private AutoInstallUtility mAutoInstallUtility;
	private int index;
	private Context mContext;
 	public static final String KEY_AUTO_INSTALLED = "auto_installed";
 	public static final boolean INSTALLED = true;
 	public static final boolean NO_INSTALLED = false;
	
	// Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    
   /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
    	AutoInstallService getService() {
            // Return this instance of LocalService so clients can call public methods
            return AutoInstallService.this;
        }
    }
    
    @Override
    public IBinder onBind(Intent intent) {
    	if(DEBUG) Log.d(TAG, "onBind " + this);
        return mBinder;
    }
            
    @Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mContext = this;
		mAutoInstallUtility = AutoInstallUtility.getAutoInstallUtility();
		if(DEBUG) Log.d(TAG, "onCreate " + this);
	}
    
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(DEBUG) Log.d(TAG, "onDestroy " + this);
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		if(DEBUG) Log.d(TAG, "onStart " + this);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if(DEBUG) Log.d(TAG, "onStartCommand " + this);
		if(null != intent){
			String apk_path = intent.getStringExtra(KEY_APK_PATH);
			if(null != apk_path && !getInstalled()){
				if(DEBUG) Log.d(TAG, "apk_path." + apk_path);
				mAutoInstallUtility.scanPreInstallApkPacket(apk_path, this);
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		if(DEBUG) Log.d(TAG, "onUnbind " + this);
		return super.onUnbind(intent);
	}
	
	public boolean getInstalled(){
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);        	
        return preferences.getBoolean(KEY_AUTO_INSTALLED, NO_INSTALLED);
    }
    
    public void setInstalled(boolean installed){
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);        	
       	SharedPreferences.Editor editor = preferences.edit();
       	editor.putBoolean(KEY_AUTO_INSTALLED, installed);
		editor.apply();
    }
}