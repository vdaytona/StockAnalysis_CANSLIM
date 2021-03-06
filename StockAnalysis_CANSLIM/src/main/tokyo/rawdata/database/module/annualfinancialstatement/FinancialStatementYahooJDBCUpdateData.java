package module.annualfinancialstatement;

import impl.update.FinancialStatementYahooUpdateMultiThreadVersion;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 1.check if there is record in independent , consolidate, interim by the
 * keywords: Local_Code, From, and Fiscal_year
 * 
 * @author Daytona
 * 
 */

public class FinancialStatementYahooJDBCUpdateData {

	public FinancialStatementYahooJDBCUpdateData() {
		// TODO Auto-generated constructor stub
	}

	public static void FinancialStatemetYahooJDBCUpdateDataImpl(String code, Connection con) {
		// TODO
		// check and update
		String typeIndependent = "independent";
		String typeConsolidate = "consolidate";
		String typeInterim = "interim";
			FinancialStatemetYahooJDBCUpdateDataExistCheck(code,
					typeIndependent, con);
			FinancialStatemetYahooJDBCUpdateDataExistCheck(code,
					typeConsolidate, con);
			FinancialStatemetYahooJDBCUpdateDataExistCheck(code, typeInterim, con);
	}

	public static void FinancialStatemetYahooJDBCUpdateDataExistCheck(String code,
			String type, Connection con) {
		ArrayList<String> UrlInput = new FinancialStatementYahooJDBCUrlDao()
				.getFinancialStatmentPageBufferedReaderYahooToString(code, type);
		Integer fiscalYearNumber = findFiscalYear(UrlInput);
		String input = "";
		for (int i = 0; i < UrlInput.size(); i++) {
			input = UrlInput.get(i);
			if (input.equals("<tr bgcolor=\"#ffffff\">")) {
				input = UrlInput.get(++i);
				if (input.contains("決算期")) {
					Integer blankYearCount = 0;
					for (int j = 1; j <= fiscalYearNumber; j++) {
						input = UrlInput.get(++i);
						input = input.substring(18, input.length() - 5);
						SimpleDateFormat sdf1 = new SimpleDateFormat(
								"yyyy年MM月期");
						SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM");
						// if input = "---", there is no record in this year
						if (input.equals("---")) {
							blankYearCount++;
							j--;
						} else {
							Date date = null;
							try {
								date = sdf1.parse(input);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							input = sdf2.format(date);
							// check if the result is exist in DB
							Boolean ifHas = checkNetCheckCodeWithDB(code, type,
									input + "01", FinancialStatementYahooUpdateMultiThreadVersion.con);
							if (ifHas.equals(false)) {
								System.out.println("new " + code + " " + input + "01" + " " +type);
								FinancialStatementYahooJDBCRecord record = new FinancialStatementYahooJDBCRecord();
								record = createFinancialStatementYahooJDBCRecord(
										UrlInput, j + blankYearCount);
								record.setCountry("Tokyo");
								record.setLocal_Code(code);
								record.setForm(type);
								record.setName_English(getEnglishName(code,con));
								insertRecordIntoSqlDB(record,con);
							}
						}
					}
				}
			}
		}
	}

	public static Boolean checkNetCheckCodeWithDB(String code, String type, String date, Connection con) {
		// Check
		Boolean ifHas = false;
		String checkifHasRecord = "SELECT COUNT(Local_Code) count FROM "
				+ namespace.DBNameSpace.getFinancailstatementDb() + " WHERE "
				+ "Local_Code = " + code + " AND Form = '" + type
				+ "' AND Fiscal_year = " + date;
		try {
			ResultSet rs = null;
			synchronized (con) {
				rs = con.prepareStatement(checkifHasRecord)
						.executeQuery();
			}
			rs.next();
			if (rs.getInt("count") == 1) {
				ifHas = true;
			} else if (rs.getInt("count") > 1) {
				System.out.println(code + " " + type + " " + date
						+ " has multiple records");
				ifHas = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ifHas;
	}

	/*
	 * public void insertNewDataIntoDB(String code, String type, String date,
	 * BufferedReader fi, Integer fiscalYearNumber) throws IOException { // TODO
	 * String input = ""; while ((input = fi.readLine()) != null) { if
	 * (input.equals("<tr bgcolor=\"#ffffff\">")) { input = fi.readLine(); input
	 * = input.substring(22, input.length() - 5).trim(); //input =
	 * FinancialStatementYahooJDBCJapaneseToEnglish //
	 * .changeIntoEnglishNameFinancialStatement(input);
	 * 
	 * if (input.equals("決算期")) { for (int i = 0; i < fiscalYearNumber; i++) {
	 * input = fi.readLine(); input = input.substring(18, input.length() - 5);
	 * SimpleDateFormat sdf1 = new SimpleDateFormat( "yyyy年MM月期");
	 * SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM"); Date date =
	 * sdf1.parse(input); input = sdf2.format(date); Boolean ifHas =
	 * checkNetCheckCodeWithDB(code, type, input); if (ifHas.equals(false)) {
	 * insertNewDataIntoDB(code, type, input, fi, i); } } break; } } }
	 * fi.close(); }
	 */

	public static Integer findFiscalYear(BufferedReader fi) {
		Integer result = 0;
		String input = null;
		try {
			while ((input = fi.readLine()) != null) {
				if (input.contains("決算期")) {
					input = fi.readLine();
					if (!input.contains("---")) {
						result++;
					}
					input = fi.readLine();
					if (!input.contains("---")) {
						result++;
					}
					input = fi.readLine();
					if (!input.contains("---")) {
						result++;
					}
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Integer findFiscalYear(ArrayList<String> UrlInput) {
		Integer result = 0;
		String input = null;
		for (int i = 0; i < UrlInput.size(); i++) {
			input = UrlInput.get(i);
			if (input.contains("決算期")) {
				i++;
				input = UrlInput.get(i);
				if (!(input.contains("---") || input.contains("tr"))) {
					result++;
				}
				i++;
				input = UrlInput.get(i);
				if (!(input.contains("---") || input.contains("tr"))) {
					result++;
				}
				i++;
				input = UrlInput.get(i);
				if (!(input.contains("---") || input.contains("tr"))) {
					result++;
				}
				break;
			}
		}
		return result;
	}

	public static String getEnglishName(String code, Connection con) {
		String name = null;
		try {
			String selectEnglishNameSql = "SELECT Name_English FROM `TokyoStockExchange_test`.`Section_Tokyo` WHERE "
					+ "Local_code = " + code;
			ResultSet rs = null;
			synchronized (con) {
				rs = con.prepareStatement(selectEnglishNameSql)
						.executeQuery();
			}
			rs.next();
			name = rs.getString("Name_English");
			if (name.contains("'")) {
				String nameArray[] = name.split("'");
				name = "";
				for (int i = 0; i < nameArray.length; i++) {
					if (i != 0) {
						name = name + "\\'" + nameArray[i];
					} else {
						name = name + nameArray[i];
					}
				}
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return name;
	}

	public static void insertRecordIntoSqlDB(FinancialStatementYahooJDBCRecord record, Connection con) {
		String insertSql = "INSERT INTO `TokyoStockExchange_test`.`FinancialStatementTokyo_test` "
				+ record.getFieldsForSqlDB()
				+ " VALUES "
				+ record.getValuesForSqlDB();
		try {
			synchronized (con) {
				con.prepareStatement(insertSql).execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static FinancialStatementYahooJDBCRecord createFinancialStatementYahooJDBCRecord(
			ArrayList<String> UrlInput, Integer fiscalYearNumber) {
		FinancialStatementYahooJDBCRecord record = new FinancialStatementYahooJDBCRecord();
		String input = "";
		FinancialStatementYahooJDBCJapaneseToEnglish changeEnglishName = new FinancialStatementYahooJDBCJapaneseToEnglish();
		FinancialStatementYahooJDBCConvertNetData convertNetData = new FinancialStatementYahooJDBCConvertNetData();
		for (int i = 0; i < UrlInput.size(); i++) {
			input = UrlInput.get(i);
			if (input.equals("<tr bgcolor=\"#ffffff\">")) {
				input = UrlInput.get(++i);
				input = input.substring(22, input.length() - 5).trim();
				String itemName = changeEnglishName
						.changeIntoEnglishNameFinancialStatement(input);
				String value = null;
				try {
					value = convertNetData
							.FinancialStatementYahooJDBCConvertNetDataImpl(
									UrlInput, input, i + fiscalYearNumber);
				} catch (IOException | ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (value != null) {
					record.setValue(itemName, value);
				}
			}
		}
		return record;
	}
}
