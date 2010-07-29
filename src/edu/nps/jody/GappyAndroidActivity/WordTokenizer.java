package edu.nps.jody.GappyAndroidActivity;

import java.util.Vector;

public class WordTokenizer 
{
	//Data Members
	String token = "";
	int i = 0; //This is a TERRIBLE way to handle the index, but necessary for now.
	Vector<String> tokenList = new Vector<String>();
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
	public Vector<String> wordTokenizeText(String text)
	{
		//Ensure a finishing character is present
		text = text.concat("\n");
		
		int state=0;
		
		char charArray[] = text.toCharArray();
		
		String currentChar;
		
		for( i = 0; i < charArray.length; i ++)
		{
			currentChar = Character.toString(charArray[i]);
			
			state = dfa(state, charArray[i]);
			
			state = buildTokens(state, currentChar);
		}
		
		return tokenList;
	}
	
	public void addTokenWithCheck(String token)
	{
		int tokenLength;
		
			if ((tokenLength = token.length()) > 1 && MARKERS.indexOf(token.substring(tokenLength-1)) > -1)
			{	
				addTokenWithCheck(token.substring(0, tokenLength-1));
				tokenList.add(token.substring(tokenLength-1));
			}
			else
			{
				if (tokenLength > 0)
				{
					tokenList.add(token);	
				}
				
			}
		
	}
	
	public void bufferTokenNoCheck(String token, int buffer)
	{
		if(buffer > 0)  //watch this number compared to beffer-1 or buffer
		{
		int length = token.length();
		addTokenNoCheck(token.substring(0, length - (buffer-1)));
		}
		else
		{
			addTokenNoCheck(token);
		}
	}
	
	public void bufferTokenWithCheck(String token, int buffer)
	{
		if(buffer > 0)//watch this number compared to beffer-1 or buffer
		{
		int length = token.length();
		addTokenWithCheck(token.substring(0, length - (buffer-1)));
		}
		else
		{
			addTokenWithCheck(token);
		}
	}
	
	public void addTokenNoCheck(String token)
	{
		int tokenLength = token.length();
		
		if (tokenLength > 0)
		{
			tokenList.add(token);
		}
		
	}
	
	public  int buildTokens(int state, String currentChar)
	{
		int buffer;
		
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
				token	=		"";
				state	= 	0;
				break;
				
			case (ADD_TOKEN_WITH_CHECK_BACK_4):
				buffer = 4;
				bufferTokenWithCheck(token, buffer);
				i 			-= 	buffer;
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
				i 			-= 	buffer;		//Just an experiment
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
			token= token.concat(currentChar);
		}
		
		return state;
	}
	
	public int dfa(int state, char currentChar)
	{
		return stateTable[state][currentChar];
	}
}
