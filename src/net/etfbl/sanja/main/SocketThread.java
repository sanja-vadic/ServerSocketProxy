package net.etfbl.sanja.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketThread implements Runnable {
	private static final long THRESHOLD_CONNECTION_DURATION_BY_REQUEST = 30;
	private Socket socket;
	
	public SocketThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		long startTime = System.currentTimeMillis() / 1000;
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream());
			
			String inputData = getInputData(in);
			
			String responseFromWebApplication = sendData(inputData);
			out.print(responseFromWebApplication);
			
			in.close();
			out.close();
			
			long endTime = System.currentTimeMillis() / 1000;
			if (endTime - startTime > THRESHOLD_CONNECTION_DURATION_BY_REQUEST) {
				System.out.println("Desava se Slow HTTP napad.");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getInputData(BufferedReader in) throws IOException {
		StringBuilder responseBuilder = new StringBuilder();
		String line = "";
		while((line = in.readLine()) != null) responseBuilder.append(line);
		return responseBuilder.toString();
	}
	
	private String sendData(String inputData) throws UnknownHostException, IOException {
		Socket s = new Socket(InetAddress.getByName("localhost"), 8080);
		PrintWriter pw = new PrintWriter(s.getOutputStream());
		pw.print(inputData);
		pw.flush();
		pw.close();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		StringBuilder responseBuilder = new StringBuilder();
		String line = "";
		while((line = in.readLine()) != null) responseBuilder.append(line);
		in.close();
		return responseBuilder.toString();
	}
	
}
