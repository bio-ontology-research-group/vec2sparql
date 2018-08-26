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

	this.handleExampleClick = this.handleExampleClick.bind(this);
	this.handleQueryChange = this.handleQueryChange.bind(this);
    }
    
    handleExampleClick(event) {
	var query = "PREFIX b2v: <http://bio2vec.net/function#> \nSELECT ?s ?o {?s ?p ?o FILTER (b2v:similarity(?s, ?o) = 1)} \nLIMIT 5";
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
		<p><a href="/#" onClick={this.handleExampleClick} >Example 1</a></p>
		<div className="text-center">
		<form className="form form-sparql" action="/ds/query" method="get">
		<div className="form-group">
		<label for="query">Enter SPARQL Query</label>
		<textarea className="form-control" name="query" id="query" rows="5" value={this.state.query} onChange={this.handleQueryChange}/>
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
