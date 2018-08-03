package bio2vec;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.function.FunctionBase1;
import org.apache.jena.sparql.util.FmtUtils;

public class getMostSimilar extends FunctionBase1
{
    public getMostSimilar() { super(); }

    public NodeValue exec(NodeValue v)
    {
        Node n = v.asNode();
        if ( ! n.isURI() )
            throw new ExprEvalException("Not a URI: "+FmtUtils.stringForNode(n));
        String str = n.getNameSpace();
        return NodeValue.makeString(str);
    }
}
