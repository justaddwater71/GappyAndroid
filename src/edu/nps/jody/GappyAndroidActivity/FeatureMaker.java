package edu.nps.jody.GappyAndroidActivity;

//TODO Add OSBMaker to this class

import java.util.HashMap;
import java.util.StringTokenizer;

public class FeatureMaker 
{
	//Constructor
		//Static class. No constructors.
	
	//Data Members
	public static final int FEATURE_OSB 		= 0;
	public static final int FEATURE_GB 	= 1;
	
	//Accessors
		//Static class. No Accessors.
	
	//Mutators
		//Static class. No mutators.
	
	//Data Methods
	public static String cleanUp(String textMSG)
	{
		//String cleanedString="";
		char	cleanedArray[];
		
		cleanedArray = textMSG.toLowerCase().toCharArray();
		
		for (int i=0;i < cleanedArray.length;i++)
			if (cleanedArray[i] > 'z' || cleanedArray[i] < 'a')
			{
				cleanedArray[i] = ' ';
			}
		
		return new String(cleanedArray);
	}
	public static String[] parseOSB(String cleanTextMSG, int maxGap)
	{
		//Tokenizer to parse out words (defined as characters surrounded by whitespace)
		StringTokenizer tokenizer = new StringTokenizer(cleanTextMSG);
		
		//Determine total number of words in message to size words array
		int totalTokens = tokenizer.countTokens();
		
		//Determine total size of resultArray. Thank you Wolfram-Alfa
		int totalOSB = (maxGap*(maxGap+1)/2)*totalTokens
			- (maxGap)*(maxGap+1)*(maxGap+2)/6;
	
		//Create resultArray to return for addition to hashMap
		String[] resultArray = new String[totalOSB];
	
		//Create words array for looping through to get gappy bigrams
		String words[] = new String[totalTokens];
		
		//Load the words array with words from cleanTextMSG
		for (int h=0; h < totalTokens; h++)
		{
			words[h] = tokenizer.nextToken();
		}
		
		/*
		 * "n" will be used to advance through result array
		 * There is no check to ensure I don't run off the
		 * end of the array.  Depending on Wolfram-Alfa
		 * formula for that.
		*/
		int n = 0;
		
		//Create loop for word1. Use totalTokens vice words.length to save cycles.
		for (int i=0; i < totalTokens; i++)
		{
			//Create loop for word2.  Going from 1 to (< maxGap + 1) vice traditional 0 to (< maxGap).
			for (int j=1; j < maxGap + 1; j++)
			{
				//Don't run off the end of the words array
				if ((i + j) > totalTokens - 1) 
				{
					break; //Get out of current word2 loop, but continue word1 loop.
				}
				//k goes from the current gap out to the maxGap
				for (int k=(j - 1); k < maxGap; k++)
				{
					resultArray[n] = words[i] + " " + words[i + j] + " " + k;
					n++;
				}
			}
		}
		
		return resultArray;
	}
	
	
	
	public static String[] parseGB(String cleanTextMSG, int maxGap)
	{
		//FIXME  This is NOT good code for parseGB.  This is copied directly from parseOSB.
		//Tokenizer to parse out words (defined as characters surrounded by whitespace)
		StringTokenizer tokenizer = new StringTokenizer(cleanTextMSG);
		
		//Determine total number of words in message to size words array
		int totalTokens = tokenizer.countTokens();
		
		//Determine total size of resultArray. 
		//FIXME  Need to think out this formula for GAPPY BIGRAMS, MAY NOT BE SAME FOR OSB!
		int totalGB = (maxGap*(maxGap+1)/2)*totalTokens
			- (maxGap)*(maxGap+1)*(maxGap+2)/6;
	
		//Create resultArray to return for addition to hashMap
		String[] resultArray = new String[totalGB];
	
		//Create words array for looping through to get gappy bigrams
		String words[] = new String[totalTokens];
		
		//Load the words array with words from cleanTextMSG
		for (int h=0; h < totalTokens; h++)
		{
			words[h] = tokenizer.nextToken();
		}
		
		/*
		 * "n" will be used to advance through result array
		 * There is no check to ensure I don't run off the
		 * end of the array.  Depending on Wolfram-Alfa
		 * formula for that.
		*/
		int n = 0;
		
		//Create loop for word1. Use totalTokens vice words.length to save cycles.
		for (int i=0; i < totalTokens; i++)
		{
			//Create loop for word2.  Going from 1 to (< maxGap + 1) vice traditional 0 to (< maxGap).
			for (int j=1; j < maxGap + 1; j++)
			{
				//Don't run off the end of the words array
				if ((i + j) > totalTokens - 1) 
				{
					break; //Get out of current word2 loop, but continue word1 loop.
				}
				//k goes from the current gap out to the maxGap
//				for (int k=(j - 1); k < maxGap; k++)
//				{
					resultArray[n] = words[i] + " " + words[i + j];// + " " + k;
					n++;
//				}
			}
		}
		
		return resultArray;
	}
	
	
	public static String[] parse(String cleanTextMSG, int maxGap, int  featureType)
	{	
		
		switch (featureType)
		{
			case FEATURE_OSB: //=0
			
				return parseOSB(cleanTextMSG, maxGap);
			
			
			case FEATURE_GB: //=1
	
				return parseGB(cleanTextMSG, maxGap);
			
			
			default:

				return null;
			
		}
	}
		
		

	
	public static HashMap<String, Integer> textToFeatureMap(String textMSG, int maxGap, HashMap<String, Integer> hashMap,int featureType)
	{
		String featureArray[] = parse(cleanUp(textMSG), maxGap, featureType);
		
		for (int i = 0; i < featureArray.length; i++)
		{
			//If the key is already there, add 1 to the value
			//This looks WAY convoluted and uses TOO MANY lookups
			//in the hashmap.  There has to be an iterator way
			//to do this smartly
			if (hashMap.containsKey(featureArray[i]))
			{
				hashMap.put(featureArray[i], hashMap.get(featureArray[i]) + 1);
			}
			else
			{
				//Prime the entry in the hashmap with count =1
				hashMap.put(featureArray[i], Integer.valueOf(1));
			}
		}
		
		return hashMap;
	}
	
}


