package net.etfbl.sanja.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
	private static final long THRESHOLD_CONNECTIONS_PER_SECOND = 30;
	private static final int PORT = 3333;
	
	public static void main(String[] args) {
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(PORT);
			long counter = 0;
			long startTimestamp = System.currentTimeMillis() / 1000;
			while(true) {
				System.out.println("Waiting for connection...");
				Socket s = ss.accept();
				counter++;
				System.out.println("Connection accepted. Number of connection: " + counter);
				
				new Thread(new SocketThread(s)).start();
				
				long countingTime = System.currentTimeMillis() / 1000 - startTimestamp;
				if(countingTime > 10) {
					long requestsPerSecond = counter / countingTime;
					if(requestsPerSecond >= THRESHOLD_CONNECTIONS_PER_SECOND || true) {
						System.out.println("Desava se DOS napad.");
						Log log = Log.builder()
								.clientAddress("127.0.0.1")
								.requestMethod("POST")
								.attackType("DOS")
								.build();
						LogClient.send(log);
					}
					counter = 0;
					startTimestamp = System.currentTimeMillis() / 1000;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
