package edu.nps.jody.GappyAndroidActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class GappyAndroidActivity extends TabActivity 
{
	//TODO Add actual version number to package
	/*FIXME Need to ensure that "Go" Button functionality is not completely wrecked after moving FIleBrowser out to its own Activity
	 * 				- Go Button does not verify existence of directory for path
	 * 				- Go Button on FileViewer is updating FieView with pseudo path info vice showing file
	 * 				- Bad path in pathView causes FileBrowser to crash
	 * 				- Need general purpose path correctness check, that's gonna suck a bit
	 */
	//TODO Read finals from res files vice hard coding in
	
	//Data Members
		//Gappy Android Data Members
		private final 	String 				PATH 			= "PATH";//getString(R.string.path);
		private			 	String				filePath 			= "/";
		private 				EditText 			path;
		private 				String				fileName 		= "";
		private				EditText			file;
		private				String 				fileContents 	= "";
		private				TextView		fileView;
		private				Spinner			maxGapSpinner;
		private				Spinner			featureTypeSpinner;
		private				ToggleButton smsReceiver;
		private				IntentFilter		smsIntentFilter;
		private	final 	String 				FILE_PATH 						= FileBrowser.FILE_PATH;//"FILE_PATH";// getString(R.string.file_path);//"FILE_PATH";
		private	final 	int 					GET_NEW_PATH 			= 0;//R.raw.get_new_path; //=0;
		private	final 	int 					GET_VIEW_FILE 			= 1;//R.raw.get_view_file;
		private	final 	String 				FILE_OPEN 						= FileBrowser.FILE_OPEN;//"FILE_OPEN"; // getString(R.string.file_open);
		//private	final 	String				FILE_CONTENT = "FILE_CONTENT"; //getString(R.string.file_content);
		private	final 	String				FILE_ABSOLUTE_PATH	= FileBrowser.FILE_ABSOLUTE_PATH;//"FILE_ABSOLUTE_PATH";
		private final	String				MAX_GAP 						= "MAX_GAP";
		private final	String				FEATURE_TYPE			 	= "FEATURE TYPE";
		private final 	boolean		RETURN_FILE					= true;
		private final	boolean		RETURN_DIR					= false;
		
		//Preference File Data Members
		private final 	String 				PREF_FILE 	= "PREF_FILE";//getString(R.string.pref_file);
		private				SharedPreferences 				pref;
		private				SharedPreferences.Editor 	editor;
		
		//Goofy test setup stuff for embedded receiver class
		int maxGap;
		int featureType;
		
	//Constructors
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	pref = getSharedPreferences(PREF_FILE, 0);
    	editor = pref.edit();
    	
        super.onCreate(savedInstanceState);
        
        filePath = pref.getString(FILE_PATH, "/");
        maxGap = pref.getInt(MAX_GAP, 4);
        featureType = pref.getInt(FEATURE_TYPE, 0);
        
        smsIntentFilter = new IntentFilter();
        smsIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        
        mainView();
    }
            
    //Methods
    private void mainView()
    {
    		setContentView(R.layout.main);
    		
    		//Create the tabs at the top of our "home view"
    		TabHost myTabHost = getTabHost();
    		
    		//Set up configuration tab
    		TabSpec myTabSpecConfig = myTabHost.newTabSpec("configTabSpec");//.setIndicator("Configure").setContent(R.id.config_table);
    		myTabSpecConfig.setIndicator("Configure", getResources().getDrawable(R.drawable.ic_menu_preferences));
    		myTabSpecConfig.setContent(R.id.config_table);
    		myTabHost.addTab(myTabSpecConfig);
    		
    		//Set up file viewer tab
    		TabSpec myTabSpecFIleViewer = myTabHost.newTabSpec("fileViewerTabSpec");//.setIndicator("Configure").setContent(R.id.config_table);
    		myTabSpecFIleViewer.setIndicator("File Viewer", getResources().getDrawable(R.drawable.ic_menu_view));
    		myTabSpecFIleViewer.setContent(R.id.file_viewer);
    		myTabHost.addTab(myTabSpecFIleViewer);
    		
    		//Set up Help/About tab
    		TabSpec myTabSpecHelpAbout = myTabHost.newTabSpec("helpAboutTabSpec");//.setIndicator("Configure").setContent(R.id.config_table);
    		myTabSpecHelpAbout.setIndicator("Help-About", getResources().getDrawable(R.drawable.ic_menu_info_details));
    		myTabSpecHelpAbout.setContent(R.id.help_about);
    		myTabHost.addTab(myTabSpecHelpAbout);
    		
    		myTabHost.setCurrentTab(0);
    		
    		//Create items to display in config tab
    							smsReceiver = (ToggleButton)findViewById(R.id.sms_receiver_toggle);
    		
					path		= (EditText)findViewById(R.id.path);
			Button		pathGo		= (Button)findViewById(R.id.path_go);
			Button		pathBrowse	= (Button)findViewById(R.id.path_browse);
			
			maxGapSpinner = (Spinner)findViewById(R.id.max_gap_spinner);
			
			featureTypeSpinner = (Spinner)findViewById(R.id.feature_type_spinner);
			
			smsReceiver.setOnClickListener(onSMSReceiverClicked);
			
			path.setText(filePath);
			pathGo.setOnClickListener(onPathGoClick);
			pathBrowse.setOnClickListener(onPathBrowse);
			
			maxGapSpinner.setOnItemSelectedListener(onMaxGapSpinnerItemSelected);
			
			featureTypeSpinner.setOnItemSelectedListener(onFeatureTypeSpinnerItemSelected);
			
			//Create items to display in fileviewer tab
					file		= (EditText)findViewById(R.id.file);
			Button		fileGo		= (Button)findViewById(R.id.file_go);
			Button		fileBrowse	= (Button)findViewById(R.id.file_browse);
			
					fileView	= (TextView)findViewById(R.id.file_view);
						
			file.setText(fileName);
			fileGo.setOnClickListener(onFileGoClick);
			fileBrowse.setOnClickListener(onFileBrowse);
			
			fileView.setText(fileContents);
			
			//Create items to display in help-about tab
			ImageView helpIcon = (ImageView)findViewById(R.id.help_icon);
			helpIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_info_details));
			
			TextView helpAboutLabel = (TextView)findViewById(R.id.help_label);
			helpAboutLabel.setText("General Help");
			
			TextView helpAboutText = (TextView)findViewById(R.id.help_about_text);
			
			//Read in the text file (should consolidate this with the fileViewer read method
			String helpText = "";
			String nextLine = "";
			//This is a cheap placeholder until I get an interactive help functioning correctly with javadoc
			try 
			{
				FileReader helpFileReader = new FileReader("README");
				BufferedReader helpBufferedReader = new BufferedReader(helpFileReader);
				try
				{
					while ((nextLine = helpBufferedReader.readLine()) != null)
					{
						helpText.concat(nextLine);
					}
					
				}
				catch (IOException ioe)
				{
					Toast.makeText(this, "IO just failed me.", Toast.LENGTH_LONG);
				}
			} 
			catch (FileNotFoundException e) 
			{
				Toast.makeText(this, "Okay, who deleted the help file?", Toast.LENGTH_LONG);
			}
			
			helpAboutText.setText(helpText);
			
    }
    
    private Spinner.OnItemSelectedListener onFeatureTypeSpinnerItemSelected = new Spinner.OnItemSelectedListener()
    {

		public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
		{
			//Get the String value of the item selected in the spinner
			
			//Put this into preferences to be read by other classes for processing SMS messages
				featureType = position;
				editor.putInt(FEATURE_TYPE, position);
				editor.commit();
		}


		public void onNothingSelected(AdapterView<?> arg0) 
		{
			// Do nothing
			return;
		}
    	
    };
    
    private Spinner.OnItemSelectedListener onMaxGapSpinnerItemSelected = new Spinner.OnItemSelectedListener()
    {
		public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
		{
			//Get the string value out of the item selected in the spinner
			String selected = (String)maxGapSpinner.getItemAtPosition(position);
			if (selected.equalsIgnoreCase(""))
			{
				onNothingSelected(parentView);
			}
			else
			{
				//int spinnerSelection = Integer.getInteger(selected);
				int spinnerSelection;
				
				spinnerSelection = Integer.parseInt(selected);
				
				//Put the integer represented by the String gotten above into the prefs file as an integer
				maxGap = spinnerSelection;
				editor.putInt(MAX_GAP, spinnerSelection);
				editor.commit();
			}
			
		}

		public void onNothingSelected(AdapterView<?> parentView) {
			// Do nothing
			return;
		}
    	
    };
    
    private ToggleButton.OnClickListener onSMSReceiverClicked = new ToggleButton.OnClickListener()
    {

		public void onClick(View v) 
		{
			if(smsReceiver.isChecked())
			//if (smsReceiver.isEnabled())
			{
				registerReceiver(SMSBroadcastReceiver, smsIntentFilter);
			}
			else
			{
				unregisterReceiver(SMSBroadcastReceiver);
			}
			
		}
    	
    };
    
    private Button.OnClickListener onPathBrowse = new Button.OnClickListener()
    {

		public void onClick(View view) 
		{
			//Launch FileBrowser as another view vice a new Activity, pass parameter to only allow directories
			filePath = path.getText().toString();
			editor.putString(PATH, filePath);
			editor.commit();
			//fileName = file.getText().toString();
			hideKeyboard(view);
			//guiBrowse(false);
			sendToFileBrowser(RETURN_DIR, filePath);
		}
    	
    };
    
    private Button.OnClickListener onFileBrowse = new Button.OnClickListener()
    {

		public void onClick(View view) 
		{
			//Launch FileBrowser as another view vice a new Activity, pass parameter to only allow files
			hideKeyboard(view);
			
			//Want to open a file to view = true
			sendToFileBrowser(RETURN_FILE, filePath);
		}
    	
    };
 
    private Button.OnClickListener onPathGoClick = new Button.OnClickListener()
    {
		public void onClick(View v) 
		{	
			String tempFilePath = path.getText().toString();
			/*editor.putString(PATH, filePath);
			editor.commit();
			fileName = file.getText().toString();*/
			File tempFile = new File(tempFilePath);
			
			hideKeyboard(path);
            
			//fileView.setText(filePath + "/" + fileName);
			if (tempFile.isDirectory())
			{
				filePath = tempFilePath;
				editor.putString(PATH, filePath);
				editor.commit();
			}
			else
			{
				path.setText(filePath);
				Toast.makeText(getBaseContext(), "That is not a directory", Toast.LENGTH_LONG);
			}
		}
    	
    };
    
    private Button.OnClickListener onFileGoClick = new Button.OnClickListener()
    {
		public void onClick(View v) 
		{
			//String tempfilePath = path.getText().toString();
			//editor.putString(PATH, filePath);
			//editor.commit();
			String tempfileName = file.getText().toString();
			
			hideKeyboard(file);

			//fileView.setText(filePath + "/" + fileName);
			
			String currentLine ="";
			
			File tempFile = new File(tempfileName);//new File(tempfilePath + "/" + tempfileName);
			
			if (tempFile.isDirectory())
			{
				//filePath = tempfilePath;
				//editor.putString(PATH, filePath);
				//editor.commit();
				
				sendToFileBrowser(RETURN_FILE, tempfileName);
				return;
			}
			else
			{
				if (tempFile.canRead())
				{
					//filePath = path.getText().toString();
					//editor.putString(PATH, filePath);
					//editor.commit();
					fileName = file.getText().toString();
					
					//File[] files = tempFile.listFiles();
					//currentDirectory = tempFile;
					//browsePath.setText(currentDirectory.getAbsolutePath());
					
					FileReader fileReader;
					try 
					{
						fileReader = new FileReader(tempFile);
						
						BufferedReader bufferedReader = new BufferedReader(fileReader);
						
						fileContents="";
						
						while ((currentLine = bufferedReader.readLine()) != null)
						{
							fileContents = fileContents.concat(currentLine + "\n");
						}
						
						fileView.setText(fileContents);
					}
					catch (FileNotFoundException e) 
					{
						Toast.makeText(getBaseContext(), "File does not exist", Toast.LENGTH_LONG);
					} catch (IOException e) {
						Toast.makeText(getBaseContext(), "I/O Error.  Please try later.", Toast.LENGTH_LONG);
					}
					
					
					/*if (files == null)
					{
						listView.setAdapter(new ArrayAdapter<String>(this, R.layout.file_row, dropPath(files)));
					}
					else
					{
						listView.setAdapter(new ArrayAdapter<String>(this, R.layout.file_row, dropPath(files)));
					}*/
				}
				else
				{
					Toast.makeText(getBaseContext(), "Permission Denied", Toast.LENGTH_LONG);
					
					/*OnClickListener fileButtonListener = new OnClickListener()
					{
						//@Override
						public void onClick(DialogInterface arg0, int arg1) 
						{
							//Do nothing
						}
					};
					
					new AlertDialog.Builder(this)
						.setTitle("Security Notice")
						.setMessage("Permission Denied")
						.setPositiveButton("OK", fileButtonListener)
						.show();*/
				}

			}
			/*else
			{	
					
						FileReader fileReader = new FileReader(tempFile);
						BufferedReader bufferedReader = new BufferedReader(fileReader);
						
						fileContents="";
						
						while ((currentLine = bufferedReader.readLine()) != null)
						{
							fileContents = fileContents.concat(currentLine + "\n");
						}
			}*/
					
		}
    	
    };
    
	private void hideKeyboard(View view)
	{
		/*
		 * Below hide-keyboard code copied from
		 * http://stackoverflow.com/questions/1109022/how-to-close-hide-the-android-soft-keyboard
		 */
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        //End copy from stackoverflow
	}
	
	private void sendToFileBrowser(boolean openTheFile, String startFilePath)
	{
		Intent		startFileBrowserIntent = new Intent(GappyAndroidActivity.this, FileBrowser.class);
		
		Bundle startFileBrowserBundle = new Bundle();
		
		startFileBrowserBundle.putString(FILE_PATH, startFilePath);
		
		startFileBrowserBundle.putBoolean(FILE_OPEN, openTheFile);
		
		startFileBrowserIntent.putExtras(startFileBrowserBundle);
		
		if (openTheFile)
		{
			startActivityForResult(startFileBrowserIntent, GET_VIEW_FILE);
		}
		else
		{
			startActivityForResult(startFileBrowserIntent, GET_NEW_PATH);
		}
	}
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
	    // See which child activity is calling us back.
	    switch (requestCode) {
	        case GET_NEW_PATH:

	            if (resultCode == RESULT_CANCELED){
	                //Do nothing
	            } 
	            else 
	            {
	                Bundle result = data.getExtras();
	                
	                filePath = result.getString(FILE_PATH);
	                path.setText(filePath);
	                
	                //Update preference file with new path
	    			editor.putString(PATH, filePath);
	    			editor.commit();
	            }
	            break;
	            
	        case GET_VIEW_FILE:
	            if (resultCode == RESULT_CANCELED){
	                //Do nothing
	            } 
	            else {
	                Bundle result = data.getExtras();
	                
	               // fileContents = result.getString(FILE_CONTENT);
	               File tempFile = new File(result.getString(FILE_ABSOLUTE_PATH));
	               String currentLine;
	                try 
					{
						FileReader fileReader = new FileReader(tempFile);
						BufferedReader bufferedReader = new BufferedReader(fileReader);
						
						fileContents="";
						
						while ((currentLine = bufferedReader.readLine()) != null)
						{
							fileContents = fileContents.concat(currentLine + "\n");
						}
					}
						catch (FileNotFoundException e) 
						{
							OnClickListener fileButtonListener = new OnClickListener()
							{
								//@Override
								public void onClick(DialogInterface arg0, int arg1) 
								{
									//Do nothing
								}
							};
							
							new AlertDialog.Builder(this)
							.setTitle("Error!")
							.setMessage("FIle Not Found")
							.setPositiveButton("OK", fileButtonListener)
							.show();
						}
						catch (IOException i)
						{
							OnClickListener fileButtonListener = new OnClickListener()
							{
								//@Override
								public void onClick(DialogInterface arg0, int arg1) 
								{
									//Do nothing
								}
							};
							
							new AlertDialog.Builder(this)
							.setTitle("Error!")
							.setMessage("IO Error")
							.setPositiveButton("OK", fileButtonListener)
							.show();
						}
										
	                fileView.setText(fileContents);

	                fileName = result.getString(FILE_ABSOLUTE_PATH);
	                
	                file.setText(fileName);
	            }
	       
	        	default://TODO Make sure we using this default correctly, I think it's window dressing right now
	        		break;
	    }
	}
	
	private BroadcastReceiver SMSBroadcastReceiver = new BroadcastReceiver()
	{

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
	                	SMS_Manager.processSMS(str, maxGap, filePath, featureType);
	                }
	                catch (IOException e)
	                {
	                	Toast.makeText(context, "Cannot write file to current directory.", Toast.LENGTH_LONG).show();
	                }
	            
	           //componentName =  intent.getComponent().toShortString();
	            //---display the new SMS message---
	            //Toast.makeText(context, componentName, Toast.LENGTH_LONG).show();
	            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	    	   }
	        }                         
	    }
		
	};
	
}