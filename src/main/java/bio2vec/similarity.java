package bio2vec;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.function.FunctionBase2;
import org.apache.jena.sparql.util.FmtUtils;
import java.util.Map;


public class similarity extends FunctionBase2 {

    Map<String, double[]> embeddings;
    
    public similarity(Map<String, double[]> embeddings) {
	super();
	this.embeddings = embeddings;
    }

    public double cosineSimilarity(double[] vectorA, double[] vectorB) {
	double dotProduct = 0.0;
	double normA = 0.0;
	double normB = 0.0;
	for (int i = 0; i < vectorA.length; i++) {
	    dotProduct += vectorA[i] * vectorB[i];
	    normA += Math.pow(vectorA[i], 2);
	    normB += Math.pow(vectorB[i], 2);
	}   
	return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
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
	if (!(this.embeddings.containsKey(v1) && this.embeddings.containsKey(v2))) {
	    throw new ExprEvalException("No embeddings!");
	}
	double[] vec1 = this.embeddings.get(v1);
	double[] vec2 = this.embeddings.get(v2);
	return NodeValue.makeNodeDouble(cosineSimilarity(vec1, vec2));
    }
}
