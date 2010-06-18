package edu.nps.jody.GappyAndroidActivity;

import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;
 
public class SmsReceiver extends BroadcastReceiver
{
	//String componentName;
	//String path;
	
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
                str += "SMS from " + msgs[i].getOriginatingAddress();                     
                str += " :";
                str += msgs[i].getMessageBody().toString();
                str += "\n";      
                
                SharedPreferences pref = context.getSharedPreferences("preferenceFile", 0);
                
                try
                {
                SMS_Manager.processSMS(msgs[i].getOriginatingAddress() + " " + msgs[i].getMessageBody(), 4, pref.getString("FILE_PATH", "/"));
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
