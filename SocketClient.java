package ClientServer;
import java.net.*;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop.Action;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;

public class SocketClient {
	
    static JFrame frame = new JFrame();
    static JPanel mainPanel = new JPanel(new BorderLayout());
    static JPanel topPnl = new JPanel();
    static JPanel bottomPnl = new JPanel(); 
    static JTextPane outputarea = new JTextPane();
    static JTextField inputarea = new JTextField();
    static boolean enterKey = false;
	
	public static void inputListenAndPrint(InputStreamReader isr) throws IOException {
		int c;
		StringBuffer buffer = new StringBuffer();
		while ( (c = isr.read()) != 13)
          if (c != 0)
			buffer.append( (char) c);
        System.out.println(buffer);
        outputarea.setText("");
		outputarea.setText(buffer.toString() + "\n");
	}
	
	public static void outputListenAndSend(OutputStreamWriter osw) throws IOException, InterruptedException {
		//Scanner inputscanner = new Scanner(System.in);
		//String input = inputscanner.nextLine() + (char) 13;
		//osw.write(input);
        //osw.flush();
		while(!enterKey) {
			Thread.sleep(10);
		}
		enterKey = false;
	}
	
	public static void main(String[] args) throws IOException {
	    String host = "66.158.185.93";
	    int port = 20000;
	    int playerID;
	    
	    
	    
	    StringBuffer instr = new StringBuffer();
	    String TimeStamp;
	    System.out.println("NIMclient Initialized");
	    try {
	        InetAddress address = InetAddress.getByName(host);
	        Socket connection = new Socket(address, port);
	        BufferedOutputStream bos = new BufferedOutputStream(connection.
	                getOutputStream());
	        OutputStreamWriter osw = new OutputStreamWriter(bos, "US-ASCII");
	        TimeStamp = new java.util.Date().toString();
	        String process = "Calling the Socket Server on "+ host + " port " + port +
	                " at " + TimeStamp +  (char) 13;
	        osw.write(process);
	        osw.flush();
	        BufferedInputStream bis = new BufferedInputStream(connection.
	            getInputStream());
	        InputStreamReader isr = new InputStreamReader(bis, "US-ASCII");
	        
	        AbstractAction action = new AbstractAction()
	        {
	            @Override
	            public void actionPerformed(ActionEvent e)
	            {
	                try {
	                	osw.write(inputarea.getText() + (char)13);
	                    osw.flush();
	                    enterKey = true;
						inputarea.setText("");
					} catch (IOException e1) {e1.printStackTrace();}
	            }
	        };
		    
	       
		    inputarea.setPreferredSize(new Dimension(120,20));
		    outputarea.setPreferredSize(new Dimension(200,200));
		    inputarea.addActionListener( action );
		    mainPanel.setBackground(Color.WHITE);
	        topPnl.add(outputarea);
	        topPnl.setBackground(Color.white);
	        bottomPnl.setBackground(Color.white);
	        bottomPnl.add(inputarea);
		    outputarea.setEditable(false);
		    mainPanel.add(topPnl, BorderLayout.NORTH);
	        mainPanel.add(bottomPnl, BorderLayout.SOUTH);
	        frame.add(mainPanel);
	        frame.setSize(400, 300);
	        frame.setVisible(true);
	        
	        // player iniit and the rest
	        // reading and writing to game in server here
	        while(true) {
	        	int c;
				StringBuffer buffer = new StringBuffer();
	        	while ( (c = isr.read()) != 13)
	      			buffer.append( (char) c);
	        	if (buffer.toString().contentEquals("read"))
	        		inputListenAndPrint(isr);
	        	else if (buffer.toString().contentEquals("write"))
	        		outputListenAndSend(osw);
	        }

	        /** Close the socket connection. */
	        //connection.close();
	        
	    } catch (IOException f) {
	        System.out.println("IOException: " + f);
	    } catch (Exception g) {
	        System.out.println("Exception: " + g);
	    }
	}
}

