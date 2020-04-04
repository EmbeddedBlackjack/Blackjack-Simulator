package blackjack;

import blackjack.environment.*;
import blackjack.player.*;
import blackjack.strategy_io.*;

/******************************************************************************
 *  File: Game.java
 *  
 *  @author Alexey Munishkin
 *	
 *<p>
 *  Implement the game BlackJack with multi-deck support for multiple players
 *  against one dealer. Prints game play to raw csv file. Reads strategy from
 *  raw csv file.
 *</p>
 *
 ******************************************************************************/

public class Game {
	final static int BLACKJACK_VAL = 21;   // value for blackjack
	final static int BLACKJACK_NUM = 2;    // number of cards for blackjack
	final static int INIT_HAND = 0;		   // start hard index
	final static int INIT_MONEY= 0;
	final static int MAX_HANDS = CONSTS_PL.MAX_HANDS;
	final static int MAX_SPLITS = (MAX_HANDS-1);

	protected Reader[] strategies;		// Strategies
	protected Writer[] output_writer;		// 6-writers at max {H,S,Sr,D,I,Sp}
	protected CONSTS_IO.WRITER_TYPE type;   // type of output data
	protected MultiDeck multi_deck;         // multi-deck
	protected int num_of_splits = 0;
	protected int num_of_decks;			    // number of decks
	protected int num_of_players;           // number of non-dealer players
	protected Money[][] players_money;		// player(s) money per hand
	protected Player[] players; 			// player(s)
	protected Dealer dealer;				// dealer
	
	//---------------------------------------------------------------
	protected boolean flag_player_blackjack = false;
	protected boolean flag_dealer_blackjack = false;
	
	protected enum GAME_STATE {
		HAND_WIN,  // terminal conditions
		HAND_LOSE, //  flags above influence too
		HAND_DRAW;
	}
	//---------------------------------------------------------------

	/** 
	 * Check blackjack for dealer's hand
	 * 
	 * @param dealer  dealer agent
	 * @return  true if dealer has blackjack, false otherwise
	 */
	protected boolean check_blackjack(Dealer dealer) {
		return (dealer.value()==BLACKJACK_VAL)&&(dealer.num_of_cards()==BLACKJACK_NUM);
	}
	/**
	 * Check blackjack for player's hand
	 * 
	 * @param player  player agent
	 * @param ihand  player's hand to check
	 * @return  true if player's hand has blackjack, false otherwise
	 */
	protected boolean check_blackjack(Player player, int ihand) {
		return (player.value(ihand)==BLACKJACK_VAL)&&(player.num_of_cards(ihand)==BLACKJACK_NUM);
	}
	
	/**
	 * Blackjack "hit": adds a card to agent
	 * 
	 * @param dealer  dealer agent
	 */
	protected void hit(Dealer dealer) {dealer.deal_to(multi_deck.deal_from());}
	/**
	 * Blackjack "hit": adds a card to agent
	 * 
	 * @param player  player agent
	 * @param ihand  player's hand to add card
	 */
    protected void hit(Player player, int ihand) {player.deal_to(multi_deck.deal_from(),ihand);}

    /**
     * Called at start of new game: deal out two cards each
     */  
    protected void deal() {
        // deal cards to players then dealer
        for (int i = 0; i < this.num_of_players; i++) { hit(players[i],INIT_HAND); }
        hit(dealer);
        for (int i = 0; i < this.num_of_players; i++) { hit(players[i],INIT_HAND); }
        hit(dealer);
    }
    
    /**
     * Update player status for particular hand
     * 
     * @param player  player agent
	 * @param ihand  player's hand to update
     * @return  {DRAW,LOSE,WIN} for hand
     */
    protected GAME_STATE update_status(Player player, int ihand) {
    	GAME_STATE state;
    	//
    	if (check_blackjack(player,ihand)) flag_player_blackjack = true; // **Win (1.5x if one hand)
    	if (check_blackjack(dealer))      flag_dealer_blackjack = true;  // **Lose (unless Insurance **Win 2x)
    	//
    	if (player.value(ihand) == dealer.value())      state = GAME_STATE.HAND_DRAW; // Push == **Draw
    	else if (player.value(ihand) > BLACKJACK_VAL)   state = GAME_STATE.HAND_LOSE; // Bust == **Lose
        else if (dealer.value() > BLACKJACK_VAL)        state = GAME_STATE.HAND_WIN;  // **Win
        else if (player.value(ihand) >  dealer.value()) state = GAME_STATE.HAND_WIN;  // **Win
        else if (player.flag_insurance&&flag_dealer_blackjack) state = GAME_STATE.HAND_WIN;
        else 										    state = GAME_STATE.HAND_LOSE;
    	//
    	return state;
    }
    
