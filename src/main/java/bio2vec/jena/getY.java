package bio2vec.jena;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.ext.xerces.util.URI;
import java.util.Map;

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
import org.apache.jena.sparql.expr.ExprEvalException ;

import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.util.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;

import org.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bio2vec.Functions;


public class getY extends PFuncSimpleAndList {

    Logger logger;
    
    public getY() {
	super();
	logger = LoggerFactory.getLogger(similarity.class);
    }

    @Override
    public void build(PropFuncArg argSubject, Node predicate,
		      PropFuncArg argObject, ExecutionContext execCxt) {
        super.build(argSubject, predicate, argObject, execCxt);
	if (argObject.getArgListSize() != 2)
            throw new QueryBuildException(
		"Object list must contain exactly two arguments, " +
		"the dataset IRI and entity IRI");
	
    }

    @Override
    public QueryIterator execEvaluated(final Binding binding,
				       final Node subject,
				       final Node predicate,
				       final PropFuncArg object,
				       final ExecutionContext execCxt) {

	if (!object.getArg(0).isURI() || !object.getArg(1).isURI()) {
            throw new ExprEvalException("Invalid arguments format");
        }

	String d = null;
	try {
	    d = new URI(object.getArg(0).getURI()).getFragment();
	} catch (Exception e) {
            throw new ExprEvalException("Dataset format is wrong") ;
	}

	if (!Var.isVar(subject))
            throw new ExprEvalException("Subject is not a variable (" + subject + ")") ;

	String v = object.getArg(1).toString();
	double y = Functions.getY(d, v);        
        return IterLib.oneResult(
	    binding, Var.alloc(subject),
	    NodeValue.makeNodeDouble(y).asNode(), execCxt);
    }

}
