package edu.nps.jody.GappyAndroidActivity;

public class MembershipCheck 
{
	//Data Members
	public static final int DROP_WORD 	= 0;
	public static final int TAG_UNK 			= 1;
	
	//Constructors
	//All of MembershipCheck is static, so no constructors
	
	//Methods
	//TODO Implement actual unigram checking method to verify individual words
	public static String check(int checkType, String text)
	{
		switch(checkType)
		{
		case DROP_WORD:
			break;
			
		case TAG_UNK:
			break;
		}
		
		return text;
	}
	
	//TODO Implement GB/OSB membership checking routine
}
