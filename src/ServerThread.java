import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.ThreadLocalRandom;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

/**
 * This thread is responsible to handle client connection.
 */
public class ServerThread extends Thread {
	
	//create new socket
	private Socket socket;

	//set the socket
	public ServerThread(Socket socket) {
		this.socket = socket;
	}

	//method to set randomly the payload of each message
	public byte[] get_payload() {
		//convert limits of the payload into bits.
		int min = 300 * 1024;
		int max = 2000 * 1024;
		
		//create randomly the size of payload
		Random rand = new Random(System.currentTimeMillis());
		// 2000 is the maximum and the 300 is our minimum
		int n = rand.nextInt(max) + min;
		
		
	//	int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
		byte[] payload = new byte[n];
		Arrays.fill(payload, (byte) 1);
		return payload;
	}

	//method to write results into files
	public void write_file(double value, double throughput, String filename) {
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
			bw.write(throughput + "\t" + value + "\n");
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
	
	//method to run 300 requests of every user
	public void run() {
		
		//maximum number of requests for each user
		int max_requests = 300;
		
		//variable for user's average throughput
		double avg_throughput_user = 0.0;
		try {
			//take start time
			long startTime = System.nanoTime();

			
			//for-loop for each user's request
			for (int i = 1; i <= max_requests; i++) {
				InputStream input = socket.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(input));

				OutputStream output = socket.getOutputStream();
				PrintWriter writer = new PrintWriter(output, true);
				
				//received "HELLO" from user
				String request_msg = reader.readLine();
				System.out.println(request_msg);
				
				//received user id from user
				String request_userid = reader.readLine();
				System.out.println(request_userid);
				
				//received IP address and port from user
				String socket_address = reader.readLine();
				System.out.println("socket address " + socket_address);
				
				//sends "WELCOME" to user
				String response = "WELCOME";
				writer.println(response + " " + request_userid);
				
				//call method
				byte payload[] = get_payload();
				
				//sends payload to user
				writer.println(payload);
				
				//count end time - start time
				long elapsedTime = System.nanoTime() - startTime;


				//checks if this is the last request
				if (i == max_requests) {
					
					//count user's average throughput 
					avg_throughput_user = ((double) max_requests) / ((double)elapsedTime);
					avg_throughput_user = avg_throughput_user * 1000000000.0;
					
					//sends user's average throughput to user
					writer.println(avg_throughput_user);
				}
			}
			
			//counts CPU Load and writes it into text file with user's average throughput
			OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
			write_file(operatingSystemMXBean.getSystemLoadAverage(), avg_throughput_user, "CPU_Load.txt");
			
			//counts Memory utilization and writes it intot text file with user's average throughput
			write_file(((double) Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024,
					avg_throughput_user, "Memory_Utilization.txt");

			socket.close();
		} catch (IOException ex) {
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}
