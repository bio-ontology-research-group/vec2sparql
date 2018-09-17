package bio2vec;

import org.apache.jena.sparql.function.Function;
import org.apache.jena.sparql.function.FunctionFactory;
import java.util.Map;

public class SimFunctionFactory implements FunctionFactory {

    public SimFunctionFactory() {
    }

    public Function create(String uri) {
	return new similarity();
    }
}
