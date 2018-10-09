#!/usr/bin/env python
import requests
import json
import click as ck

INDEX_NAME = 'bio2vec'
ELASTIC_INDEX_URL = 'http://es:9200/{0}'.format(INDEX_NAME)

@ck.command()
@ck.option('--dataset', '-d', help='Dataset name')
@ck.option('--filename', '-f', help='File embeddings')
def main(dataset, filename):
    configure_index(dataset)
    with open(filename) as f:
        ind = 1
        for line in f:
            it = line.strip().split('\t')
            id = it[0]
            vector = map(lambda x: str(
                x[0]) + '|' + '%.6f' % float(x[1]), enumerate(it[1:])) 
            vector = list(vector)
            vector = ' '.join(vector)
            doc = {'id': id, '@model_factor': vector}
            r = requests.post(
                ELASTIC_INDEX_URL + '/' + dataset + '/' + str(ind), json=doc)
            ind += 1
            print(r.json()['result'])



def configure_index(dataset):
    r = requests.head(ELASTIC_INDEX_URL)
    if r.status_code == 404:
        create_index()
    mapping = {
        dataset : {
            "properties" : {
                "@model_factor": {
                    "type": "text",
                    "term_vector": "with_positions_offsets_payloads",
                    "analyzer" : "payload_analyzer"
                },
                "id": {"type": "keyword"}
            }
        }
    }
    r = requests.post(ELASTIC_INDEX_URL + '/' + dataset + '/_mapping', json=mapping)
    

def create_index():
    settings = {
        "settings" : {
            "analysis": {
                "analyzer": {
                    "payload_analyzer": {
                        "type": "custom",
                        "tokenizer":"whitespace",
                        "filter":"delimited_payload_filter"
                    }
                }
            }
        }
    }
    r = requests.put(ELASTIC_INDEX_URL, json=settings)
    
if __name__ == '__main__':
    main()
