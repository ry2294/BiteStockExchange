
var FB = require('fb');


var user = '/me/friends';
exports.getFBFriends = function(req,res,callback)
{ 
	  console.log("Reached FB API");
	  var fb_token = req.body.fb_token;
	  console.log("FB TOKEN");
	  console.log(fb_token);
	  FB.setAccessToken(fb_token);

	  FB.api(user, function (response) {
		  if(!response || response.error) {
		   console.log(response ? 'Error occurred' : response.error);
		   return;
		  }
		  callback(response);
	});
}

exports.getUserName = function(req,res,callback)
{ 
	  console.log("Reached FB API for user data");
	  var fb_token = req.body.fb_token;
	  console.log("FB TOKEN");
	  console.log(fb_token);
	  FB.setAccessToken(fb_token);

	  FB.api('/me', function (response) {
		  if(!response || response.error) {
		   console.log(response ? 'Error occurred' : response.error);
		   return;
		  }
		  callback(response);
	});
}