package Labs.reverse_server_multi_threaded;

import java.net.*;
import java.util.Scanner;
import java.io.*;

/**
 * This program demonstrates a simple TCP/IP socket client that reads input from
 * the user and prints echoed message from the server.
 */
public class Client {

	public static void main(String[] args) {
		if (args.length < 2)
			return;

		String hostname = args[0];
		int port = Integer.parseInt(args[1]);
		int N = 10;
		int user_id = 1;

		try {

			while (user_id <= N) {
				Socket socket = new Socket(hostname, port);
				new ClientThread(socket, user_id).start();
				user_id++;
			}
		} catch (UnknownHostException ex) {

			System.out.println("Server not found: " + ex.getMessage());

		} catch (IOException ex) {

			System.out.println("I/O error: " + ex.getMessage());
		}
	}
}