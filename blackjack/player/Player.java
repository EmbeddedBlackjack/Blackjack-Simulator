package blackjack.player;

import blackjack.environment.Card;

/******************************************************************************
 *  File: Player.java
 *  
 *  (<a href=https://introcs.cs.princeton.edu/java/36inheritance/>link</a>)
 *  
 *<p>  
 *  Implement a player that holds a pile of cards.
 *</p>
 *
 ******************************************************************************/

public class Player {  
	final static int MAX_CARDS = CONSTS_PL.MAX_CARDS;
	final static int MAX_HANDS = CONSTS_PL.MAX_HANDS;
    
    private Card[][] cards = new Card[MAX_HANDS][MAX_CARDS]; // the cards
    private Card recent_card;
    private int[] num_of_cards = new int[MAX_HANDS];         // number of cards per hand
    private int N = 0;                                       // total number of cards
    private int jhand = 1;
    private String name;                                     // player's name
    
    /*
     * Public calls -------------------------------------------------
     */
    public boolean flag_insurance = false;
    
    public Player(String name) {
        this.name = name;
        for (int i = 0; i < MAX_HANDS; i++) { num_of_cards[i] = 0; }
    }
    
    public Card peak(int ihand) { return cards[ihand][0]; }
    
    /**
     * Move card from ihand to next empty hand.
     * 
     * @param ihand  index of hand to split
     */
    //  possible only when two same cards
    public void split(int ihand) { 
    	Card splitted_card = cards[ihand][--num_of_cards[ihand]];
    	cards[jhand][num_of_cards[jhand]++] = splitted_card;
    	jhand++; // increment to next empty hand for future split
    }
    
    /**
     * Insert card into specific hand of player.
     * 
     * @param c  card to add to hand.
     * @param hand  hand which card is added to.
     */
    public void deal_to(Card c, int ihand) { 
    	recent_card = c; 
    	cards[ihand][num_of_cards[ihand]++] = c; 
    	N++; 
    } 
    
    public void reset() { 		// discard all cards
    	for (int i = 0; i < MAX_HANDS; i++)
    		num_of_cards[i] = 0; 
    	N=0; 
    	jhand=1; 				// reset parameters
    } 
    public int num_of_cards()             { return N; }             // return total number of cards
    public int num_of_cards(int ihand)    { return num_of_cards[ihand]; }
    public int get_current_splitted_hand(){ return (jhand-1); }
    public Card get_recent_card()         { return recent_card; }	// useful for printing time progression
    public boolean same_card_ranks(int ihand) {
    	boolean flag = false;
    	if (num_of_cards(ihand) == 2) {
    		flag = (cards[ihand][1].rank() == cards[ihand][0].rank());
    	}
    	return flag;
    }
    
    /**
     * @param ihand  particular player's hand
     * @return  value of player's hand
     */
    public int value(int ihand) {
        int val = 0;
        boolean has_ace = false;
        for (int icard = 0; icard < num_of_cards[ihand]; icard++) {
        	val += cards[ihand][icard].rank();
        	if (cards[ihand][icard].is_ace()) has_ace = true;
        }
        if (has_ace && (val <= 11)) val = val + 10;    // handle ace = 1 or 11
        return val;
    }

    // For debugging ------------------------------------------------
    /**
     * Print out cards in player's hands.
     */
    public String toString() {
        String s = name;
        for (int ihand = 0; ihand < MAX_HANDS; ihand++) {
        	s = s + "\n  hand" + ihand + " (" + value(ihand) + ")  ";
            for (int i = 0; i < num_of_cards[ihand]; i++)
                s += cards[ihand][i] + " ";
        }
        return s;
    }
    
    /*
     * Test client --------------------------------------------------
     */
    public static void main(String[] args) { 
    	Player player = new Player("gambler");
    	System.out.println(player);
    	System.out.println("Card number= " + player.num_of_cards());
    	System.out.println();
    	player.deal_to(new Card(15), 0); // put 4D card in 0nd hand
    	player.deal_to(new Card(12), 0); // put AC card in 0nd hand
    	player.deal_to(new Card(26), 0); // put 2H card in 0rd hand
    	System.out.println(player);
    	System.out.println("Card number= " + player.num_of_cards());
    	System.out.println();
    	System.out.println("Split recent two cards!");
    	player.split(0); // 
    	player.split(0);
    	System.out.println(player);
    	System.out.println("Card number= " + player.num_of_cards());
    }
}
