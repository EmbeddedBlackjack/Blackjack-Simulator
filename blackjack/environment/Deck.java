package blackjack.environment;

/******************************************************************************
 *  File: Deck.java
 *  
 *  (<a href=https://introcs.cs.princeton.edu/java/36inheritance/>link</a>)
 *
 *<p>
 *  Implement a deck of cards.
 *</p>
 *
 ******************************************************************************/

public class Deck { 
    final static int DECK_SIZE = CONSTS_EN.MAX_DECK_SIZE; 

    protected Card[] cards;          // the cards  - cards[0] is bottom of deck
    protected int N;                 // number of cards

    /*
     * Public calls -------------------------------------------------
     */
    /**
     * Constructor for creating a deck of cards.
     */
    public Deck()  {
        N = DECK_SIZE;
        cards = new Card[N];
        for (int i = 0; i < N; i++)
            cards[N - i - 1] = new Card(i);
    }

    public Card deal_from()   { return cards[--N]; }
    public boolean is_empty() { return (N == 0);   }
    public int size()         { return N;          }
    public void reset()		  { N = DECK_SIZE;     }

    /**
     * Shuffles cards.
     */
    public void shuffle() {
        for (int i = 0; i < N; i++) {
            int r = (int) (Math.random() * i);
            Card swap = cards[i];
            cards[i]  = cards[r];
            cards[r]  = swap;
        }
    }

    // For debugging ------------------------------------------------
    public String toString() {
        String s = "Deck  ";
        for (int i = N - 1; i >= 0; i--)
            s += cards[i] + " ";
        return s;
    }

    /*
     * Test client --------------------------------------------------
     */
    public static void main(String[] args) { 
    	Deck deck = new Deck();
    	System.out.println(deck); // ordered deck
    	deck.shuffle();
    	System.out.println(deck); // randomized deck
    }
}
