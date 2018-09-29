import React, { Component } from 'react';
import logo from './logo_small.png';
import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';

class App extends Component {

    constructor(props) {
	super(props);
	
	this.state = {
	    query: '',
	};

	this.handleExample1Click = this.handleExample1Click.bind(this);
	this.handleExample2Click = this.handleExample2Click.bind(this);
	this.handleExample3Click = this.handleExample3Click.bind(this);
	this.handleQueryChange = this.handleQueryChange.bind(this);
	this.handleActionChange = this.handleActionChange.bind(this);
    }
    
    handleExample1Click(event) {
	var query = 'PREFIX b2v: <http://bio2vec.net/graph_embeddings/function#>\n' +
	    'PREFIX MGI: <http://www.informatics.jax.org/gene/MGI_>\n' +
	    'PREFIX obo: <http://purl.obolibrary.org/>\n' +
	    'PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n' +
	    'PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n' +
	    'SELECT ?sim ?dis (b2v:similarity(?sim, MGI:97490) as ?val) \n' +
	    '{\n' +
	    ' ?sim b2v:mostSimilar(MGI:97490 10000) .\n' +
	    ' ?sim a obo:disease .\n' +
	    ' ?sim rdfs:label ?dis\n' +
	    '}\n';
	this.setState({ query: query, action: '/graph_embeddings/query' });
    }

    handleExample2Click(event) {
	var query = 'PREFIX b2v: <http://bio2vec.net/graph_embeddings/function#>\n' +
	    'PREFIX MGI: <http://www.informatics.jax.org/gene/MGI_>\n' +
	    'PREFIX obo: <http://purl.obolibrary.org/>\n' +
	    'PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n' +
	    'PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n' +
	    'SELECT ?sim (b2v:similarity(?sim, MGI:97490) as ?val) \n' +
	    '{\n' +
	    ' ?sim b2v:mostSimilar(MGI:97490 100) .\n' +
	    ' {?sim a obo:phenotype } UNION {?sim a obo:function} .\n' +
	    '}\n';
	this.setState({ query: query, action: '/graph_embeddings/query'});
    }

    handleExample3Click(event) {
	var query = 'PREFIX b2v: <http://bio2vec.net/patient_embeddings/function#>\n' +
	    'PREFIX BVP: <http://bio2vec.net/patients/BVP_>\n' + 
	    'PREFIX IMG: <http://bio2vec.net/patients/IMG_>\n' +
	    'PREFIX BV: <http://bio2vec.net/patients/>\n' +
	    'SELECT ?sim (b2v:similarity(?sim, IMG:00009890_001.png) as ?val) ?p ?f\n' +
	    '{\n' +
	    ' ?sim b2v:mostSimilar(IMG:00009890_001.png 10) .\n' +
	    ' ?sim BV:patient ?p .\n' +
	    ' ?sim BV:finding ?f .\n' +
	    '}\n';
	this.setState({ query: query, action: '/patient_embeddings/query'});
    }

    handleQueryChange(event) {
	this.setState({query: event.target.value});
    }

    handleActionChange(event) {
	this.setState({action: event.target.value});
    }
    
    render() {
	return (
		<div className="App">
		<header className="App-header">
		<img src={logo} className="App-logo" alt="logo" />
		<h1 className="App-title">Vec2SPARQL</h1>
		</header>
		<main className="container">
		<div className="form-sparql">
		<ul className="list-unstyled">
		<li>Example 1. Select disease associations of Pax6 (MGI:97490) gene.
		<a href="/#" onClick={this.handleExample1Click}> show</a></li>
		<li>Example 2. Select function and phenotype  associations of Pax6 (MGI:97490) gene.
		<a href="/#" onClick={this.handleExample2Click}> show</a></li>
		<li>Example 3. Select first 10 patients with a similar image.
		<a href="/#" onClick={this.handleExample3Click}> show</a></li>
		</ul>
		
		<form className="form" action={this.state.action} method="get">
		<div className="form-group row">
		<label class="col-sm-3" for="query">SPARQL Query</label>
		<div class="col-sm-9">
		<textarea className="form-control" name="query" id="query" rows="11" value={this.state.query} onChange={this.handleQueryChange}/>
		</div>
		</div>
		<div className="form-group row">
		<label for="format" class="col-sm-3">Output format</label>
		<div class="col-sm-9">
		<select id="format" name="format" class="form-control">
		<option selected value="">-----</option>
		<option>json</option>
		<option>text</option>
		<option>xml</option>
		<option>csv</option>
		</select>
		</div>
		</div>
		<div className="form-group row">
		<label for="dataset" class="col-sm-3">Output format</label>
		<div className="col-sm-9">
		<select id="dataset" class="form-control" value={this.state.action} onChange={this.handleActionChange}>
		<option selected value="/graph_embeddings/query">MGI</option>
		<option value="/patient_embeddings/query">Patients</option>
		</select>
		</div>
		</div>
		<button className="btn btn-primary" type="submit">Submit</button>
		</form>
		</div>
		</main>
		<footer className="footer">
		</footer>
		</div>
	);
    }
}

export default App;
