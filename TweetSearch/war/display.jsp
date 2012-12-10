<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import = "org.json.simple.JSONArray"  %>
<%@ page import = "org.json.simple.JSONObject"  %>
<%@ page import = "org.json.simple.parser.JSONParser"  %>
<%@ page import = "org.json.simple.parser.ParseException"  %>
<%@ page import = "java.util.*"  %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="tweetSearch.TweetSearchResult" %>
<%@ page import="tweetSearch.Sentiment" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<%


	TweetSearchResult  result = (TweetSearchResult)request.getAttribute("result");
	Entity e =null;
	int k = 0;
	if(result.sentiment.equals("Positive"))
		e = result.positive.get(0);
	else if(result.sentiment.equals("Neutral"))
		e = result.neutral.get(0);
	else if(result.sentiment.equals("Negative"))
		e=result.negative.get(0);
	
	if(e!=null){
 %> 
 
 <table border="true">
        <th>Search Topic</th>
    	<th>Tweet Id</th>
    	<th>User Id</th>
    	<th>From User</th>
    	<th>From User Name</th>
    	<th>Created At</th>
    	<th>Image</th>
    	<th>Tweet</th>
    	<th>Sentiment</th>
 
 
 
 
 <tr>
 <td>
 <%=
     result.searchQuery
 
 %>
 
 </td>
 <td>
 <%=
    	e.getProperty("id_str")
 %>
 </td>
 
 <td>
 <%=
		 e.getProperty("from_user_id_str")     
 %>
 </td>
 
 <td>
 <%=
		 e.getProperty("from_user")   
 %>
 </td>
 
 <td>
 <%=
		e.getProperty("from_user_name")     
 %>
 </td>
 
 <td>
 <%=
		 e.getProperty("created_at")    
 %>
 </td>

 <td>
 <img src = "<%=e.getProperty("profile_image_url")%>" ></img>
 </td>

 <td>
 <%=
		 e.getProperty("text")    
 %>
 </td>
 <td>
 <%=
 	result.sentiment
 %>
 </td>
 </tr> 	
 <%
	}
 %> 	
  
 </table>
 

</body>
</html>