package bio2vec.jena;

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

import bio2vec.Functions;

public class similarity extends FunctionBase2 {

    String dataset;
    
    public similarity(String dataset) {
	super();
	this.dataset = dataset;
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
	double sim = Functions.cosineSimilarity(v1, v2);
	return NodeValue.makeNodeDouble(sim);
    }
}
