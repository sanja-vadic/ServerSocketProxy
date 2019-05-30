package net.etfbl.sanja.main;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketThread implements Runnable {
	private static final long THRESHOLD_CONNECTION_DURATION_BY_REQUEST = 10;
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
			String requestMethod = inputData.substring(0, inputData.indexOf(" "));
			requestMethod = requestMethod.equals("GET") || requestMethod.equals("POST") ? requestMethod : "UNDEFINED";
			
			long endTime = System.currentTimeMillis() / 1000;
			
			String responseFromWebApplication = sendData(inputData);
			out.print(responseFromWebApplication);
			out.flush();
			
			in.close();
			out.close();
			
			
			if (endTime - startTime > THRESHOLD_CONNECTION_DURATION_BY_REQUEST) {
				System.out.println("Desava se Slow HTTP napad.");
				Log log = Log.builder()
						.clientAddress(socket.getInetAddress().getHostAddress())
						.requestMethod(requestMethod)
						.attackType("SLOW_HTTP")
						.build();
				LogClient.send(log);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getInputData(BufferedReader in) throws IOException {
		StringBuilder responseBuilder = new StringBuilder();
		String line = "";
		while((line = in.readLine()) != null) {
			if(line.equals("")) {
				//System.out.println("Last character");
				break;
			}
			responseBuilder.append(line).append("\r\n");
		//	System.out.println("Input data line: " + line);
		}
		responseBuilder.append("\r\n");
		//System.out.println("Data: " + responseBuilder.toString());
		return responseBuilder.toString();
	}
	
	private String sendData(String inputData) throws UnknownHostException, IOException {
		Socket s = new Socket(InetAddress.getByName("localhost"), 8080);
		PrintWriter pw = new PrintWriter(s.getOutputStream());
		pw.print(inputData);
		pw.flush();
		InputStreamReader in = new InputStreamReader(s.getInputStream());
		
		StringBuilder responseBuilder = new StringBuilder();
		String line = "";
		//System.out.println("Prima podatke");
		long startTime = System.currentTimeMillis();
		
		int character;
		while ((character = in.read()) != -1)
		{
			responseBuilder.append((char) character);
		}
		
		//System.out.println("Time: " + ((System.currentTimeMillis()) - startTime) + "ms");
		//System.out.println("Primio podatke: " + responseBuilder.toString());
		in.close();
		pw.close();
		s.close();
		return responseBuilder.toString();
	}
	
}
