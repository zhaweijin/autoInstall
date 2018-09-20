package com.imtbox.install;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;

import android.R.menu;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

public class AutoInstallUtility{
	//
	public static final String TAG = "AutoInstallUtility";
	public static final boolean DEBUG = false;
	
	private File[] mFiles;
	private Thread mInstallApkThread;
	private static AutoInstallUtility mAutoInstallUtility;
	
	public static AutoInstallUtility getAutoInstallUtility(){
		if(null == mAutoInstallUtility){
			mAutoInstallUtility = new AutoInstallUtility();
		}
		return mAutoInstallUtility;
	}
	
	private AutoInstallUtility() {
		;
	}
	
    private String installApkCmd(String apkAbsolutePath){    	
    	String[] args = { "pm", "install", "-r", apkAbsolutePath };  
    	String result = null;  
    	ProcessBuilder processBuilder = new ProcessBuilder(args);  
    	Process process = null;  
    	InputStream errIs = null;  
    	InputStream inIs = null;  
    	try {  
    	    ByteArrayOutputStream baos = new ByteArrayOutputStream();  
    	    int read = -1;  
    	    process = processBuilder.start();  
    	    errIs = process.getErrorStream();  
    	    while ((read = errIs.read()) != -1) {  
    	        baos.write(read);  
    	    }  
    	    baos.write('\n');  
    	    inIs = process.getInputStream();  
    	    while ((read = inIs.read()) != -1) {  
    	        baos.write(read);  
    	    }  
    	    byte[] data = baos.toByteArray();  
    	    result = new String(data);  
    	} catch (IOException e) {  
    	    e.printStackTrace();  
    	} catch (Exception e) {  
    	    e.printStackTrace();  
    	} finally {  
    	    try {  
    	        if (errIs != null) {  
    	            errIs.close();  
    	        }  
    	        if (inIs != null) {  
    	            inIs.close();  
    	        }  
    	    } catch (IOException e) {  
    	        e.printStackTrace();  
    	    }  
    	    if (process != null) {  
    	        process.destroy();  
    	    }  
    	}  
    	return result;
    }
    
    public void scanPreInstallApkPacket(String apkAbsolutePath,final Activity activity,final Handler mHandler){
 		File dir = new File(apkAbsolutePath);
 
 		if(null != dir){
 			mFiles = dir.listFiles(new FileFilter() {			
    			@Override
    			public boolean accept(File file) { 
    				return (file.isDirectory() || file.getName().matches("^.*?\\.(apk)$"));
    			}
    		});
 			if(null != mFiles){ 				
 				mInstallApkThread = new Thread(new Runnable() {					
					@Override
					public void run() {						
						int ilen = mFiles.length;
						String apkAbsolutePath = null;
						String result = null;
						boolean isDir= false;						
						for(int i = 0; i < ilen; i++){
							Message msg = new Message();
							msg.obj = (i+1)+"/"+ilen;
							mHandler.sendMessage(msg);
							apkAbsolutePath = mFiles[i].getAbsolutePath();
							isDir = mFiles[i].isDirectory();
							if(null == apkAbsolutePath || isDir) continue;							
							result = installApkCmd(apkAbsolutePath);
							if(DEBUG) Log.d(TAG, "" + i + "." + apkAbsolutePath +
									" result." + result);							
						}
						setInstalled(activity,true);
						Message msg = new Message();
						msg.obj = "finished";
						mHandler.sendMessage(msg);
						if(null != mFiles) mFiles = null;
						if(null != mInstallApkThread) mInstallApkThread = null;
					}
				});
 				mInstallApkThread.start();
 			}
 		}		
 	} 	


    
 	public void scanPreInstallApkPacket(String apkAbsolutePath, final AutoInstallService service){
 		File dir = new File(apkAbsolutePath);
 		if(null != dir){
 			mFiles = dir.listFiles(new FileFilter() {			
    			@Override
    			public boolean accept(File file) { 
    				return (file.isDirectory() || file.getName().matches("^.*?\\.(apk)$"));
    			}
    		});
 			if(null != mFiles){ 				
 				mInstallApkThread = new Thread(new Runnable() {					
					@Override
					public void run() {						
						int ilen = mFiles.length;
						String apkAbsolutePath = null;
						String result = null;
						boolean isDir= false;						
						for(int i = 0; i < ilen; i++){
							apkAbsolutePath = mFiles[i].getAbsolutePath();
							isDir = mFiles[i].isDirectory();
							if(null == apkAbsolutePath || isDir) continue;							
							result = installApkCmd(apkAbsolutePath);
							if(DEBUG) Log.d(TAG, "" + i + "." + apkAbsolutePath +
									" result." + result);							
						}
						service.setInstalled(true);
						if(null != mFiles) mFiles = null;
						if(null != mInstallApkThread) mInstallApkThread = null;
					}
				});
 				mInstallApkThread.start();
 			}
 		}		
 	} 	
 	

 	
    public void setInstalled(Activity activity,boolean installed){
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);        	
       	SharedPreferences.Editor editor = preferences.edit();
       	editor.putBoolean(AutoInstallService.KEY_AUTO_INSTALLED, installed);
		editor.apply();
    }
}