package blackjack.environment;

/******************************************************************************
 *  File: Money.java
 *  
 *  @author Alexey Munishkin
 *
 *<p>
 *  Implement the money purse for use by the player (and dealer).
 *</p>
 *
 ******************************************************************************/

public class Money {
	final static int INIT_BET = CONSTS_EN.INIT_BET; 
	final static int PF_NORMAL = CONSTS_EN.PF_NORMAL;
	
	private double money_purse;       // money not betting currently
	private double money_bet;         // money currently betting
	
	/*
     * Public calls -------------------------------------------------
     */
	public Money(double init_money) {
		money_purse = init_money - INIT_BET;
		money_bet   = INIT_BET;
	}
	
	public double current_money() { return money_purse; }
	public double current_bet() { return money_bet; }
	
	// operations
	public void get_back_money() {win_money(PF_NORMAL-1);} // get bet money back
	public void win_money(double pf)  {money_purse += money_bet*(pf+1); money_bet=0;}
	public void lose_money() {money_bet=0;}
	public void bet_money(double bet_money)  {money_purse -= bet_money; money_bet += bet_money;}
}
