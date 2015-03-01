package testEurLex;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
	
	  public Crawler(String URL, String path, String celexCode){
		  	System.out.println("Crawling "+URL);
			Document doc = connect(URL);
			
			// We extract the content
			String text = doc.getElementsByTag("body").text();
			
			// We only print files that have a HTML version
			if (text.contains("The requested document does not exist.")){
				System.out.println("The document does not exist in HTML version");
			} else{
				try {
				    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path+celexCode+".txt")));
				    out.println(text);
				    out.close();
				} catch (IOException e) {
				    System.out.println("error");
				}
			}
	  }
		  
	  private static Document connect(String url) {
		    sleep(4);
		  	Document doc = null;
		  	try{
		  	Connection.Response response = Jsoup.connect(url)
		  			.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
		            .timeout(1000*5) //it's in milliseconds, so this means 5 seconds.              
		            .execute();
			  	int statusCode = response.statusCode();
			  	if(statusCode == 200) {
			  		try {
				    	// we identify ourselves as the google bot
				    	doc = Jsoup.connect(url)
				              .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
				              .referrer("http://www.google.com") 
				              .timeout(1000*5) //it's in milliseconds, so this means 5 seconds.              
				              .get();
				    } catch (NullPointerException e) {
				        // TODO Auto-generated catch block
				        e.printStackTrace();
				    } catch (HttpStatusException e) {
				        e.printStackTrace();
				    } catch (IOException e) {
				        // TODO Auto-generated catch block
				        e.printStackTrace();
				    }
			  	}
			  	else {
			  		System.out.println("received error code : " + statusCode);
			  	}
		  	} catch (IOException e) {
	            System.out.println(e);
	        }
		    return doc;
	  }
	  
	  protected static void sleep(int seconds) {
		   try {
		       Thread.sleep(seconds * 1000);
		   } catch (Exception ignored) {
		   }
		}

}
