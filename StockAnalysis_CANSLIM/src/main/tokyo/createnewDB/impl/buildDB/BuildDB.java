package impl.buildDB;
//create database on a new warehouse

import impl.download.HistoricalQuoteDownload;
import impl.download.ListOfTSEListedDownloadImpl;
import impl.update.HistoricalQuoteUpdateMultiThreadVersion;

public class BuildDB {
	
	public static void main(String args[]) {
		
		// TODO Auto-generated constructor stub
		
		// 1.Download list of listed companies 
		
		//ListOfTSEListedDownloadImpl.start();
		
		// 2.Download historical quotes
		
		HistoricalQuoteDownload.start();
		
	}

}
