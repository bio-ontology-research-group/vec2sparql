# Vec2SPARQL
SPARQL Endpoint with functions for computing embedding similarities

### Run

A `graph.ttl` and embeddings needs to be provided in `workspace/data` folder

```bash
docker-compose up
```

> Files goes to `workspace/`

⚠️ ElasticSearch might face permissions issues

```bash
sudo chmod 777 -R workspace/
```

### Load index

The [index](https://github.com/bio-ontology-research-group/vec2sparql/tree/master/index) needs to be run before to load embeddings in Elasticsearch. Do it before `docker-compose up` 

You will need the following files to run the index:

* `graph.ttl`
* `graph_embeddings.txt.gz`
* `graph_patients.ttl`
* `patients_embeddings.txt`

**Run**

```bash
docker run -it --network vec2sparql_net -v $PWD/workspace/data:/data umids/vec2sparql-load-embeddings -d patient_embeddings -f /data/patients_embeddings.txt
```

Datasets can be either:

* `graph_embeddings`
* `patient_embeddings`

**Build manually**

We are pulling the indexing container from DockerHub, but you can also build it manually

```bash
docker build -t umids/vec2sparql-load-embeddings index
```