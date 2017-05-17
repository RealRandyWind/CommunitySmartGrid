const resultRoutes = require('./result_routes');
const dataRoutes = require('./data_routes');

module.exports = function(app, db) {
	resultRoutes(app, db);
	dataRoutes(app, db);
};