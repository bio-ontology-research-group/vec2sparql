package bio2vec;

import org.apache.jena.sparql.pfunction.PropertyFunction;
import org.apache.jena.sparql.pfunction.PropertyFunctionFactory;
import java.util.Map;

public class MostSimPropertyFunctionFactory implements PropertyFunctionFactory {

    private Map<String, double[]> embeddings;
    
    public MostSimPropertyFunctionFactory(Map<String, double[]> embeddings) {
	this.embeddings = embeddings;
    }

    public PropertyFunction create(String uri) {
	return new mostSimilar(this.embeddings);
    }
}
