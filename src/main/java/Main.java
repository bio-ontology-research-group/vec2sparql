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
import bio2vec.*;

public class Main {

    public void run(String[] args) {
	LogCtl.setJavaLogging();
	Dataset ds = RDFDataMgr.loadDataset("data/bio-knowledge-graph.n3");

	Map<String, double[]> embeddings = readEmbeddings();

	FunctionRegistry.get().put("http://bio2vec.net/function#similarity", new SimFunctionFactory(embeddings)) ;

	FusekiServer fs = FusekiServer.create()
	    .add("/ds", ds, true)
	    .build();
	
	fs.start();
    }

    private Map<String, double[]> readEmbeddings() {
	Map<String, double[]> embeddings = new HashMap<String, double[]>();
	String fileName = "data/bio-knowledge-graph.embeddings";
	try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {
	    String line;
	    while((line = br.readLine()) != null) {
		String[] items = line.split("\t");
		double[] vec = new double[items.length - 1];
		for (int i = 1; i < items.length; i++) {
		    vec[i - 1] = Double.parseDouble(items[i]);
		}
		embeddings.put(items[0], vec);
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return embeddings;
    }
    
    public static void main(String[] args) {
	new Main().run(args);
    }
}