    /**
     * Resets all flags for all players and dealer
     */
    protected void reset_status() {
    	flag_player_blackjack = false;
    	flag_dealer_blackjack = false;
    	for (int i = 0; i < num_of_players; i++) {players[i].flag_insurance = false;}
    }
    
    
    /*
     * Public calls -------------------------------------------------
     */
    public Game() {
    	// TODO
    }
    
    /**
     * Constructor for making a new game. This version outputs time 
     * progression of a game for the HARD strategy.
     * 
     * @param csv_infile  input HARD strategy raw csv file 
     * @param csv_outfile  output raw csv file for time progression
     * @param num_of_players  number of players for game
     * @param num_of_decks  number of decks for game
     * @throws Exception  io
     */
    public Game(String csv_infile, String csv_outfile, int num_of_players, int num_of_decks) throws Exception {
    	// load strategies
    	strategies = new Reader[1]; 				// default: 1 strategy for time progression
    	strategies[0] = new Reader(csv_infile,29,11);
    	strategies[0].read();
    	strategies[0].close();
    	// setup for output file
    	type = CONSTS_IO.WRITER_TYPE.TIME1; 		// default: time progression
    	output_writer = new Writer[1];
    	output_writer[0] = new Writer(csv_outfile,1);
    	// create new multi-deck and randomize it
    	this.num_of_decks = num_of_decks;
    	multi_deck = new MultiDeck(this.num_of_decks); 
    	multi_deck.shuffle();
    	//
    	// create seats for multi-player and add money
    	this.num_of_players = num_of_players;
    	players = new Player[num_of_players];   
    	players_money = new Money[MAX_HANDS][num_of_players];
    	for (int i = 0; i < num_of_players; i++) {
            players[i] = new Player(""+i); 			// default: player names are numbers
            for (int ihand = 0; ihand < MAX_HANDS; ihand++) players_money[ihand][i] = new Money(INIT_MONEY);
    	}
    	// add dealer
    	dealer = new Dealer();
    }
    /**
     * Constructor for making a new game. This version outputs data for 
     * MemBrain NN software from running the HARD strategy.
     * 
     * @param csv_infile_dir  directory where multiple HARD strategies
     * @param csv_outfile_dir  directory for output data for MemBrain
     * @param num_of_strategies  number of strategies to go over for MemBrain
     * @throws Exception  io
     */
    public Game(String csv_infile_dir, String csv_outfile_dir, int num_of_strategies) throws Exception {
    	// load strategies
    	strategies = new Reader[num_of_strategies];
    	for (int i = 0; i < num_of_strategies; i++) {
    		String csv_infile = (csv_infile_dir + i + ".csv"); // default: strategies numbered starting with 0
    		strategies[i] = new Reader(csv_infile,29,11);
    		strategies[i].read();
        	strategies[i].close();
    	}
    	// setup for output file
    	this.type = CONSTS_IO.WRITER_TYPE.MEMBRAIN;
    	output_writer = new Writer[6];
    	for (int i = 0; i < 6; i++) {
    		String csv_outfile = (csv_outfile_dir + i + ".csv"); // default: output to numbers
    		output_writer[i] = new Writer(csv_outfile,num_of_strategies);
    	}
    	// create new multi-deck and randomize it
    	this.num_of_decks = 6;					// default: 6 deck
    	multi_deck = new MultiDeck(this.num_of_decks); 
    	multi_deck.shuffle();
    	//
    	// create seats for multi-player and add money
    	this.num_of_players = 1;				// default: 1 player
    	players = new Player[num_of_players];   
    	players_money = new Money[MAX_HANDS][num_of_players];
    	for (int i = 0; i < num_of_players; i++) {
            players[i] = new Player(""+i); 		// default: player names are numbers
            for (int ihand = 0; ihand < MAX_HANDS; ihand++) players_money[ihand][i] = new Money(INIT_MONEY);
    	}
    	// add dealer
    	dealer = new Dealer();
    }
    
