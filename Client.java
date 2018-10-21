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
	public static ArrayList<Double> throughput_user = new ArrayList<Double>();
	public static ArrayList<Double> throughput_repetitions = new ArrayList<Double>();
	
	public static void main(String[] args) {
		if (args.length < 2)
			return;

		String hostname = args[0];
		int port = Integer.parseInt(args[1]);
		int repet = Integer.parseInt(args[2]);

		try {
			int N = 110;
			for (int j = 10; j <= N; j += 10) {
				ArrayList<ClientThread> threads = new ArrayList<ClientThread>();
				for (int i = 1; i <= repet; i++) {
					//long beforeUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
					int user_id = 1;
					while (user_id <= j) {
						Socket socket = new Socket(hostname, port);
						ClientThread clientThread = new ClientThread(socket, user_id, j,repet);
						clientThread.start();
						threads.add(clientThread);
						user_id++;
					}
					for(int k=0;k<threads.size();k++){
						try {
							threads.get(k).join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					//long afterUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
					//long actualMemUsed=afterUsedMem-beforeUsedMem;
					//System.out.println(actualMemUsed);
				}
			}
		} catch (UnknownHostException ex) {

			System.out.println("Server not found: " + ex.getMessage());

		} catch (IOException ex) {

			System.out.println("I/O error: " + ex.getMessage());
		}
	}
}