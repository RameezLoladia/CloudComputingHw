package tweetSearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;





@SuppressWarnings("serial")
public class tweetSearchServlet extends HttpServlet {	
	    public void doGet(HttpServletRequest request, HttpServletResponse response)
	            throws IOException, ServletException {
	        response.setContentType("text/plain");
	        MemcacheService syncCache = null;
	        String searchTopic = request.getParameter("content");
	        String key = searchTopic;
	        if(syncCache==null){
	        	syncCache = MemcacheServiceFactory.getMemcacheService();
	        	syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
	        }
	        TweetSearchResult result = ( TweetSearchResult) syncCache.get(key);
	        request.setAttribute("query",searchTopic);
	        if(result == null){
	        	
	        	searchTopic = searchTopic.replaceAll(" ","%20");
	        	String searchUrl = "http://search.twitter.com/search.json?q="+searchTopic;
	        	URL url = new URL(searchUrl);
	        	System.out.println(url);
	        	BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	        	String value = "";
	        	String inputLine;
	        	while ((inputLine = in.readLine()) != null) {
	        		value += inputLine;
	        	}
	        	in.close();
	        	System.out.println(value);
	        	JSONParser parser = new JSONParser();

	        	try {

	        		String jsonText = value;
	        		Object obj = parser.parse(jsonText);

	        		JSONObject jsonObject = (JSONObject) obj;
	        	    List<Map<String,String>> results =( List<Map<String,String>>)jsonObject.get("results");
	        	    System.out.println(results);
	        	    String query =(String)request.getAttribute("query");
	        	    System.out.println(query);
	        	    Key queryKey = KeyFactory.createKey("query", query);
	        	    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	        	    int positive = 0, negative = 0, neutral = 0;
	        	    TweetSearchResult r = new TweetSearchResult();
	        	    r.searchQuery=key;
	        	    for(int i=0;i<results.size();i++){
	        	    	Entity searchTweet = new Entity("Tweet",queryKey);
	        	        searchTweet.setProperty("id_str",results.get(i).get("id_str"));
	        	        searchTweet.setProperty("from_user_id_str",results.get(i).get("from_user_id_str"));
	        	        searchTweet.setProperty("from_user",results.get(i).get("from_user"));
	        	        searchTweet.setProperty("from_user_name",results.get(i).get("from_user_name"));
	        	        searchTweet.setProperty("created_at",results.get(i).get("created_at"));
	        	        searchTweet.setProperty("profile_image_url",results.get(i).get("profile_image_url"));
	        	        searchTweet.setProperty("text",results.get(i).get("text")  );
	        	        String urlQuery =  results.get(i).get("text");
	        	      //  urlQuery = urlQuery.replaceAll("[^a-zA-Z0-9/\\s]" , "\\s");
	        	   
	        	        	
	        	        	
	        	        
	        	        
	        	        String searchUrl1 = "http://twittersentiment.appspot.com/api/classify?text="+URLEncoder.encode(urlQuery, "ISO-8859-1");
	        	         
	        	         
	        	        URL url1 = new URL(searchUrl1.toString());
	        	        System.out.println(url1);
	    	        	BufferedReader in1 = new BufferedReader(new InputStreamReader(url1.openStream()));
	    	        	String value1 = "";
	    	        	String inputLine1;
	    	        	while ((inputLine1 = in1.readLine()) != null) {
	    	        		value1 += inputLine1;
	    	        	}
	    	        	in1.close();
	    	        	System.out.println(value1);
	    	        	Object obj1 = parser.parse(value1);

		        		JSONObject jsonObject1 = (JSONObject) obj1;
		        		Map<String,Object> results1 = (Map<String,Object>)jsonObject1.get("results");
		        		Long polarity = (Long)(results1.get("polarity"));
		        		if(polarity==0){
		        			  searchTweet.setProperty("sentiment","Negative" );
		        			  negative++;
		        			  r.negative.add(searchTweet);
		        		}
		        		else if(polarity==2){
		        			 searchTweet.setProperty("sentiment","Neutral");
		        			 neutral++;
		        			 r.neutral.add(searchTweet);
		        		}
		        		else{
		        			 searchTweet.setProperty("sentiment","Positive" );
		        			 positive++;
		        			 r.positive.add(searchTweet);
		        		}
		        			
	        	        datastore.put(searchTweet);
	        	    	
	        	    }
	        	    
	        	    if(positive>=negative && positive >= neutral){
	        	    	r.sentiment = "Positive";
	        	    }
	        	    else if(negative>=positive && negative >= neutral){
	        	    	r.sentiment = "Negative";
	        	    }
	        	    else{
	        	    	r.sentiment = "Neutral";
	        	    }
	                syncCache.put(key,r);
	                request.setAttribute("result",r); 
	                RequestDispatcher rd = getServletContext().getRequestDispatcher("/display.jsp");
	                rd.forward(request, response);
	          } catch (Exception e) {
	        		e.printStackTrace();
	          }
	        }
	        	else{
	        		request.setAttribute("result",result); 
	                RequestDispatcher rd = getServletContext().getRequestDispatcher("/display.jsp");
	                rd.forward(request, response);
	        	}
	        			        	
	            
	    }            
	

}