    /**
     * Initialization for blackjack game:
     * resets card hands for players and dealer, and shuffle deck.
     */
    public void game_init() {
    	multi_deck.reset();
    	multi_deck.shuffle();
    	num_of_splits = 0; 	// reset
    	for (int i = 0; i < this.num_of_players; i++) { players[i].reset(); }
        dealer.reset();
    	deal();
    }
    
    /**
     * Simulate one game play of blackjack.
     * 
     * @param istrategy  index of strategy to run with
     */
    public void game_sim(int istrategy) {
    	// players' moves
    	for (int i = 0; i < num_of_players; i++) {
    		for (int ihand = 0; ihand < (num_of_splits+1); ihand++) {
    			// each players' bet per hand
        		players_money[ihand][i].bet_money( 1 ); 		// TODO:
    			//
    			boolean flag_play_game = true;
    			while( flag_play_game ) {
    				//
    				int irow = 0;
    				int icol = 0;
    				if (players[i].same_card_ranks(ihand)) { 	// Splitable strategy
    					//System.out.println("Splitable!");
    					irow = 18 + (players[i].peak(ihand)).rank() - 1;
    					if (irow==18) irow += 10;	// ace
    				} else { 									// HARD strategy
    					irow = players[i].value(ihand) - 3;
    					if (irow>18) break; 		// bust == **lose
    				}
    				icol = (dealer.peak()).rank() - 1;
					if (icol==0) icol=10; 			// ace
					//System.out.println("r= "+irow+" c= "+icol);
    				String action = strategies[istrategy].get_policy(irow,icol);
    				if (CONSTS_IO.WRITER_TYPE.TIME1 == type) output_writer[0].add_move(players[i],i,action);
    				//
    				double bet = 0;
    	    		switch (action) {
    	    		case "H":
    	    			hit(players[i],ihand);
    	    			break;
    	    		case "S":
    	    			flag_play_game = false;
    	    			break;
    	    		case "Sr":
    	    			bet = players_money[ihand][i].current_bet()/2.0;
    	    			players_money[ihand][i].get_back_money();
    	    			players_money[ihand][i].bet_money(bet);
    	    			players_money[ihand][i].lose_money();
    	    			flag_play_game = false;
    	    			break;
    	    		case "D":
    	    			bet = players_money[ihand][i].current_bet();
    	    			players_money[ihand][i].bet_money(bet);
    	    			hit(players[i],ihand);
    	    			flag_play_game = false;
    	    			break;
    	    		case "I":
    	    			bet = players_money[ihand][i].current_bet()/2.0;
    	    			players_money[ihand][i].bet_money(bet);
    	    			players[i].flag_insurance = true;
    	    			flag_play_game = false;
    	    			break;
    	    		case "Sp":
    	    			if (num_of_splits < MAX_SPLITS) {
    	    				players[i].split(ihand); 
    	    				//System.out.println(players[i]);
    	    				hit(players[i],ihand);
    	    				hit(players[i],players[i].get_current_splitted_hand());
    	    				num_of_splits++;
    	    			} else {
    	    				flag_play_game = false;
    	    			}
    	    			break;
    	    		}
    	    	}
    		}
    	}
    	// dealer's move
    	while ( dealer.value() < CONSTS_PL.DEARLER_VAL ) {hit(dealer);}
    }
    
    /**
     * Update current status for current end game.
     */
    public void game_status() {
    	for (int i = 0; i < num_of_players; i++) {
    		for (int ihand = 0; ihand < (num_of_splits+1); ihand++) {
    			GAME_STATE state = update_status(players[i],ihand);
    			//
    			switch (state) {
    			case HAND_DRAW:
    				players_money[ihand][i].get_back_money();
    				//System.out.println("DRAW");
    				break;
    			case HAND_WIN:
    				if (flag_player_blackjack) {
    					players_money[ihand][i].win_money(CONSTS_EN.PF_BLACKJACK);
    					//System.out.println("WIN 1.5x");
    				} else if(players[i].flag_insurance) {
    					players_money[ihand][i].win_money(CONSTS_EN.PF_INSURANCE);
    					//System.out.println("WIN 2x");
    				} else {
    					players_money[ihand][i].win_money(CONSTS_EN.PF_NORMAL);
    					//System.out.println("WIN");
    				}
    				break;
    			case HAND_LOSE:
    				players_money[ihand][i].lose_money();
    				//System.out.println("LOSE");
    				break;
    			}
    			reset_status();
    		}
    	}
    	if (CONSTS_IO.WRITER_TYPE.TIME1 == type) {output_writer[0].write_moves();}
    }
    
