#!/usr/bin/env python
import requests
import json

ELASTIC_INDEX_URL = 'http://es:9200/bio2vec/patient_embeddings/'

def main():
    with open('/data/patients_embeddings.txt') as f:
        ind = 1
        for line in f:
            it = line.strip().split('\t')
            id = it[0]
            vector = map(lambda x: str(x[0]) + '|' + '%.6f' % float(x[1]), enumerate(it[1:])) 
            vector = list(vector)
            vector = ' '.join(vector)
            doc = {'id': id, '@model_factor': vector}
            r = requests.post(ELASTIC_INDEX_URL + str(ind), json=doc)
            ind += 1
            print(r.json()['result'])


if __name__ == '__main__':
    main()
