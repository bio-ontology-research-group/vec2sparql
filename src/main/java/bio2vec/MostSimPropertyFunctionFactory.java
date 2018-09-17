package bio2vec;

import org.apache.jena.sparql.pfunction.PropertyFunction;
import org.apache.jena.sparql.pfunction.PropertyFunctionFactory;
import java.util.Map;

public class MostSimPropertyFunctionFactory implements PropertyFunctionFactory {

    
    public MostSimPropertyFunctionFactory() {
    }

    public PropertyFunction create(String uri) {
	return new mostSimilar();
    }
}