    /**
     * Net profit for player.
     * 
     * @param iplayer  index of player to check
     * @return  net profit
     */
    public double game_profit(int iplayer) {
    	double profit = 0;
    	for (int ihand = 0; ihand < MAX_HANDS; ihand++) 
    		profit += (players_money[ihand][iplayer].current_money()-INIT_MONEY);
    	return profit;
    }
    
    public void game_reset_money(int iplayer) {
    	for (int ihand = 0; ihand < MAX_HANDS; ihand++)
    		players_money[ihand][iplayer] = new Money(INIT_MONEY);
    }
    
    
    // calls for MB database ----------------------------------------
    public void game_MB_add_header() {
    	for (int i = 0; i < 6; i++) output_writer[i].MB_add_header(271);
    }
    public void game_MB_add_stat(int istrategy, double stat) {
    	for (int i = 0; i < 6; i++) {
    		String action = "";
    		switch (i) { 
    		case 0: action = "H"; break;	// hit
    		case 1: action = "S"; break;	// stand
    		case 2: action = "Sr"; break;	// surrender
    		case 3: action = "D"; break;	// double down
    		case 4: action = "I"; break;	// insurance
    		case 5: action = "Sp"; break;	// spilt
    		}
    		output_writer[i].MB_add_stat(strategies[istrategy].get_policy_table(),istrategy,271,action,stat);
    	}
    }
    public void game_MB_finish() {
    	for (int i = 0; i < 6; i++) output_writer[i].MB_write();
    }
    
        
    // For debugging ------------------------------------------------
    public void print_player_info(int iplayer) {
    	System.out.println(players[iplayer]);
    }
    public void print_dealer_info() {
    	System.out.println(dealer);
    }
    
    /*
     * Test client --------------------------------------------------
     */
    public static void main(String[] args) throws Exception {
    	String csv_infile_dir = "C:\\Users\\munis\\Documents\\_code\\Blackjack\\Blackjack-Simulator\\";
    	String csv_outfile_dir= "C:\\Users\\munis\\Documents\\_code\\Blackjack\\Blackjack-Simulator\\";
    	//
    	CONSTS_IO.WRITER_TYPE type = CONSTS_IO.WRITER_TYPE.TIME1; // select tester
    	//
    	Game bj_game;
    	switch (type) {
    	case TIME1:
    		String csv_infile = (csv_infile_dir + "basic_hard.csv");
    		String csv_outfile= (csv_outfile_dir+ "basic_hard_hist_out.csv");
    		bj_game = new Game(csv_infile,csv_outfile,1,6); // 1 player, 6 decks  
    		bj_game.game_init();
        	bj_game.game_sim(0); 				// play one game for strategy0 (default)
        	bj_game.game_status();
    		break;
    	default: // MEMBRAIN is default so complier doesn't complain...
    	case MEMBRAIN:
    		csv_infile_dir += "data_stat\\";
    		csv_outfile_dir += "MB_data_in\\";
    		int tot_num_of_strategies = 10; 	// 10 strategies
    		bj_game = new Game(csv_infile_dir,csv_outfile_dir,tot_num_of_strategies); 
    		bj_game.game_MB_add_header();
    		for (int i = 0; i < tot_num_of_strategies; i++) {
    			bj_game.game_init();
        		bj_game.game_sim(i); 			// play one game per strategy
        		bj_game.game_status();
        		bj_game.game_MB_add_stat(i,bj_game.game_profit(0)); // add to MemBrain data
    		}
    		bj_game.game_MB_finish();
    		break;
    	}
    	System.out.println("See outfile for data out details");
    	double profit = bj_game.game_profit(0); // get profit from 1st player
    	bj_game.print_player_info(0);
    	bj_game.print_dealer_info();
    	System.out.println("Player0 profit= " + profit);
    }
    
}
