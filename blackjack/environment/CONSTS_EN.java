package blackjack.environment;

/******************************************************************************
 *  File: CONSTS_EN.java
 *  
 *<p>
 *  Not code, but used for constants in blackjack.environment .
 *</p>
 *
 ******************************************************************************/

public class CONSTS_EN {
	public static int MAX_CARDS     = 52;   // max number of cards in a standard deck
	public static int MAX_DECK_SIZE = 52;	// max number of cards in a deck
	
	public static int INIT_BET        = 0;	// default bet when starting game
	public static int    PF_NORMAL    = 1;
	public static double PF_BLACKJACK = 1.5;// normal=1, blackjack=1.5, insurance=2
	public static int    PF_INSURANCE = 2;
}
