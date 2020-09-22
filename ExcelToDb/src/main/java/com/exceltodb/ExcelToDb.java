package com.exceltodb;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.exceltodb.addtodb.AddToDbClass;
import com.exceltodb.searchdb.SearchDb;

public class ExcelToDb {

	public static void main(String[] args) {

		try {

			String jdbcURL = "jdbc:mysql://localhost:3306/registration";
			String username = "root";
			String password = "root@123";
			Class.forName("com.mysql.jdbc.Driver");

			String excelFilePath = "C:\\Users\\karthikeyan.bala\\Downloads\\Students.xlsx";

			Connection connection = null;

			connection = DriverManager.getConnection(jdbcURL, username, password);

			FileInputStream inputStream = new FileInputStream(excelFilePath);

			Workbook workbook = new XSSFWorkbook(inputStream);

			Sheet firstSheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = firstSheet.iterator();
			rowIterator.next();
			int Entry = 1;

			while (rowIterator.hasNext()) {

				Row row = rowIterator.next();
				String Ref_number = row.getCell(0).getStringCellValue();
				int Account_Number = (int) row.getCell(1).getNumericCellValue();
				int Date_Time = (int) row.getCell(2).getNumericCellValue();
				String Description = row.getCell(3).getStringCellValue();
				int Withdrawals = (int) row.getCell(4).getNumericCellValue();
				int credit = (int) row.getCell(5).getNumericCellValue();
				int runningBalance = credit - Withdrawals;
				if (runningBalance <= 0) {
					System.out.println("balance in negative..." + runningBalance);
					runningBalance *= -1;
				}
				System.out.println(Ref_number + "-" + Account_Number + "-" + Date_Time + "-" + Description + "-"
						+ Withdrawals + "-" + credit + "- " + runningBalance);
				if (Entry == 1) {
					System.out.println("adding to DB...Entry no - " + Entry);
					AddToDbClass.addToDb(Ref_number, Account_Number, Date_Time, Description, Withdrawals, credit,
							runningBalance, connection);
				} else {
					System.out.println("adding to DB...Entry no - " + Entry);
					System.out.println("calling search function...");
					int AvailableBalance = SearchDb.SearchInDb(Account_Number, Entry, connection);
					int ActualBalance = runningBalance + AvailableBalance;
					System.out.println("actual balance after Transaction - " + ActualBalance);
					AddToDbClass.addToDb(Ref_number, Account_Number, Date_Time, Description, Withdrawals, credit,
							ActualBalance, connection);

				}
				Entry++;

			}

		} catch (IOException ex1) {
			System.out.println("Error reading file");
			ex1.printStackTrace();
		} catch (SQLException ex2) {
			System.out.println("Database error");
			ex2.printStackTrace();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
