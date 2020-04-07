package blackjack;

import blackjack.environment.*;
import blackjack.player.*;
import blackjack.strategy_io.*;

/******************************************************************************
 *  File: GameStats.java
 *  
 *  @author Alexey Munishkin
 *	
 *<p>
 *  Implement the game BlackJack with multi-deck support for multiple players
 *  against one dealer. Prints game play to raw csv file. Reads strategy from
 *  raw csv file.
 *</p>
 *<p>
 *	Runs multiple game plays (can be on order of thousands) to generate
 *  empirical statistics about particular strategy for BlackJack.
 *</p>
 *
 ******************************************************************************/

public class GameStats extends Game {
	
	private double[] emp_expected_profit;
	
	/*
     * Public calls -------------------------------------------------
     */
	public GameStats(String csv_infile_dir, String csv_outfile_dir, int num_of_strategies) throws Exception {
		//
		emp_expected_profit = new double[num_of_strategies];
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
	
	public void gamestats_N_sim(int N, int istrategy) {
		emp_expected_profit[istrategy] = 0; // reset for new calc
		for (int i = 0; i < N; i++) {
			game_reset_money(0); // default: 1 player
			game_init();
			game_sim(istrategy);
			game_status();
			emp_expected_profit[istrategy] += (1.0/(double)N)*game_profit(0); // default: 1 player
		}
	}
	
	public void gamestats_MB_add_stat(int istrategy) {
		game_MB_add_stat(istrategy,emp_expected_profit[istrategy]);
	}
	
	// For debugging ------------------------------------------------
    public void print_strategy_stats_info(int istrategy) {
    	System.out.println("Player0 expected profit= " + emp_expected_profit[istrategy]);
    }
	
	/*
     * Test client --------------------------------------------------
     */
    public static void main(String[] args) throws Exception {
    	String csv_infile_dir = "C:\\Users\\munis\\Documents\\_code\\EmbeddedBlackjack\\Blackjack-Simulator\\";
    	String csv_outfile_dir= "C:\\Users\\munis\\Documents\\_code\\EmbeddedBlackjack\\Blackjack-Simulator\\";
    	//
    	GameStats bj_game_stat;
    	csv_infile_dir += "data_in_stat\\";
    	csv_outfile_dir += "MB_data_in\\";
    	int tot_num_of_strategies = 12; 			// 12 strategies
    	int tot_runs = 20000;						// 20000 sim runs per strategy
    	//
    	bj_game_stat = new GameStats(csv_infile_dir,csv_outfile_dir,tot_num_of_strategies); 
    	bj_game_stat.game_MB_add_header();
    	for (int i = 0; i < tot_num_of_strategies; i++) {
    		bj_game_stat.gamestats_N_sim(tot_runs,i);
        	bj_game_stat.gamestats_MB_add_stat(i); 	// add to MemBrain data
        	bj_game_stat.print_strategy_stats_info(i);
    	}
    	bj_game_stat.game_MB_finish();
    	System.out.println("See outfile for data out details");
    }
	
}