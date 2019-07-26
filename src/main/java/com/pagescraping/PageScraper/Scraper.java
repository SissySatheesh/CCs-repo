package com.pagescraping.PageScraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author sissy 
 * Web Page Scraper program.
 */
public class Scraper {
   
	static Logger myLogger = Logger.getLogger(Scraper.class.getName());
	
	private HtmlPage page;
	private HashMap<String, Integer> analyzeMap;
	private long totalTime;
	private long pageSize;
	
	/**
	 * No argument Constructor : initialize analyzeMap to generate report.
	 */
	public Scraper() {
		this.analyzeMap = new LinkedHashMap<String, Integer>();
	}

	/**
	 * @param baseUrl Method will populate the HtmlPage object. Calculate the total
	 *                time take to get the page loaded
	 */
	private void getPageLoder(String baseUrl)
			throws IOException, FailingHttpStatusCodeException, MalformedURLException {
		final WebClient client = new WebClient();
		client.getOptions().setCssEnabled(false);
		client.getOptions().setJavaScriptEnabled(false);
		client.getOptions().setThrowExceptionOnScriptError(false);
		try {
			page = client.getPage(baseUrl);

			totalTime = page.getWebResponse().getLoadTime();
			pageSize = page.getWebResponse().getContentLength();
			myLogger.info("===>>> Page loaded Sucessfully. ");
			
		} catch (NullPointerException exc) {
			myLogger.info("===>>> Webpage not loaded");
			throw new RuntimeException("Webpage not loaded");
		} finally {
			client.close();
		}

	}

	/**
	 * @param keys Method will create a Map for given html tag element and no.of
	 *             times it appear in web page.
	 */
	private void analyzePage(String[] keys) throws Exception {

		analyzeMap.clear();

		for (String key : keys) {

			analyzeMap.put(key, page.getByXPath(key).size());
		}

	}

	/**
	 * @param baseUrl Method will generate a report based on analysis.
	 */
	public void generateReport(String baseUrl) {
		StringBuilder sb = new StringBuilder();
		String[] keys = { "//a[@href]", "//figure|//picture/source|picture/img|//img[@src]", "//video|//iframe" };

		try {
            
			myLogger.info("===>>> calling new Scraper().getPageLoder(baseurl)");
			getPageLoder(baseUrl);
			myLogger.info("===>>> calling new Scraper().analyzePage(baseurl)");
			analyzePage(keys);

			sb.append(String.format("%40s%n", "WebPage Analyze Report"));
			sb.append(String.format("%n"));
			sb.append(String.format("%s %10s %10s%n", "Total time to lode page", ":", this.totalTime + "ms"));
			sb.append(String.format("%n"));
			sb.append(String.format("%s %10s %10s%n", "Total Content - Length ", ":", this.pageSize));
			sb.append(String.format("%n"));

			for (Entry<String, Integer> entry : analyzeMap.entrySet()) {

				if (entry.getKey() == "//a[@href]") {
					sb.append(String.format("%s %10s %10s%n", "No.of Links in Webpage ", ":", entry.getValue()));
				} else if (entry.getKey() == "//figure|//picture/source|picture/img|//img[@src]") {
					sb.append(String.format("%s %10s %10s%n", "No.of Images in Webpage", ":", entry.getValue()));
				} else if (entry.getKey() == "//video|//iframe") {
					sb.append(String.format("%s %10s %10s%n", "No.of videos in Webpage", ":", entry.getValue()));
				}

			}

			System.out.println(sb.toString());

		} catch (IllegalFormatException exc) {
			sb.append("Report generating Error:There are insufficient arguments for Formting ");
		} catch (NullPointerException exc) {
			sb.append("Report generating Error:Format is null ");
		} catch (Exception exc) {
			sb.append(exc.getMessage());
			myLogger.info("===>>> "+exc.getMessage());
			exc.printStackTrace();
		}
	}

	/**
	 * @return the HtmlPage;
	 */
	public HtmlPage getPage() {
		return page;
	}

	/**
	 * 
	 * @return the totalTime
	 */
	public long getTotalTime() {
		return totalTime;
	}

	/**
	 * @return the pageSize
	 */
	public long getPageSize() {
		return pageSize;
	}

}
