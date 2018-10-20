package Labs.reverse_server_multi_threaded;

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

	public static void main(String[] args) {
		if (args.length < 2)
			return;

		String hostname = args[0];
		int port = Integer.parseInt(args[1]);
		int repetitions = Integer.parseInt(args[2]);

		try {
			int N = 10;
			for (int i = 1; i <= repetitions; i++) {
				int user_id = 1;
				while (user_id <= N) {
					Socket socket = new Socket(hostname, port);
					ClientThread clientThread = new ClientThread(socket, user_id, N);
					clientThread.start();
					user_id++;
				}
				N++;
			}
		} catch (UnknownHostException ex) {

			System.out.println("Server not found: " + ex.getMessage());

		} catch (IOException ex) {

			System.out.println("I/O error: " + ex.getMessage());
		}
	}
}