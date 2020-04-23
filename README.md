# Vec2SPARQL
SPARQL Endpoint with functions for computing embedding similarities

### Run

A `graph.ttl` and embeddings needs to be provided in `workspace/data` folder

```bash
docker-compose up --build
```

> Files goes to `workspace/`

⚠️ ElasticSearch might face permissions issues

```bash
sudo chmod 777 -R workspace/
```

ElasticSearch will crash without warning if you don't have enough RAM available on your machine, restart your computer to free some memory if this happen.

See also: [documentation to deploy ElasticSearch in production](https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html#docker-prod-prerequisites)

### Load index

The [index](https://github.com/bio-ontology-research-group/vec2sparql/tree/master/index) needs to be run before to load embeddings in Elasticsearch. Do it before `docker-compose up` 

You will need the following files to run the index:

* `graph.ttl`
* `graph_embeddings.txt.gz`
* `graph_patients.ttl`
* `patients_embeddings.txt`

**To load index run:**

```bash
# Load graph embeddings (MGI)
docker run -it --network vec2sparql_net -v $PWD/workspace/data:/data umids/vec2sparql-load-embeddings -d graph_embeddings -f /data/graph_embeddings.txt

# Load patient embeddings
docker run -it --network vec2sparql_net -v $PWD/workspace/data:/data umids/vec2sparql-load-embeddings -d patient_embeddings -f /data/patients_embeddings.txt

# Load protein embeddings
docker run -it --network vec2sparql_net -v $PWD/workspace/data:/data umids/vec2sparql-load-embeddings -d protein_embeddings -f /data/protein_embeddings.txt
```

Parameters:

* `-d` : the dataset (used to separate indexes in ElasticSearch)
  * `graph_embeddings`
  * `patient_embeddings`
  * `protein_embeddings`
* `-f` : the embeddings file (put it in the shared volume)

> Go to http://localhost/ to query the embeddings generated

Submitting a SPARQL query using the web UI does not work properly, once you submitted the query you need to hard reload the page to the generated URL

* For example, hard reload at `http://localhost/patient_embeddings/query?query=select+*+{%0D%0A%3Fs+%3Fp+%3Fo+.+%0D%0A}+limit+100&format=csv` to get results of the query

* Or directly from the backend API (to avoid using the API proxied to the frontend, which increase the frictions and chances of networking issues)
  * `http://localhost:3330/patient_embeddings/query?query=select+*+{%0D%0A%3Fs+%3Fp+%3Fo+.+%0D%0A}+limit+100&format=csv`

More complex query:

```
http://localhost:3330/protein_embeddings/query?query=PREFIX+b2v%3A+<http%3A%2F%2Fbio2vec.net%2Fprotein_embeddings%2Ffunction%23>%0D%0APREFIX+MGI%3A+<http%3A%2F%2Fwww.informatics.jax.org%2Fgene%2FMGI_>%0D%0APREFIX+obo%3A+<http%3A%2F%2Fpurl.obolibrary.org%2F>%0D%0APREFIX+ncbigene%3A+<http%3A%2F%2Fbio2rdf.org%2Fncbigene%2F>%0D%0APREFIX+rdf%3A+<http%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23>%0D%0APREFIX+rdfs%3A+<http%3A%2F%2Fwww.w3.org%2F2000%2F01%2Frdf-schema%23>%0D%0ASELECT+%3Fsim+%3Flabel+(b2v%3Asimilarity(%3Fsim%2C+ncbigene%3A79589)+as+%3Fval)+%0D%0A{%0D%0A+%3Fsim+b2v%3AmostSimilar(ncbigene%3A79589+10)+.%0D%0A+%3Fsim+rdfs%3Alabel+%3Flabel+.%0D%0A}%0D%0A&format=csv
```

```
PREFIX b2v: <http://bio2vec.net/protein_embeddings/function#>
PREFIX MGI: <http://www.informatics.jax.org/gene/MGI_>
PREFIX obo: <http://purl.obolibrary.org/>
PREFIX ncbigene: <http://bio2rdf.org/ncbigene:>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
SELECT ?sim 
{
 ?sim b2v:mostSimilar(ncbigene:79589 10000) .
}
```

**Build manually**

We are pulling the indexing container from DockerHub, but you can also build it manually

```bash
docker build -t umids/vec2sparql-load-embeddings index
```