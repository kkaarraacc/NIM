package ClientServer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class NimGame {
	
	public int heap_selection;
	public int taken;
	public boolean game_active;
	public boolean players_connected;
	public String[] players = new String[2];
	public int[] heap_rules = new int[] {7, 5, 3};
	public int[] heap_marbles = new int[3]; // three heaps
	public int turn = 0;
	public int player_ready = 0;
	public Scanner inputscanner = new Scanner(System.in); // user input
	public String playerinput;
	public String[] name_input = new String[2];
	public boolean game_initialized = false;
	
	
	public void startGame(OutputStreamWriter osw, InputStreamReader isr, int playerID, Socket connection, boolean winner) throws InterruptedException, IOException {
		initializeVariables();
		game_active = true;
		if (!game_initialized) { //identify player first time
			// identify player
	    	if(playerID <= 1) {
	    		osw.write("read" + (char)13); osw.flush();
	    		osw.write("You are Player " + (playerID+1) + (char)13 ); osw.flush(); // end the stream with (char)13
	    	} else {
	    		osw.write("read" + (char)13); osw.flush();
	    		osw.write("Game is full. Connection closed." + (char)13); osw.flush();
	    		connection.close();
	    	}
			osw.write("read" + (char)13); osw.flush();
			osw.write("Enter your name: " + (char)13 ); osw.flush();
			osw.write("write" + (char)13); osw.flush();
			name_input[playerID] = listenForInput(isr);
	    	player_ready ++;
	    	if (player_ready < 2) {
	    		osw.write("read" + (char)13); osw.flush();
	    		osw.write("Hi " + name_input[playerID] + "\n" + "Waiting for other player..." + (char)13); osw.flush();
	    	} else {
	    		osw.write("read" + (char)13); osw.flush();
	    		osw.write("Starting game..." + (char)13); osw.flush();
	    	}
	    	players[0] = playerID == 0 ? name_input[playerID] : players[0];
	    	players[1] = playerID == 1 ? name_input[playerID] : players[1];
	    	while(player_ready < 2) {
	    		Thread.sleep(100);
	    	}
	    	game_initialized = true;
		}
    	
		do {
			if (turn == playerID) {
				getPlayerInput(osw, isr); // player input
				heap_marbles[heap_selection] -= taken;
				if (gameOver()) {
					turn = (turn + 1) % 2;
					winner = true;
					osw.write("read" + (char)13); osw.flush();
					osw.write(getResults() + (char)13); osw.flush();
					game_active = false;
				}
				else
					turn = (turn + 1) % 2;
			} else {
				osw.write("read" + (char)13); osw.flush();
				osw.write(getTableStatus() + "\n" + "It's " + players[(playerID+1)%2] + "'s turn..." +  (char)13); osw.flush();
				Thread.sleep(100);
		    	while (turn == (playerID+1)%2)
		    		Thread.sleep(100);
			    if (game_active) {
		    		osw.write("read"); osw.flush();
		    		osw.write(players[(playerID+1)%2] + " took " + taken + " marbles from heap" + heap_selection + (char)13); osw.flush();
			    } else {
			    	winner = false;
			    	osw.write("read" + (char)13); osw.flush();
					osw.write(getResults() + (char)13); osw.flush();
			    }
			}
		} while(game_active);
		player_ready = 0;
		playAgain(osw, isr, playerID, connection, winner);
	}
	
	public void getPlayerInput(OutputStreamWriter osw, InputStreamReader isr) throws IOException { // confirm the input with server
		taken = 0;
		heap_selection = 0;
		do {
			osw.write("read" + (char)13); osw.flush();
			osw.write(getTableStatus() + "\n" + players[turn] + ", which heap will you take from?" + (char)13); osw.flush();
			osw.write("write" + (char)13); osw.flush();
			playerinput = listenForInput(isr);
			heap_selection = Integer.valueOf(playerinput)-1; // -1 translates the intended heap number to its actual location in the heap_marbles array
			if (heap_selection >= 0 && heap_selection < heap_rules.length) {
				if (heap_marbles[heap_selection] == 0) {
					osw.write("read" + (char)13); osw.flush();
					osw.write("That heap has no marbles" + (char)13); osw.flush();
					heap_selection = -1; // bounds for valid input are from 0 to 2
				}
			}
		} while (heap_selection <= -1 || heap_selection >= heap_rules.length);	
		do {
			osw.write("read" + (char)13); osw.flush();
			osw.write(getTableStatus() + "\n"+ "How many marbles you will take?" + (char)13); osw.flush();
			osw.write("write" + (char)13); osw.flush();
			playerinput = listenForInput(isr);
			taken = Integer.valueOf(playerinput);		
				if (taken > heap_marbles[heap_selection])
					taken = heap_marbles[heap_selection];
		} while (taken <= 0);
	}
	
	public String getResults() {
		StringBuilder string = new StringBuilder();
		string.append(players[(turn+1)%2] + " loses...\n");
		string.append(players[turn] + " wins!\n");
		return string.toString();
	}
	
	public boolean gameOver() {
		int count = 0;
		for (int i = 0; i < heap_marbles.length; i++) {
			count += heap_marbles[i];
		}
		if (count == 0)
			return true;
		return false;
	}
	
	public void playAgain(OutputStreamWriter osw, InputStreamReader isr, int playerID, Socket connection, boolean winner) throws InterruptedException, IOException {
		boolean exit = false;
		String playerOne;
		String playerTwo;
		String temp;
		do {
			osw.write("read" + (char)13); osw.flush();
			osw.write("Play Again?<y/n>: " + (char)13); osw.flush();
			osw.write("write" + (char)13); osw.flush();
			playerinput = listenForInput(isr);
			if (playerinput.equals("y")) {
				player_ready ++;
		    	if (player_ready < 2) {
		    		osw.write("read" + (char)13); osw.flush();
		    		osw.write("Waiting for other player..." + (char)13); osw.flush();
		    		playerID = winner ? 1 : 0;
		    		playerOne = winner ? players[0] : players[1];
		    		playerTwo = winner ? players[1] : players[0];
		    	} else {
		    		osw.write("read" + (char)13); osw.flush();
		    		osw.write("Starting game..." + (char)13); osw.flush();
		    		playerID = winner ? 1 : 0;
		    		playerOne = winner ? players[0] : players[1];
		    		playerTwo = winner ? players[1] : players[0];
		    	}
		    	while(player_ready < 2) {
		    		Thread.sleep(100);
		    	}
		    	players[0] = playerOne;
		    	players[1] = playerTwo;
				startGame(osw, isr, playerID, connection, winner);
			}
			else if (playerinput.equals("n"))
				exit = true;
		} while (!exit);	
	}
	
	public String getTableStatus() {
		StringBuilder string = new StringBuilder();
		string.append("-=Current Table=-\n"); // output to player
		for (int i = 0; i < heap_marbles.length; i++) {
			string.append("Heap" + (i+1) + ": " + heap_marbles[i] + " marbles \n");
		}
		return string.toString();
	}
	
	public void initializeVariables() {
		for (int i = 0; i < heap_rules.length; i++) {
			heap_marbles[i] = heap_rules[i];
		}
		turn = 0;
	}
	
	public String listenForInput(InputStreamReader isr) throws IOException {
		int c;
		StringBuffer buffer = new StringBuffer();
        while ( (c = isr.read()) != 13)
        	buffer.append( (char) c);
        return buffer.toString();    
	}
}