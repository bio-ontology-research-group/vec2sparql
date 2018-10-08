#!/usr/bin/env python

EXP_CODES = set(['EXP', 'IDA', 'IPI', 'IMP', 'IGI', 'IEP', 'TAS', 'IC'])

def main():
    rdf_images()

def mapping():
    mp = {}
    with open('data/mappingFile.txt') as f:
        for line in f:
            it = line.strip().split()
            mp[it[1]] = it[0]

    w = open('data/graph_embeddings.txt', 'w')
    with open('data/rdf/out.txt') as f:
        next(f)
        for line in f:
            it = list(line.strip().split())
            it[0] = mp[it[0]]
            line = '\t'.join(it)
            w.write(line + '\n')
    w.close()

def rdf_images():
    w = open('data/graph_patients.ttl', 'w')
    w.write('@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n')
    w.write('@prefix rdfs:   <http://www.w3.org/2000/01/rdf-schema#> .\n')
    w.write('@prefix BVP: <http://bio2vec.net/patients/BVP_> .\n')
    w.write('@prefix IMG: <http://bio2vec.net/patients/IMG_> .\n')
    w.write('@prefix BV: <http://bio2vec.net/patients/> .\n')

    e = open('data/patients_embeddings.txt', 'w')
    
    with open('data/patients.csv') as f:
        next(f)
        for line in f:
            it = line.strip().split(',')
            img_id = 'IMG:' + it[0]
            p_id = 'BVP:' + it[3]
            w.write(img_id + ' BV:finding "' + it[1] + '" .\n')
            w.write(img_id + ' BV:follow_up ' + it[2] + ' .\n')
            w.write(img_id + ' BV:patient "' + p_id + '" .\n')
            w.write(img_id + ' BV:age "' + it[4] + '" .\n')
            w.write(img_id + ' BV:gender "' + it[5] + '" .\n')
            w.write(img_id + ' BV:view_position "' + it[6] + '" .\n')
            embeds = ','.join(it[7:])[1:-1].replace(', ', '\t')
            e.write(
                'http://bio2vec.net/patients/IMG_{}'.format(it[0]) +
                '\t' + embeds + '\n')
    w.close()
    e.close()

def rdf_mgi():
    w = open('data/rdf/graph.ttl', 'w')
    w.write('@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n')
    w.write('@prefix rdfs:   <http://www.w3.org/2000/01/rdf-schema#> .\n')
    w.write('@prefix MP:  <http://purl.obolibrary.org/obo/MP_> .\n')
    w.write('@prefix HP:  <http://purl.obolibrary.org/obo/HP_> .\n')
    w.write('@prefix GO:  <http://purl.obolibrary.org/obo/GO_> .\n')
    w.write('@prefix MGI: <http://www.informatics.jax.org/gene/MGI_> .\n')
    w.write('@prefix jax: <http://www.informatics.jax.org/> .\n')
    w.write('@prefix obo: <http://purl.obolibrary.org/> .\n')
    w.write('@prefix RO: <http://purl.obolibrary.org/obo/RO_> .\n')
    w.write('@prefix OMIM: <http://purl.bioontology.org/ontology/OMIM/> .\n')

    has_pheno = 'RO:0002200'
    has_func = 'RO:0000085'

    genes = set()
    functions = set()
    phenotypes = set()
    diseases = set()
    with open('data/rdf/MGI_GenePheno.rpt') as f:
        for line in f:
            it = line.strip().split('\t')
            mp_id = it[4]
            mgi_ids = it[6].split('|')
            for mgi_id in mgi_ids:
                if mgi_id:
                    w.write(mgi_id + ' ' + has_pheno + ' ' + mp_id + ' .\n')
                    genes.add(mgi_id)
                    phenotypes.add(mp_id)

    with gzip.open('data/rdf/gene_association.mgi.gz', 'rt') as f:
        for line in f:
            if line.startswith('!'):
                continue
            it = line.strip().split('\t')
            if it[3] == 'NOT':
                continue
            if it[6] not in EXP_CODES:
                continue
            mgi_id = it[1]
            go_id = it[4]
            w.write(mgi_id + ' ' + has_func + ' ' + go_id + ' .\n')
            genes.add(mgi_id)
            functions.add(go_id)

    dis_labels = {}
    with open('data/rdf/phenotype_annotation.tab') as f:
        for line in f:
            it = line.strip().split('\t')
            if it[0] != 'OMIM':
                continue
            if it[3] == 'NOT':
                continue
            hp_id = it[4]
            dis_id = it[0] + ':' + it[1]
            dis_labels[dis_id] = it[2]
            w.write(dis_id + ' ' + has_pheno + ' ' + hp_id + ' .\n')
            phenotypes.add(hp_id)
            diseases.add(dis_id)
    for gene in genes:
        w.write(gene + ' rdf:type jax:gene .\n')
    for pheno in phenotypes:
        w.write(pheno + ' rdf:type obo:phenotype .\n')
    for go in functions:
        w.write(go + ' rdf:type obo:function .\n')
    for dis in diseases:
        w.write(dis + ' rdf:type obo:disease .\n') 
        w.write(dis + ' rdfs:label "' + dis_labels[dis] + '" .\n')
            
    w.close()


if __name__ == '__main__':
    main()
