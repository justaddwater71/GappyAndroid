package edu.nps.jody.GappyAndroidActivity;

import java.util.List;

public class WordTokenizer 
{
	//Data Members
	//String text;
	char charArray[];
	String currentChar;
	List<String> tokenList;
	//String token;
	String buffer;
	//int state;
	int stateTable[][];
	
	//Constants
	final static int ADD_TOKEN_WITH_CHECK_ADD_MARKER_NO_CHECK 	= -1;
	final static int ADD_TOKEN_WITH_CHECK_CLEAR_TOKEN 						= -2;
	final static int ADD_TOKEN_NO_CHECK_ADD_CHAR 								= -3;
	final static int ADD_TOKEN_WITH_CHECK_USE_BUFFER 						= -4;
	final static int ADD_TOKEN_WITH_CHECK_ADD_CHAR 							= -5;
	final static int ADD_TOKEN_NO_CHECK_CLEAR_TOKEN 							= -6;
	final static String MARKERS = "!@#$%&*()-+={}[]|:;<>,.\\ \"";
	
	//Constructors
	WordTokenizer(int stateTable[][])
	{
		this.stateTable = stateTable;
	}
	
	//Methods
	public List<String> wordTokenizeText(String text)
	{
		int state=0;
		
		charArray = text.toCharArray();
		
		String token = "";
		
		for(int i = 0; i < charArray.length; i ++)
		{
			String currentChar = Character.toString(charArray[i]);
			state = dfa(state, charArray[i]);
			
			if (state < 0)
			{
				switch(state)
				{
				case (ADD_TOKEN_WITH_CHECK_ADD_MARKER_NO_CHECK):
					
					addTokenWithCheck(token);
					token = currentChar;;
					addTokenNoCheck(token);
					token = "";
					
					break;
				
				case (ADD_TOKEN_WITH_CHECK_CLEAR_TOKEN):
					addTokenWithCheck(token);
					token = "";
					
					break;
				
				case (ADD_TOKEN_NO_CHECK_ADD_CHAR):
					addTokenNoCheck(token);
					token = currentChar;
					
					break;
				
				case(ADD_TOKEN_WITH_CHECK_USE_BUFFER):
					
					break;
				
				case (ADD_TOKEN_WITH_CHECK_ADD_CHAR):
					addTokenWithCheck(token);
					token = currentChar;
					
					break;
				
				case (ADD_TOKEN_NO_CHECK_CLEAR_TOKEN):
					addTokenNoCheck(token);
					token = "";
					break;
				}
			}
			else
			{
				token.concat(Character.toString(charArray[i]));
			}
		}
		
		return tokenList;
	}
	
	public void addTokenWithCheck(String token)
	{
		int tokenLength;
		
		if ((tokenLength = token.length()) > 0)
		{
			if (MARKERS.indexOf(token.substring(tokenLength-1)) > -1)
			{
				addTokenWithCheck(token.substring(0, tokenLength-2));
				tokenList.add(token.substring(tokenLength-1));
			}
			else
			{
				tokenList.add(token);	
			}
		}
		
		
	}
	
	public void addTokenNoCheck(String token)
	{
		tokenList.add(token);
	}
	
	public int dfa(int state, char currentChar)
	{
		return stateTable[state][ currentChar];
	}
}
