package edu.nps.jody.GappyAndroidActivity;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
//import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
//import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class GappyAndroidActivity extends Activity 
{
	//Data Members
		//GappyAndroid
		String		 filePath = "/";
		String		 fileName = "";
		EditText 	path;
		EditText	 file;
		TextView fileView;
		
		//guiBrowse
		File 			currentDirectory = new File("/proc");
		ListView listView;
		TextView browsePath;
	
	//Constructors
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        mainView();
        
        /*setContentView(R.layout.main);
        
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
        fileBrowse.setOnClickListener(onFileBrowse);*/
    }
    
    //Methods
    private void mainView()
    {
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
    
    private Button.OnClickListener onPathBrowse = new Button.OnClickListener()
    {

		public void onClick(View view) 
		{
			//Launch FileBrowser as another view vice a new Activity, pass parameter to only allow directories
			guiBrowse();
		}
    	
    };
    
    private Button.OnClickListener onFileBrowse = new Button.OnClickListener()
    {

		public void onClick(View v) 
		{
			//Launch FileBrowser as another view vice a new Activity, pass parameter to only allow files
			guiBrowse();
		}
    	
    };
 
    private Button.OnClickListener onPathGoClick = new Button.OnClickListener()
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
    
    private Button.OnClickListener onFileGoClick = new Button.OnClickListener()
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
    
    private void guiBrowse()
    {
    		setContentView(R.layout.browser);
    		
    		//Create a list view, populate it with an array of files, display the view and listen for click
            listView = (ListView)findViewById(R.id.listView);
            
            File[] files= new File("/proc").listFiles();
            
            String[] fileNames = dropPath(files);
            
            listView.setAdapter(new ArrayAdapter<String>(this, R.layout.file_row, fileNames));
            
            listView.setOnItemClickListener(onListClick);
            
            //If the user needs to go "up" the directory, here is the button to do it
            Button up = (Button)findViewById(R.id.up);
            Button select = (Button)findViewById(R.id.select);
            
            up.setOnClickListener(onUpClick);
            select.setOnClickListener(onSelectClick);
            
            //Keep updating the address location
            browsePath = (TextView)findViewById(R.id.browsePath);
            browsePath.setText("/proc");
            
    }
    
    private Button.OnClickListener onSelectClick = new Button.OnClickListener()
    {

		public void onClick(View v) 
		{
			path.setText(currentDirectory.getAbsolutePath());
			filePath=currentDirectory.getAbsolutePath();
			
			mainView();
			
		}
    	
    };
    
    
 //Ensure current directory has a parent, if it does, browseTo it
    private View.OnClickListener onUpClick = new View.OnClickListener()
    {
		//@Override
		public void onClick(View v) 
		{
			File dotDot = currentDirectory.getParentFile();
			
			if (dotDot == null)
			{
				DialogInterface.OnClickListener atRootButtonListener = new DialogInterface.OnClickListener()
				{
					//@Override
					public void onClick(DialogInterface arg0, int arg1) 
					{
						//Do nothing
					}
				};
				
				new AlertDialog.Builder(listView.getContext())
					.setTitle("At the top")
					.setMessage("Current directory has no parent!")
					.setPositiveButton("OK", atRootButtonListener)
					.show();
			}
			else
			{
				currentDirectory = dotDot;
				browsePath.setText(currentDirectory.getAbsolutePath());
				listView.setAdapter(new ArrayAdapter<String>(listView.getContext(), R.layout.file_row, dropPath(dotDot.listFiles())));
			}

		}

    };
    

	private OnItemClickListener onListClick = new OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
				browseTo(position);
		}
	};
    

	
	private void browseTo(int position)
	{	
		String fileName = (String)listView.getItemAtPosition(position);
		
		File tempFile = new File(currentDirectory.getAbsolutePath() + "/" + fileName);
		
		if (tempFile.isDirectory())
		{
			if (tempFile.canRead())
			{
				File[] files = tempFile.listFiles();
				currentDirectory = tempFile;
				browsePath.setText(currentDirectory.getAbsolutePath());
				
				if (files == null)
				{
					listView.setAdapter(new ArrayAdapter<String>(this, R.layout.file_row, dropPath(files)));
				}
				else
				{
					listView.setAdapter(new ArrayAdapter<String>(this, R.layout.file_row, dropPath(files)));
				}
			}
			else
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
					.setTitle("Security Notice")
					.setMessage("Permission Denied")
					.setPositiveButton("OK", fileButtonListener)
					.show();
			}

		}
		else
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
				.setTitle("File Selected!")
				.setMessage("That is not a directory")
				.setPositiveButton("OK", fileButtonListener)
				.show();
		}
	}
	
	private String[] dropPath (File[] files)
	{
		String[] fileOnly = new String[files.length];
		
		for (int i = 0; i < files.length; i++)
		{
			fileOnly[i] = files[i].getName();
		}
		
		return fileOnly;
	}
    
}