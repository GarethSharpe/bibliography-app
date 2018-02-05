import java.io.*;
import java.net.*;
import java.util.Scanner;
import javax.swing.*;

public class Client {

	public static void main(String[] args) throws IOException {

		String ipAddress = null, port = null;
		int portNumber;
	
		if (args.length != 0) {
			JOptionPane.showMessageDialog(null, "Usage: java Client");
            System.err.println(
                "Usage: java Client");
            System.exit(1);
	    }

	    ipAddress = JOptionPane.showInputDialog(
	    	null, "Please specify an ip address.", "localhost");
	    if (ipAddress == null) { System.exit(0); }

	    port = JOptionPane.showInputDialog("Please specify a port number.");
	    if (port == null) { System.exit(0); }

	    while (!isNumeric(port))
	    	port = JOptionPane.showInputDialog("Please specify a numeric port number.");

	    portNumber = Integer.parseInt(port);

	    try (
	    	Socket clientSocket = new Socket(ipAddress, portNumber);
	    	Scanner userInput = new Scanner(System.in);
	    	Scanner input = new Scanner(clientSocket.getInputStream());
	    	PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
	    ) {
	        try {
	            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
	        } catch (UnsupportedLookAndFeelException ex) {
	            ex.printStackTrace();
	        } catch (IllegalAccessException ex) {
	            ex.printStackTrace();
	        } catch (InstantiationException ex) {
	            ex.printStackTrace();
	        } catch (ClassNotFoundException ex) {
	            ex.printStackTrace();
	        }
	        // Schedule a job for the event dispatching thread:
	        javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                new GUI("Bibliography App", input, output);
	            }
	        });

	    	String message;
	    	while ((message = userInput.nextLine()) != null) {
	    		System.out.println(message);
	    	}

	    } catch (IOException e) {
	    	JOptionPane.showMessageDialog(
                        null, "Could not connect to " + ipAddress + " on port " + portNumber + ".");
	    	System.exit(1);
	    }
	}

	public static boolean isNumeric(String str) {  

		try {  
	    	double d = Double.parseDouble(str);  
	  	} catch(NumberFormatException ex) {  
	    	return false;  
	  	}  

	  	return true;  
	}

}