package edu.nps.jody.GappyAndroidActivity;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FileBrowser extends Activity {
    //Data Members
	File currentDirectory = new File("/proc");
	ListView listView;
	TextView browsePath;
	
	
	//Constructor
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser);
        
        //Create a list view, populate it with an array of files, display the view and listen for click
        listView = (ListView)findViewById(R.id.listView);
        
        File[] files= new File("/proc").listFiles();
        
        String[] fileNames = dropPath(files);
        
        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.file_row, fileNames));
        
        listView.setOnItemClickListener(OnListClick);
        
        //If the user needs to go "up" the directory, here is the button to do it
        Button up = (Button)findViewById(R.id.up);
        
        up.setOnClickListener(OnUpClick);
        
        //Keep updating the address location
        browsePath = (TextView)findViewById(R.id.browsePath);
        browsePath.setText("/proc");
        
    }
    
    //Ensure current directory has a parent, if it does, browseTo it
    
    //TODO THINK THIS SHOULD BE BUTTON.ONCLICKLISTENER, NOT VIEW. -- NEED TO TEST
    private View.OnClickListener OnUpClick = new View.OnClickListener()
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
    

	private OnItemClickListener OnListClick = new OnItemClickListener()
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