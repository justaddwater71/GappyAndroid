package edu.nps.jody.GappyAndroidActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GappyAndroidActivity extends Activity 
{
	String filePath = "/";
	String fileName = "";
	EditText path;
	EditText file;
	TextView fileView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        			path		= (EditText)findViewById(R.id.path);
        Button		pathGo		= (Button)findViewById(R.id.pathGo);
        Button		pathBrowse	= (Button)findViewById(R.id.pathBrowse);

        			file		= (EditText)findViewById(R.id.file);
        Button		fileGo		= (Button)findViewById(R.id.fileGo);
        Button		fileBrowse	= (Button)findViewById(R.id.fileBrowse);
        
        			fileView	= (TextView)findViewById(R.id.fileView);
        
        path.setText(filePath);
        pathGo.setOnClickListener(onPathGoClick);
        pathBrowse.setOnClickListener(onPathBrowse);
        
        file.setText(fileName);
        fileGo.setOnClickListener(onFileGoClick);
        fileBrowse.setOnClickListener(onFileBrowse);
    }
    
    private Button.OnClickListener onPathBrowse = new OnClickListener()
    {

		public void onClick(View view) 
		{
			Context context = getBaseContext();
			
			Intent intent = new Intent(context, FileBrowser.class);
			
			startActivityForResult(intent, Activity);
			
		}
    	
    };
    
    private Button.OnClickListener onFileBrowse = new OnClickListener()
    {

		public void onClick(View v) 
		{
			Context context = getBaseContext();
			
			Intent intent = new Intent(context, FileBrowser.class);
			
			context.startActivity(intent);
			
		}
    	
    };
 
    private Button.OnClickListener onPathGoClick = new OnClickListener()
    {
		public void onClick(View v) 
		{	
			filePath = path.getText().toString();
			fileName = file.getText().toString();
			path.getContext();
			
			/* Below hide-keyboard code copied from
			 * http://stackoverflow.com/questions/1109022/how-to-close-hide-the-android-soft-keyboard
			 */
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(path.getApplicationWindowToken(), 0);
            //End copy from stackoverflow
            
			fileView.setText(filePath + "/" + fileName);
		}
    	
    };
    
    private Button.OnClickListener onFileGoClick = new OnClickListener()
    {
		public void onClick(View v) 
		{
			filePath = path.getText().toString();
			fileName = file.getText().toString();
			file.getContext();
			
			/*
			 * Below hide-keyboard code copied from
			 * http://stackoverflow.com/questions/1109022/how-to-close-hide-the-android-soft-keyboard
			 */
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(file.getApplicationWindowToken(), 0);
            //End copy from stackoverflow
            
			fileView.setText(filePath + "/" + fileName);
		}
    	
    };
    
}