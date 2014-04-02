package com.download.financialstatementquarterdownload.kabupro.jdbc;

/**
 * 1. get code list
 * 2. get Name_English From DB
 * 3. get Info From URL to Record
 * 4. create Table in DB
 * 5. insert data into Table
 */
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.download.historicaldatadownload.yahoo.jdbc.DataSourceUtil;
import com.download.historicaldatadownload.yahoo.jdbc.dao.CodeListsDao;
import com.download.historicaldatadownload.yahoo.jdbc.dao.NameEnglishDao;

public class FinancialStatementQuarterDownLoadKabuproImpl {

	public FinancialStatementQuarterDownLoadKabuproImpl() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String args[]) {
		System.out.println("start get code list!");
		ArrayList<String> codeList = new CodeListsDao()
				.getCodeListsFromFinancialStatement();
		System.out.println("finish get code list!");
		Connection con = null;
		ArrayList<FinancialStatementQuarterDownLoadKabuproRecord> result = new ArrayList<>();
		try {
			con = DataSourceUtil.getTokyoDataSourceRoot().getConnection();
			System.out.println("start get code name!");
			HashMap<String, String> nameEnglish = NameEnglishDao
					.getNameEnglishDao(con);
			System.out.println("finish get code name!");
			for (String code : codeList) {
				System.out.println("download " + code + ", "
						+ (codeList.size() - codeList.indexOf(code))
						+ " to go!!");
				FinancialStatementQuarterDownLoadKabuproRecord record = new FinancialStatementQuarterDownLoadKabuproRecord();
				record.setLocal_Code(code);
				record.setName_English(nameEnglish.get(code));
				ArrayList<FinancialStatementQuarterDownLoadKabuproRecord> resultTemp = FinancialStatementQuarterDownLoadKabuproFetchUrl
						.setRecordFromURL(record);
				if (resultTemp != null) {
					result.addAll(resultTemp);
				}
			}
			System.out.println("start drop the table");
			FinancialStatementQuarterDownLoadKabuproInsertDB.dropTable(con);
			System.out.println("finish drop the table");
			System.out.println("start create the table");
			FinancialStatementQuarterDownLoadKabuproInsertDB.createTable(con);
			System.out.println("finish create the table");
			System.out.println("start insert date to the table");
			FinancialStatementQuarterDownLoadKabuproInsertDB.insertIntoDB(
					result, con);
			System.out.println("" + "finish insert date to the table");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
