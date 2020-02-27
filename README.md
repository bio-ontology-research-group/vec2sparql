# Vec2SPARQL
SPARQL Endpoint with functions for computing embedding similarities

### Run

A `graph.ttl` and embeddings needs to be provided in `workspace/data` folder

```bash
docker-compose up
```

> Files goes to `workspace/`

### Load index

The [index](https://github.com/bio-ontology-research-group/vec2sparql/tree/master/index) needs to be run before to load embeddings in Elasticsearch. Do it before `docker-compose up` 

```bash
python3 index.py -d patient_data -f ../workspace/data/patients_embeddings.txt
```

**Build**

```bash
docker build -t umids/vec2sparql-load-embeddings index
```

**Run**

```bash
docker run -it -v $PWD/workspace/data:/data umids/vec2sparql-load-embeddings -d patient_data -f /data/patients_embeddings.txt
```

