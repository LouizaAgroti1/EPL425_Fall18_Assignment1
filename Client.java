import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.*;

/**
 * This program demonstrates a simple TCP/IP socket client that reads input from
 * the user and prints echoed message from the server.
 */
public class Client {

	/*public static ArrayList<Double> RTT = new ArrayList<Double>();
	public static ArrayList<Double> repetitions = new ArrayList<Double>();*/
	public static int N = 10;
	public static double RTT [] = new double [N];
	public static int repetitions=0;
	public static int count_users=0;
	
	public static double throughput [] = new double [N];
	public static int repet_throughput=0;
	public static int count_users_throughput=0;
	
	public static void main(String[] args) {
		if (args.length < 2)
			return;
		Arrays.fill(RTT, 0.0);
		String hostname = args[0];
		int port = Integer.parseInt(args[1]);
		int repet = Integer.parseInt(args[2]);

		try {
			ArrayList<ClientThread> threads = new ArrayList<ClientThread>();
			for (int i = 1; i <= repet; i++) {
				int user_id = 1;
				count_users=0;
				count_users_throughput=0;
				while (user_id <= N) {
					Socket socket = new Socket(hostname, port);
					ClientThread clientThread = new ClientThread(socket, user_id, N, repet);
					clientThread.start();
					threads.add(clientThread);
					user_id++;
				}
				
				for (int k = 0; k < threads.size(); k++) {
					try {
						threads.get(k).join();
					} catch (InterruptedException e) {
						e.printStackTrace();
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