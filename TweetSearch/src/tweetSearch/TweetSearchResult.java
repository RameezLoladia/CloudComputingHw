package tweetSearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Entity;

public class TweetSearchResult implements Serializable{
	
	public String searchQuery;
	public List<Entity> positive= new ArrayList<Entity>();
	public List<Entity> negative=new ArrayList<Entity>();
	public List<Entity> neutral=new ArrayList<Entity>();
	public String sentiment;
}
