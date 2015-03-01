package testEurLex;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ParseXML {
	
	public ParseXML(String myFolder, String document, String language) {
			
		try {
			
			// We open the XML file
			File fXmlFile = new File(myFolder+document);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);			
			
			// We obtain the list of results
			doc.getDocumentElement().normalize();
		 	NodeList nList = doc.getElementsByTagName("result");
		 	
		 	// For each result
			for (int temp = 0; temp < nList.getLength(); temp++) {
		 
				Node nNode = nList.item(temp);
		 
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		 
					Element eElement = (Element) nNode;
		 
					// We extract the CELEX code and print it to both files from categories and titles
					String celexCode = eElement.getElementsByTagName("ID_CELEX").item(0).getTextContent().replace(" ","").replace("\n","");
					
					// We print the CELEX code in the titles file and the categories file
					try {
					    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(myFolder+"titles_"+language.toUpperCase()+".txt", true)));
					    out.print(celexCode+"\t");
					    out.close();
					} catch (IOException e) {
					    System.out.println("error");
					}
					try {
					    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(myFolder+"categories.txt", true)));
					    out.print(celexCode+"\t");
					    out.close();
					} catch (IOException e) {
					    System.out.println("error");
					}
					
					// We extract the TITLE and print it to the titles file with UTF-8 encoding
					String title = eElement.getElementsByTagName("EXPRESSION_TITLE").item(0).getTextContent().replace("\n","");
					try {
					    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(myFolder+"titles_"+language.toUpperCase()+".txt",true),"UTF-8"));
					    out.write(title+"\n");
					    out.close();
					} catch (IOException e) {
					    System.out.println("error");
					}
					
					// We extract the Eurovoc descriptors. We do it only for one language (English)!!
					if (language.equals("en")){
						getEurovocDescriptors(eElement,myFolder);
					}
					
					// We crawl the URL of the document ONLY IF IT IS AN HTML (some documents don't have an HTML version)
					String URL = "http://eur-lex.europa.eu/legal-content/"+language.toUpperCase()+"/TXT/HTML/?uri=CELEX:"+celexCode;
					new Crawler(URL,myFolder,celexCode);
					
				}
			}
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
	}

	private void getEurovocDescriptors(Element eElement, String myFolder){
		// We extract the Eurovoc descriptors
		NodeList nListEur = eElement.getElementsByTagName("WORK_IS_ABOUT_CONCEPT_EUROVOC_CONCEPT");
		
		for (int temp1 = 0; temp1 < nListEur.getLength(); temp1++) {
			Node nNodeEur = nListEur.item(temp1);
			
			Element eElementEur = (Element) nNodeEur;
			NodeList nListEur2 = eElementEur.getElementsByTagName("PREFLABEL");
			String descriptor = nListEur2.item(0).getTextContent();
			//System.out.println(descriptor);	
			try {
			    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(myFolder+"categories.txt",true),"UTF-8"));
			    out.write(descriptor+"\t");
			    out.close();
			} catch (IOException e) {
			    System.out.println("error");
			}
		}
		
		try {
		    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(myFolder+"categories.txt",true),"UTF-8"));
		    out.write("\n");
		    out.close();
		} catch (IOException e) {
		    System.out.println("error");
		}
		
		return;
	}
	
}