package impl.update;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.sql.DataSource;

import module.historicalquotes.UpdateHistoricalQuotes;
import datasource.DataSourceUtil;
import jdbcdao.CodeListsDao;

public class HistoricalQuoteUpdateMultiThreadVersion {
	
	private final static DataSource dataSource = DataSourceUtil.DINGUNSW();

	private final static String year = String.valueOf(Calendar.getInstance()
			.get(Calendar.YEAR));
	private final static String month = String.valueOf(Calendar.getInstance()
			.get(Calendar.MONTH) + 1);
	private final static String day = String.valueOf(Calendar.getInstance()
			.get(Calendar.DATE));
	private final static String startYear = "1983";
	private final static String startMonth = "1";
	private final static String startDay = "1";
	public static Connection con = null;
	public static Integer count = 0;
	public static ArrayList<String> codeLists = new ArrayList<>();

	public HistoricalQuoteUpdateMultiThreadVersion() {
		// TODO Auto-generated constructor stub
	}

	public static String getYear() {
		return year;
	}

	public static String getMonth() {
		return month;
	}

	public static String getDay() {
		return day;
	}

	public static String getStartyear() {
		return startYear;
	}

	public static String getStartmonth() {
		return startMonth;
	}

	public static String getStartday() {
		return startDay;
	}
	
	public static void main(String args){
		start();
	}
	
	public static void start() {
		Long startTime = Calendar.getInstance().getTimeInMillis();
		run(8);
		Long endTime = Calendar.getInstance().getTimeInMillis();
		Integer minute = (int) ((endTime - startTime) / (long)(1000 * 60));
		Integer second = (int)((endTime - startTime) / (long)(1000)) % 60;
		System.out.println("running time : " + minute + " minutes " + second + " seconds");
	}

	public static void run(Integer splitNumber) {
		System.out.println("start updating quotes...");
		CodeListsDao clDao = new CodeListsDao();
		codeLists = clDao.getCodeLists(dataSource);
		count = codeLists.size();
		ArrayList<updateThread> threadGroup = new ArrayList<>();
		for (int i = 1; i <= splitNumber; i++) {
			updateThread thread = new updateThread("Thread" + i);
			System.out.println("Thread" + i + " is created!");
			threadGroup.add(thread);
		}
		try {
			con = DataSourceUtil.DINGUNSW().getConnection();
			for (updateThread thread : threadGroup) {
				thread.start();
				System.out.println(thread.getName() + " is starting");
			}
			for (updateThread thread : threadGroup) {
				thread.join();
				System.out.println(thread.getName() + " is finished!");
			}
			System.out.println("finished");

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
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
	}
}

class updateThread extends Thread {
	public updateThread() {
		super();
	}

	public updateThread(String str) {
		super(str);
	}

	public void run() {
		UpdateHistoricalQuotes update = new UpdateHistoricalQuotes();
		update.updateCode(super.getName());
	}

}
