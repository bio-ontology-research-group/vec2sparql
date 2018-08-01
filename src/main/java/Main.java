import org.apache.jena.fuseki.embedded.FusekiServer;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Dataset;


public class Main {

    public static void main(String[] args) {
	Dataset ds = DatasetFactory.createTxnMem();
	FusekiServer fs = FusekiServer.create()
	    .add("/ds", ds)
	    .build();
	
	fs.start();
    }
}
