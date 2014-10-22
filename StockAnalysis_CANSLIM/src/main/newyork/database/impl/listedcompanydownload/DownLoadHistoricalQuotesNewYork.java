package impl.listedcompanydownload;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;

import module.listedcompanydownload.DownLoadHistoricalQuotesNewYorkImpl;
import datasource.DataSourceUtil;

public class DownLoadHistoricalQuotesNewYork {

	private static Connection con = null;

	public static void main(String args[]) {
		start();
	}

	public static void start() {

		Long startTime = Calendar.getInstance().getTimeInMillis();
		try {
			con = DataSourceUtil.DINGUNSW().getConnection();
			DownLoadHistoricalQuotesNewYorkImpl
					.downloadHistoricalQuotesNewYorkImpl(con);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		Long endTime = Calendar.getInstance().getTimeInMillis();
		Integer minute = (int) ((endTime - startTime) / (long) (1000 * 60));
		Integer second = (int) ((endTime - startTime) / (long) (1000)) % 60;
		System.out.println("DownLoadHistoricalQuotesNewYork running time : "
				+ minute + " minutes " + second + " seconds");
	}

}