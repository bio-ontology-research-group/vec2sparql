package bio2vec;

import java.io.*;
import java.util.*;
import org.json.*;

import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.util.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;


public class Functions {

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
    
    public static double[] getXY(String d, String v) {
	double x = 0.0, y = 0.0;
	JSONObject obj = getObject(d, v);
	if (obj != null) {
	    x = Double.parseDouble(obj.get("x").toString());
	    y = Double.parseDouble(obj.get("y").toString());
	}
	return new double[] {roundTo3(x), roundTo3(y)};
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
		JSONArray vec1 = obj.getJSONArray("embedding");
		obj = (JSONObject)((JSONObject)arr.get(1)).get("_source");
		JSONArray vec2 = obj.getJSONArray("embedding");
		double dotProduct = 0.0;
		double normA = 0.0;
		double normB = 0.0;
		for (int i = 0; i < vec1.length(); i++) {
		    double a = vec1.getDouble(i);
		    double b = vec2.getDouble(i);
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

    public static ArrayList<String[]> mostSimilar(String d, String v, int size, String type) {
	ArrayList<String[]> result = new ArrayList<String[]>();
	JSONObject obj = getObject(d, v);
	if (obj == null) {
	    return result;
	}

	JSONObject queryPart = null;
	if (type != null) {
		queryPart = new JSONObject()
			.put("match", new JSONObject()
				.put("type", type));
	} else {
		queryPart = new JSONObject().put("match_all", new JSONObject());
	}

	JSONArray eArray = obj.getJSONArray("embedding");
	String query = new JSONObject()
	    .put("query", new JSONObject()
			.put("script_score", new JSONObject()
				.put("query", queryPart)	
			   	.put("script", new JSONObject()
					.put("source", "cosineSimilarity(params.vector, doc['embedding']) + 1")
					.put("params", new JSONObject()
						.put("vector", eArray)))))
	    .put("size", size)
	    .toString();
	obj = queryIndex(d, query);	
	if (obj == null) {
	    return result;
	}
	JSONArray arr = (JSONArray)((JSONObject)obj.get("hits")).get("hits");
	for (int i = 0; i < arr.length(); i++) {
	    obj = (JSONObject)arr.get(i);
	    String score = obj.get("_score").toString();
	    obj = (JSONObject)obj.get("_source");
	    String id = obj.get("id").toString();
	    String x = obj.get("x").toString();
	    String y = obj.get("y").toString();
	    result.add(new String[]{id, score, x, y});
	}
        
	return result;
    }

    public static JSONObject queryIndex(String dataset, String query) {
	CloseableHttpClient client = HttpClients.createDefault();
	JSONObject result = null;
	try {
		ESConnection conn = ESConnection.getInstance();
		Request request = new Request("POST", "/" + conn.getIndexPrefix() + dataset + "/_search");
		request.setJsonEntity(query);
		Response response = conn.getRestClient().performRequest(request);
		HttpEntity entity = response.getEntity();
		int statusCode = response.getStatusLine().getStatusCode();
			
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
		
		
		// HttpPost post = new HttpPost(ELASTIC_INDEX_URI +
		// 			     dataset + "/_search");
		// StringEntity requestEntity = new StringEntity(query,
		// 					      ContentType.APPLICATION_JSON);
		// post.setEntity(requestEntity);
		// CloseableHttpResponse response = client.execute(post);
		// try {
		//     // Execute the method.
		//     int statusCode = response.getStatusLine().getStatusCode();
		//     HttpEntity entity = response.getEntity();
			
		//     if (statusCode < 200 || statusCode >= 300) {
		// 	System.err.println("Method failed: " + response.getStatusLine());
		//     } else {
		// 	// Read the response body.
		// 	String responseBody = EntityUtils.toString(entity, "UTF-8");
		// 	// Deal with the response.
		// 	// Use caution: ensure correct character encoding and is not binary data
		// 	result = new JSONObject(responseBody);
		//     }
		//     EntityUtils.consume(entity);
		// } finally {
		//     // Release the connection.
		//     response.close();
		// }
	    // } finally {
		// client.close();
	    // }
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
	return result;
    }
}
