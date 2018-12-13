package bio2vec.jena;

import org.apache.jena.sparql.pfunction.PropertyFunction;
import org.apache.jena.sparql.pfunction.PropertyFunctionFactory;
import java.util.Map;

public class SimFunctionFactory implements PropertyFunctionFactory {

    public SimFunctionFactory() {
    }

    public PropertyFunction create(String uri) {
	return new similarity();
    }
}
