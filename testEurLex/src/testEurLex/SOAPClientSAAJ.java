package testEurLex;

import java.io.File;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

public class SOAPClientSAAJ {
 
	private static String[] languages = {"en"}; // List of languages we want to fetch
	private static Integer numPages = 1; // Number of result pages we want to fetch
	private static Integer pageSize = 10; // Each result page will have this number of documents. If you take numPages = 10 and pageSize = 5 you will retrieve a total of 50 documents
	// We only search for the specified Eurovoc categories and only documents which are published after 2000
	private static String query = "<![CDATA[SELECT DC, CC, DD_DISPLAY, TI_DISPLAY, PS_ID, DN, URI_TYPE WHERE ("+
	"DC = \"life sciences\" OR \"physical sciences\" OR \"applied sciences\" OR \"earth sciences\" OR \"space science\" OR \"behavioural sciences\" OR \"social sciences\" OR \"State\" OR \"political ideology\" OR \"political institution\" OR \"political philosophy\" OR \"political power\""
			+ "AND (PD >= 01/01/2000))]]>";
	
    public static void main(String args[]) throws Exception {

    	System.out.println("This is the query we are going to execute");
    	System.out.println("-----------------------------------------");
    	System.out.println(query);
    	// Path where to save the results
    	String myFolder = "/Users/silviagdiez/Documents/workspace - Luna/testEurLex/src/misc/";
    	
    	// For each language
    	for (int l=0; l<languages.length; l++){
    		String language = languages[l];
    		
    		// For each page in the results
        	for (int i=0; i<numPages; i++){		
        		Integer page = i+1;
        		
        		// Create SOAP Connection
                SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
                SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        		
        		// Send SOAP Message to SOAP Server
                String url = "http://eur-lex.europa.eu/EURLexWebService";
                SOAPMessage soapResponse = soapConnection.call(createSOAPRequestEurLex03(page,language), url);
         
                // print SOAP Response
                //System.out.print("Response SOAP Message:");
                //soapResponse.writeTo(System.out);
                soapConnection.close();
                
                // We save response to file
                String fileName = "results_EurLex.xml";
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.transform(
                		 soapResponse.getSOAPPart().getContent(),
                		 new StreamResult(new File(myFolder+fileName))); 

                // We parse the SOAP response to extract the CELEX codes
                new ParseXML(myFolder,fileName,language);
        	}
    	}
    	
        
    }
 
    private static SOAPMessage createSOAPRequestEurLex03(Integer page, String language) throws Exception {
        /*
        Constructed SOAP Request Message:
        <soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:sear="http://eur-lex.europa.eu/search">
          <soap:Header>
            <wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" soap:mustUnderstand="true">
              <wsse:UsernameToken xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" wsu:Id="UsernameToken-1">
                <wsse:Username>garcisk</wsse:Username>
                <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">Your received password</wsse:Password>
              </wsse:UsernameToken>
            </wsse:Security>
          </soap:Header>
          <soap:Body>
            <sear:searchRequest>
              <sear:expertQuery>
              <![CDATA[(Directory_code = "0510" OR "0520" OR "0610" OR "062020" OR "0630" OR "1030" OR "1210" OR "1310" OR "1330" OR "1340" OR "151010" OR "15102020" OR "15102030" OR "15102040" OR
              "15102050" OR "15103010" OR "15103030" OR "152010" OR "1620" OR "173010" OR "191040" OR "1530") AND (CELEX_number = "3*" OR CELEX_number = "5*")]]>
              </sear:expertQuery>
              <sear:page>1</sear:page>
              <sear:pageSize>100</sear:pageSize>
              <sear:searchLanguage>en</sear:searchLanguage>
            </sear:searchRequest>
          </soap:Body>
        </soap:Envelope>
        */
               
        // We use SOAP v1.2
        MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
 
        // URI of our search service
        String serverURI = "http://eur-lex.europa.eu/search";
       
        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("sear", serverURI);
       
       
        // SOAP Header
        try {
        	SOAPFactory factory = SOAPFactory.newInstance();
        	String prefix = "wsse";
        	String uri = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
        	SOAPElement securityElem = factory.createElement("Security", prefix, uri);
        	
        	// Security token
        	SOAPElement tokenElem = factory.createElement("UsernameToken", prefix, uri);
        	tokenElem.addAttribute(QName.valueOf("wsu:Id"), "UsernameToken-1");
        	tokenElem.addAttribute(QName.valueOf("xmlns:wsu"), "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
        	
        	// Username and password
        	SOAPElement userElem = factory.createElement("Username", prefix, uri);
        	userElem.addTextNode("garcisk");
        	SOAPElement pwdElem = factory.createElement("Password", prefix, uri);
        	pwdElem.addTextNode("Fjnc1BzWG2m");
        	pwdElem.addAttribute(QName.valueOf("Type"), "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText");
        	tokenElem.addChildElement(userElem);
        	tokenElem.addChildElement(pwdElem);
        	
        	securityElem.addChildElement(tokenElem);
        	securityElem.setAttribute("mustUnderstand", "true");
        	SOAPHeader header = envelope.getHeader();
        	header.addChildElement(securityElem);
        	
        	
        } catch (Exception e) {
        		e.printStackTrace();
        }
           
       
        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElem = soapBody.addChildElement("searchRequest", "sear");
        
        // Expert query in EurLex
        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("expertQuery", "sear");
        soapBodyElem1.addTextNode(query);
       
        // Result page
        SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("page", "sear");
        soapBodyElem2.addTextNode(page.toString());
        //soapBodyElem2.addTextNode("1");
       
        // Page size (maximum size)
        SOAPElement soapBodyElem3 = soapBodyElem.addChildElement("pageSize", "sear");
        soapBodyElem3.addTextNode(pageSize.toString());
        //soapBodyElem3.addTextNode("1");
       
        // Search language
        SOAPElement soapBodyElem4 = soapBodyElem.addChildElement("searchLanguage", "sear");
        soapBodyElem4.addTextNode(language);
        //soapBodyElem4.addTextNode("en");
 
        soapMessage.saveChanges();
 
        // Print the request message
        System.out.println("\n\nRequest SOAP Message");
    	System.out.println("------------------");
        soapMessage.writeTo(System.out);
        System.out.println();
        System.out.println();
        System.out.println();
 
        return soapMessage;
    }
 
}
