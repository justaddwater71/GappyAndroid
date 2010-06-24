package edu.nps.jody.GappyAndroidActivity;

//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
//import android.content.DialogInterface;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
//import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
//import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
//import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.TabSpec;

public class GappyAndroidActivity extends TabActivity 
{
	//Data Members
		//Config file
		private final String PREF_FILE 	= getString(R.string.pref_file);
		private final String PATH 			= getString(R.string.path);
		
		//GappyAndroid
		private			 String		 filePath 			= "/";
		
		//PATH, filePath;
		private 			EditText 	path;
		
		private 			String		 fileName 		= "";
		private			EditText	 file;
		
		private			String 		fileContents 	= "";
		private			TextView fileView;
		
		//public static final String ACTION_UPDATE_PATH = "edu.nps.jody.intent.custom.ACTION_UPDATE_PATH";
		private			final String 	FILE_PATH = getString(R.string.file_path);//"FILE_PATH";
		private			final int 		GET_NEW_PATH = R.raw.get_new_path; //=0;
		private			final int 		GET_VIEW_FILE = R.raw.get_view_file;
		private			final String 	FILE_OPEN = getString(R.string.file_open);
		private			SharedPreferences pref;
		private			SharedPreferences.Editor editor;
		
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
    		
    		TabHost myTabHost = getTabHost();
    		
    		TabSpec myTabSpecConfig = myTabHost.newTabSpec("configTabSpec");//.setIndicator("Configure").setContent(R.id.config_table);
    		myTabSpecConfig.setIndicator("Configure", getResources().getDrawable(R.drawable.ic_menu_preferences));
    		myTabSpecConfig.setContent(R.id.config_table);
    		myTabHost.addTab(myTabSpecConfig);
    		
    		TabSpec myTabSpecFIleViewer = myTabHost.newTabSpec("fileViewerTabSpec");//.setIndicator("Configure").setContent(R.id.config_table);
    		myTabSpecFIleViewer.setIndicator("File Viewer", getResources().getDrawable(R.drawable.ic_menu_view));
    		myTabSpecFIleViewer.setContent(R.id.file_viewer);
    		myTabHost.addTab(myTabSpecFIleViewer);
    		
    		TabSpec myTabSpecHelpAbout = myTabHost.newTabSpec("helpAboutTabSpec");//.setIndicator("Configure").setContent(R.id.config_table);
    		myTabSpecHelpAbout.setIndicator("Help-About", getResources().getDrawable(R.drawable.ic_menu_info_details));
    		myTabSpecHelpAbout.setContent(R.id.help_about);
    		myTabHost.addTab(myTabSpecHelpAbout);
    		
    		myTabHost.setCurrentTab(0);
    		
					path		= (EditText)findViewById(R.id.path);
			Button		pathGo		= (Button)findViewById(R.id.path_go);
			Button		pathBrowse	= (Button)findViewById(R.id.path_browse);
			
					file		= (EditText)findViewById(R.id.file);
			Button		fileGo		= (Button)findViewById(R.id.file_go);
			Button		fileBrowse	= (Button)findViewById(R.id.file_browse);
			
					fileView	= (TextView)findViewById(R.id.file_view);
			
			path.setText(filePath);
			//updateReceiver();
			pathGo.setOnClickListener(onPathGoClick);
			pathBrowse.setOnClickListener(onPathBrowse);
			
			file.setText(fileName);
			fileGo.setOnClickListener(onFileGoClick);
			fileBrowse.setOnClickListener(onFileBrowse);
			
			fileView.setText(fileContents);
    }
    
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
			
			//guiBrowse(true)
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
	
	private void sendToFileBrowser(boolean directory)
	{
		
		Intent		startFileBrowserIntent = new Intent(GappyAndroidActivity.this, FileBrowser.class);
		
		Bundle startFileBrowserBundle = new Bundle();
		
		startFileBrowserBundle.putString(FILE_PATH, filePath);
		
		startFileBrowserBundle.putBoolean(FILE_OPEN, directory);
		
		startFileBrowserIntent.putExtras(startFileBrowserBundle);
		
		if (directory)
		{
				startActivityForResult(startFileBrowserIntent, GET_NEW_PATH);
		}
		else
		{
			startActivityForResult(startFileBrowserIntent, GET_VIEW_FILE);
		}
	}
    
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
	    // See which child activity is calling us back.
	    switch (requestCode) {
	        case GET_NEW_PATH:
	            // This is the standard resultCode that is sent back if the
	            // activity crashed or didn't doesn't supply an explicit result.
	            if (resultCode == RESULT_CANCELED){
	                //Do nothing
	            } 
	            else {
	                Bundle result = data.getExtras();
	                
	                filePath = result.getString(FILE_PATH);
	                path.setText(filePath);
	                
	    			editor.putString(PATH, filePath);
	    			editor.commit();
	    			fileName = file.getText().toString();
	            }
	            break;
	            
	        case GET_VIEW_FILE:
	            if (resultCode == RESULT_CANCELED){
	                //Do nothing
	            } 
	            else {
	                Bundle result = data.getExtras();
	                
	                //filePath = result.getString(FILE_PATH);
	                //path.setText(filePath);
	                
	    			//editor.putString(PATH, filePath);
	    			//editor.commit();
	                //TODO Get the file view mechanism straightened out, are we passing a FIle or a String read from File in FileBrowser?
	    			fileName = file.getText().toString();
	            }
	        default:
	            break;
	    }
	}
	
}