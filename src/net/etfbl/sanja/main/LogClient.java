package net.etfbl.sanja.main;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import com.mashape.unirest.http.Unirest;

public class LogClient {
	
	public static String send(Log log) {
		try {
			JSONObject root = new JSONObject();
			root.put("clientAddress", log.getClientAddress());
			root.put("requestMethod", log.getRequestMethod());
			root.put("attackType", log.getAttackType());
			
			URL url = new URL("http://localhost:8080/SigurnostSafe/EmptyLogServlet");
			HttpURLConnection  conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true); //Potrebno ako se salje request body
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			out.write(root.toString().getBytes()); //npr. paramsStr = "name=Ime&lastname=Prezime";
			out.close();
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
