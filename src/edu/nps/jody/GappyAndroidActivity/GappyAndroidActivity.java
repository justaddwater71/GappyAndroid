package edu.nps.jody.GappyAndroidActivity;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
	//FIXME Make OSB/GB selector actually DO something
	//FIXME Make the MaxGap selector actually DO something
	
	//Data Members
		//Gappy Android Data Members
		private final 	String 		PATH 			= "PATH";//getString(R.string.path);
		private			 	String		filePath 			= "/";
		private 				EditText 	path;
		private 				String		fileName 		= "";
		private				EditText	file;
		private				String 		fileContents 	= "";
		private				TextView fileView;
		private				Spinner	maxGapSpinner;
		private				Spinner	featureTypeSpinner;
		private	final 	String 		FILE_PATH = "FILE_PATH";// getString(R.string.file_path);//"FILE_PATH";
		private	final 	int 			GET_NEW_PATH = 0;//R.raw.get_new_path; //=0;
		private	final 	int 			GET_VIEW_FILE = 1;//R.raw.get_view_file;
		private	final 	String 		FILE_OPEN ="FILE_OPEN"; // getString(R.string.file_open);
		private	final 	String		FILE_CONTENT = "FILE_CONTENT"; //getString(R.string.file_content);
		private	final 	String		FILE_ABSOLUTE_PATH = "FILE_ABSOLUTE_PATH";
		private final	String		MAX_GAP = "MAX_GAP";
		private final	String		FEATURE_TYPE = "FEATURE TYPE";
		
		//Preference File Data Members
		private final 	String 		PREF_FILE 	= "PREF_FILE";//getString(R.string.pref_file);
		private				SharedPreferences 				pref;
		private				SharedPreferences.Editor 	editor;
		
	//Constructors
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	pref = getSharedPreferences(PREF_FILE, 0);
    	editor = pref.edit();
    	
        super.onCreate(savedInstanceState);
        
        filePath = pref.getString(FILE_PATH, "/");
        
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
    		
    		ToggleButton smsReceiver = (ToggleButton)findViewById(R.id.sms_receiver_toggle);
    		
					path		= (EditText)findViewById(R.id.path);
			Button		pathGo		= (Button)findViewById(R.id.path_go);
			Button		pathBrowse	= (Button)findViewById(R.id.path_browse);
			
					file		= (EditText)findViewById(R.id.file);
			Button		fileGo		= (Button)findViewById(R.id.file_go);
			Button		fileBrowse	= (Button)findViewById(R.id.file_browse);
			
					fileView	= (TextView)findViewById(R.id.file_view);
			
					maxGapSpinner = (Spinner)findViewById(R.id.max_gap_spinner);
					
					featureTypeSpinner = (Spinner)findViewById(R.id.feature_type_spinner);
					
			smsReceiver.setOnClickListener(onSMSReceiverClicked);
					
			path.setText(filePath);
			pathGo.setOnClickListener(onPathGoClick);
			pathBrowse.setOnClickListener(onPathBrowse);
			
			file.setText(fileName);
			fileGo.setOnClickListener(onFileGoClick);
			fileBrowse.setOnClickListener(onFileBrowse);
			
			fileView.setText(fileContents);
			
			maxGapSpinner.setOnItemSelectedListener(onMaxGapSpinnerItemSelected);
			
			featureTypeSpinner.setOnItemSelectedListener(onFeatureTypeSpinnerItemSelected);
    }
    
    private Spinner.OnItemSelectedListener onFeatureTypeSpinnerItemSelected = new Spinner.OnItemSelectedListener()
    {

		public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
		{
			//Get the String value of the item selected in the spinner
			
			//Put this into preferences to be read by other classes for processing SMS messages
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
				editor.putInt(MAX_GAP, spinnerSelection);
				editor.commit();
			}
			
		}

		public void onNothingSelected(AdapterView<?> parentView) {
			// FIXME Should this just be a //Do nothing ?
			return;
		}
    	
    };
    
    private ToggleButton.OnClickListener onSMSReceiverClicked = new ToggleButton.OnClickListener()
    {

		public void onClick(View v) 
		{
			// TODO Come up with a miracle to enable and disable a BroadcastReceiver
			
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
			sendToFileBrowser(false);
		}
    	
    };
    
    private Button.OnClickListener onFileBrowse = new Button.OnClickListener()
    {

		public void onClick(View view) 
		{
			//Launch FileBrowser as another view vice a new Activity, pass parameter to only allow files
			hideKeyboard(view);
			
			//Want to open a file to view = true
			sendToFileBrowser(true);
		}
    	
    };
 
    private Button.OnClickListener onPathGoClick = new Button.OnClickListener()
    {
		public void onClick(View v) 
		{	
			filePath = path.getText().toString();
			editor.putString(PATH, filePath);
			editor.commit();
			fileName = file.getText().toString();
			
			hideKeyboard(path);
            
			fileView.setText(filePath + "/" + fileName);
		}
    	
    };
    
    private Button.OnClickListener onFileGoClick = new Button.OnClickListener()
    {
		public void onClick(View v) 
		{
			filePath = path.getText().toString();
			editor.putString(PATH, filePath);
			editor.commit();
			fileName = file.getText().toString();
			
			hideKeyboard(file);

			fileView.setText(filePath + "/" + fileName);
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
	
	private void sendToFileBrowser(boolean openTheFile)
	{
		
		Intent		startFileBrowserIntent = new Intent(GappyAndroidActivity.this, FileBrowser.class);
		
		Bundle startFileBrowserBundle = new Bundle();
		
		startFileBrowserBundle.putString(FILE_PATH, filePath);
		
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
	                
	                fileContents = result.getString(FILE_CONTENT);
	                
	                fileView.setText(fileContents);

	                fileName = result.getString(FILE_ABSOLUTE_PATH);
	                
	                file.setText(fileName);
	            }
	        default://TODO Make sure we using this default correctly, I think it's window dressing right now
	            break;
	    }
	}
	
}