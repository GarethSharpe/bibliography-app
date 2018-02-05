import java.net.*;
import java.io.*;

public class Server {

	public static void main(String[] args) {

		BookService BookService = new BookService();

		if (args.length != 1) {
			System.err.println("Usage: java Server <port number>");
			System.exit(1);
		}

		int portNumber = Integer.parseInt(args[0]);
		boolean listening = true;

		try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
			while (listening) {
				ServerThread serverThread = new ServerThread(serverSocket.accept(), BookService);
				serverThread.start();
			}
		} catch (IOException e) {
			System.err.println("could not listen on port " + portNumber);
			System.exit(-1);
		}
	}
}