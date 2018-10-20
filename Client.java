import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

/**
 * This program demonstrates a simple TCP/IP socket client that reads input from
 * the user and prints echoed message from the server.
 */
public class Client {

	public static ArrayList<Double> RTT = new ArrayList<Double>();
	public static ArrayList<Double> repetitions = new ArrayList<Double>();
	
	public static void main(String[] args) {
		if (args.length < 2)
			return;

		String hostname = args[0];
		int port = Integer.parseInt(args[1]);
		int repet = Integer.parseInt(args[2]);

		try {
			int N = 40;
			for (int j = 10; j <= N; j += 10) {
				for (int i = 1; i <= repet; i++) {
					int user_id = 1;
					while (user_id <= j) {
						Socket socket = new Socket(hostname, port);
						ClientThread clientThread = new ClientThread(socket, user_id, j,repet);
						clientThread.start();
						user_id++;
					}
				}
			}
		} catch (UnknownHostException ex) {

			System.out.println("Server not found: " + ex.getMessage());

		} catch (IOException ex) {

			System.out.println("I/O error: " + ex.getMessage());
		}
	}
}