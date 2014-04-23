package dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

public class UrlDao {

	public UrlDao() {
		// TODO Auto-generated constructor stub
	}

	public static ArrayList<String> getUrlBuffer(String urlString) {
		ArrayList<String> result = new ArrayList<>();
		BufferedReader fi = null;
		Boolean ifReaded = false;
		HttpURLConnection set = null;
		while (ifReaded == false) {
			try {
				result = new ArrayList<>();
				URL url = new URL(urlString);
				set = (HttpURLConnection) url.openConnection();
				set.setReadTimeout(1000 * 30);
				set.connect();
				fi = new BufferedReader(new InputStreamReader(
						set.getInputStream()));
				String input = null;
				String charset = null;
				while ((input = fi.readLine()) != null) {
					if (input.contains("charset=")) {
						charset = input.substring(
								input.indexOf("charset=") + 8, input.length());
						Integer indexOfInvoke = charset.indexOf("\"");
						charset = charset.substring(0, indexOfInvoke);
						break;
					}
				}
				set = (HttpURLConnection) url.openConnection();
				fi = new BufferedReader(new InputStreamReader(
						set.getInputStream(), charset));
				while ((input = fi.readLine()) != null) {
					result.add(input);
				}
				ifReaded = true;
			} catch (SocketTimeoutException e) {
				set.disconnect();
				System.out.println("time out");
			} catch (ConnectException e) {
				set.disconnect();
				System.out.println("connection timeout");
			} catch (IOException e) {
				set.disconnect();
				e.printStackTrace();
			}
		}
		return result;
	}
}