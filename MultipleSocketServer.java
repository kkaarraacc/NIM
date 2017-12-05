package ClientServer;
import java.net.*;
import java.io.*;
import java.util.*;

public class MultipleSocketServer implements Runnable {

	public static int player_ready = 0;
	public static NimGame nimgame = new NimGame();
	
	private Socket connection;
	private String TimeStamp;
	private int playerID;
	boolean winner;
	
	
	public static String listenForInput(InputStreamReader isr) throws IOException {
		int c;
		StringBuffer buffer = new StringBuffer();
        while ( (c = isr.read()) != 13)
        	buffer.append( (char) c);
        return buffer.toString();    
	}
	
	public static void main(String[] args) {
		int port = 20000;
		int count = 0;
		
		try{
			ServerSocket socket = new ServerSocket(port);
			System.out.println("MultipleSocketServer Initialized");
			while (true) {
				Socket connection = socket.accept();
				Runnable runnable = new MultipleSocketServer(connection, count);
				count ++;
				Thread thread = new Thread(runnable);
				thread.start();
			}
		}
		catch (Exception e) {}
	}
	
	MultipleSocketServer(Socket s, int i) {
	  this.connection = s;
	  this.playerID = i;
	}
	
	// entry point for a socketconnection
	public void run() {
	    try {
	      BufferedInputStream is = new BufferedInputStream(connection.getInputStream());
	      InputStreamReader isr = new InputStreamReader(is);
	      int character;
	      String inputstring;
	      StringBuffer process = new StringBuffer();
	      
	      // startup
	      while((character = isr.read()) != 13) {
	        process.append((char)character);
	      }
	      System.out.println(process);
	      try {
	        Thread.sleep(1000); //need to wait 10 seconds to pretend that we're processing something
	      }
	      catch (Exception e){}
	      TimeStamp = new java.util.Date().toString();
	      BufferedOutputStream os = new BufferedOutputStream(connection.getOutputStream());
	      OutputStreamWriter osw = new OutputStreamWriter(os);
	      
	      nimgame.startGame(osw, isr, playerID, connection, winner);
	    }
	    catch (Exception e) {
	      System.out.println(e);
	    }
	    finally {
	      try {
	        connection.close();
	     }
	      catch (IOException e){}
	    }
	}
}
