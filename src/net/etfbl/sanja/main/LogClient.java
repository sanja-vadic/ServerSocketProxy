package net.etfbl.sanja.main;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;


public class LogClient {
	
	public static String send(Log log) {
		try {
			JSONObject root = new JSONObject();
			root.put("clientAddress", log.getClientAddress());
			root.put("requestMethod", log.getRequestMethod());
			root.put("attackType", log.getAttackType());
			
			System.out.println("ServerProxyJSON: " + root.toString());
			
			URL url = new URL("http://localhost:8080/SigurnostSafe/EmptyLogServlet?clientAddress=" + log.getClientAddress() 
			+ "&requestMethod=" + log.getRequestMethod()
			+ "&attackType=" + log.getAttackType());
			HttpURLConnection  conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuffer response = new StringBuffer();
			in.lines().forEach(line -> response.append(line));
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
}
