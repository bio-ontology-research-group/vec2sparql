package bio2vec;

import org.elasticsearch.client.indices.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.common.unit.TimeValue;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.client.CredentialsProvider;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class  ESConnection { 

    private static ESConnection conn = null; 
    private static String indexPrefix = null;
    private static Logger logger = LoggerFactory.getLogger(ESConnection.class);

    private RestHighLevelClient esClient = null;
    private RestClient restClient = null;
   
    private ESConnection() { 
        try {
            FileInputStream input = new FileInputStream( "./resources/config.properties");
            
            Properties prop = new Properties();
            prop.load(input);

            String username =  prop.get("es.username").toString();
            String password = prop.get("es.password").toString();
            this.indexPrefix = prop.get("es.index.prefix").toString();
            String[] urls = prop.get("es.url").toString().split(",");
            List<URL> esUrls = new ArrayList<URL>();
            HttpHost[] hosts = new HttpHost[urls.length];
            logger.info("Connectiong to elasticsearch:" + Arrays.toString(urls));
            int idx=0;
            for (String url:urls) {
                URL esUrl= new URL(url);
                hosts[idx] = new HttpHost(esUrl.getHost(), esUrl.getPort(), esUrl.getProtocol());
                idx++;
            }


            if (!username.isEmpty() &&  !password.isEmpty()) {
                final CredentialsProvider credentialsProvider =
                    new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(username, password));
            
                this.restClient = RestClient.builder(hosts)
                    .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(
                            HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder
                            .setDefaultCredentialsProvider(credentialsProvider);
                    }
                }).build();
            } else {
                URL esUrl= esUrls.get(0);
                this.restClient = RestClient.builder(new HttpHost(esUrl.getHost(), esUrl.getPort(), esUrl.getProtocol())).build();
            }
            
            logger.info("Connected to elasticsearch:" + this.restClient.toString());
            // this.esClient = new RestHighLevelClient(restClient);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 
  
    public static ESConnection getInstance() { 
        if (conn == null) {
            conn = new ESConnection(); 
        }
        return conn; 
    } 

    // public RestHighLevelClient getESClient() {
    //     return this.esClient;
    // }

    public RestClient getRestClient() {
        return this.restClient;
    }

    public String getIndexPrefix() {
        return this.indexPrefix;
    }
} 