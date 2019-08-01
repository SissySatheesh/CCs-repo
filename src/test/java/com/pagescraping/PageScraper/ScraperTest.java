package com.pagescraping.PageScraper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.host.css.ComputedCSSStyleDeclaration;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLElement;


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
		final WebClient client = new WebClient(BrowserVersion.CHROME);
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
		Assert.assertEquals(115,page.getByXPath("//a[@href]").size());
		Assert.assertEquals(9,page.getByXPath("//figure|//picture/source|picture/img|//img[@src]").size());
		Assert.assertEquals(0,page.getByXPath("//video/source[@src]").size());
		
		
	}
//	
	@Test
	public void testProcessImage() {
		
		HtmlElement figure = (HtmlElement) page.getByXPath("//figure|//picture/source|picture/img|//img[@src]").get(0);	
	   ComputedCSSStyleDeclaration cs = ((HTMLElement) figure.getScriptableObject()).getCurrentStyle();
	  
	   Assert.assertEquals("url(/v/home/el/images/heroes/iphone-xr/main__bmngiblug0mq_largetall_2x.jpg)", cs.getBackgroundImage());
	   Assert.assertEquals("471px",  (cs.getHeight()));
	   Assert.assertEquals("865px",  (cs.getWidth()));
		
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
		Assert.assertEquals(8607,pageSize);
		
	}
}
