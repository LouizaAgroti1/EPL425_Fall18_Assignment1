import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;

/**
 * This program demonstrates a simple TCP/IP socket client that simulates N concurrent
 * users that generate a series of requests towards the server.
 */
public class Client {

	// maximum number of concurrent simulated users
	public static int N = 10;
	
	// an array that contains the communication latency (time between sending a request and receiving the respective response (RTT)) of each user 
	public static double RTT [] = new double [N];
	
	// number of repeated client-runs (to obtain more accurate results) used for computation of RTT
	public static int repetitions=0;
	
	// number of concurrent simulated users used for computation of RTT
	public static int count_users=0;
	
	// an array that contains server throughput (the amount of requests a server satisfies in a given interval(1000000 ns)) needed for each user
	public static double throughput [] = new double [N];
	
	// number of repeated client-runs (to obtain more accurate results) used for computation of server throughput
	public static int repet_throughput=0;
	
	// number of concurrent simulated users used for computation of server throughput 
	public static int count_users_throughput=0;
	
	public static void main(String[] args) {
		
		// checks if the number of given arguments is correct
		if (args.length < 3)
			return;
		
		// initializes RTT array with 0
		Arrays.fill(RTT, 0.0);
		
		// server's address
		String hostname = args[0];
		
		// value of port
		int port = Integer.parseInt(args[1]);
		
		// maximum number of repeated client-runs
		int repet = Integer.parseInt(args[2]);

		try {
			// a list in which each created thread is saved
			ArrayList<ClientThread> threads = new ArrayList<ClientThread>();
			
			//
			for (int i = 1; i <= repet; i++) {
				// the simulated user-id
				int user_id = 1;
				
				count_users=0;
				count_users_throughput=0;
				
				while (user_id <= N) {
					
					// create of a socket and bind it to port value and hostname value 
					Socket socket = new Socket(hostname, port);
					
					// create a thread that simulates one user
					ClientThread clientThread = new ClientThread(socket, user_id, N, repet);
					clientThread.start();
					
					// add the current thread into a list
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
