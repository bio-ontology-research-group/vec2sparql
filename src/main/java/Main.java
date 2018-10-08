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
import com.beust.jcommander.Parameter;
import com.beust.jcommander.JCommander;


public class Main {

    @Parameter(names={"--port", "-p"}, required=false)
    int port = 3330;
    
    
    public void run() {
	LogCtl.setJavaLogging();

	String[] datasets = new String[]{
	    "graph_embeddings",
	    "patient_embeddings"
	};
	String[] files = new String[]{
	    "data/graph.ttl",
	    "data/graph_patients.ttl"
	};

	FusekiServer.Builder fsb = FusekiServer.create()
	    .setPort(this.port);

	final PropertyFunctionRegistry reg = PropertyFunctionRegistry
	    .chooseRegistry(ARQ.getContext());
	    
	for (int i = 0; i < datasets.length; i++) {
	    Dataset ds = RDFDataMgr.loadDataset(files[i]);
	    fsb.add("/" + datasets[i], ds, true);
	    FunctionRegistry.get()
		.put("http://bio2vec.net/" + datasets[i] + "/function#similarity",
		     new SimFunctionFactory(datasets[i]));
	    reg.put("http://bio2vec.net/" + datasets[i] + "/function#mostSimilar",
		    new MostSimPropertyFunctionFactory(datasets[i]));
	    PropertyFunctionRegistry.set(ARQ.getContext(), reg);
	}

	FusekiServer fs = fsb.build();
	fs.start();
    }

    public static void main(String[] args) {
	Main main = new Main();
	JCommander jcom = JCommander.newBuilder()
            .addObject(main)
            .build();
	try {
	    jcom.parse(args);
	    main.run();
	} catch (Exception e) {
	    System.err.println(e.getMessage());
	    jcom.usage();
	}
    }
}
