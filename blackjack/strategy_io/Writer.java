package blackjack.strategy_io;

import java.io.*;

import blackjack.player.*;
import blackjack.environment.*;

/******************************************************************************
 *  File: Writer.java
 *  
 *  @author Alexey Munishkin
 *  
 *<p>
 *  Implement a strategy writer to a raw csv file. Prints out in time 
 *  progression or MemBrain Lesson input file.
 *</p>
 *
 ******************************************************************************/
  
public class Writer {  
	
	private PrintStream ps;
	private String[] data;			// depends on type selected
	private int num_of_rows = 0;	// max row size for data printing
	
    /*
     * Public calls -------------------------------------------------
     */
	public Writer(String csv_file, int num_of_rows) throws Exception {
		ps = new PrintStream(new File(csv_file));
		this.num_of_rows = num_of_rows;
		data = new String[num_of_rows];
		for (int i = 0; i < num_of_rows; i++) data[i] = "";
	}
	
	// Type 1 write: time progression of game -----------------------
	/**
	 * Add pattern data for time progression for game play.
	 * 
	 * @param player  time progression of game play for this player
	 * @param iplayer  index of player
	 * @param action  action taken by player
	 */
	public void add_move(Player player, int iplayer, String action) {
		data[iplayer] += action;
		switch (action) {
		case "H":
		case "D":
			data[iplayer] += (" " + player.get_recent_card());
			break;
		}
		data[iplayer] += ","; //end cell for raw csv file
	}
	/**
	 * Finalizes the data and puts it in the raw csv file.
	 */
	public void write_moves() {for (int i = 0; i < num_of_rows; i++) ps.println(data[i]);} 
	
	// Type 2 write: serves as input to MemBrain NN -----------------
	/**
	 * Generate header for MemBrain Lesson input raw csv file.
	 * 
	 * @param icols  number of inputs to MemBrain NN
	 */
	public void MB_add_header(int icols) {
		for (int icol = 0; icol < icols; icol++) {	//   sets size for next rows
			if ((icols-1) == icol) {data[0] += "Out,\n";} 
			else                   {data[0] += ("In" + icol + ",");}
		}
	}
	/**
	 * Add pattern data for MemBrain Lesson input raw csv file.
	 * 
	 * @param strategy_table  comes from HARD strategy read in by Reader
	 * @param irow  strategy number per row
	 * @param icols  number of inputs to MemBrain NN
	 * @param strategy  {H,S,Sr,D,I,Sp}
	 * @param stat  output to MemBrain NN
	 */
	public void MB_add_stat(String[][] strategy_table, int irow, int icols, String strategy, double stat) {
		int jrow = 1;
		int jcol = 1;
		for (int icol = 0; icol < icols; icol++) { 
			//System.out.println("ir= "+irow+" ic= "+icol);
			if((icols-1) != icol) { 				// --- input ---
				if (0 == icol % 10) {jrow++; jcol=1;}
				else                {jcol++;}
				//System.out.println("jr= "+jrow+" jc= "+jcol);
				if (strategy_table[jrow][jcol].equals(strategy)) {data[irow] += "1,";}
				else                                             {data[irow] += "0,";}
			} else {								// --- output ---
				data[irow] += ("" + stat + ","); // "\n" not needed b/c of header call
			}
		}
	}
	/**
	 * Finalizes the data and puts it in the raw csv file.
	 */
	public void MB_write() {write_moves();}
	
	/**
	 * Make sure to call this to close file after done.
	 */
	public void close() {ps.close();}
	
	// For debugging ------------------------------------------------
	public void print_data() {
		for (int i = 0; i < num_of_rows; i++) {
			System.out.println(data[i]); 
		}
	}
	public void print_data_from_file(String csv_file, int num_of_cols) throws Exception {
		Reader csv_reader = new Reader(csv_file,(num_of_rows+1),num_of_cols);
		csv_reader.read();
		csv_reader.close();
		System.out.println(csv_reader);
	}
	
	/*
     * Test client --------------------------------------------------
     */	
	public static void main(String[] args) throws Exception {
		String csv_file = "C:\\Users\\munis\\Documents\\_code\\Blackjack\\Blackjack-Simulator\\basic_hard_hist_out.csv";
		//
		CONSTS_IO.WRITER_TYPE type = CONSTS_IO.WRITER_TYPE.MEMBRAIN; // select tester
		//
		Writer csv_writer;
		int icols;
		switch (type) {
		case TIME1:
			csv_writer = new Writer(csv_file,1);
			Player player0 = new Player("0");
			icols = (int) Math.max((Math.random()*12),1); 
			System.out.println("Total moves= " + icols);
			for (int i = 0; i < icols; i++) {
				int j = (int) (Math.random() * (i+5));
				player0.deal_to(new Card(j),0);
				csv_writer.add_move(player0, 0, "H");
			}
			csv_writer.write_moves();
			break;
		default: // MEMBRAIN is default so complier doesn't complain...
		case MEMBRAIN:
			int num_of_strategies = 10; 	// number of data points
			csv_writer = new Writer(csv_file,num_of_strategies);
			csv_writer.print_data();
			icols = (270 + 1); 		// hard strategy columns plus 1 for output
			csv_writer.MB_add_header(icols);
			for (int i = 0; i < num_of_strategies; i++) {
				String csv_file2 = "C:\\Users\\munis\\Documents\\_code\\Blackjack\\Blackjack-Simulator\\data_stat\\"+i+".csv";
				Reader csv_reader = new Reader(csv_file2,29,11);
				csv_reader.read();
				csv_reader.close();
				//
				double stat = (int) (Math.random()*12); // output
				csv_writer.MB_add_stat(csv_reader.get_policy_table(),i,icols,"D",stat);
			}
			csv_writer.MB_write();
			break;
		}
		csv_writer.close();
		System.out.println("Data print raw");
		csv_writer.print_data();
		System.out.println();
		System.out.println("Data print file");
		csv_writer.print_data_from_file(csv_file,icols);
	}
	
}