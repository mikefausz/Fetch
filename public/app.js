
$(document).ready(function() {
  fetchApp.init();
});

var fetchApp = {
  urls: {
    // URL ROUTES JAMES CREATES WILL GO HERE
    user:              '/user',
    loginDriver:       '/login-Driver',
    loginUser:         '/login-User',
    driver:            '/driver',
    userRequests:      '/user-requests',
    request:           '/request',
  },


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

                  $('.logoutButton').on('click', function () {
                  $('#loginPage').addClass('active');
                  $('#loginPage').siblings().removeClass('active');
    });

    // ON NEW REQUEST FORM SUBMISSION (USER SIDE)
      // get value of request text input
      // add new request to database and DOM

    // ON DELETE/COMPLETE REQUEST BUTTON CLICK (USER SIDE)
      // delete request from database and DOM

    // ON ACCEPT REQUEST BUTTON CLICK (DRIVER SIDE)
      // change request status to accepted
  },

  // ALL THE OTHER FUNCTIONS WE WRITE WILL GO HERE

  addNewDriver: function(driverName) {
      // ajax POST call to driversUrl
      // will add a new driver object to database
  },

  getDriverRequests: function(driverId) {
      // will filter requests matching the driverId
      // these will be requests the driver has committed to
      // fulfill, but has not yet completed
  },

  addNewUser: function(userName) {
    $.ajax({
      url: fetchApp.urls.user,
      method: 'POST',
      data: {user: userName},
      success: function(user) {
        console.log("added user " + userName);
      },
      error: function(err) {
        console.log("ERROR", err);
      },
    });
  },

  loginUser: function(userName) {
    $.ajax({
      url: fetchApp.urls.loginUser,
      method: 'POST',
      data: {name: userName},
      success: function(response) {
        console.log("logged in" + userName);
      },
    });
  },

  getUserRequests: function() {
   $.ajax({
     url: fetchApp.urls.userRequests,
     method:"GET",
     success: function(requests){
       console.log("gotit"+requests)
     },
   });
  },

  addRequest: function(requestText) {
    $.ajax({
      url: fetchApp.urls.request,
      method: 'POST',
      data: requestText,
      success: function(response) {
        console.log("gave new request to james");
      },
    });
    // now needs to add to DOM
  },


  acceptRequest: function(requestId, driverId) {
    // ajax PUT call to requestsUrl
    // will change the status of a request from open to committed
    // and add the driverId to the request
  },

  deleteRequest: function(requestId) {
    // ajax DELETE call to requestsUrl
    // will delete a request from the requests JSON object
    // when a user deletes or confirms delivery
  },

  addRequestsToDom: function(requests) {
    // will add the given requests to the DOM
  },

  buildRequestHtml: function(template,data) {
    var requestHtml = _.template(template);
    console.log(requestHtml(data));
  return requestHtml(data);
  },
  
addRequestToDom: function(request,template,target){
  $(target).html(fetchApp.buildRequestHtml(template, request));
},

};
