package bio2vec.jena;

import org.apache.jena.sparql.function.Function;
import org.apache.jena.sparql.function.FunctionFactory;
import java.util.Map;

public class GetXFunctionFactory implements FunctionFactory {
    
    public GetXFunctionFactory() {
    }

    public Function create(String uri) {
	return new getX();
    }
}
