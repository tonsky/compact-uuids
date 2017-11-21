#!/usr/local/bin/node

var fs = require('fs'),
    vm = require('vm');

global.performance = { now: function () {
  var t = process.hrtime();
  return t[0] * 1000 + t[1] / 1000000;
} }

global.goog = {};

global.CLOSURE_IMPORT_SCRIPT = function(src) {
  require('./target/none/goog/' + src);
  return true;
};

function nodeGlobalRequire(file) {
  vm.runInThisContext.call(global, fs.readFileSync(file), file);
}

if (fs.existsSync("./target/none")) {
  nodeGlobalRequire('./target/none/goog/base.js');
  nodeGlobalRequire('./target/none/cljs_deps.js');
  goog.require('compact_uuids.core.test');
} else {
  nodeGlobalRequire('./target/compact_uuids.js');
}

compact_uuids.core.test.test_all();
