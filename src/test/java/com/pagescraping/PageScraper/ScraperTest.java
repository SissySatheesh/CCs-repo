package com.pagescraping.PageScraper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


/**
 * @author sissy
 * Class test Scraper.java
 *
 */
public class ScraperTest {
   
	private String baseUrl = "https://www.apple.com";
	private  HtmlPage page;
	private long totalTime;
	private long pageSize;
	
   
	/**
	 * @throws Exception
	 * load page and populate totalTime and pageSize.
	 * Must run before every test case.
	 */
	@Before
	public void init() throws Exception {
		final WebClient client = new WebClient();
		client.getOptions().setCssEnabled(true);
		client.getOptions().setJavaScriptEnabled(true);
		client.getOptions().setThrowExceptionOnScriptError(false);
		try {
			 page = client.getPage(baseUrl);
			 totalTime = page.getWebResponse().getLoadTime();
			 pageSize = page.getWebResponse().getContentLength();
		}  
		finally {
			client.close();
		}
	}
	
	/**
	 * tests Scraper.generateReport()
	 */
	@Test
	public void testGenerateReport() {
		Assert.assertTrue(page.isHtmlPage());
		Assert.assertNotEquals(0, totalTime);
		Assert.assertEquals(119,page.getElementsByTagName("a").size());
		Assert.assertEquals(9,page.getElementsByTagName("figure").size());
		Assert.assertEquals(2,page.getElementsByTagName("video").size());
		
		
	}
	
	/**
	 * tests Scraper.getPage()
	 */
	@Test
	public void testGetPage() {
		Assert.assertNotNull(page);
		Assert.assertTrue(page.isHtmlPage());
		
	}

	/**
	 * tests Scraper.getTotalTime()
	 */
	@Test
	public void testGetTotalTime() {
		Assert.assertNotEquals(0, totalTime);
		
	}
	
	/**
	 * tests Scraper.getPageSize()
	 */
	@Test
	public void testGetPageSize() {
		Assert.assertEquals(9144,pageSize);
		
	}
}
