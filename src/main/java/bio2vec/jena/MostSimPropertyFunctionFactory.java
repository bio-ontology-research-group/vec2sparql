package bio2vec.jena;

import org.apache.jena.sparql.pfunction.PropertyFunction;
import org.apache.jena.sparql.pfunction.PropertyFunctionFactory;
import java.util.Map;

public class MostSimPropertyFunctionFactory implements PropertyFunctionFactory {

    String dataset;
    
    public MostSimPropertyFunctionFactory(String dataset) {
	this.dataset = dataset;
    }

    public PropertyFunction create(String uri) {
	return new mostSimilar(this.dataset);
    }
}
