package edu.nps.jody.GappyAndroidActivity;

import java.util.List;

public class WordTokenizer 
{
	//Data Members
	//String text;
	//char charArray[];
	//String currentChar;
	List<String> tokenList;
	//String token;
	//String buffer;
	//int state;
	int stateTable[][];
	//int bufferState;
//	String specialBreakWords[] = {"<S>", "</S>"};
	//String specialNoBreakWords[] = {"..."};
	
	//Constants
	//Encountered marker, check previous word for trailing special characters
	final static int ADD_TOKEN_WITH_CHECK_ADD_MARKER_NO_CHECK 			=  -1;
	
	//Encountered blank, _, or del, check previous word for trailing characters
	final static int ADD_TOKEN_WITH_CHECK_CLEAR_TOKEN 								=  -2;
	
	/*One character into special sequence, did not complete special sequence,
	check previous word for trailing special charaters, back up and look at 
	current character again from state 0.*/
	final static int ADD_TOKEN_WITH_CHECK_ADD_CHAR 									=  -3;
	
	//Encounterd blank, _, or del during after completion of special sequence.
	final static int ADD_TOKEN_NO_CHECK_CLEAR_TOKEN 									=  -4;
	
	final static int ADD_TOKEN_NO_CHECK_ADD_CHAR 										=  -5;
	
	/*Special sequence interrupted.  Check previous word for special trailing characters
	Back up and look at the sequence of past X number of characters from state 0.*/
	final static int ADD_TOKEN_BUFFER_LAST_CHARACTERS_WITH_CHECK_2	=  -6;
	final static int ADD_TOKEN_BUFFER_LAST_CHARACTERS_WITH_CHECK_3	=   -7;
	final static int ADD_TOKEN_BUFFER_LAST_CHARACTERS_WITH_CHECK_4	=  -8;
	
	/*Special sequence completed.  Separate previous word from special sequence.
	Submit special sequence with no trailing character check.*/
	final static int ADD_TOKEN_BUFFER_LAST_CHARACTERS_NO_CHECK_2 		=  -9;
	final static int ADD_TOKEN_BUFFER_LAST_CHARACTERS_NO_CHECK_3 		=-10;
	static final int ADD_TOKEN_BUFFER_LAST_CHARACTERS_NO_CHECK_4 		= -11;
	
	final static String MARKERS = "!@#$%&*()-+={}[]|:;<>,.\\ \"";//After verify, make this part of the constructor
	
	
	//Constructors
	WordTokenizer(int stateTable[][])
	{
		this.stateTable = stateTable;
		//this.bufferState = bufferState;
	}
	
	//Methods
	public List<String> wordTokenizeText(String text)
	{
		int state=0;
		
		char charArray[] = text.toCharArray();
		
		String token = "";
		
		String currentChar;
		
//		int charArrayLength;
		
//		StringBuffer charSequence = new StringBuffer();;
//		
//		boolean specialWordFound;
		
		for(int i = 0; i < charArray.length; i ++)
		{
			currentChar = Character.toString(charArray[i]);
//			charArrayLength = charArray.length;
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
					state =0;
					break;
				
				case (ADD_TOKEN_WITH_CHECK_CLEAR_TOKEN):
					addTokenWithCheck(token);
					token = "";
					state=0;
					break;
					
				case (ADD_TOKEN_WITH_CHECK_ADD_CHAR):
					addTokenWithCheck(token);
					//token = currentChar;
					state=0;
					//Back up the pointer and read this character again -- kinda like a Turing machine
					i -= 1;
					break;
				
				case (ADD_TOKEN_NO_CHECK_CLEAR_TOKEN):
					addTokenNoCheck(token);
					token = "";
					state=0;
					break;
					
				case (ADD_TOKEN_NO_CHECK_ADD_CHAR):
					addTokenNoCheck(token);
					//token = currentChar;
					state=0;
					//Back up the pointer and read this character again -- kinda like a Turing machine
					i -= 1;
					break;
				
				/*case(ADD_TOKEN_CHECK_FOR_SPECIAL_BREAK_WORDS):
					addTokenWithCheck(token);
					charSequence.delete(0, charSequence.length());
					specialWordFound = false;
					
					for (int j=0;j < specialBreakWords.length;j++)
					{
						if (i + specialBreakWords[j].length() < charArrayLength)
						{
							for (int k = i; k < (i+j);k++)
							{
								charSequence.append(charArray[k]);
							}
							
							if (specialBreakWords[j].contentEquals(charSequence))
							{
								addTokenNoCheck(specialBreakWords[j]);
								i = i + j;
								specialWordFound = true;
								break;
							}
						}
					}
					
					if (!specialWordFound)
					{
						addTokenNoCheck(currentChar);
					}
					break;*/
							
				case (ADD_TOKEN_BUFFER_LAST_CHARACTERS_WITH_CHECK_2):
					token.concat(currentChar);
					bufferTokenWithCheck(token, 2);
					token = "";
					i -= 1;; 
					break;
				
				case (ADD_TOKEN_BUFFER_LAST_CHARACTERS_WITH_CHECK_3):
					token.concat(currentChar);
					bufferTokenWithCheck(token, 3);
					token = "";
					i -= 2;
					break;
					
				case(ADD_TOKEN_BUFFER_LAST_CHARACTERS_WITH_CHECK_4):
					token.concat(currentChar);
					bufferTokenWithCheck(token, 4);
					token = "";
					i -= 3;
					break;
					
				case (ADD_TOKEN_BUFFER_LAST_CHARACTERS_NO_CHECK_2):
					token.concat(currentChar);
					bufferTokenNoCheck(token, 2);
					token = "";
					state=0;
					break;
				
				case (ADD_TOKEN_BUFFER_LAST_CHARACTERS_NO_CHECK_3):
					token.concat(currentChar);
					bufferTokenNoCheck(token, 3);
					token = "";
					state=0;
					break;
					
				case (ADD_TOKEN_BUFFER_LAST_CHARACTERS_NO_CHECK_4):
					token.concat(currentChar);
					bufferTokenNoCheck(token, 4);
					token = "";
					state = 0;
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
