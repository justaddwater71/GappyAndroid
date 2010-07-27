 /** FeatureMaker is a set of static methods and static constants that are used to convert strings of words into 
  * word features that can be used for natural language processing.
  * 
     * @author      Jody Grady <jhgrady@nps.edu>
     * @version     2010.0703
     * @since       1.6
     */

package edu.nps.jody.GappyAndroidActivity;

import net.didion.jwnl.*;
import net.didion.jwnl.dictionary.Dictionary;
import opennlp.tools.coref.mention.JWNLDictionary;
import opennlp.tools.sentdetect.*;
import opennlp.maxent.*;
import opennlp.maxent.io.BinaryGISModelReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

public class FeatureMaker 
{
	//Constructor
		//Static class. No constructors.
	
	//Data Members
		//FIXME This would be better as a static map or array vice hard coding ints here and Strings down in a modified get
	public static final int 			FEATURE_OSB 						= 0;
	public static final int 			FEATURE_GB 							= 1;
	public static final String 		FEATURE_LABEL_OSB 			= "OSB";
	public static final String 		FEATURE_LABEL_GB				= "GB";
	public static final String 		MODEL_FILE 							= "EnglishSD.bin.gz";
	public static final String 		LEMMA_DICTIONARY_FILE 	= "lemmaDictionaryFIle.bin";
	public static final String 		LEMMA_DICTIONARY_PATH	= "";//TODO Figure out this remote WordNET thing
	public static final int			NO_STEMMING 						= 0;
	public static final int			PORTER_STEMMING				=1;
	public static final int			YASS_STEMMING					=2;
	public static final int			LEMMATIZE							=3;
	public static final char		QUESTION								= '?';
	public static final char		EXCLAMATION						= '!';
	public static final char		SINGLE_QUOTE						= '\'';
	public static final char		LEFT_ANGLE_BRACKET		= '<';
	public static enum				QUESTION_EXCLAMATION	{QUESTION, EXCLAMATION};			
	
	
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
		//TODO Need to generalize this for caps on/off, punctuation on/off, <s>/</s> on/off
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
		//TODO Ensure this works for case of included punctuation.
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
		//FIXME text should be tokenized HERE, not in parseOSB and parseGB
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

