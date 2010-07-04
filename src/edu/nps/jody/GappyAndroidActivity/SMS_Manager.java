 /**SMS_Manager is a set of static methods and static constants that 
  * are used to convert SMS message into strings of words that are in
  * turned converted into word features that can be used for natural 
  * language processing. Depends on FeatureMaker class to function.
  * 
     * @author      Jody Grady <jhgrady@nps.edu>
     * @version     2010.0703
     * @since       1.6
     */

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
	//No magic constants for SMS manager, so no data members
	
	//Accessors
	//All methods static, so no data members to access
	
	//Mutators
	//All methods static, so no data members to mutate
	
	//Methods

	/**
     * Convert a SMS message into a feature set and write that feature set out to a given path
     *
     *This method strips the phone number out of the SMS message, sends the text message to the
     *class FeatureMaker to have the text processed.  The returning String array is written to a file
     *containing the phone number, feature type, and gap size.  This method stores the feature
     *set AS TEXT not as a hash.  That can be accomplished in a separate class as needed to make 
     *storage more efficient and processing faster.
     * 
     * @param SMS					String consisting of one SMS message with phone number still attached.
     * @param maxGap			Integer that specifies the maximum distance between words to use for creating features.
     * @ param path					String containing path to the directory where processed SMS message feature set should be written.
     * @param featureType  	integer value to be converted into a String describing the feature type.
     * @return 							none.
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
		HashMap<String, Integer> hashMap = FeatureMaker.textToFeatureMap(textMSG, maxGap, fileToMap(phoneNumber, maxGap, FeatureMaker.featureTypeToLabel(featureType), path), featureType);
		
		mapToFile(phoneNumber, maxGap, featureType, hashMap, path);
	}
	
	/**
     * Validate that the given path is a valid directory on the target system.
     *
     *This method simply creates a File object from the given String path and ensures that Object
     *exists within the target system.
     * 
     * @param SMS					String consisting of one SMS message with phone number still attached.
     * @param maxGap			Integer that specifies the maximum distance between words to use for creating features.
     * @param path					String containing path to the directory where processed SMS message feature set should be written.
     * @param featureType  	integer value to be converted into a String describing the feature type.
     * @return 							String path if that path is valid.
     * @exception						throws FileNotFoundException if the path is not found.
     */
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
	
	/**
     * Reads a HashMap and convert it into a file
     *
     *This method iterates through a HashMap of features and counts to create a text file of those same
     *features and counts.
     * 
     * @param phoneNumber		String representing the phone number pulled from the original SMS message
     * @param maxGap				Integer that specifies the maximum distance between words to use for creating features.
     * @param featureType  		integer value to be converted into a String describing the feature type.
     * @param path						String containing path to the directory where processed SMS message feature set should be written.
     * @return 								None.
     * @exception							throws IOException if there is an internal IO issue.
     */
	public static void mapToFile(String phoneNumber, int maxGap, int featureType, HashMap<String, Integer> hashMap, String path) throws IOException
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
		File file = new File(path + phoneNumber + "-" + FeatureMaker.featureTypeToLabel(featureType) + "-" + maxGap + ".txt");
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
	
	/**
     * Reads a text file and convert it into a HashMap.
     *
     *This method iterates through a text file of features and counts to create a HashMap of those same
     *features and counts.
     * 
     * @param phoneNumber		String representing the phone number pulled from the original SMS message
     * @param maxGap				Integer that specifies the maximum distance between words to use for creating features.
     * @param featureType  		integer value to be converted into a String describing the feature type.
     * @param path						String containing path to the directory where processed SMS message feature set should be written.
     * @return 								None.
     * @exception							throws IOException if there is an internal IO issue.
     */
	public static HashMap<String, Integer> fileToMap(String phoneNumber, int maxGap, String featureTypeName, String path) throws IOException
	{
		HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
		
		File file = new File(path + phoneNumber + "-" + featureTypeName + "-" + maxGap + ".txt");
		
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
