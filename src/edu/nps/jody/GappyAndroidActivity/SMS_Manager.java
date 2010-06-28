package edu.nps.jody.GappyAndroidActivity;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;

public class SMS_Manager 
{
	//Constructors
	//All methods static, so no constructors
	
	//Data Members
	//All methods static, so no data members
	
	//Accessors
	//All methods static, so no data members to access
	
	//Mutators
	//All methods static, so no data members to mutate
	
	//Methods
	
	/*
	 * Okay, I kludged this. Here's what SHOULD happen:
	 * 		Get the raw SMS (check)
	 * 		Separate phone number from SMS body (check)
	 * 		Use phone number to determine if a hashfile already exists (check)
	 * 		If exists, load the file into a hashmap (working)
	 * 		else load minimum hashmap built from pre-determined hashmap
	 * 		OR
	 * 		have NO predetermined feature set, just add to text files
	 * 		OR BOTH
	 */
	
	public static void processSMS(String SMS, int maxGap, String path, int featureType) throws IOException
	{
		/*
		 * Not assuming a standard feature set for this.  Using that would
		 * Jam up a SDCard with tons of zero count features unnecessarily.
		 * Not using a standard feature set for storage means:
		 * - more flexibility for applying store gappy bigrams to arbitary
		 *   feature sets
		 * - less storage required
		 * - 2 pass required for a new gappy bigram, the first pass checks
		 *   for the gappy bigram already in the hash map, if the gappy bigram
		 *   is found, then the count is incremented by 1.  If the gappy bigram
		 *   is NOT found, then the gappy bigram is added.
		 */
		
		//Parse the phone number out of the SMS message.  The phone number is simply
		//a text prefix on the actual message body separated by a space character.
		int space = SMS.indexOf(" ");
		String phoneNumber = SMS.substring(0, space);
		String textMSG		= SMS.substring(space+1);
		
		
		//Validate that the path ends with the character'/', if it does not, add '/'
		//to the end of path.  If path is is blank, then simply add '/'.  This is overloaded
		//with public static void processSMS(String SMS, int maxGap) where no path
		//is given and '/' is added by default.
		path = validatePath(path);

		//Make HashMap from gappy bigrams of current message
		HashMap<String, Integer> hashMap = FeatureMaker.textToFeatureMap(textMSG, maxGap, fileToMap(phoneNumber, maxGap, path), featureType);
		
		mapToFile(phoneNumber, maxGap, hashMap, path);
	}
	
	
	//Path validation belongs in a separate I/O static class.  Need to gen that up.
	public static String validatePath(String path) throws FileNotFoundException
	{
		//Verify path exists
		if (new File(path).exists())
		{
			//Ensure that path ends with a "/"
			if (!path.endsWith("/"))
			{
				path = path + "/";
			}
		}
		else
		{
			throw new FileNotFoundException();
		}
		
		return path;
	}
	
	//I don't like how very UNgeneral the 2G is here.  I need it for 2Gram with the -maxGap to indicate
	//gappy bigram, but this will trip me up if I expand this program.
	public static void mapToFile(String phoneNumber, int maxGap, HashMap<String, Integer> hashMap, String path) throws IOException
	{	
		/*
		 * According to java reference site, using a FileOutputStream with no other
		 * parameters will default to overwrite.  This is kinda dicey since I'd
		 * normally make a .bak file, write a new file, make sure that new file
		 * is happy, and THEN delete the .bak.  Since my filebrowser is providing
		 * the file path, there shouldn't be too much pain and suffering, but a
		 * path validation is in the package to be sure since a text entry option
		 * should not be too far behind on this project.
		*/
		
		//For required file output in this method
		File file = new File(path + phoneNumber + "-" + "2G" + "-" + maxGap + ".txt");
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		PrintWriter printWriter = new PrintWriter(fileOutputStream);

		//MapKey here equals "word1 word2 gap"
		String mapKey;
		
		/*
		 * mapIterator moves through the map to write out to the file
		 * bad news here is the Iterator will not write out the gappy
		 * bigrams in alphabetical order.  Once this gappy bigram file
		 * is read into a premade feature set, it won't matter, but
		 * not having the gappy bigrams in alphabetical order may
		 * have bad consequences later on.
		*/
		Iterator<String> mapIterator = hashMap.keySet().iterator();
		
		while (mapIterator.hasNext())
		{
			mapKey = mapIterator.next();
			//Convoluted printing of mapKey (word1 word2 gap + " " + count gotten by getting value assigned to mapKey
			printWriter.println(mapKey + " " + hashMap.get(mapKey).hashCode());
		}
		
		fileOutputStream.flush();
		printWriter.flush();
		
		
		printWriter.close();
		fileOutputStream.close();
	}
	
	public static HashMap<String, Integer> fileToMap(String phoneNumber, int maxGap, String path) throws IOException
	{
		HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
		
		File file = new File(path + phoneNumber + "-" + "2G" + "-" + maxGap + ".txt");
		
		//If this phone number already has a file, read it into the hashMap before
		//processing the latest message
		if (file.exists())
		{
			//Admin overhead to read a file
			FileReader		fileReader		= new FileReader(file);
			BufferedReader bufferedReader 	= new BufferedReader(fileReader);
			
			//String variable to hold each line of the text file
			String 	readLine;
			int		lastSpace;
			
			while ((readLine = bufferedReader.readLine()) != null)
			{
				//Find position of last space
				lastSpace = readLine.lastIndexOf(" ");
				
				//Break readLine into mapKey (word1 word2 gap) and count
				hashMap.put(readLine.substring(0, lastSpace), Integer.valueOf(readLine.substring(lastSpace + 1)));
			}
		}
		
		return hashMap;
		
	}
}
