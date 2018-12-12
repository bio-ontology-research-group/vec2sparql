package bio2vec.rdf4j;

import org.eclipse.rdf4j.query.algebra.evaluation.function.Function;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.query.algebra.evaluation.ValueExprEvaluationException;
import org.eclipse.rdf4j.query.algebra.evaluation.function.Function;

import bio2vec.Functions;

public class SimilarityFunction implements Function {

    public String getURI() {
	return Functions.NAMESPACE + "function#similarity";
    }

    public Value evaluate(ValueFactory valueFactory, Value... args)
	throws ValueExprEvaluationException {
	if (args.length != 3) {
	    throw new ValueExprEvaluationException("similarity function requires"
						   + "exactly 3 arguments, got "
						   + args.length);
	}
	double sim = Functions.cosineSimilarity(
	    args[0].stringValue(), args[1].stringValue(), args[2].stringValue());
	return valueFactory.createLiteral(sim);
    }
}
