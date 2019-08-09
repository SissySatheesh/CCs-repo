package com.pagescraping.PageScraper;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlFigure;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlMedia;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLElement;

/**
 * @author sissy Web Page Scraper program.
 */
/**
 * @author sissy
 *
 */
public class Scraper {

	static Logger myLogger = Logger.getLogger(Scraper.class.getName());

	private HtmlPage page;
	private HashMap<String, List<Object>> analyzeMap;
	private long totalTime;
	private long pageSize;
	private String baseUrl;

	/**
	 * No argument Constructor : initialize analyzeMap to generate report.
	 */
	public Scraper(String baseUrl) {
		this.baseUrl = baseUrl;
		this.analyzeMap = new LinkedHashMap<String, List<Object>>();
	}

	/**
	 * @param baseUrl Method will populate the HtmlPage object. Calculate the total
	 *                time take to get the page loaded
	 */
	private void getPageLoder(String baseUrl)
			throws IOException, FailingHttpStatusCodeException, MalformedURLException {
		final WebClient client = new WebClient(BrowserVersion.CHROME);
		client.getOptions().setCssEnabled(true);
		client.getOptions().setJavaScriptEnabled(true);
		client.getOptions().setThrowExceptionOnScriptError(false);
		client.getOptions().setThrowExceptionOnFailingStatusCode(false);
		client.waitForBackgroundJavaScript(1000);
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
			
			if(key == "//figure|//picture/source|picture/img|//img") {
	
				List<Object> list = page.getByXPath(key);
				myLogger.info("===>>> calling new Scraper().processImage(list)");
				analyzeMap.put(key,processImage(list));
			}
		
			else {
			  analyzeMap.put(key, page.getByXPath(key));
			} 
			
		}
	}

	/**
	 * @param list
	 * @return process images and returns image list 
	 * @throws Exception
	 */
	private List<Object> processImage(List<Object> list) throws Exception  {
		
		List<Object> imageList =  new ArrayList<Object>();
		
		for (Object obj : list){
			if(obj instanceof HtmlFigure) {
			HtmlFigure fig = (HtmlFigure) obj;
			
			   if(fig.getByXPath("//figure/video").isEmpty()) {
				   if(getImageUrl(fig) != null)
			  
				       imageList.add(obj);	     
			   }
			 
		    }
			else {
			       imageList.add(obj);
			}
	    }// end of for loop
		
		return imageList;
	}
	

	/**
	 * @param obj
	 * @return Method returns url String for Images
	 */
	private String getImageUrl(Object obj) {
		String url =null;
		
		HtmlElement figure = (HtmlElement) obj;
	    
		
		if(figure instanceof HtmlImage) {
			
			if(!figure.getAttribute("src").isEmpty()) {
				
				url = figure.getAttribute("src");
			}
		}
		else {
		  String cssUrl = ((HTMLElement) figure.getScriptableObject()).getCurrentStyle().getBackgroundImage();
		 
		  if(cssUrl.equalsIgnoreCase("none")  || cssUrl.isEmpty()) {
			  
			  url = null;
		  }
		  else {
			 int firstDex = cssUrl.indexOf("(");
			int lastDex = cssUrl.indexOf(")");
				if (firstDex != -1 && lastDex != -1)
					url = this.baseUrl + cssUrl.substring(firstDex + 1, lastDex);
		  }
		}
		
		return url;
	}
	
	/**
	 * @param list
	 * @return : Method returns a String with url and size of images .
	 * @throws Exception
	 * 
	 */
	private String gerenerateImageDetails(List<Object> list) throws Exception {

		StringBuilder sb = new StringBuilder();
		String size = "";

		sb.append(String.format("%n%n"));
		sb.append(String.format("%60s%n", "Images details"));
		sb.append(String.format("%n"));
		if (list.isEmpty()) {
			sb.append("No Images to display");
		} else {
			sb.append(String.format("%-120s%-10s  %n", "url ", "Size"));
			sb.append(String.format("%n"));

			for (Object obj : list) {
				
				String url = getImageUrl(obj);

				if (url != null) {

					myLogger.info("===>>> calling new Scraper().getAssetSize(url)");
					size = getImageSize(url);
				}
				sb.append(String.format("%-120s%-10s %n", url, size));

			}
		}
		return sb.toString();
	}
	
	
	/**
	 * @param obj
	 * @return Method returns url String for videos
	 */
	private String getVideoUrl(Object obj) {
		String url =null;
		
		HtmlElement video = (HtmlElement) obj;
		
		if(video instanceof HtmlMedia) {
			
			url = video.getAttribute("src");
		}
		else if(video instanceof HtmlFigure) {
		 url = video.getAttribute("src");
		   
		}
		return url;
	}

	/**
	 * @param list
	 * @return : Method generates a String with url and size of videos .
	 * @throws Exception
	 * 
	 */
	private String generateVideoDetails(List<Object> list) throws Exception {

		StringBuilder sb = new StringBuilder();
		String size ="";

		sb.append(String.format("%n%n"));
		sb.append(String.format("%60s%n", "Video details"));
		sb.append(String.format("%n"));
		if (list.isEmpty()) {
			sb.append("No videios to display");
		} else {
			sb.append(String.format("%-120s%-10s %n", "url ", "Size"));
			sb.append(String.format("%n"));

			for (Object obj : list) {
				
				String url = getVideoUrl(obj);

				if (url != null) {
					myLogger.info("===>>> calling new Scraper().getAssetSize(url)");
					size = getVideoSize(url);
				}

				sb.append(String.format("%-120s%-10s %n", url,size+"kb"));

			}
		}

		return sb.toString();
	}

	/**
	 * @param baseUrl Method will generate a report based on analysis.
	 */
	public void generateReport() {

		StringBuilder sb = new StringBuilder();
		String[] keys = { "//a[@href]", "//figure|//picture/source|picture/img|//img", "//video[@src]|//figure/video" };
		String imageDetails = "";
		String videoDetails = "";

		try {

			myLogger.info("===>>> calling new Scraper().getPageLoder(baseurl)");
			getPageLoder(baseUrl);
			myLogger.info("===>>> calling new Scraper().analyzePage(baseurl)");
			analyzePage(keys);

			for (Entry<String, List<Object>> entry : analyzeMap.entrySet()) {
				if (entry.getKey() == "//figure|//picture/source|picture/img|//img") {
					myLogger.info("===>>> calling new Scraper().gerenerateImageDetails(List<Object> list)");
					imageDetails = gerenerateImageDetails(entry.getValue());
				}

				if (entry.getKey() == "//video[@src]|//figure/video") {
					myLogger.info("===>>> calling new Scraper(). generateVideoDetails(List<Object> list)");
					videoDetails = generateVideoDetails(entry.getValue());
				}

			}

			sb.append(String.format("%40s%n", "WebPage Analyze Report"));
			sb.append(String.format("%n"));
			sb.append(String.format("%s %10s %10s%n", "Total time to load page", ":", this.totalTime + "ms"));
			sb.append(String.format("%n"));
			sb.append(String.format("%s %10s %10s%n", "Total Content - Length ", ":", this.pageSize));
			sb.append(String.format("%n"));

			for (Entry<String, List<Object>> entry : analyzeMap.entrySet()) {

				if (entry.getKey() == "//a[@href]") {
					sb.append(String.format("%s %10s %10s%n", "No.of Links in Webpage ", ":", entry.getValue().size()));
				} else if (entry.getKey() == "//figure|//picture/source|picture/img|//img") {
					sb.append(String.format("%s %10s %10s%n", "No.of Images in Webpage", ":", entry.getValue().size()));
				} else if (entry.getKey() == "//video[@src]|//figure/video") {
					sb.append(String.format("%s %10s %10s%n", "No.of videos in Webpage", ":", entry.getValue().size()));
				}

			}

			sb.append(imageDetails);
			sb.append(videoDetails);
			
			System.out.println(sb.toString());

			myLogger.info("===>>> Report generated Sucessfully. ");

		} catch (IllegalFormatException exc) {
			sb.append("Report generating Error:There are insufficient arguments for Formting ");
		} catch (NullPointerException exc) {
			sb.append("Report generating Error:Format is null ");
		} catch (Exception exc) {
			sb.append(exc.getMessage());
			myLogger.info("===>>> " + exc.getMessage());
			exc.printStackTrace();
		}
	}

	/**
	 * @param urlStr
	 * @return Method returns Size of an image in Kb
	 */
	public String getImageSize(String urlStr) {
		
		double size = 0;
		int height = 0;
		int width = 0;

		try {
			URL url = new URL(urlStr);
			myLogger.info("===>url -- " + url);
			URLConnection conn =  url.openConnection();

			// get the size of image
			size = (double) conn.getContentLength() / 1024;

			InputStream in = conn.getInputStream();
			BufferedImage image = ImageIO.read(in);
			
			// get the dimension
			width = image.getWidth();
			height = image.getHeight();
		} catch (FileNotFoundException exc) {
			exc.printStackTrace();
			return "Image not found";	

		} catch (IOException exc) {
             
			exc.printStackTrace();
		}

		myLogger.info("====> size - " + size);
		myLogger.info("====> height - " + height + " width - " + width);

		DecimalFormat df = new DecimalFormat("#.0");

		return df.format(size)+" kb";
	}
	
	/**
	 * @param urlStr
	 * @return Method returns Size of an image in Kb
	 */
	public String getVideoSize(String urlStr) {
		
		double size = 0;

		try {
			URL url = new URL(urlStr);
			myLogger.info("===>url -- " + url);
			 HttpURLConnection conn = null;
			 conn = (HttpURLConnection) url.openConnection();
             
			 conn.setRequestMethod("HEAD");
	          conn.getInputStream();
			
	          // get the size of image
			size = (double) conn.getContentLength() / (1024*1024);
			System.out.println("==>Size:"+size);

		} catch (FileNotFoundException exc) {
			exc.printStackTrace();
			return "video not found";	

		} catch (IOException exc) {
             
			exc.printStackTrace();
		}

		myLogger.info("====> size - " + size);

		DecimalFormat df = new DecimalFormat("#.0");

		return df.format(size)+" mb";
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
