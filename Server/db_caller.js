/*
*
*
*/

// call the packages we need
var express    = require('express');        // call express
var app        = express();                 // define our app using express
var bodyParser = require('body-parser');

var db = require('./db_int.js');


// configure app to use bodyParser()
// this will let us get the data from a POST
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());
var port = process.env.PORT || 16386;        // set our port


// ROUTES FOR OUR API
// =============================================================================
var router = express.Router();              // get an instance of the express Router


// middle-ware to use for all requests
router.use(function(req, res, next) {
    // do logging
    console.log('We will be routiing you now');
    next(); // make sure we go to the next routes and don't stop here
});


// test route to make sure everything is working (accessed at GET http://localhost:16386/api)
router.get('/', function(req, res) {
   // Logic to show student here
   res.json({ message: 'Welcome to our restaurant backend!!' });
});

// more routes for our API will happen here


/************************************************************
*
* Menu API Endpoints
*
*************************************************************/
//API end point to get student details (accessed at GET http://localhost:16386/api/student/id)
router.route('/friend/activity/:user_id')

    // get the student with that id (accessed at GET http://localhost:16386/api/student/:student_id)
    .get(function(req, res) {

      // Logic to show student here
      db.getFriendActivity(req,res,handleResult);
      function handleResult(response, err){
          if(err)
          {
            console.error(err.stack || err.message);
            return;
          }
          res.json(response.body);
          console.log("Friend activity fetched");
      }
    });

router.route('/friend/invite/:user_id')

    // get the student with that id (accessed at GET http://localhost:16386/api/student/:student_id)
    .get(function(req, res) {

      // Logic to show student here
      db.getFriendNearby(req,res,handleResult);
      function handleResult(response, err){
          if(err)
          {
            console.error(err.stack || err.message);
            return;
          }
          res.json(response.body);
          console.log("Friends nearby fetched");
      }
  });


router.route('/friend/invite')

  .post(function(req, res) {
  db.notifyFriend(req, res, handleResult);
  function handleResult(response){
    console.log('Callback received');
    console.log("Status code " +response.statusCode);
    if(response.statusCode == 200){
      console.log('200');
      res.status(200);
      res.json({ message: 'Friend notified', returnStatus : '200'});
    }
  }
});

router.route('/user/register')

    .post(function(req, res) {

      db.addUser(req, res, handleResult);
      function handleResult(response){
        console.log('Callback received');
        console.log("Status code " + response.statusCode);
        if(response.statusCode == 200){
        console.log('200');
        res.status(200);
        res.json({ message: 'User added!', returnStatus : '200'});
        }
      }
    });

router.route('/user/enter')

    .post(function(req, res) {

      db.userEnter(req, res, handleResult);
      function handleResult(response){
        console.log('Callback received');
        console.log("Status code " + response.statusCode);
        if(response.statusCode == 200){
        console.log('200');
        res.status(200);
        res.json({ message: 'User entered in Geofence!', returnStatus : '200'});
        }
      }
    });

router.route('/user/exit')

    .post(function(req, res) {

      db.userExit(req, res, handleResult);
      function handleResult(response){
        console.log('Callback received');
        console.log("Status code " + response.statusCode);
        if(response.statusCode == 200){
        console.log('200');
        res.status(200);
        res.json({ message: 'User exited Geo Fence!', returnStatus : '200'});
        }
      }
    });

//API end point to get student details (accessed at GET http://localhost:16386/api/student/id)
router.route('/menu')

    // get the student with that id (accessed at GET http://localhost:16386/api/student/:student_id)
    .get(function(req, res) {
      // Logic to show student here
      db.getMenuItems(req,res,handleResult);
      function handleResult(response, err){
          if(err)
          {
            console.error(err.stack || err.message);
            return;
          }
          res.json(response.body);
          console.log("Request handled; Menu Returned");
      }
    });
    
router.route('/menu/order')

  .post(function(req, res) {
  db.addOrder(req, res, handleResult);
  function handleResult(response){
    console.log('Callback received');
    console.log("Status code " +response.statusCode);
    if(response.statusCode == 200){
      console.log('200');
      res.status(200);
      res.json({ message: 'Order Placed!', returnStatus : '200'});
    }
  }
});


// REGISTER OUR ROUTES -------------------------------
// all of our routes will be prefixed with /api
app.use('/api', router);

// START THE SERVER
// =============================================================================
app.listen(port);
console.log('Magic happens on port ' + port);