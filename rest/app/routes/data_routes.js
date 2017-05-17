var ObjectID = require('mongodb').ObjectID;
var Long = require('mongodb').Long;
var Timestamp = require('mongodb').Timestamp;

var Data = function(_id) {
	this.item = {};
	if(_id) { this.item._id = new ObjectID(_id); }
	this.item.timestamp = Timestamp.fromNumber(Date.now());
}

Data.query = function(query) {
	const bson_query = {};
	if(!query) { return bson_query; }

	if(query._id) { bson_query._id = new ObjectID(query._id); }
	if(query.flag) { bson_query.flag = Long.fromString(query.flag); }
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

	if(query.signal_min || query.signal_max) {
		bson_query.signal = {};
		if(query.signal_min) { bson_query.signal.$gte = parseFloat(query.signal_min); }
		if(query.signal_max) { bson_query.signal.$lte = parseFloat(query.signal_max); }
	}

	if(query.activity_min || query.activity_max) {
		bson_query.activity = {};
		if(query.activity_min) { bson_query.activity.$gte = parseFloat(query.activity_min); }
		if(query.activity_max) { bson_query.activity.$lte = parseFloat(query.activity_max); }
	}

	if(query.handled_min || query.handled_max) {
		bson_query.handled = {};
		if(query.handled_min) { bson_query.handled.$gte = parseInt(query.handled_min); }
		if(query.handled_max) { bson_query.handled.$lte = parseInt(query.handled_max); }
	}

	if(query.unhandled_min || query.unhandled_max) {
		bson_query.unhandled = {};
		if(query.unhandled_min) { bson_query.unhandled.$gte = parseFloat(query.unhandled_min); }
		if(query.unhandled_max) { bson_query.unhandled.$lte = parseFloat(query.unhandled_max); }
	}

	return bson_query;
}

Data.prototype = {

	constructor: Data,

	bson: function() {
		return this.item;
	},

	populate: function(item) {
		if(!item) { return };
		if(item._id) { this.item._id = new ObjectID(item._id); }
		if(item.timestamp) { this.item.timestamp = Timestamp.fromString(item._timestamp).toNumber(); }
		if(item.device) { this.item._identifier = item.device; }
		if(item.interval) { this.item.interval = Long.fromString(item.interval); }
		if(item.signal) { this.item.signal = parseFloat(item.signal); }
		if(item.activity) { this.item.activity = parseFloat(item.activity); }
		if(item.unhandled) { this.item.unhandled = parseInt(item.unhandled); }
		if(item.handled) { this.item.handled = parseInt(item.handled); }
		if(item.flag) { this.item.flag = Long.fromString(item.flag); }
		return this;
	},
}

module.exports = function(app, db) {
	app.get('/data', function(req, ret) {
		const query = Data.query(req.query);
		db.collection('data').find(query).toArray(function(err, items) {
			if(err) { ret.send({ 'error': 'An error has occurred' }); }
			else { ret.send(items); }
		});
	});

	app.get('/data/:_id', function(req, ret) {
		const query = Data.query(req.params);
		db.collection('data').findOne(query, function(err, item) {
			if(err) { ret.send({ 'error': 'An error has occurred' }); }
			else { ret.send(item); }
		});
	});

	/* Manipulators */
	app.delete('/data/:_id', function(req, ret) {
		// ret.send({ 'warning': 'not yet implemented' });
		const query = Data.query(req.params);
		db.collection('data').remove(query, function(err, item) {
			if (err) { ret.send({'error':'An error has occurred'}); }
			else { ret.send('data ' + req.params._id + ' deleted!'); } 
		});
	});

	app.put('/data/:_id', function(req, ret) {
		// ret.send({ 'warning': 'not yet implemented' });
		const data = (new Data()).populate(req.body);
		const query = Data.query(req.params);
		const item = data.bson();
		db.collection('data').update(query, item, function(err, res) {
			if (err) { ret.send({'error':'An error has occurred'}); }
			else { ret.send(item); }
		});
	});

	app.post('/data', function(req, ret) {
		// ret.send({ 'warning': 'not yet implemented' });
		const data = (new Data()).populate(req.body);
		const item = data.bson();
		db.collection('data').insert(item, function(err, res) {
			if(err) { ret.send({ 'error': 'An error has occurred' }); }
			else { ret.send(res.ops[0]); }
		});
	});
}