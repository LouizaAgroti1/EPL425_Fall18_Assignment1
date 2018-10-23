import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * This program demonstrates a simple TCP/IP socket server that responses every
 * request from the N users. This server is multi-threaded.
 */
public class Server {
	
	public static void main(String[] args) {
		
		if (args.length < 1)
			return;

		//First argument of server is the port
		int port = Integer.parseInt(args[0]);

		//create new server socket 
		try (ServerSocket serverSocket = new ServerSocket(port)) {

			//Prints server's port
			System.out.println("Server is listening on port " + port);
			
			
			while (true) {
				//method to accept new clients
				Socket socket = serverSocket.accept();
				System.out.println("New client connected");
				
				//start the ServerThread's file
				new ServerThread(socket).start();
			}
			

		} catch (IOException ex) {
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}
