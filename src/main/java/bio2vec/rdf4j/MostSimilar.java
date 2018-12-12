package bio2vec.rdf4j;

import org.eclipse.rdf4j.common.iteration.CloseableIteration;
import org.eclipse.rdf4j.common.iteration.CloseableIteratorIteration;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.algebra.evaluation.ValueExprEvaluationException;
import org.eclipse.rdf4j.spin.function.InverseMagicProperty;
import org.eclipse.rdf4j.spin.function.spif.*;
import java.util.*;

import bio2vec.Functions;

public class MostSimilar implements InverseMagicProperty {

    @Override
    public String getURI() {
	return Functions.NAMESPACE + "function#mostSimilar";
    }

    @Override
    public CloseableIteration<? extends List<? extends Value>, QueryEvaluationException> evaluate(final ValueFactory valueFactory, Value... args)
	throws QueryEvaluationException
    {
	if (args.length != 3) {
	    throw new ValueExprEvaluationException(String.format("%s requires 3 arguments, got %d", getURI(), args.length));
	}
	
	String d = ((Literal)args[0]).stringValue();
	String v = ((Literal)args[1]).stringValue();
	int size = 0;
	try {
	    size = ((Literal)args[2]).intValue();
	} catch (NumberFormatException e) {
	    throw new ValueExprEvaluationException(String.format("%s requires for second argument integer value", getURI()));
	}

	ArrayList<String> results = Functions.mostSimilar(d, v, size);

	return new CloseableIteratorIteration<List<? extends Value>, QueryEvaluationException>(SingleValueToListTransformer.transform(new Iterator<Value>() {
		
		int pos = 0;
		
		@Override
		public boolean hasNext() {
		    return (pos < results.size());
		}
		
		@Override
		public Value next() {
		    return valueFactory.createIRI(results.get(pos++));
		}
		
		@Override
		public void remove() {
		    throw new UnsupportedOperationException();
		}
	    }));
    }
}
