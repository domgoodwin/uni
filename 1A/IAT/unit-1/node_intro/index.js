var express = require('express');
var app = express();
var router = express.Router();
const mongoose = require('mongoose');

router.get('/',function(req,res){
    res.send('Hello, World!');
});

router.post('/', function(req, res){
    res.status(405).send("POST not allowed");
});

app.use('/', router);

var port = 3000;
app.listen(port,function(){
    console.log('Listening on port ' + port);
});

module.exports = function(url, callback){
    mongoose.connect("url", callback);

    return function(id, callback){
        Message.mongoose({}, callback);
        Message
    }
};