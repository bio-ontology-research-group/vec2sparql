package bio2vec;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.function.FunctionBase2;
import org.apache.jena.sparql.util.FmtUtils;
import java.io.*;
import java.util.*;
import org.json.*;

import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.util.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;

public class getMostSimilar extends FunctionBase2 {

    Map<String, double[]> embeddings;
    
    public getMostSimilar(Map<String, double[]> embeddings) {
	super();
	this.embeddings = embeddings;
    }

    public NodeValue exec(NodeValue nv1, NodeValue nv2) {
        Node n = nv1.asNode();
        if (!n.isURI())
            throw new ExprEvalException("Not a URI: " + FmtUtils.stringForNode(n));
	if (!nv2.isInteger())
            throw new ExprEvalException("Not an integer: " + nv2.toString());
	String v = nv1.asString();
	int size = nv2.getInteger().intValue();
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
	    .put("size", size)
	    .toString();
	JSONArray result = new JSONArray();
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
		    for (int i = 0; i < arr.length(); i++) {
			obj = (JSONObject)((JSONObject)arr.get(i)).get("_source");
			res = obj.get("id").toString();
			result.put(res);
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
        return NodeValue.makeString(result.toString());
    }
}
