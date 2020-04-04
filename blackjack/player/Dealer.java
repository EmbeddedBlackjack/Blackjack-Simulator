package blackjack.player;

import blackjack.environment.Card;

/******************************************************************************
 *  File: Dealer.java
 *  
 *  (<a href=https://introcs.cs.princeton.edu/java/36inheritance/>link</a>)
 *  
 *<p>
 *  Implement a dealer that holds a pile of cards with only one hand.
 *</p>
 *
 ******************************************************************************/

public class Dealer {
	final static int MAX_CARDS = CONSTS_PL.MAX_CARDS;
	
	private Card[] cards = new Card[MAX_CARDS];   // the cards
	private int N;                                // total number of cards
	
	/*
     * Public calls -------------------------------------------------
     */
	public Dealer() {
		N = 0;
	}
	
	/**
	 * @return  first card of dealer's hand.
	 */
	public Card peak()          { return cards[0]; } // return first card
	public void deal_to(Card c) { cards[N++] = c; }  // add card
	public void reset()         { N = 0; }           // discard all cards
	public int num_of_cards()   { return N; }        // return total number of cards

	/**
	 * @return  value of dealer's hand.
	 */
    public int value() {
        int val = 0;
        boolean has_ace = false;
        for (int icard = 0; icard < N; icard++) {
        	val = val + cards[icard].rank();
        	if (cards[icard].is_ace()) has_ace = true;
        }
        if (has_ace && (val <= 11)) val = val + 10;  // handle ace = 1 or 11
        return val;
    }
    
    // For debugging ------------------------------------------------
    /**
     * Print out cards in dealer's hand.
     */
    public String toString() {
        String s = "dealer\n" + "  (" + value() + ")  ";
        for (int i = 0; i < N; i++) { s += cards[i] + " "; }
        return s;
    }

	/*
	 * Test client --------------------------------------------------
	 */
	public static void main(String[] args) { 
		Dealer dealer = new Dealer();
		System.out.println(dealer);
		System.out.println("Card number= " + dealer.num_of_cards());
		System.out.println();
		dealer.deal_to(new Card(15)); // put 4D card in hand
		dealer.deal_to(new Card(12)); // put AC card in hand
		dealer.deal_to(new Card(26)); // put 2H card in hand
		System.out.println(dealer);
		System.out.println("Card number= " + dealer.num_of_cards());
	}
}
