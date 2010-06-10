package edu.nps.jody.GappyAndroidActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class GappyAndroidActivity extends Activity 
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        TextView	path		= (TextView)findViewById(R.id.path);
        Button		pathGo		= (Button)findViewById(R.id.pathGo);
        TextView	file		= (TextView)findViewById(R.id.file);
        Button		fileGo		= (Button)findViewById(R.id.fileGo);
        TextView	fileView	= (TextView)findViewById(R.id.fileView);
        
        pathGo.setOnClickListener(onPathGoClick);
        fileGo.setOnClickListener(onFileGoClick);
    }
    
    private Button.OnClickListener onPathGoClick = new OnClickListener()
    {
		public void onClick(View arg0) 
		{
			// TODO Auto-generated method stub
			
		}
    	
    };
    
    private Button.OnClickListener onFileGoClick = new OnClickListener()
    {
		public void onClick(View v) 
		{
			// TODO Auto-generated method stub
			
		}
    	
    };
}