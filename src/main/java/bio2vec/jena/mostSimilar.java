package bio2vec.jena;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.util.FmtUtils;

import org.apache.jena.atlas.iterator.Iter ;
import org.apache.jena.atlas.lib.StrUtils ;
import org.apache.jena.graph.Node ;
import org.apache.jena.graph.NodeFactory ;
import org.apache.jena.query.QueryBuildException;
import org.apache.jena.rdf.model.impl.Util ;
import org.apache.jena.sparql.core.Var ;
import org.apache.jena.sparql.engine.ExecutionContext ;
import org.apache.jena.sparql.engine.QueryIterator ;
import org.apache.jena.sparql.engine.binding.Binding ;
import org.apache.jena.sparql.engine.binding.BindingFactory ;
import org.apache.jena.sparql.engine.iterator.QueryIterPlainWrapper ;
import org.apache.jena.sparql.pfunction.PFuncSimpleAndList ;
import org.apache.jena.sparql.pfunction.PropFuncArg ;
import org.apache.jena.sparql.util.IterLib;
import java.io.*;
import java.util.*;
import org.json.*;

import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.util.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;

import bio2vec.Functions;

public class mostSimilar extends PFuncSimpleAndList {

    String dataset;
    
    public mostSimilar(String dataset) {
	super();
	this.dataset = dataset;
    }

    @Override
    public void build(PropFuncArg argSubject, Node predicate, PropFuncArg argObject, ExecutionContext execCxt) {
        super.build(argSubject, predicate, argObject, execCxt);

        if (argObject.getArgListSize() != 2)
            throw new QueryBuildException("Object list must contain exactly two arguments, the URI and number of most similar nodes") ;
    }

    @Override
    public QueryIterator execEvaluated(final Binding binding, final Node subject, final Node predicate, final PropFuncArg object, final ExecutionContext execCxt) {

        if (!object.getArg(0).isURI() || !object.getArg(1).isLiteral()) {
            return IterLib.noResults(execCxt);
        }
                
	String v = object.getArg(0).toString();
	int size = Integer.parseInt(object.getArg(1).getLiteralLexicalForm().toString());

	ArrayList<String> arr = Functions.mostSimilar(v, size);
	if (arr.size() == 0) {
	    return IterLib.noResults(execCxt);
	}
	ArrayList<Node> result = new ArrayList<Node>();
        for (int i = 0; i < arr.size(); i++) {
	    result.add(NodeFactory.createURI(arr.get(i)));
	}

	if (Var.isVar(subject)) {
            
            // Case: Subject is variable. Return all results.
            
            final Var subjectVar = Var.alloc(subject);

            Iterator<Binding> it = Iter.map(
                    result.iterator(),
                    item -> BindingFactory.binding(binding, subjectVar, item));
            return new QueryIterPlainWrapper(it, execCxt);
            
        } else if ( Util.isSimpleString(subject) ) {
            // Case: Subject is a plain literal.
            // Return input unchanged if it is one of the tokens, or nothing otherwise
                return IterLib.noResults(execCxt);
        }
        
        // Any other case: Return nothing
        return IterLib.noResults(execCxt);
    }

}
