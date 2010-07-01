package edu.nps.jody.GappyAndroidActivity;

//import java.io.BufferedReader;
import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
		private		File 			currentDirectory;
		private		ListView listView;
		private		TextView browsePath;
		private		String		filePath; 
		//private		String		fileContents;
		//private		EditText 	path;
		//private		String		fileName = "";
		private		boolean	openFile;
		
		public final static String FILE_PATH 			= "FILE_PATH";
		public final static String FILE_OPEN 			= "FILE_OPEN"; //getString(R.string.file_open);
		//private final String FILE_CONTENT 	= "FILE_CONTENT"; //getString(R.string.file_content);
		public final static String	FILE_ABSOLUTE_PATH = "FILE_ABSOLUTE_PATH";
		
		//Constructor
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser);
        
        Bundle extras = this.getIntent().getExtras();
        
        filePath = extras.getString(FILE_PATH);
        
        //path		= (EditText)findViewById(R.id.path);
        
        currentDirectory = new File(filePath);
        
        openFile = extras.getBoolean(FILE_OPEN);

    		//Create a list view, populate it with an array of files, display the view and listen for click
            listView = (ListView)findViewById(R.id.list_view);
            
            File[] files= new File(filePath).listFiles();
            
            String[] fileNames = dropPath(files);
            
            listView.setAdapter(new ArrayAdapter<String>(this, R.layout.file_row, fileNames));
            
            Button up = (Button)findViewById(R.id.up);
            Button select = (Button)findViewById(R.id.select);
            Button cancel = (Button)findViewById(R.id.cancel);
            
            up.setOnClickListener(onUpClick);
            
            cancel.setOnClickListener(onCancelClick);
            
            if (openFile)
            {
            	listView.setOnItemClickListener(onOpenListClick);
            	select.setTextColor(R.color.address_background);
            }
            else
            {
            	listView.setOnItemClickListener(onListClick);
            	select.setOnClickListener(onSelectClick);
            }
            
           
            

            
            //Keep updating the address location
            browsePath = (TextView)findViewById(R.id.browse_path);
            browsePath.setText(filePath);
            
            
    }
    
    private Button.OnClickListener onCancelClick = new Button.OnClickListener()
    {

		public void onClick(View v) {
			Intent resultIntent = new Intent();
			
			setResult(RESULT_CANCELED, resultIntent);
			
			finish();
		}
    	
    };
    
    private Button.OnClickListener onSelectClick = new Button.OnClickListener()
    {
    	
		public void onClick(View v) 
		{
			filePath=currentDirectory.getAbsolutePath();
			//fileName="";
			//fileContents="";
			
			//Return fileContents to GappyAndroidActivity along with the full path and name of the file being viewed
			Intent resultIntent = new Intent();
			
			Bundle results = new Bundle();
			
			results.putString(FILE_PATH, filePath);
			
			//results.putString(FILE_CONTENT, fileContents);
			
			resultIntent.putExtras(results);
			
			setResult(RESULT_OK, resultIntent);
			
			finish();
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
			if (openFile)
			{
				browseOpenTo(position);
			}
			else
			{
				browseTo(position);
			}
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
			
			if (openFile)
			{
				browseOpenTo(position);
			}
			else
			{
				new AlertDialog.Builder(this)
					.setTitle("File Selected!")
					.setMessage("That is not a directory")
					.setPositiveButton("OK", fileButtonListener)
					.show();
			}
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
		//String currentLine ="";
		
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
				/*try 
				{
					FileReader fileReader = new FileReader(tempFile);
					BufferedReader bufferedReader = new BufferedReader(fileReader);
					
					fileContents="";
					
					while ((currentLine = bufferedReader.readLine()) != null)
					{
						fileContents = fileContents.concat(currentLine + "\n");
					}*/
					
					Bundle results = new Bundle();
					
					//results.putString(FILE_CONTENT, fileContents);
					results.putString(FILE_ABSOLUTE_PATH, tempFile.getAbsolutePath());
					
					Intent resultIntent = new Intent();
					
					resultIntent.putExtras(results);
					
					setResult(RESULT_OK, resultIntent);
					
					filePath=currentDirectory.getAbsolutePath();

					//fileName=tempFile.getName();
					
					finish();
				} 
				/*catch (FileNotFoundException e) 
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
		}*/
	}
    
 }