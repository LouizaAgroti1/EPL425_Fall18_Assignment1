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

	public void run() {
		int max_requests = 300;
		double avg_throughput_user = 0.0;
		try {
			long startTime = System.nanoTime();
			int requests = 0;
			int count_sec = 0;
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
				long elapsedTime = System.nanoTime() - startTime;

				if (elapsedTime > 1000000) {
					count_sec++;
					startTime = System.nanoTime();
				}

				if (i == max_requests) {
					avg_throughput_user = ((double) max_requests) / ((double) count_sec);
					writer.println(avg_throughput_user);
				}
			}
			OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory
					.getOperatingSystemMXBean();
			write_file(operatingSystemMXBean.getSystemLoadAverage(), avg_throughput_user, "CPU_Load.txt");
			write_file(((double) Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024,
					avg_throughput_user, "Memory_Utilization.txt");

			socket.close();
		} catch (IOException ex) {
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}
