const resultRoutes = require('./result_routes');

module.exports = function(app, db) {
	resultRoutes(app, db);
};