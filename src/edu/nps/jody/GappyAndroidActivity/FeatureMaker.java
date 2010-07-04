 /** FeatureMaker is a set of static methods and static constants that are used to convert strings of words into 
  * word features that can be used for natural language processing.
  * 
     * @author      Jody Grady <jhgrady@nps.edu>
     * @version     2010.0703
     * @since       1.6
     */

package edu.nps.jody.GappyAndroidActivity;

import java.util.HashMap;
import java.util.StringTokenizer;

public class FeatureMaker 
{
	//Constructor
		//Static class. No constructors.
	
	//Data Members
		//FIXME This would be better as a static map or array vice hard coding ints here and Strings down in a modified get
	public static final int 		FEATURE_OSB = 0;
	public static final int 		FEATURE_GB 	= 1;
	public static final String FEATURE_LABEL_OSB 	= "OSB";
	public static final String FEATURE_LABEL_GB		= "GB";
	
	//Accessors
		//Static class. No Accessors.
	
	//Mutators
		//Static class. No mutators.
	
	//Data Methods
	
	/**
     * Convert integer feature type to a label  (OSB, Gappy Bigram, etc).
     *
     * Authorative location for integer/label pairs.  Currenty supports Orthgonal Sparse Bigrams (OSB) and Gappy Bigrams (GB).
     *
     * @param featureType  	integer value to be converted into a String describing the feature type.
     * @return 							String describing the feature type associated with the parameter integer value.
     */
	public static String featureTypeToLabel(int featureType)
	{
		HashMap<Integer, String> typeToLabel = new HashMap<Integer, String>();
		
		typeToLabel.put(FEATURE_OSB, FEATURE_LABEL_OSB);
		typeToLabel.put(FEATURE_GB, FEATURE_LABEL_GB);
		
		return typeToLabel.get(featureType);
	}
	
	/**
     * Remove punctuation marks and replace capitalized letters with lowercase letters.
     *
     * Remove punctuation marks and change uppercase letters to make focus on words themselves in 
     * natural language processing vice capitalization and proximity to punctuation.  Also eliminates
     * non-words such as emoticons and sentence emphasis (ie !!!!!).
     *
     * @param textMSG		  	String of text to be cleaned up.
     * @return 							String of cleaned up text.
     */
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
	
	/**
     * Convert a String text message into a set of Orthogonal Sparse Bigrams (OSB).
     *
     * Tokenize each word in the provided message.  Pair up individual words into Orthodgonal Sparse Bigrams (OSB).
     * For instance, "the quick brown fox jumped over" gets converted into into the quick 0, the quick 1, the quick 2, 
     * the quick 3, for a maximum gap of 4 and continues for "the brown", "the fox" etc.  The build continues for "quick brown"
     * "quick fox" etc.  OSB is different from Gappy Bigram (GB) in that OSB keeps count of the lesser included distance
     * between word1 and word2 where GB only pairs the words.
     *
     * @param textMSG		  	String of text to be converted.
     * @param maxGap			Integer that specifies the maximum distance between words to use for creating features.
     * @return 							String array of word pairs with distance created from the text message parameter.
     */
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
	
	/**
     * Convert a String text message into a set of Gappy Bigrams.
     * 
     * Tokenize each word in the provided message.  Pair up individual words into Orthodgonal Sparse Bigrams (OSB).
     * For instance, "the quick brown fox jumped over" gets converted into into the quick 0, the quick 1, the quick 2, 
     * the quick 3, for a maximum gap of 4 and continues for "the brown", "the fox" etc.  The build continues for "quick brown"
     * "quick fox" etc.  OSB is different from Gappy Bigram (GB) in that OSB keeps count of the lesser included distance
     * between word1 and word2 where GB only pairs the words.
     *
     * @param textMSG		  	String of text to be converted.
     * @param maxGap			Integer that specifies the maximum distance between words to use for creating features.
     * @return 							String array of word pairs with distance created from the text message parameter.
     */
	public static String[] parseGB(String cleanTextMSG, int maxGap)
	{
		//Tokenizer to parse out words (defined as characters surrounded by whitespace)
		StringTokenizer tokenizer = new StringTokenizer(cleanTextMSG);
		
		//Determine total number of words in message to size words array
		int totalTokens = tokenizer.countTokens();
		
		//Determine total size of resultArray. 
		//FIXME  Getting null in resulting file for Gappy Bigram.  Root it out and kill it
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
	
	/**
     * Convert a String text message into a feature set.
     *
     * Based on the feature type provided
     *
     * @param textMSG		  	String of text to be converted.
     * @param maxGap			Integer that specifies the maximum distance between words to use for creating features.
     * @param featureType		integer value representing type of feature to extract from text message (ie OSB or GB).
     * @return 							String array of word pairs with distance created from the text message and feature type parameters.
     */
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

	/**
     * Convert a String text message into a set of Orthogonal Sparse Bigrams (OSB).
     *
     * Tokenize each word in the provided message.  Pair up individual words into Orthodgonal Sparse Bigrams (OSB).
     * For instance, "the quick brown fox jumped over" gets converted into into the quick 0, the quick 1, the quick 2, 
     * the quick 3, for a maximum gap of 4 and continues for "the brown", "the fox" etc.  The build continues for "quick brown"
     * "quick fox" etc.  OSB is different from Gappy Bigram (GB) in that OSB keeps count of the lesser included distance
     * between word1 and word2 where GB only pairs the words.
     *
     * @param textMSG		  	String of text to be converted.
     * @param maxGap			Integer that specifies the maximum distance between words to use for creating features.
     * @param hashMap			previously built HashMap using the same feature set and maximum gap.
     * @param featureType		integer value representing type of feature to extract from text message (ie OSB or GB).
     * @return 							HashMap of features to the integer count of the number of occurrences of that feature in the text message. 
     */
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


