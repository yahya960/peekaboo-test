cordova.define("peekaboo.peekaboo", function(require, exports, module) {
var exec = require("cordova/exec");

var PLUGIN_NAME = "peekaboo";

exports.init = function(arg0, success, error) {
	exec(success, error, PLUGIN_NAME, "init", [arg0]);
};

window.initPeekaboo = function(arg0, success, error) {
	exec(success, error, PLUGIN_NAME, "init", [arg0]);
};

});
