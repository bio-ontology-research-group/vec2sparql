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
import org.apache.jena.sparql.engine.binding.BindingMap ;
import org.apache.jena.sparql.engine.binding.BindingFactory ;
import org.apache.jena.sparql.engine.iterator.QueryIterPlainWrapper ;
import org.apache.jena.sparql.pfunction.PFuncListAndList ;
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


public class getXY extends PFuncListAndList {

    Logger logger;
    
    public getXY() {
	super();
	logger = LoggerFactory.getLogger(getXY.class);
    }

    @Override
    public void build(PropFuncArg argSubject, Node predicate,
		      PropFuncArg argObject, ExecutionContext execCxt) {
        super.build(argSubject, predicate, argObject, execCxt);
	if (argSubject.getArgListSize() != 2)
            throw new QueryBuildException(
		"Subject list must contain exactly two arguments, " +
		"x and y variables");
        
	if (argObject.getArgListSize() != 2)
            throw new QueryBuildException(
		"Object list must contain exactly two arguments, " +
		"the dataset IRI and entity IRI");
        
    }

    @Override
    public QueryIterator execEvaluated(final Binding binding,
				       final PropFuncArg subject,
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

	Node xNode = subject.getArg(0);
	Node yNode = subject.getArg(1);

	if (!Var.isVar(xNode) || !Var.isVar(yNode))
            throw new ExprEvalException("Subject should contain two variables") ;

	String v = object.getArg(1).toString();
	double[] x = Functions.getXY(d, v);

	final Var xVar = Var.alloc(xNode);
	final Var yVar = Var.alloc(yNode);

	BindingMap b = BindingFactory.create(binding);
	b.add(xVar, NodeValue.makeNodeDouble(x[0]).asNode());
	b.add(yVar, NodeValue.makeNodeDouble(x[1]).asNode());
	
        return IterLib.result(b, execCxt);
    }

}
