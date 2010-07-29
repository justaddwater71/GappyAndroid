package edu.nps.jody.GappyAndroidActivity;

import java.util.List;

public class WordTokenizer 
{
	//Data Members
	List<String> tokenList;
	int stateTable[][];
	
	//Constants
	final static int ADD_TOKEN_WITH_CHECK_ADD_CURRENT_CHAR	=  -1;
	final static int ADD_TOKEN_WITH_CHECK_BACK_0							=  -2;
	final static int ADD_TOKEN_WITH_CHECK_BACK_1							=  -3;
	final static int ADD_TOKEN_WITH_CHECK_BACK_2							=  -4;
	final static int ADD_TOKEN_WITH_CHECK_BACK_3							=  -5;
	final static int ADD_TOKEN_WITH_CHECK_BACK_4							=  -6;
	
	final static int ADD_TOKEN_NO_CHECK_ADD_CURRENT_CHAR		=  -7;
	final static int ADD_TOKEN_NO_CHECK_BACK_0								=  -8;
	final static int ADD_TOKEN_NO_CHECK_BACK_1								=  -9;
	final static int ADD_TOKEN_NO_CHECK_BACK_2								=-10;
	final static int ADD_TOKEN_NO_CHECK_BACK_3								=-11;
	final static int ADD_TOKEN_NO_CHECK_BACK_4								=-12;
	
	final static String MARKERS = "!@#$%&*()-+={}[]|:;<>,.\\ \"";//TODO After verify, make this part of the constructor
	
	
	//Constructors
	WordTokenizer(int stateTable[][])
	{
		this.stateTable = stateTable;
	}
	
	//Methods
	public List<String> wordTokenizeText(String text)
	{
		int state=0;
		
		char charArray[] = text.toCharArray();
		
		String token = "";
		
		String currentChar;
		
		int buffer;
		
		for(int i = 0; i < charArray.length; i ++)
		{
			currentChar = Character.toString(charArray[i]);
			state = dfa(state, charArray[i]);
			
			if (state < 0)
			{
				switch(state)
				{
				case (ADD_TOKEN_WITH_CHECK_ADD_CURRENT_CHAR):
					addTokenWithCheck(token);
					token = currentChar;;
					addTokenNoCheck(token);
					token = "";
					state =0;
					break;
				
				case (ADD_TOKEN_WITH_CHECK_BACK_0):
					buffer = 0;
					bufferTokenWithCheck(token, buffer);
					i 			-= 	buffer;
					token	=		"";
					state	= 	0;
					break;
					
				case (ADD_TOKEN_WITH_CHECK_BACK_1):
					buffer = 1;
					bufferTokenWithCheck(token, buffer);
					i 			-= 	buffer;
					token	=		"";
					state	= 	0;
					break;
					
				case (ADD_TOKEN_WITH_CHECK_BACK_2):
					buffer = 2;
					bufferTokenWithCheck(token, buffer);
					i 			-= 	buffer;
					token	=		"";
					state	= 	0;
					break;
					
				case (ADD_TOKEN_WITH_CHECK_BACK_3):
					buffer = 3;
					bufferTokenWithCheck(token, buffer);
					i 			-= 	buffer;
					i 			-= 	3;
					token	=		"";
					state	= 	0;
					break;
					
				case (ADD_TOKEN_WITH_CHECK_BACK_4):
					buffer = 4;
					bufferTokenWithCheck(token, buffer);
					i 			-= 	buffer;
					i 			-= 	4;
					token	=		"";
					state	= 	0;
					break;
					
				case (ADD_TOKEN_NO_CHECK_ADD_CURRENT_CHAR):
					addTokenNoCheck(token);
					token = currentChar;;
					addTokenNoCheck(token);
					token = "";
					state =0;
				break;
				
				case (ADD_TOKEN_NO_CHECK_BACK_0):
					buffer 	= 0;
					bufferTokenNoCheck(token, buffer);
					i 			-= 	buffer;
					token	=		"";
					state	= 	0;
					break;
					
				case (ADD_TOKEN_NO_CHECK_BACK_1):
					buffer 	= 1;
					bufferTokenNoCheck(token, buffer);
					i 			-= 	buffer;
					token	=		"";
					state	= 	0;
					break;
					
				case (ADD_TOKEN_NO_CHECK_BACK_2):
					buffer 	= 2;
					bufferTokenNoCheck(token, buffer);
					i 			-= 	buffer;
					token	=		"";
					state	= 	0;
					break;
					
				case (ADD_TOKEN_NO_CHECK_BACK_3):
					buffer 	= 3;
					bufferTokenNoCheck(token, buffer);
					i 			-= 	buffer;
					token	=		"";
					state	= 	0;
					break;
					
				case (ADD_TOKEN_NO_CHECK_BACK_4):
					buffer 	= 4;
					bufferTokenNoCheck(token, buffer);
					i 			-= 	buffer;
					token	=		"";
					state	= 	0;
					break;
				}
			}
			else
			{
				token.concat(currentChar);
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
	
	public void bufferTokenNoCheck(String token, int buffer)
	{
		int length = token.length();
		addTokenWithCheck(token.substring(0, length - (buffer+1)));
		addTokenNoCheck(token.substring(length - buffer));
	}
	
	public void bufferTokenWithCheck(String token, int buffer)
	{
		int length = token.length();
		addTokenWithCheck(token.substring(0, length - (buffer)));	
		tokenList.add(token.substring(length - buffer, length - buffer + 1));
	}
	
	public void addTokenNoCheck(String token)
	{
		tokenList.add(token);
	}
	
	public int dfa(int state, char currentChar)
	{
		return stateTable[state][currentChar];
	}
}
