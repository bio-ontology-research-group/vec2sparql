package bio2vec;

import java.io.*;
import java.util.*;
import org.json.*;

import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.util.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;


public class Functions {
    
    public static final String ELASTIC_INDEX_URI = "http://es:9200/bio2vec_test/";
    public static final String NAMESPACE = "http://bio2vec.net/";

    public static double roundTo3(double a) {
	return (int)(a * 1000 + 0.5) / 1000.0;
    }

    public static JSONObject getObject(String d, String v) {
	String query = new JSONObject()
	    .put("query", new JSONObject()
		 .put("term", new JSONObject()
		      .put("id", v)))
	    .toString();

	JSONObject obj = queryIndex(d, query);
	if (obj == null) {
	    return null;
	}
	JSONArray arr = (JSONArray)((JSONObject)obj.get("hits")).get("hits");
	if (arr.length() == 0) {
	    return null;
	}
	obj = (JSONObject)((JSONObject)arr.get(0)).get("_source");
	return obj;
    }
    
    public static double getX(String d, String v) {
	double res = 0.0;
	JSONObject obj = getObject(d, v);
	if (obj != null) {
	    res = Double.parseDouble(obj.get("x").toString());
	}
	return roundTo3(res);
    }

    public static double getY(String d, String v) {
	double res = 0.0;
	JSONObject obj = getObject(d, v);
	if (obj != null) {
	    res = Double.parseDouble(obj.get("y").toString());
	}
	return roundTo3(res);
    }
    
    public static double cosineSimilarity(String d, String v1, String v2) {
	double res = 0.0;
	String query = new JSONObject()
	    .put("query", new JSONObject()
		 .put("constant_score", new JSONObject()
		      .put("filter", new JSONObject()
			   .put("terms", new JSONObject()
				.put("id", new JSONArray().put(v1).put(v2))))))
				     
	    .toString();
	JSONObject obj = queryIndex(d, query);
	if (obj != null) {
	    JSONArray arr = (JSONArray)((JSONObject)obj.get("hits")).get("hits");
	    if (arr.length() == 2) {
		obj = (JSONObject)((JSONObject)arr.get(0)).get("_source");
		String[] vec1 = obj.get("@model_factor").toString().split(" ");
		obj = (JSONObject)((JSONObject)arr.get(1)).get("_source");
		String[] vec2 = obj.get("@model_factor").toString().split(" ");
		double dotProduct = 0.0;
		double normA = 0.0;
		double normB = 0.0;
		for (int i = 0; i < vec1.length; i++) {
		    double a = Double.parseDouble(vec1[i].split("\\|")[1]);
		    double b = Double.parseDouble(vec2[i].split("\\|")[1]);
		    dotProduct += a * b;
		    normA += a * a;
		    normB += b * b;
		}
		res = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	    } else if (v1.equals(v2) && arr.length() == 1) {
		res = 1.0;
	    }
	}
	return roundTo3(res);
    }

    public static ArrayList<String> mostSimilar(String d, String v, int size) {
	ArrayList<String> result = new ArrayList<String>();
	JSONObject obj = getObject(d, v);
	if (obj == null) {
	    return result;
	}
	String res = obj.get("@model_factor").toString();
	JSONArray eArray = new JSONArray();
	for (String x: res.split(" ")) {
	    eArray.put(Double.valueOf(x.split("\\|")[1]));
	}
	String query = new JSONObject()
	    .put("query", new JSONObject()
		 .put("function_score", new JSONObject()
		      .put("script_score", new JSONObject()
			   .put("script", new JSONObject()
				.put("inline", "payload_vector_score")
					.put("lang", "native")
				.put("params", new JSONObject()
				     .put("field", "@model_factor")
				     .put("vector", eArray)
				     .put("cosine", true))))
		      .put("boost_mode", "replace")))
	    .put("sort", new JSONArray()
		 .put(new JSONObject()
		      .put("_score", "desc")))
	    .put("size", size)
	    .toString()
	    .replaceAll("0,", "0.0,")
	    .replaceAll("0]", "0.0]");
	
	obj = queryIndex(d, query);	
	if (obj == null) {
	    return result;
	}
	JSONArray arr = (JSONArray)((JSONObject)obj.get("hits")).get("hits");
	for (int i = 0; i < arr.length(); i++) {
	    obj = (JSONObject)((JSONObject)arr.get(i)).get("_source");
	    res = obj.get("id").toString();
	    result.add(res);
	}
        
	return result;
    }

    public static JSONObject queryIndex(String dataset, String query) {
	CloseableHttpClient client = HttpClients.createDefault();
	JSONObject result = null;
	try {
	    try {
		HttpPost post = new HttpPost(ELASTIC_INDEX_URI +
					     dataset + "/_search");
		StringEntity requestEntity = new StringEntity(query,
							      ContentType.APPLICATION_JSON);
		post.setEntity(requestEntity);
		CloseableHttpResponse response = client.execute(post);
		try {
		    // Execute the method.
		    int statusCode = response.getStatusLine().getStatusCode();
		    HttpEntity entity = response.getEntity();
			
		    if (statusCode < 200 || statusCode >= 300) {
			System.err.println("Method failed: " + response.getStatusLine());
		    } else {
			// Read the response body.
			String responseBody = EntityUtils.toString(entity, "UTF-8");
			// Deal with the response.
			// Use caution: ensure correct character encoding and is not binary data
			result = new JSONObject(responseBody);
		    }
		    EntityUtils.consume(entity);
		} finally {
		    // Release the connection.
		    response.close();
		}
	    } finally {
		client.close();
	    }
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
	return result;
    }
}
