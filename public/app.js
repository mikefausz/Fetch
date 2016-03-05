
$(document).ready(function() {
  fetchApp.init();
});

var fetchApp = {
  urls: {
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
     $(".logoutButton").on('click', function(){
        $(this).closest('section').removeClass('active');
        $('#loginPage').addClass('active');
     });
     
    // ON NEW REQUEST FORM SUBMISSION (USER SIDE)
    $('#userPage').on('click', '#submitRequest', function(event) {
      var requestText = $('#newRequestText').val();
      $('#newRequestText').val("");
      fetchApp.addRequest(requestText);
    });

    // ON DELETE/COMPLETE REQUEST BUTTON CLICK (USER SIDE)
    $('#userPage').on('click', '.deleteButton', function(event) {
      fetchApp.deleteRequest($(this).data('id'));
    });

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
      data: {user: userName},
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


  getUserRequests: function() {
   $.ajax({
     url: fetchApp.urls.userRequests,
     method:"GET",
     success: function(requests){
       console.log("gotit"+requests);
       fetchApp.addRequestsToDom(JSON.parse(requests), templates.user,'#userRequests');

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
        fetchApp.getUserRequests();
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
        console.log('request deleted');
        fetchApp.getUserRequests();
      },
      error: function (err) {
        console.log("error: ", err);
      },
    });
  },

  addRequestsToDom: function(requests, template, target) {
    var htmlStr = "";
    requests.forEach(function(request){
      htmlStr += fetchApp.buildRequestHtml(template, request);
    });
    $(target).html(htmlStr);
  },

  addRequestToDom: function(request,template,target){
    $(target).prepend(fetchApp.buildRequestHtml(template, request));
  },

  buildRequestHtml: function(template,data) {
    var requestHtml = _.template(template);
    return requestHtml(data);
  },
};
