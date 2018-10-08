package bio2vec;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.function.FunctionBase2;
import org.apache.jena.sparql.util.FmtUtils;
import java.util.Map;

import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.util.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;

import org.json.*;

public class similarity extends FunctionBase2 {

    String dataset;
    
    public similarity(String dataset) {
	super();
	this.dataset = dataset;
    }

    public double roundTo3(double a) {
	return (int)(a * 1000 + 0.5) / 1000.0;
    }
    
    public double cosineSimilarity(String v1, String v2) {
	double res = 0.0;
	String query = new JSONObject()
	    .put("query", new JSONObject()
		 .put("constant_score", new JSONObject()
		      .put("filter", new JSONObject()
			   .put("terms", new JSONObject()
				.put("id", new JSONArray().put(v1).put(v2))))))
				     
	    .toString();
	JSONObject obj = Utils.queryIndex(this.dataset, query);
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
	return res;
    }
    
    public NodeValue exec(NodeValue nv1, NodeValue nv2) {
	Node n1 = nv1.asNode();
        if (!n1.isURI())
            throw new ExprEvalException("Not a URI: " + FmtUtils.stringForNode(n1));
        Node n2 = nv2.asNode();
        if (!n2.isURI())
            throw new ExprEvalException("Not a URI: " + FmtUtils.stringForNode(n2));
	String v1 = nv1.asString();
	String v2 = nv2.asString();
	double sim = roundTo3(cosineSimilarity(v1, v2));
	return NodeValue.makeNodeDouble(sim);
    }
}
