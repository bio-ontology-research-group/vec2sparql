package bio2vec.jena;

import org.apache.jena.sparql.pfunction.PropertyFunction;
import org.apache.jena.sparql.pfunction.PropertyFunctionFactory;
import java.util.Map;

public class GetXFunctionFactory implements PropertyFunctionFactory {
    
    public GetXFunctionFactory() {
    }

    public PropertyFunction create(String uri) {
	return new getX();
    }
}
