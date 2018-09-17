import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import org.apache.jena.fuseki.embedded.FusekiServer;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Dataset;
import org.apache.jena.atlas.logging.LogCtl;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.function.FunctionRegistry;
import org.apache.jena.sparql.pfunction.PropertyFunctionRegistry;
import org.apache.jena.query.ARQ;
import bio2vec.*;

public class Main {

    public void run(String[] args) {
	LogCtl.setJavaLogging();
	Dataset ds = RDFDataMgr.loadDataset("data/graph.nt");

	
	FunctionRegistry.get().put("http://bio2vec.net/function#similarity",
				   new SimFunctionFactory());
	
	final PropertyFunctionRegistry reg = PropertyFunctionRegistry.chooseRegistry(ARQ.getContext());
	reg.put("http://bio2vec.net/function#mostSimilar", new MostSimPropertyFunctionFactory());
	PropertyFunctionRegistry.set(ARQ.getContext(), reg);

	FusekiServer fs = FusekiServer.create()
	    .add("/ds", ds, true)
	    .build();
	
	fs.start();
    }

    public static void main(String[] args) {
	new Main().run(args);
    }
}
