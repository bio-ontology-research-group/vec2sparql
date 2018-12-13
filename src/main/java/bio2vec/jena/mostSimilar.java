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
import org.apache.jena.sparql.engine.binding.BindingMap ;
import org.apache.jena.sparql.engine.binding.BindingFactory ;
import org.apache.jena.sparql.engine.iterator.QueryIterPlainWrapper ;
import org.apache.jena.sparql.pfunction.PFuncListAndList ;
import org.apache.jena.sparql.pfunction.PropFuncArg ;
import org.apache.jena.sparql.util.IterLib;
import java.io.*;
import java.util.*;
import org.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jena.ext.xerces.util.URI;


import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.util.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;

import bio2vec.Functions;

public class mostSimilar extends PFuncListAndList {

    Logger logger;
    
    public mostSimilar() {
	super();
	logger = LoggerFactory.getLogger(mostSimilar.class);
    }

    @Override
    public void build(PropFuncArg argSubject, Node predicate,
		      PropFuncArg argObject, ExecutionContext execCxt) {
        super.build(argSubject, predicate, argObject, execCxt);
	if (argSubject.getArgListSize() != 4)
            throw new QueryBuildException(
		"Subject list must contain exactly four variables, " +
		"entity IRI, similarity, x and y");
	
	if (argObject.getArgListSize() != 3)
            throw new QueryBuildException(
		"Object list must contain exactly three arguments, " +
		"the dataset IRI, entity IRI and number of most similar nodes");
	        
    }

    @Override
    public QueryIterator execEvaluated(final Binding binding,
				       final PropFuncArg subject,
				       final Node predicate,
				       final PropFuncArg object,
				       final ExecutionContext execCxt) {
	if (!object.getArg(0).isURI() ||
	    !object.getArg(1).isURI() || !object.getArg(2).isLiteral()) {
            throw new ExprEvalException("Invalid arguments format");
        }
	
	String d = null;
	try {
	    d = new URI(object.getArg(0).getURI()).getFragment();
	} catch (Exception e) {
	    return IterLib.noResults(execCxt);
	}
	String v = object.getArg(1).toString();
	int size = Integer.parseInt(
	    object.getArg(2).getLiteralLexicalForm().toString());
	ArrayList<String[]> arr = Functions.mostSimilar(d, v, size);
	if (arr.size() == 0) {
	    return IterLib.noResults(execCxt);
	}
	ArrayList<Node[]> result = new ArrayList<Node[]>();
        for (int i = 0; i < arr.size(); i++) {
	    result.add(new Node[]{
		    NodeFactory.createURI(arr.get(i)[0]),
		    NodeValue.makeNodeDouble(Double.parseDouble(arr.get(i)[1])).asNode(),
		    NodeValue.makeNodeDouble(Double.parseDouble(arr.get(i)[2])).asNode(),
		    NodeValue.makeNodeDouble(Double.parseDouble(arr.get(i)[3])).asNode()
		});
	}

	Node node = subject.getArg(0);
	Node sim = subject.getArg(1);
	Node xNode = subject.getArg(2);
	Node yNode = subject.getArg(3);
	
	if (Var.isVar(node) && Var.isVar(sim)) {
            
            final Var nodeVar = Var.alloc(node);
	    final Var simVar = Var.alloc(sim);
	    final Var xVar = Var.alloc(xNode);
	    final Var yVar = Var.alloc(yNode);

            Iterator<Binding> it = Iter.map(
                    result.iterator(),
                    item -> {
			BindingMap b = BindingFactory.create(binding);
			b.add(nodeVar, item[0]);
			b.add(simVar, item[1]);
			b.add(xVar, item[2]);
			b.add(yVar, item[3]);
			return b;
		    });
            return new QueryIterPlainWrapper(it, execCxt);
            
        }

        // Any other case: Return nothing
        return IterLib.noResults(execCxt);
    }

}
