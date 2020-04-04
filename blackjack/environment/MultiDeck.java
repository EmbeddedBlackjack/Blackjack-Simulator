package blackjack.environment;

/******************************************************************************
 *  File: MultiDeck.java
 *  Extends: Deck.java
 *
 *	@author Alexey Munishkin
 *
 *<p>
 *  Implement a multi-deck of cards. Usual casinos use 2,4,6, or 8 decks
 *</p>
 *
 ******************************************************************************/

public class MultiDeck extends Deck {
	
    private int num_of_decks;			  // number of decks
    
    /*
     * Public calls -------------------------------------------------
     */
    public MultiDeck(int num_of_decks)  {
        this.num_of_decks = num_of_decks;
        N = DECK_SIZE*this.num_of_decks;  // number of cards
        cards = new Card[N];
        for (int i = 0; i < N; i++)
            cards[N - i - 1] = new Card(i);
    }
    
    public void reset() { N = (DECK_SIZE*num_of_decks); }

    // For debugging ------------------------------------------------
    public String toString() {
        String s = "MultiDeck: \n";
        for (int i = N - 1; i >= 0; i--) {
            s += cards[i] + " ";
            if (i % DECK_SIZE == 0) { s += "\n"; }
        }
        return s;
    }

    /*
     * Test client --------------------------------------------------
     */
    public static void main(String[] args) { 
    	MultiDeck multi_deck = new MultiDeck(2); // Test with 2 decks
    	System.out.println("Card number= " + multi_deck.size() + "\n");
    	System.out.println(multi_deck); // ordered multi-deck
    	multi_deck.shuffle();
    	System.out.println(multi_deck); // randomized multi-deck
    }
}
