package blackjack.strategy_io;

import java.io.*;  
import java.util.Scanner;

/******************************************************************************
 *  File: Reader.java
 *  
 *  @author Alexey Munishkin
 *  
 *<p>
 *  Implement a strategy reader from a raw csv file.
 *</p>
 *
 ******************************************************************************/
  
public class Reader {  
	
	private Scanner sc;
	private String[][] data;            // strategy table
	private int num_of_rows = 0;
	private int num_of_cols = 0; 		// keep track of strategy table size
	
    /*
     * Public calls -------------------------------------------------
     */
	public Reader(String csv_file, int num_of_rows, int num_of_cols) throws Exception {
		sc = new Scanner(new File(csv_file));
		sc.useDelimiter(",");   //sets the delimiter pattern 
		//
		this.num_of_rows = num_of_rows;
		this.num_of_cols = num_of_cols;
		data = new String[num_of_rows][num_of_cols];
	}
	
	public void read() {
		int irow = 0;
		int icol = 0;
		while (sc.hasNext()) {  
			data[irow][icol] = sc.next(); 
			icol++;
			if (num_of_cols == icol) {irow++; icol=0;}
			if (num_of_rows == irow) break; // stop scanning as can't handle any more
		}
	}
	
	/**
	 * Make sure to call this to close scanner after done.
	 */
	public void close() {sc.close();} 
	public String[][] get_policy_table() { return data; }
	public String get_policy(int irow, int icol) { return data[irow][icol]; }
	
	// For debugging ------------------------------------------------
    /**
     * Prints out current data which just read in.
     */
    public String toString() {
        String s = "";
        for (int irow = 0; irow < num_of_rows; irow++)
        	for (int icol = 0; icol < num_of_cols; icol++)
        		s += (" " + data[irow][icol]);
        return s;
    }
	
	/*
     * Test client --------------------------------------------------
     */
	public static void main(String[] args) throws Exception {
		String csv_file = "C:\\Users\\munis\\Documents\\_code\\EmbeddedBlackjack\\Blackjack-Simulator\\basic_hard.csv";
		Reader csv_reader = new Reader(csv_file,29,11);
		System.out.println("rows = " + csv_reader.num_of_rows + " cols = " + csv_reader.num_of_cols);
		System.out.println();
		System.out.println(csv_reader);
		csv_reader.read();
		csv_reader.close();
		System.out.println();
		System.out.println(csv_reader);
		System.out.println();
		System.out.println(csv_reader.get_policy(18,10));
	}  
	
}
