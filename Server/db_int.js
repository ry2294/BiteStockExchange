var pg = require('pg');
var fb = require('./fb_api.js')
var connectionString = 'postgres://postgres:postgres@localhost:5432/bse_db';
var client = new pg.Client(connectionString);
client.connect();

exports.addUser = function(req,res,callback)
{

	var user_id = req.body.user_id;
	//var user_name = req.body.user_name;
	var fb_token = req.body.fb_token


	console.log('Connected to database');
	console.log('User id ' + req.body.user_id);
	fb.getUserName(req, res, handleResult);
	function handleResult(response){	
		var user_name = response.name;
	    var query = client.query("insert into user_tbl(user_id,user_name,fb_token) values($1,$2,$3)", [user_id,user_name,fb_token]);
		query.on('end', function(result) {
			console.log("Query executed");

			fb.getFBFriends(req, res, handleResult);
			function handleResult(response){
			    console.log("Back to DB_INT from FB");
			    var friend_array = [];
			    console.log("RESPONSE");
			    console.log(response);	
			   
			    var friend_array = response.data;

			    console.log(friend_array);
			    
			    var queryToDeleteConnections= client.query("delete from user_connections_tbl where user_id = $1",[user_id]);
				queryToDeleteConnections.on('end', function(result)
				{				    
			    count = 0;
			    for(var j in friend_array){
			    	count++;
			    	friend_id = friend_array[j].id;

						var queryToInsertConnections= client.query("insert into user_connections_tbl(user_id,friend_id) values($1,$2)", [user_id,friend_id]);
						queryToInsertConnections.on('end', function(result)
						{	
							if(count == friend_array.length)
							{
							res.status(200);
							res.json({"User ID added with his connections" : user_id});
				        	callback(res);
				        }
						});
				   
			    }
			   });
			    

			}
		});
	}
}


exports.addOrder = function(req,res,callback)
{
	var order_id = Math.floor((Math.random() * 10000000) + 1);
	order_id = order_id + '';
	var user_id = req.body.user_id;
	console.log('Connected to database');
	console.log('User id' + req.body.user_id);
	console.log(req.body.order_summary);
	var array = JSON.parse(req.body.order_summary);
	var count = 0;

	for(var j in array){

		   count++;
		   var	item_id = array[j].item_id;
		   var price = array[j].price;
		   var query = client.query("insert into order_tbl(item_id,price,user_id,order_id) values($1,$2,$3,$4)", [item_id,price,user_id,order_id]);
		   query.on('end', function(result) {
				var queryToUpdateCount = client.query("update menu_item_tbl set order_count = i.order_count + 1 from (select order_count from menu_item_tbl where item_id = $1)i where item_id = $2",[item_id, item_id]);
				queryToUpdateCount.on('end', function(result)
				{					
					if(count == array.length)
					{
						var queryToUpdatePrice= client.query("update menu_item_tbl set high_price = (i.high_price * 1.2),low_price = (i.low_price * 1.2), order_count = 0 from (select high_price,low_price from menu_item_tbl where order_count > 6)i where order_count > 6");
						queryToUpdatePrice.on('end', function(result){
							res.status(200);
					        res.json({"order_id" : order_id});
					        callback(res);
						});	

					}
				});
			});

	}
}


exports.getMenuItems = function(req,res,callback)
{ 
	var menu_items = [];
	console.log('Connected to database');
	var query = client.query("Select * from menu_item_tbl");
	query.on('row', function(row) {
		console.log('Row received') ;

		var item =	{
					item_id : row.item_id, 
					item_name : row.item_name, 
					high_price : row.high_price, 
					low_price : row.low_price,
					item_category : row.item_category
					};

					menu_items.push(item);
	});
	query.on('end', function(row) {

		res.status(200).json(menu_items);
		callback(res);
	});
}

exports.getFriendActivity = function(req,res,callback)
{ 
	var user_id = req.params.user_id;;
	var fav_list = [];
	console.log('Connected to database');
	var query = client.query("select count(*) as count,u.friend_id, o.item_id as item_id, m.item_name as item_name, user_tbl.user_name as friend_name from user_connections_tbl as u, order_tbl as o, menu_item_tbl as m, user_tbl where o.user_id = u.friend_id and u.user_id = $1 and m.item_id = o.item_id and u.friend_id = user_tbl.user_id group by u.friend_id, o.item_id, m.item_name, user_tbl.user_name order by count desc;", [user_id]);
	query.on('row', function(row) {
		console.log('Row received') ;
		var item ={
					item_name : row.item_name, 
					friend_name : row.friend_name, 
					count : row.count 
				  };

				fav_list.push(item);
	});
	query.on('end', function(row) {
		res.json(fav_list);
		callback(res);
	});
}