package edu.nps.jody.GappyAndroidActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
//import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
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
		//Config file
		final static String PREF_FILE = "preferenceFile";
		final static String PATH = "filePath";
		
		
	
		//GappyAndroid
		String		 filePath = "/";
		
		//PATH, filePath;
		EditText 	path;
		
		String		 fileName = "";
		EditText	 file;
		
		String fileContents = "";
		TextView fileView;
		
		//guiBrowse
		File 			currentDirectory = new File(filePath);
		ListView listView;
		TextView browsePath;
		
		public static final String ACTION_UPDATE_PATH = "edu.nps.jody.intent.custom.ACTION_UPDATE_PATH";
		public static final String FILE_PATH = "FILE_PATH";
		SharedPreferences pref;
		SharedPreferences.Editor editor;
		
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
			fileName = file.getText().toString();
			hideKeyboard(view);
			guiBrowse(false);
		}
    	
    };
    
    private Button.OnClickListener onFileBrowse = new Button.OnClickListener()
    {

		public void onClick(View view) 
		{
			//Launch FileBrowser as another view vice a new Activity, pass parameter to only allow files
			hideKeyboard(view);
			
			guiBrowse(true);
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
    
    private void guiBrowse(boolean openFIle)
    {
    		setContentView(R.layout.browser);
    		
    		//Create a list view, populate it with an array of files, display the view and listen for click
            listView = (ListView)findViewById(R.id.list_view);
            
            File[] files= new File(filePath).listFiles();
            
            String[] fileNames = dropPath(files);
            
            listView.setAdapter(new ArrayAdapter<String>(this, R.layout.file_row, fileNames));
            
            if (openFIle)
            {
            	listView.setOnItemClickListener(onOpenListClick);
            }
            else
            {
            	listView.setOnItemClickListener(onListClick);
            }
            
            //If the user needs to go "up" the directory, here is the button to do it
            Button up = (Button)findViewById(R.id.up);
            Button select = (Button)findViewById(R.id.select);
            
            up.setOnClickListener(onUpClick);
            select.setOnClickListener(onSelectClick);
            
            //Keep updating the address location
            browsePath = (TextView)findViewById(R.id.browse_path);
            browsePath.setText(filePath);
            
    }
    
    private Button.OnClickListener onSelectClick = new Button.OnClickListener()
    {

		public void onClick(View v) 
		{
			path.setText(currentDirectory.getAbsolutePath());
			filePath=currentDirectory.getAbsolutePath();
			editor.putString(PATH, filePath);
			editor.commit();
			fileName="";
			fileContents="";
			
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
		String localFileName = (String)listView.getItemAtPosition(position);
		
		File tempFile = new File(currentDirectory.getAbsolutePath() + "/" + localFileName);
		
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
    
	private OnItemClickListener onOpenListClick = new OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
				browseOpenTo(position);
		}
	};
	
	private void browseOpenTo(int position)
	{	
		String localFileName = (String)listView.getItemAtPosition(position);
		String currentLine ="";
		
		
		File tempFile = new File(currentDirectory.getAbsolutePath() + "/" + localFileName);
		
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
				try 
				{
					FileReader fileReader = new FileReader(tempFile);
					BufferedReader bufferedReader = new BufferedReader(fileReader);
					
					fileContents="";
					
					while ((currentLine = bufferedReader.readLine()) != null)
					{
						fileContents = fileContents.concat(currentLine + "\n");
					}
					
					
					//Update the fields for the main view
					path.setText(currentDirectory.getAbsolutePath());
					filePath=currentDirectory.getAbsolutePath();
					editor.putString(PATH, filePath);
					editor.commit();//TODO Really need to make an updatePath method if the receiver thing works
					fileName=tempFile.getName();
					
					//Go back to the mainview
					mainView();
					
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
					//e.printStackTrace();
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
		}
	}
	
/*	private void updateReceiver()
	{
		Intent pathUpdate = new Intent();
		
		pathUpdate.setAction(ACTION_UPDATE_PATH);
		
		pathUpdate.putExtra(FILE_PATH, filePath);
		
		getBaseContext().sendBroadcast(pathUpdate);
	}*/
	
}