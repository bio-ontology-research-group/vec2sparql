package bio2vec;

import org.apache.jena.sparql.function.Function;
import org.apache.jena.sparql.function.FunctionFactory;
import java.util.Map;

public class SimFunctionFactory implements FunctionFactory {

    private Map<String, double[]> embeddings;
    
    public SimFunctionFactory(Map<String, double[]> embeddings) {
	this.embeddings = embeddings;
    }

    public Function create(String uri) {
	return new similarity(this.embeddings);
    }
}
