package vocal;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTMLExtractor {

	public static final String wikiURL = "https://en.wikipedia.org";
	
	public static String getWikiText(String page) {
		String html = getHTML(wikiURL + page);
		char[] htmlArr = html.toCharArray();
		StringBuilder builder = new StringBuilder();
		boolean inP = false;
		boolean inTag = false;
		int numTags = 0;
		for (int i = 0; i < htmlArr.length; i++) {
			// break after 2000 chars for now
			if (i > 100000) {
				break;
			}
			if (htmlArr[i] == '<') {
				inTag = true;
			} else if (htmlArr[i] == '>') {
				inTag = false;
			} else if (inTag && htmlArr[i] == 'p' && htmlArr[i-1] == '<') {
				inP = true;
			} else if (inTag && htmlArr[i] == 'p' && htmlArr[i-1] == '/' && htmlArr[i-2] == '<') {
				inP = false;
			} else if (!inTag && inP) {
				builder.append(htmlArr[i]);
			}
		}
		return builder.toString();
	}
	
	private static String getHTML(String pageURL) {
		HttpURLConnection connection = null;  
		try {
			//Create connection
			URL url = new URL(pageURL);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");
			
			//Get Response
			int responseCode = connection.getResponseCode();
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+ 
			String line;
			while((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\n');
			}
			rd.close();
			return response.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if(connection != null) {
				connection.disconnect(); 
			}
		}

//		//add request header
//		
//
//		int responseCode = con.getResponseCode();
//		System.out.println("\nSending 'GET' request to URL : " + url);
//		System.out.println("Response Code : " + responseCode);
//
//		BufferedReader in = new BufferedReader(
//		        new InputStreamReader(con.getInputStream()));
//		String inputLine;
//		StringBuffer response = new StringBuffer();
//
//		while ((inputLine = in.readLine()) != null) {
//			response.append(inputLine);
//		}
//		in.close();
//
//		//print result
//		System.out.println(response.toString());
	}
	
}
