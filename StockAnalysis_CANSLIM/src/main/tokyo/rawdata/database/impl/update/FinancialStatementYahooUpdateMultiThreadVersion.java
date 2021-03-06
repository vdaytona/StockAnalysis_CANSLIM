package impl.update;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.sql.DataSource;

import module.annualfinancialstatement.FinancialStatementYahooJDBCUpdateData;
import datasource.DataSourceUtil;
import jdbcdao.CodeListsDao;

/**
 * 1.check if the table of financial statement is exist 2.if not, create the
 * table 3.get all the code list in TSE 4.loop:{ 5.check if there is much new
 * information for this code 6.if yes, insert (update) the data into the table}
 * 
 * @author Daytona
 * 
 */

public class FinancialStatementYahooUpdateMultiThreadVersion {

	private static ArrayList<String> codeList;
	private static Integer threadNumber;
	private static Integer totalCount;
	public static Connection con;

	public FinancialStatementYahooUpdateMultiThreadVersion() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String args[]) {
		Long startTime = Calendar.getInstance().getTimeInMillis();
		run(8);
		Long endTime = Calendar.getInstance().getTimeInMillis();
		Integer minute = (int) ((endTime - startTime) / (long)(1000 * 60));
		Integer second = (int)((endTime - startTime) / (long)(1000)) % 60;
		System.out.println("running time : " + minute + " minutes " + second + " seconds");
	}

	public static void run(Integer splitNumber) {
		threadNumber = splitNumber;
		codeList = new CodeListsDao().getCodeLists(DataSourceUtil.DINGUNSW());
		totalCount = codeList.size();

		System.out.println("all record is : " + totalCount);
		ArrayList<financialStatementUpdateThread> threadGroup = new ArrayList<>();
		try {
			con = DataSourceUtil.getTokyoDataSourceRoot().getConnection();
			for (int i = 0; i < threadNumber; i++) {
				threadGroup.add(new financialStatementUpdateThread("Thread"
						+ (i + 1)));
				threadGroup.get(i).start();
				System.out.println("Thread" + i + " is starting!");
			}

			for (financialStatementUpdateThread thread : threadGroup) {
				try {
					thread.join();
					System.out.println(thread.getName() + " has finished!");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
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

		System.out.println("Financial Statement update is finished!");
	}

	public static void financiaStatementUpdate(Connection con, String name) {
		String code = null;
		Boolean ifHashNext = false;
		synchronized (codeList) {
			code = codeList.get(0);
			codeList.remove(0);
			ifHashNext = true;
		}
		while (ifHashNext == true) {
			if (Integer.valueOf(code) >= 0) {
				FinancialStatementYahooJDBCUpdateData
						.FinancialStatemetYahooJDBCUpdateDataImpl(code, con);
			}
			synchronized (totalCount) {
				System.out.println("FinancialStatement : " + name + ": " + code +  " is updated, " + --totalCount + " is left");
			}
			synchronized (codeList) {
				if (codeList.size() != 0) {
					code = codeList.get(0);
					codeList.remove(0);
				} else {
					ifHashNext = false;
				}
			}
		}
	}

	public DataSource getDataSource() {
		return DataSourceUtil.getTokyoDataSourceRoot();
	}

}

class financialStatementUpdateThread extends Thread {
	private Connection con = FinancialStatementYahooUpdateMultiThreadVersion.con;

	public financialStatementUpdateThread() {
		super();
	}

	public financialStatementUpdateThread(String str) {
		super(str);
	}

	public void run() {
		FinancialStatementYahooUpdateMultiThreadVersion.financiaStatementUpdate(con, super.getName());
	}
}
