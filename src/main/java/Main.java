import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import org.apache.jena.fuseki.embedded.FusekiServer;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.function.FunctionRegistry;
import org.apache.jena.sparql.pfunction.PropertyFunctionRegistry;
import org.apache.jena.query.ARQ;
import bio2vec.jena.*;
import bio2vec.Functions;
import bio2vec.ESConnection;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.JCommander;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {

    Logger logger;

    @Parameter(names={"--port", "-p"}, required=false)
    int port = 3330;
    

    public Main() {
	logger = LoggerFactory.getLogger(Main.class);
	ESConnection.getInstance();
    }
    
    public void run() throws Exception{

	logger.info("Run function is excecuted");
	
	FusekiServer.Builder fsb = FusekiServer.create()
	    .setPort(this.port);

	final PropertyFunctionRegistry reg = PropertyFunctionRegistry
	    .chooseRegistry(ARQ.getContext());
	    
	reg.put(Functions.NAMESPACE + "function#getXY",
		new GetXYFunctionFactory());
	reg.put(Functions.NAMESPACE + "function#mostSimilar",
		new MostSimPropertyFunctionFactory());
	reg.put(Functions.NAMESPACE + "function#similarity",
		new SimFunctionFactory());
	PropertyFunctionRegistry.set(ARQ.getContext(), reg);
	Dataset ds = DatasetFactory.create();
	fsb.add("/ds", ds, true);
	
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
