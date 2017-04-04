var ObjectID = require('mongodb').ObjectID;
var Long = require('mongodb').Long;
var Timestamp = require('mongodb').Timestamp;

var Result = function(_id) {
	this.item = {};
	if(_id) { this.item._id = new ObjectID(_id); }
	this.item.timestamp = Timestamp.fromNumber(Date.now());
}

Result.query = function(query) {
	const bson_query = {};
	if(!query) { return bson_query; }
	
	if(query._id) { bson_query._id = new ObjectID(query._id); }
	if(query.flag) { bson_query.flag = Long.fromString(query.flag); }
	if(query.action) { bson_query.action = query.action; }
	if(query.device) { bson_query._identifier = query.device; }

	if(query.from || query.to) {
		bson_query.timestamp = {};
		if(query.from) { bson_query.timestamp.$gte = Timestamp.fromString(query.from).toNumber(); }
		if(query.to) { bson_query.timestamp.$lte = Timestamp.fromString(query.to).toNumber(); }
	}

	if(query.interval_min || query.interval_max) {
		bson_query.interval = {};
		if(query.interval_min) { bson_query.interval.$gte = Long.fromString(query.interval_min); }
		if(query.interval_max) { bson_query.interval.$lte = Long.fromString(query.interval_max); }
	}

	if(query.weight_min || query.weight_max) {
		bson_query.weight = {};
		if(query.weight_min) { bson_query.weight.$gte = parseFloat(query.weight_min); }
		if(query.weight_max) { bson_query.weight.$lte = parseFloat(query.weight_max); }
	}

	return bson_query;
}

Result.prototype = {

	constructor: Result,

	bson: function() {
		return this.item;
	},

	populate: function(item) {
		if(!item) { return };
		if(item._id) { this.item._id = new ObjectID(item._id); }
		if(item.timestamp) { this.item.timestamp = Timestamp.fromString(item._timestamp).toNumber(); }
		if(item.device) { this.item._identifier = item.device; }
		if(item.action) { this.item.action = action; }
		if(item.interval) { this.item.interval = Long.fromString(item.interval); }
		if(item.flag) { this.item.flag = Long.fromString(item.flag); }
		if(item.weight) { this.item.weight = parseFloat(item.weight); }
		return this;
	},
}


module.exports = function(app, db) {
	app.get('/results', function(req, ret) {
		const query = Result.query(req.query);
		db.collection('results').find(query).toArray(function(err, items) {
			if(err) { ret.send({ 'error': 'An error has occurred' }); }
			else { ret.send(items); }
		});
	});

	app.get('/results/:_id', function(req, ret) {
		const query = Result.query(req.params);
		db.collection('results').findOne(query, function(err, item) {
			if(err) { ret.send({ 'error': 'An error has occurred' }); }
			else { ret.send(item); }
		});
	});

	/* Manipulators */
	app.delete('/results/:_id', function(req, ret) {
		// ret.send({ 'warning': 'not yet implemented' });
		const query = Result.query(req.params);
		db.collection('results').remove(query, function(err, item) {
			if (err) { ret.send({'error':'An error has occurred'}); }
			else { ret.send('result ' + req.params._id + ' deleted!'); } 
		});
	});

	app.put('/results/:_id', function(req, ret) {
		// ret.send({ 'warning': 'not yet implemented' });
		const result = (new Result()).populate(req.body);
		const query = Result.query(req.params);
		const item = result.bson();
		db.collection('results').update(query, item, function(err, res) {
			if (err) { ret.send({'error':'An error has occurred'}); }
			else { ret.send(item); }
		});
	});

	app.post('/results', function(req, ret) {
		// ret.send({ 'warning': 'not yet implemented' });
		const result = (new Result()).populate(req.body);
		const item = result.bson();
		db.collection('results').insert(item, function(err, res) {
			if(err) { ret.send({ 'error': 'An error has occurred' }); }
			else { ret.send(res.ops[0]); }
		});
	});
}