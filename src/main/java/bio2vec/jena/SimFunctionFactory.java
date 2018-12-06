package bio2vec.jena;

import org.apache.jena.sparql.function.Function;
import org.apache.jena.sparql.function.FunctionFactory;
import java.util.Map;

public class SimFunctionFactory implements FunctionFactory {

    String dataset;
    
    public SimFunctionFactory(String dataset) {
	this.dataset = dataset;
    }

    public Function create(String uri) {
	return new similarity(this.dataset);
    }
}
