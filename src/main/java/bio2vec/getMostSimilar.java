package bio2vec;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.function.FunctionBase1;
import org.apache.jena.sparql.util.FmtUtils;
import java.io.*;
import java.util.*;
import org.json.*;

import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.util.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;

public class getMostSimilar extends FunctionBase1 {

    Map<String, double[]> embeddings;
    
    public getMostSimilar(Map<String, double[]> embeddings) {
	super();
	this.embeddings = embeddings;
    }

    public NodeValue exec(NodeValue nv)
    {
        Node n = nv.asNode();
        if (!n.isURI())
            throw new ExprEvalException("Not a URI: " + FmtUtils.stringForNode(n));
        String v = nv.asString();
	if (!this.embeddings.containsKey(v)) {
	    throw new ExprEvalException("No embedding for a " + v);
	}
	String res = v;
	double[] e = embeddings.get(v);
	JSONArray eArray = new JSONArray();
	for (double x: e) {
	    eArray.put(x);
	}
	String datasetName = "bio-knowledge-graph";
	String query = new JSONObject()
	    .put("query", new JSONObject()
		 .put("function_score", new JSONObject()
		      .put("query", new JSONObject()
			   .put("query_string", new JSONObject()
				.put("query", "dataset_name: " + datasetName)))
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
		      .put("_score", "desc"))
		 .put(new JSONObject()
		      .put("id", "desc")))
	    .put("size", 1)
	    .toString();
	
	CloseableHttpClient client = HttpClients.createDefault();
	try {
	    try { 
		HttpPost post = new HttpPost("http://10.254.145.46:9200/bio2vec/_search");
		StringEntity requestEntity = new StringEntity(query,
							      ContentType.APPLICATION_JSON);
		post.setEntity(requestEntity);
		CloseableHttpResponse response = client.execute(post);
		try {
		    // Execute the method.
		    int statusCode = response.getStatusLine().getStatusCode();
		    
		    if (statusCode < 200 || statusCode >= 300) {
			System.err.println("Method failed: " + response.getStatusLine());
		    }
	    
		    // Read the response body.
		    HttpEntity entity = response.getEntity();
		    String responseBody = EntityUtils.toString(entity, "UTF-8");
		    // Deal with the response.
		    // Use caution: ensure correct character encoding and is not binary data
		    JSONObject obj = new JSONObject(responseBody);
		    JSONArray arr = (JSONArray)((JSONObject)obj.get("hits")).get("hits");
		    obj = (JSONObject)((JSONObject)arr.get(0)).get("_source");
		    res = obj.get("id").toString();
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
        return NodeValue.makeString(res);
    }
}
