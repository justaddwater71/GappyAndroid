package edu.nps.jody.GappyAndroidActivity;

import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
//import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;
 
public class SmsReceiver extends BroadcastReceiver
{
	String path = "/sdcard";
	int featureType = 0;
	int maxGap = 4;
	
    @Override
    public void onReceive(Context context, Intent intent) 
    {
        //---get the SMS message passed in---
        Bundle bundle = intent.getExtras();        
        SmsMessage[] msgs = null;
        String str = "";            
       if (bundle != null)
        {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];            
            for (int i=0; i<msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);                
                str += msgs[i].getOriginatingAddress();                     
                str += " ";
                str += msgs[i].getMessageBody().toString();
                str += "\n";      
                
                //SharedPreferences pref = context.getSharedPreferences("PREF_FILE", 0);
                //String path = pref.getString("FILE_PATH", "/sdcard");
                //int featureType = pref.getInt("FEATURE_TYPE", FeatureMaker.FEATURE_OSB);
                //int maxGap = pref.getInt("MAX_GAP", 4);
                
                try
                {
                	SMS_Manager.processSMS(str, maxGap, path, featureType);
                }
                catch (IOException e)
                {
                	Toast.makeText(context, "That sucked", Toast.LENGTH_LONG).show();
                }
            
           //componentName =  intent.getComponent().toShortString();
            //---display the new SMS message---
            //Toast.makeText(context, componentName, Toast.LENGTH_LONG).show();
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    	   }
        }                         
    }
    
/*	public String getPath()
	{
		return path;
	}*/
}
