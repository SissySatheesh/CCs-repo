package com.pagescraping.PageScraper;

import java.util.logging.Logger;

/**
 * @author sissy
 * This Class analyze a web page and generate report
 * Expecting  URL as argument
 *
 */
public class WebPageAnalyzer {
	
	static Logger myLogger = Logger.getLogger(WebPageAnalyzer.class.getName());
	
	public static void main(String[] args) {
		try {
			if (args.length == 0) {
				throw new RuntimeException("No argument passed");
			}
			
			String baseUrl = args[0];
			
			
			if (baseUrl.startsWith("https://")) {
				myLogger.info("===>>> URL : " + baseUrl);
				Scraper scraper = new Scraper();
				myLogger.info("===>>> calling new Scraper().generateReport() method from WebPageAnalyzer ");
				scraper.generateReport(baseUrl);				
				
			} else {
				throw new RuntimeException("Invalid Argument Passed.Expecting a valid URl");
			}
		} catch (Exception exc) {
			myLogger.info("===>>> "+exc.getMessage());
			exc.printStackTrace();
		}

	}
}
