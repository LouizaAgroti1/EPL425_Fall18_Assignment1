package Labs.reverse_server_multi_threaded;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class ClientThread extends Thread {

	private Socket socket;
	private int user_id;

	public ClientThread(Socket socket, int user_id) {
		this.socket = socket;
		this.user_id = user_id;
	}

	public void run() {
		int max_requests = 300;
		try {
			String socket_address = socket.getRemoteSocketAddress().toString();
			for (int i = 1; i <= max_requests; i++) {
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
				/*
				 * for (byte b:payload){
				 * System.out.print(Integer.toBinaryString(b & 255 |
				 * 256).substring(1)); }
				 */
			}
			socket.close();
		} catch (IOException ex) {
			System.out.println("Client exception: " + ex.getMessage());
			ex.printStackTrace();
		}

	}
}
