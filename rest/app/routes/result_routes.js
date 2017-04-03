var ObjectID = require('mongodb').ObjectID;
var Long = require('mongodb').Long;
var Timestamp = require('mongodb').Timestamp;


module.exports = function(app, db) {
	app.get('/results', function(req, ret) {
		const device = req.query.device;
		const from = req.query.from;
		const to = req.query.to;
		const flag = req.query.flag;
		const action = req.query.action;
		const query = {};
		

		if(from || to) {
			query.timestamp = {};
			if(from) { query.timestamp.$gt = Timestamp.fromString(from).toNumber(); }
			if(to) { query.timestamp.$lt = Timestamp.fromString(to).toNumber(); }
		}
		if(flag) { query.flag = Long.fromString(flag); }
		if(action) { query.action = action; }
		if(device) { query._identifier = device; }

		db.collection('results').find(query).toArray(function(err, items) {
			if(err) { ret.send({ 'error': 'An error has occurred' }); }
			else { ret.send(items); }
		});
	});

	app.get('/results/:id', function(req, ret) {
		const id = req.params.id;
		const query = { '_id': new ObjectID(id) };
		db.collection('results').findOne(query, function(err, item) {
			if(err) { ret.send({ 'error': 'An error has occurred' }); }
			else { ret.send(item); }
		});
	});

	/* Manipulators */
	app.delete('/results/:id', function(req, ret) {
		ret.send({ 'warning': 'not yet implemented' });
		/*
		const id = req.params.id;
		const query = { '_id': new ObjectID(id) };
		db.collection('results').remove(query, function(err, item) {
			if (err) { ret.send({'error':'An error has occurred'}); }
			else { ret.send('result ' + id + ' deleted!'); } 
		});
		*/
	});

	app.put('/results/:id', function(req, ret) {
		ret.send({ 'warning': 'not yet implemented' });
		/*
		const id = req.params.id;
		const query = { '_id': new ObjectID(id) };
		const item = {};
		db.collection('results').update(query, item, function(err, res) {
			if (err) { ret.send({'error':'An error has occurred'}); }
			else { ret.send(item); }
		});
		*/
	});

	app.post('/results', function(req, ret) {
		ret.send({ 'warning': 'not yet implemented' });
		/*
		const item = {};
		db.collection('results').insert(item, function(err, res) {
			if(err) { ret.send({ 'error': 'An error has occurred' }); }
			else { ret.send(res.ops[0]); }
		});
		*/
	});
}