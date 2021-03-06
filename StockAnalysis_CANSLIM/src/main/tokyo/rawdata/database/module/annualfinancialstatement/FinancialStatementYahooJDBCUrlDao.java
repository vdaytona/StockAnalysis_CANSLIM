package module.annualfinancialstatement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FinancialStatementYahooJDBCUrlDao {

	public FinancialStatementYahooJDBCUrlDao() {
		// TODO Auto-generated constructor stub
	}

	public BufferedReader getFinancialStatmentPageBufferedReaderYahoo(
			String code, String type) {

		String urlString = "http://profile.yahoo.co.jp/" + type + "/" + code;
		BufferedReader fi = null;
		try {
			URL url = new URL(urlString);
			HttpURLConnection set = (HttpURLConnection) url.openConnection();
			Boolean ifHasInfo = true;
			fi = new BufferedReader(new InputStreamReader(set.getInputStream()));
			if (ifHasInfo == true) {
				System.out.println("code : " + code + "  " + ifHasInfo);
				String input = "";
				String charset = "";
				while ((input = fi.readLine()) != null) {
					if (input.contains("charset=")) {
						charset = input.substring(
								input.indexOf("charset=") + 8,
								input.length() - 2);
						break;
					}
				}
				fi = new BufferedReader(new InputStreamReader(
						set.getInputStream(), charset));
			}
		} catch (IOException e) {
			return null;
		}

		return fi;
	}

	public ArrayList<String> getFinancialStatmentPageBufferedReaderYahooToString(
			String code, String type) {
		ArrayList<String> result = new ArrayList<>();
		String urlString = "http://profile.yahoo.co.jp/" + type + "/" + code;
		URL url = null;
		HttpURLConnection set = null;
		try {
			url = new URL(urlString);
			set = (HttpURLConnection) url.openConnection();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedReader fi = null;
		Boolean ifHasInfo = true;
		try {
			fi = new BufferedReader(new InputStreamReader(set.getInputStream()));
		} catch (IOException e) {
			ifHasInfo = false;
		}
		try {
			if (ifHasInfo == true) {
				String input = "";
				String charset = "";
				while ((input = fi.readLine()) != null) {
					if (input.contains("charset=")) {
						charset = input.substring(
								input.indexOf("charset=") + 8,
								input.length() - 2);
						break;
					}
				}
				fi = new BufferedReader(new InputStreamReader(
						set.getInputStream(), charset));
				while ((input = fi.readLine()) != null) {
					result.add(input);
				}

			}
		} catch (IOException e) {
			ifHasInfo = false;
		} finally {
			set.disconnect();
		}
		return result;
	}

}
