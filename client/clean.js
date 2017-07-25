var del = require('node-delete');

del.sync([
  'node_modules/**',
  '../public/ui/**'
], {force: true});
