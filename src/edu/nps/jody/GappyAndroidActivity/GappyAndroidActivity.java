package edu.nps.jody.GappyAndroidActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
        path.setText(filePath);
        Button		pathGo		= (Button)findViewById(R.id.pathGo);
        			file		= (EditText)findViewById(R.id.file);
        file.setText(fileName);
        Button		fileGo		= (Button)findViewById(R.id.fileGo);
        			fileView	= (TextView)findViewById(R.id.fileView);
        /*
        path.setOnClickListener(onPathClick);
        file.setOnClickListener(onFileClick);
        */
        pathGo.setOnClickListener(onPathGoClick);
        fileGo.setOnClickListener(onFileGoClick);
    }
    /*
    private TextView.OnClickListener onPathClick = new OnClickListener()
    {

		public void onClick(View v) 
		{
			path.
			
		}
    
    };
    
    private TextView.OnClickListener onFileClick = new OnClickListener()
    {

		public void onClick(View v) 
		{
			
		}
    
    };
    	*/
    private Button.OnClickListener onPathGoClick = new OnClickListener()
    {
		public void onClick(View v) 
		{	
			//TextView temp = (TextView)path.getText();
			filePath = path.getText().toString();
			fileName = file.getText().toString();
			path.getContext();
			//Toast.makeText(fileView.getContext(), filePath + fileName, Toast.LENGTH_LONG);
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(path.getApplicationWindowToken(), 0);
			fileView.setText(filePath + "/" + fileName);
		}
    	
    };
    
    private Button.OnClickListener onFileGoClick = new OnClickListener()
    {
		public void onClick(View v) 
		{
			//TextView temp = (TextView)file.getText();
			filePath = path.getText().toString();
			fileName = file.getText().toString();
			file.getContext();
			//Toast.makeText(fileView.getContext(), filePath + fileName, Toast.LENGTH_LONG);
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(file.getApplicationWindowToken(), 0);
			fileView.setText(filePath + "/" + fileName);
		}
    	
    };
}