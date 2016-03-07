$(document).ready(function() {
  fetchApp.init();
});

var fetchApp = {

  urls: {
    user:             '/user',
    driver:           '/driver',
    driverRequests:   '/driver-requests',
    openRequests:     '/open-requests',
    loginDriver:      '/login-Driver',
    loginUser:        '/login-User',
    userRequests:     '/user-requests',
    userOpenRequests: '/user-open-requests',
    request:          '/request',
    update:           '/update-request',
    delete:           '/delete-request',
  },

  loggedIn: false,
  userType: "",

  init: function(){
    fetchApp.initStyling();
    fetchApp.initEvents();
  },

  initStyling: function(){
    // ANY PAGE-LOAD STYLING WILL GO HERE
  },

  initEvents: function(){
    fetchApp.autoUpdateRequests();
    // ON LOGIN FORM SUBMISSION
    $('#loginForm').on('submit', function (event) {
      event.preventDefault();
       var username = "";
       if ($('select[name=userType]').val() === 'user' &&
           $('input[type=checkbox]').is(":checked")) {
                   username = $('input[name="userName"]').val();
                   fetchApp.addNewUser(username);
                   fetchApp.loginUser(username);
       }
       else if ($('select[name=userType]').val() === 'user' &&
               !$('input[type=checkbox]').is(":checked")) {
                   username = $('input[name="userName"]').val();
                   fetchApp.loginUser(username);
       }
       else if ($('select[name=userType]').val() === 'driver' &&
                $('input[type=checkbox]').is(":checked")) {
                   username = $('input[name="userName"]').val();
                   fetchApp.addNewDriver(username);
                   fetchApp.loginDriver(username);
       }
       else {
                   username = $('input[name="userName"]').val();
                   fetchApp.loginDriver(username);
       }
     });

    // ON LOGOUT BUTTON CLICK
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

    // ON DELETE REQUEST BUTTON CLICK (USER SIDE)
    $('#userPage').on('click', '.deleteButton', function(event) {
      fetchApp.cancelRequest($(this).data('id'));
    });

    // ON COMPLETE REQUEST BUTTON CLICK (USER SIDE)
    $('#userPage').on('click', '.completeButton', function(event) {
      fetchApp.completeRequest($(this).data('id'));
    });

    // ON ACCEPT REQUEST BUTTON CLICK (DRIVER SIDE)
    $('#driverPage').on('click', '.acceptButton', function(event) {
      fetchApp.acceptRequest($(this).data('id'));
    });
  },

  setWelcome: function(username) {
    if (fetchApp.userType === 'DRIVER') {
      $('#driverPage').find('span').html(username);
    }
    else {
      $('#userPage').find('span').html(username);
    }
  },

  loginDriver: function(driverName) {
    $.ajax({
      url: fetchApp.urls.loginDriver,
      method: 'POST',
      data: {name:driverName},
      success: function(driverId) {
        console.log("logged in driver" + driverId);
        $('#driverPage').addClass('active');
        $('#loginPage').removeClass('active');
        fetchApp.loggedIn = true;
        fetchApp.userType = 'DRIVER';
        fetchApp.setWelcome(driverName);
        fetchApp.updateRequests();
      },
      error: function(err) {
        console.log("ERROR", err);
        alert("Welcome to Fetch!\n\nPlease create a new Driver Account.");
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

  getDriverRequests: function() {
      $.ajax({
        url:fetchApp.urls.driverRequests,
        method:'GET',
        success: function(requests) {
         console.log("DRIVER ACCEPTED"+requests);
         fetchApp.addRequestsToDom(JSON.parse(requests), templates.accepted,'#acceptedRequests');
       },
       error:function(err){
         console.log("ERROR",err);
       },
     });
  },

  getOpenRequests: function() {
      $.ajax({
        url:fetchApp.urls.openRequests,
        method:'GET',
        success: function(requests) {
         console.log("driver OPEN"+requests);
         fetchApp.addRequestsToDom(JSON.parse(requests), templates.open,'#openRequests');
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
        $('#userPage').addClass('active');
        $('#loginPage').removeClass('active');
        fetchApp.loggedIn = true;
        fetchApp.userType = 'USER';
        fetchApp.setWelcome(user);
        fetchApp.updateRequests();
      },
      error: function (err) {
      console.log("error: ", err);
      alert("Welcome to Fetch!\n\nPlease create a new User Account.");
    },
    });
  },

  getUserRequests: function() {
   $.ajax({
     url: fetchApp.urls.userRequests,
     method:"GET",
     success: function(requests){
       console.log("USER ACCEPTED"+requests);
       fetchApp.addRequestsToDom(JSON.parse(requests), templates.user,'#userRequests');
     },
     error: function (err) {
       console.log("error: ", err);
     }
   });
  },

  getUserOpenRequests: function() {
   $.ajax({
    // url: fetchApp.urls.userOpenRequests,
    url: '/user-open-requests',
     method:"GET",
     success: function(requests){
       console.log("USER OPEN "+requests);
       fetchApp.addRequestsToDom(JSON.parse(requests), templates.userOpen,'#openUserRequests');
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
        fetchApp.updateRequests();
      },
      error: function (err) {
        console.log("error: ", err);
      },
    });
  },

  acceptRequest: function(requestId) {
    $.ajax({
      url: fetchApp.urls.update,
      method:'POST',
      data: {status:"ACCEPTED",id:requestId},
      success: function(){
        console.log('request accepted by driver');
        fetchApp.updateRequests();
      },
      error: function (err) {
        console.log("error: ", err);
      },
    });
  },

  completeRequest: function(requestId) {
    $.ajax({
      url: fetchApp.urls.update,
      method:'POST',
      data: {status:"COMPLETED",id:requestId},
      success: function(){
        console.log('request completed by user (delivered)');
        fetchApp.updateRequests();
      },
      error: function (err) {
        console.log("error: ", err);
      },
    });
  },

  cancelRequest: function(requestId) {
    $.ajax({
      url: fetchApp.urls.delete,
      method: "POST",
      data:{requestId:requestId},
      success: function(){
        console.log('request deleted');
        fetchApp.updateRequests();
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

  updateRequests: function() {
    if (fetchApp.userType === 'DRIVER') {
      fetchApp.getDriverRequests();
      fetchApp.getOpenRequests();
    }
    else {
      fetchApp.getUserRequests();
      fetchApp.getUserOpenRequests();
    }
  },

  autoUpdateRequests: function() {
    window.setInterval(function(){
      if (fetchApp.loggedIn) {
        fetchApp.updateRequests();
      }
    }, 2000);
  },
};
