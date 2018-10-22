import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.ThreadLocalRandom;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;


/**
 * This thread is responsible to handle client connection.
 */
public class ServerThread extends Thread {
	private Socket socket;

	public ServerThread(Socket socket) {
		this.socket = socket;
	}

	public byte[] get_payload() {
		int min = 300 * 1024;
		int max = 2000 * 1024;
		int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
		byte[] payload = new byte[randomNum];
		Arrays.fill(payload, (byte) 1);
		return payload;
	}

	public void run() {
		int max_requests = 300;
		try {
			long startTime = System.nanoTime();
			int requests=0;
			int count_sec=0;
			for (int i = 1; i <= max_requests; i++) {
				InputStream input = socket.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(input));

				OutputStream output = socket.getOutputStream();
				PrintWriter writer = new PrintWriter(output, true);
				String request_msg = reader.readLine();

				System.out.println(request_msg);
				String request_userid = reader.readLine();
				System.out.println(request_userid);
				String socket_address = reader.readLine();
				System.out.println("socket address " + socket_address);
				String response = "WELCOME";

				writer.println(response + " " + request_userid);
				byte payload[] = get_payload();
				writer.println(payload);
				long elapsedTime = System.nanoTime()-startTime;
				
				if(elapsedTime>1000000){
					count_sec++;
					startTime = System.nanoTime();
				}
				
				if(i==max_requests){
					double avg_throughput_user= ((double)max_requests)/((double) count_sec);
					writer.println(avg_throughput_user);
				}
			}
		       OperatingSystemMXBean operatingSystemMXBean = 
		    	          (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		    	       System.out.println("Load "+operatingSystemMXBean.getSystemLoadAverage());
			
			socket.close();
		} catch (IOException ex) {
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}
