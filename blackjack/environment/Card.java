package blackjack.environment;

/******************************************************************************
 *  File: Card.java
 *  
 *  (<a href=https://introcs.cs.princeton.edu/java/36inheritance/>link</a>)
 *
 *<p>
 *  Implement a playing card in a standard 52 card deck.
 *</p>
 *
 ******************************************************************************/

public class Card { 
	final static int MAX_CARDS = CONSTS_EN.MAX_CARDS;
	
    private int suit; // Hearts, Clubs, Diamonds, Spades
    private int rank; // number from 1 to 13

    /*
     * Public calls -------------------------------------------------
     */
    /**
     * Constructor for creating a new card with suit {Hearts,Clubs,Diamonds,Spades}
     * and with rank {1 to 13}.
     * 
     * @param card  number that maps to a particular card
     */
    public Card(int card) {
    	int tmp_card = card % MAX_CARDS;  // make sure card is within range
        rank = tmp_card % 13;
        suit = tmp_card / 13;
    }

    /**
     * Checks is a card is an ace.
     * 
     * @return  true if an ace, false otherwise
     */
    public boolean is_ace() { return rank == 12; }
 
    /**
     * @return  1 for ace, 10 for jqk, actual rank for other cards.
     */
    public int rank()  {
        if (rank == 12) return  1;
        if (rank >=  8) return 10;
        return rank + 2;
    }

    // For debugging ------------------------------------------------
    /**
     * Represent cards like "2H", "9C", "JS", "AD".
     */
    public String toString() {
        String ranks = "23456789TJQKA";
        String suits = "CDHS";
        return ranks.charAt(rank) + "" + suits.charAt(suit);
    }


}

