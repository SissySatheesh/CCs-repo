package com.pagescraping.PageScraper;

/**
 * @author sissy
 * This Class analyze a web page and generate report
 * Expecting  URL as argument
 *
 */
public class WebPageAnalyzer {
	
	public static void main(String[] args) {
		try {
			if (args.length == 0)
				throw new RuntimeException("No argument passed");
			
			String baseUrl = args[0];
			if (baseUrl.startsWith("https://")) {
				Scraper scraper = new Scraper();
				scraper.generateReport(baseUrl);
			} else {
				throw new RuntimeException("Invalid Argument");
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}

	}
}
