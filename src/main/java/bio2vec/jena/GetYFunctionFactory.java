package bio2vec.jena;

import org.apache.jena.sparql.function.Function;
import org.apache.jena.sparql.function.FunctionFactory;
import java.util.Map;

public class GetYFunctionFactory implements FunctionFactory {

    public GetYFunctionFactory() {
    }

    public Function create(String uri) {
	return new getY();
    }
}
