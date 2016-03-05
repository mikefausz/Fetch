
$(document).ready(function() {
  fetchApp.init();
});

templates = [];

templates.userRequest = [
  // this will be the HTML for the request listings
  // that the USER sees
  // will have a DRIVER name if accepted
].join("");

templates.acceptedRequest = [
  // very similar to above except
  // this will be the HTML for the request listings
  // that the DRIVER has accepted but not completed
  // will have USER name on it
].join("");

templates.openRequest = [
  // very similar to above except
  // this will be the HTML for the  open request listings
  // that the DRIVER sees
  // will have USER name on it
].join("");


var fetchApp = {
  urls: {
    // usersUrl: 'http://tiny-tiny.herokuapp.com/collections/users',
    // driversUrl: 'http://tiny-tiny.herokuapp.com/collections/drivers',
    // requestsUrl: 'http://tiny-tiny.herokuapp.com/collections/requests',
    // URL ROUTES JAMES CREATES WILL GO HERE
    user:          '/user',
    driver:        '/driver',
    driverRequest:  '/driver-requests',
    loginDriver:   '/login-Driver',
    loginUser:     '/login-User',
    userRequests:  '/user-requests',
    request:       '/request',
    update: '/update-request',
    delete: '/delete-request',
  },

  // MAYBE SOME EMPTY OBJECTS TO STORE 'GET' DATA LOCALLY
    // ONE FOR USER DATA
    // ONE FOR OPEN REQUESTS TO BE FILTERED

  init: function(){
    fetchApp.initStyling();
    fetchApp.initEvents();
  },

  initStyling: function(){
    // ANY PAGE-LOAD STYLING WILL GO HERE
    // (probably won't have any)
  },

  initEvents: function(){
    // ALL OUR CLICK EVENTS WILL LIVE HERE

    // ON LOGIN FORM SUBMISSION

  $('#letsGo').on('click', function () {
       var username = "";
       if ($('select[name=userType]').val() === 'user' &&
           $('input[type=checkbox]').is(":checked")) {
                   username = $('input[name="userName"]').val();
                   fetchApp.addNewUser(username);
                   // add only this user's open requests to DOM
       }
       else if ($('select[name=userType]').val() === 'user' &&
               !$('input[type=checkbox]').is(":checked")) {
                   $('#userPage').addClass('active');
                   $('#loginPage').removeClass('active');
                   username = $('input[name="userName"]').val();
                   fetchApp.loginUser(username);
                   fetchApp.getUserRequests();
                   // add only this user's open requests to DOM
       }
       else if ($('select[name=userType]').val() === 'driver' &&
                $('input[type=checkbox]').is(":checked")) {
                   username = $('input[name="userName"]').val();
                   fetchApp.addNewDriver(username);
                   // add only this user's open requests to DOM
       }
       else {
                   $('#driverPage').addClass('active');
                   $('#loginPage').removeClass('active');
                   username = $('input[name="userName"]').val();
                   fetchApp.loginDriver(username);
                   // add only this user's open requests to DOM
       }
     });
    //  $("logoutButton").on('click', function(){
    //     $('#userPage',"#").removeClass('active');
    //     $('#loginPage').addClass('active');
    //  })
    // ON NEW REQUEST FORM SUBMISSION (USER SIDE)
      // get value of request text input
      // add new request to database and DOM

    // ON DELETE/COMPLETE REQUEST BUTTON CLICK (USER SIDE)
      // delete request from database and DOM

    // ON ACCEPT REQUEST BUTTON CLICK (DRIVER SIDE)
      // change request status to accepted
  },

  loginDriver: function(driverId) {
    $.ajax({
      url: fetchApp.urls.loginDriver,
      method: 'POST',
      data: {name:driverId},
      success: function(driverId) {
        console.log("logged in driver" + driverId);
      },
      error: function(err) {
        console.log("ERROR", err);
      },
    });
  },

  addNewDriver: function(driverName) {
    $.ajax({
      url: fetchApp.urls.driver,
      method: 'POST',
      data: {driver:driverName},
      success: function(driverName) {
        console.log("added new driver" + driverName);
      },
      error: function(err) {
        console.log("ERROR", err);
      },
    });
  },

  getDriverRequests: function(driverId) {
      $.ajax({
        url:fetchApp.urls.driverRequest,
        method:'GET',
        data:{driver:driverId},
        success: function(requests) {
         console.log("driver got request"+requests);
       },
       error:function(err){
         console.log("ERROR",err);
       },
     });
  },

  addNewUser: function(userName) {
    $.ajax({
      url: fetchApp.urls.user,
      method: 'POST',
      data: {user:userName},
      success: function(user) {
        console.log("added username " + userName);
      },
      error: function(err) {
        console.log("ERROR", err);
      },
    });
  },

  loginUser: function(user) {
    $.ajax({
      url: fetchApp.urls.loginUser,
      method: 'POST',
      data: {name:user},
      success: function(response) {
        console.log("logged in" + user);
      },
      error: function (err) {
      console.log("error: ", err);
    },
    });
  },

  getUserRequests: function(requests) {
   $.ajax({
     url: fetchApp.urls.userRequests,
     method:"GET",
     success: function(requests){
       console.log("gotit"+requests)
     },
     error: function (err) {
       console.log("error: ", err);
     }
   });
  },

  addRequest: function(requestText) {
    $.ajax({
      url: fetchApp.urls.request,
      method: 'POST',
      data: {requestText:requestText},
      success: function(response) {
        console.log("gave new request to james");
      },
      error: function (err) {
        console.log("error: ", err);
      },
    });
    // now needs to add to DOM
  },

  acceptRequest: function(requestId) {
    $.ajax({
      url: fetchApp.urls.update,
      method:'POST',
      data: {status:"accpted",id:requestId},
      success: function(){
        console.log('request accepted by driver');
      },
      error: function (err) {
        console.log("error: ", err);
      },
    });
  },

  deleteRequest: function(requestId) {
    $.ajax({
      url: fetchApp.urls.delete,
      method: "POST",
      data:{requestId:requestId},
      success: function(){
        console.log('request deleted')
      },
      error: function (err) {
        console.log("error: ", err);
      },
    });
  },

  addRequestsToDom: function(requests) {
    // will add the given requests to the DOM
  },

  buildUserRequestHtml: function(request) {
    // will use userRequest template and underscore
  },

  buildAcceptedRequestHtml: function(request) {
    // will use acceptedRequest template and underscore
  },

  buildOpenRequestHtml: function(request) {
    // will use openRequest template and underscore
  },

};
