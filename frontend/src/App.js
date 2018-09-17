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
	this.handleQueryChange = this.handleQueryChange.bind(this);
    }
    
    handleExample1Click(event) {
	var query = "PREFIX b2v: <http://bio2vec.net/function#> \nSELECT ?s ?o {?s ?p ?o FILTER (b2v:similarity(?s, ?o) = 0.5)} \nLIMIT 5";
	this.setState({ query: query });
    }

    handleExample2Click(event) {
	var query = "PREFIX b2v: <http://bio2vec.net/function#> \nSELECT ?o ?sim {?s ?p ?o .  ?sim b2v:mostSimilar(?o 5)} \nLIMIT 20";
	this.setState({ query: query });
    }

    handleQueryChange(event) {
	this.setState({query: event.target.value});
    }
    
    render() {
	return (
		<div className="App">
		<header className="App-header">
		<img src={logo} className="App-logo" alt="logo" />
		<h1 className="App-title">Vec2SPARQL</h1>
		</header>
		<main className="container">
		<p><a href="/#" onClick={this.handleExample1Click} >Example 1</a>
		&nbsp;&nbsp;<a href="/#" onClick={this.handleExample2Click} >Example 2</a>
	    </p>
		<div className="text-center">
		<form className="form form-sparql" action="/ds/query" method="get">
		<div className="form-group row">
		<label class="col-sm-3" for="query">SPARQL Query</label>
		<div class="col-sm-9">
		<textarea className="form-control" name="query" id="query" rows="5" value={this.state.query} onChange={this.handleQueryChange}/>
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