	public static String[] tokenize(String text)
	{
		char[] textArray = text.toCharArray();
		
		int textArrayLength = textArray.length;
		
		Vector<String> tokenVector = new Vector<String>();
		
		String currentToken = "";
		
		char currentChar = ' ';
		
		for (int i =0; i < textArrayLength; i++)
		{
			currentChar = textArray[i];
			
			//If this character is outiside standard Unicode, ignore it.
			//TODO the spec on this is Unicode, ensure that means UTF8 vice UTF16
			 if (Character.isDefined(currentChar))
			{
				if (Character.isLetter(currentChar))
				{
					currentToken.concat(Character.toString(currentChar));
				}
				else if (Character.isDigit(currentChar))
				{
					currentToken.concat(Character.toString(currentChar));
				}
				else if (Character.isWhitespace(currentChar))
				{
					tokenVector.add(currentToken);
					currentToken = "";
				}
				//Treat ! and ? as definitive end of sentence punctuation, and therefore separate tokens
				else if (currentChar == '!' || currentChar == '?')
				{
					tokenVector.add(currentToken);
					currentToken = Character.toString(currentChar);
					tokenVector.add(currentToken);
					currentToken = "";
				}
				//FIXME The array count end and character searches are TOO CLUNKY.  THINK THIS OUT!!!
				//Deal with possibility that '<' leads into <S>, </S>, or <UNK>
				else if (currentChar == '<')
				{
						if ((i+2) < textArrayLength && textArray[i+1] == 'S' && textArray[i+2] == '>')
						{
							tokenVector.add(currentToken);
							currentToken = "<S>";
							tokenVector.add(currentToken);
							currentToken= "";
							i += 2;
						}
						else if ((i+3) < textArrayLength && textArray[i+1] == '/' && textArray[i+2] == 'S' && textArray[i+3] == '>')
						{
							tokenVector.add(currentToken);
							currentToken = "</S>";
							tokenVector.add(currentToken);
							currentToken= "";
							i += 3;
						}
						else if ((i+4) < textArrayLength && textArray[i+1] == 'U' && textArray[i+2] == 'N' && textArray[i+3] == 'K' && textArray[i+4] =='>')
						{
							tokenVector.add(currentToken);
							currentToken = "<UNK>";
							tokenVector.add(currentToken);
							currentToken= "";
							i += 4;				
						}
						else
						{
							currentToken.concat(">");
						}
					}
				}
			 	//Web1T README says that 's, is a separate word, n't is a separate word, soooo, 'd would be a separate word, too?
				 else if (currentChar == '\'')
				 {
					 if ((i+1) < textArrayLength && (textArray[i+1] == 't'))
					 {
						 if (((i-1) > -1)  && textArray[i=1] == 'n')
						 {
							 tokenVector.add(currentToken);
							 currentToken = "'t";
							 tokenVector.add(currentToken);
							 currentToken = "";
							 i += 1;
						 }
						 //FIXME Seems like there should be an else after a 't without a preceding n
					 }
					 else if ((i+1) < textArrayLength && (textArray[i+1] == 'm' || textArray[i+1] =='s'))
					 {
						 currentToken = currentChar + Character.toString(textArray[i+1]);
						 tokenVector.add(currentToken);
						 currentToken="";
						 i += 1;
					 }
					 else if ((i+2) < textArrayLength && (textArray[i+1] == 'v' && textArray[i+2] == 'e') || (textArray[i+1] == 'l' && textArray[i+2] == 'l') || (textArray[i+1] == 'r' && textArray[i+2] == 'e'))
					 {
						 tokenVector.add(currentToken);
						 currentToken = currentChar + Character.toString(textArray[i+1]) + Character.toString(textArray[i+2]);
						 tokenVector.add(currentToken);
						 currentToken="";
						 i += 2;
					 }
				 }
				 else
				 {
					//Treat this like a char quote or embedded quote
					 tokenVector.add(currentToken);
					 currentToken = Character.toString(SINGLE_QUOTE);
					 tokenVector.add(currentToken);
					 currentToken="";
				 }
			}
		
		return (String[])tokenVector.toArray();
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
	
	/**
	 * Preprocess incoming text based on intended feature set.
	 * 
	 * Process the incoming text based on chosen features set, namely, whether to allow capitalization,
	 * punctuation, unknown words, sentence boundaries, and stemming.  The choices of feature set
	 * impact the size of the required files.  Specifically, capitalization reduces the required
	 * feature set files (for reference, not for each text message),  removing punctuation reduces the 
	 * required file size, dropping unknown text vice substituting with the <UNK> marker reduces the
	 * required file size, adding SentenceBoundaries increases the required file size, and using stemming
	 * decreases the required file size.
	 * 
	 * @param text										Incoming text message to be processed
	 * @param removePunctuation			Remove all capitalization if set to true, leave capitalization in text if false.
	 * @param makeLowerCase					Convert entire message to lower case letters if true.  Leave capitals alone if false.
	 * @param dropUnknownWord				Remove unknown word if set to true.  Convert unknown words to <UNK> marker if false.
	 * @param dropUnknownGram			Remove unknown grams if set to true.  Convert unknown grams to <UNKGRAM> marker if false.
	 * @param addSentenceBoundaries	Find sentence boundaries and mark with <s> (start) and </s> (end) if true. Do not find sentence boundaries if false.
	 * @param doStemming						Removed common word suffixes if true.  Leave suffixes in place if false
	 * @return
	 * @throws IOException 
	 */
	public static String preProcessText(String text, boolean removePunctuation, boolean makeLowerCase, boolean dropUnknownWord, boolean addSentenceBoundaries, boolean doStemming) throws IOException
	{
		String processedText = text;
		
		//Order of applying this processing IS significant.  
		
		//If we add sentence boundaries before stemming, we might confuse the stemming routine.
		if (doStemming)
		{
			processedText = wordStemming(processedText);
		}
		//If we strip the punctuation BEFORE doing sentence tokenizer, the sentence tokenizer won't be happy.  
		if (addSentenceBoundaries)
		{
			processedText = sentenceTokenizer(processedText);
		}
		
		//If we're going to strip capitalization, need to do that BEFORE putting in the <UNK> grams
		//so we don't munge the <UNK> tag
		if (makeLowerCase)
		{
			processedText = processedText.toLowerCase();
		}
		
		if (dropUnknownWord)
		{
			processedText = MembershipCheck.check(MembershipCheck.DROP_WORD, processedText);
		}
		else
		{
			processedText = MembershipCheck.check(MembershipCheck.TAG_UNK, processedText);
		}
		
		if (removePunctuation)
			{
				processedText = noPunctuation(processedText);
			}
		
		return processedText;
	}
	
	//Much of this code borrowed from Wicked Cool Java (No Starch Press)	Copyright (C) 2005 Brian D. Eubanks
	private static String lemmatize(String text) throws JWNLException, IOException 
	{
		//FIXME What happens when punctuation is not stripped and the text is broken on whitespace only?  We need to deal with splitting text that has punctuation in it.
		JWNL.initialize(new FileInputStream(new File(LEMMA_DICTIONARY_FILE)));
		
		String postagText = posTag(text);
		
		String textArray[] = text.split(" ");
		
		String lemmaArray[];
		
		String lemmas = "";
		//FIXME JWNL.initizalize and JWNLDictionary should not necesary reference the same file.  Sort this out!
		
		JWNLDictionary dictionary = new JWNLDictionary(LEMMA_DICTIONARY_FILE);
		
		int textArraySize = textArray.length;
		
		for (int i=0; i < textArraySize; i++)
		{
			lemmaArray = null;
			
			lemmaArray = dictionary.getLemmas(textArray[i], "VERB");
			
			lemmas.concat(lemmaArray[0] + " ");
		}
		
		return lemmas;
	}

	private static String posTag(String text)
	{
		return text;
	}
	
	public static String noPunctuation(String text)
	{	
		int 		textLength = text.length();
		char[] textArray = text.toCharArray();
		char[] processedArray = new char[textLength];
		int 		processedIndex = 0;
		
		for (int i=0;i < textLength; i++)
		{
			if (Character.isLetterOrDigit(textArray[i]))
			{
				processedArray[processedIndex] = textArray[i];
				processedIndex++;
			}
		}
						
		return new String(processedArray);
	}
	
	public static String sentenceTokenizer(String text) throws IOException
	{
		String processedText = text;
		
			SentenceDetectorME sbd = new SentenceDetectorME(new BinaryGISModelReader(new File(MODEL_FILE)).getModel());
			
			String sentenceArray[] = sbd.sentDetect(text);
			
			int sentenceArrayLength = sentenceArray.length;
			
			for (int i=0; i < sentenceArrayLength; i++)
			{
				processedText.concat("<S>");
				processedText.concat(sentenceArray[i]);
				processedText.concat("</S>");
			}

		return processedText;
	}
	
	//FIXME Implement word stemming method
	public static String wordStemming(String text)
	{
		return text;
	}
	
	public static boolean charInEnum(char currentChar, char[] charArray)
	{
		//FIXME should be a list of char not a list of char[].  Something is still wrong here.
		return Arrays.asList(charArray).contains(currentChar);
	}
}


