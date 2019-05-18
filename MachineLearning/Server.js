const express = require('express');
const bodyParser = require('body-parser');
const fs = require('fs');
const app = express();
app.use(bodyParser.urlencoded({extended : false}));
const child_process=require('child_process');
const Home=process.cwd()

var server = app.listen(8081, function () {
	var host = server.address().address;
	var port = server.address().port;
   
	console.log("Example app listening at http://%s:%s", host, port);
});


app.post('/', (req, res) => {
	console.log(req.body.Body);

	//Change the file directory as required
	//"req.body.Body" should not be changed


	fs.writeFile(Home+'Predict\\Predfile.txt', req.body.Body, 'utf8', function(err) {
  		if(err) {
  			return console.log(err);
  		}
    	else{
    		Predict(res);
    		}
    	
    })
	
});


app.post('/train', (req, res) => {
	console.log(req.body.Body);

	//Change the file directory as required
	//"req.body.Body" should not be changed
	//"req.body.Name" should not be changed
	var filename = req.body.Name + '.txt';
	fs.writeFile(Home+'\\Samples\\'+filename, req.body.Body, 'utf8', function(err) {
  		if(err) {
  			return console.log(err);
  		}
    })
	res.send("Received " + req.body.Name);
});

function Predict(res){
	//console.log("HERE")
	var prediction=child_process.spawn('python',[Home+'\\predict.py','-f=Predfile.txt'])
	prediction.stdout.on('data',function(data){
		console.log(data.toString().split("\n")[1]);
		res.send(data.toString().split("\n")[1]);
	}); 
}