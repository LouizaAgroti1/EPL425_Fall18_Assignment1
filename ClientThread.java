import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Simulates a client as a thread. It sends a number of requests to the server 
 * and reads the corresponding responses from the server. It writes to files
 * the RTT and Throughput for 1 to N users.
 */
public class ClientThread extends Thread {
	
	// the socket that the current thread listens to
	private Socket socket;
	
	// the simulated user-id
	private int user_id;
	
	// maximum number of concurrent simulated users
	private int N;
	
	// maximum number of repeated client-runs
	private int repet;

	/**
	 * Initializes the client thread
	 * 
	 * @param socket The socket that the current thread listens to for connection
	 * @param user_id The simulated user-id
	 * @param N Maximum number of concurrent simulated users
	 * @param repet Maximum number of repeated client-runs
	 */
	public ClientThread(Socket socket, int user_id, int N, int repet) {
		this.socket = socket;
		this.user_id = user_id;
		this.N = N;
		this.repet = repet;
	}
	
	/**
	 * Writes an array in a given file
	 * 
	 * @param array The array that will be written in the file
	 * @param N Maximum number of concurrent simulated users 
	 * @param filename The name of the file
	 */
	public void write_file_array(double array [], int N, String filename) {
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			File file = new File(filename);

			// it creates the file if the file is not already present
			if (!file.exists()) {
				file.createNewFile();
			}

			// appends to the file
			fw = new FileWriter(file, true);
			bw = new BufferedWriter(fw);
			for(int i=0;i<array.length;i++){
				bw.write(array[i] + " " + (i+1) + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();

			}
		}

	}
	
	/**
	 * Runs the current thread. It sends requests to the server and get the corresponding responses
	 * from the server. It computes and writes in a file the RTT and throughput 
	 * for 1 to N users.
	 */
	public void run() {
		
		// maximum number of requests that a user can send
		int max_requests = 300;
		
		// the sum of RTT for a number of requests and responses with the server
		long sumRTT = 0;
		
		// the average throughput for a number of requests and responses with the server
		double avg_throughput_user = 0.0;
		
		try {
			String socket_address = socket.getRemoteSocketAddress().toString();
			for (int i = 1; i <= max_requests; i++) {
				long startTime = System.nanoTime();
				OutputStream output = socket.getOutputStream();
				PrintWriter writer = new PrintWriter(output, true);
				String text = "HELLO";
				writer.println(text);
				writer.println(user_id);
				writer.println(socket_address);
				InputStream input = socket.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(input));
				String response = reader.readLine();
				System.out.println(response);
				byte[] payload = reader.readLine().getBytes();
				long RTT = System.nanoTime() - startTime;
				sumRTT += RTT;
				if (i == max_requests) {
					avg_throughput_user = Double.parseDouble(reader.readLine());
					Client.throughput[user_id-1]=avg_throughput_user;
					Client.count_users_throughput++;
				}
			}
			Client.RTT[user_id-1]+=(sumRTT/max_requests);
			Client.count_users++;
			socket.close();
		} catch (IOException ex) {
			System.out.println("Client exception: " + ex.getMessage());
			ex.printStackTrace();
		}

		/*
		 * Increases the number of repetitions until all
		 * users exchange information with the server
		 * (used for the calculation of RTT)
		 */
		if (Client.count_users == N) {
			Client.repetitions++;
		}
		
		/*
		 * Increases the number of repetitions until all
		 * users exchange information with the server
		 * (used for the calculation of Throughput)
		 */
		if(Client.count_users_throughput == N){
			Client.repet_throughput++;
		}

		/*
		 * Checks if the maximum number of repetitions is reached.
		 * If so it calculates the RTT for 1 to N users and write 
		 * it to file "RTT.txt"
		 */
		if (Client.repetitions == repet) {
			for(int i=0;i<Client.RTT.length;i++){
				Client.RTT[i]= ((double) Client.RTT[i])/((double)repet);
			}
			double tmp[]=new double[N];
			for(int i=0;i<Client.RTT.length;i++){
				double sum=0.0;
				for(int j=0;j<i+1;j++){
					sum+=Client.RTT[j];
				}
				tmp[i]=sum;
			}
			write_file_array(tmp,N,"RTT.txt");
		}
		
		/*
		 * Checks if the maximum number of repetitions is reached.
		 * If so it calculates the throughput for 1 to N users and write 
		 * it to file "Throughput.txt"
		 */
		if (Client.repet_throughput == repet) {
			for(int i=0;i<Client.throughput.length;i++){
				Client.throughput[i]= ((double) Client.throughput[i])/((double)repet);
			}
			double tmp_throughput[]=new double[N];
			for(int i=0;i<Client.throughput.length;i++){
				double sum_throughput=0.0;
				for(int j=0;j<i+1;j++){
					sum_throughput+=Client.throughput[j];
				}
				tmp_throughput[i]=sum_throughput/(i+1);
			}
			write_file_array(tmp_throughput,N,"Throughput.txt");
		}
	}
}