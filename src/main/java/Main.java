import org.apache.jena.fuseki.embedded.FusekiServer;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Dataset;
import org.apache.jena.atlas.logging.LogCtl;
import org.apache.jena.riot.RDFDataMgr;

public class Main {

    public static void main(String[] args) {
	LogCtl.setJavaLogging();
	Dataset ds = RDFDataMgr.loadDataset("data/bio-knowledge-graph.n3");
	FusekiServer fs = FusekiServer.create()
	    .add("/ds", ds, true)
	    .build();
	
	fs.start();
    }
}
