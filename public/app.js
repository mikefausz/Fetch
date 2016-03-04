$(document).ready(function() {
  fetchApp.init();
});

templates = [];

templates.userRequest = [
  // this will be the HTML for the request listings
  // that the USER sees
  // will have a DRIVER name if accepted
].join("");

templates.driverRequest = [
  // very similar to above except
  // this will be the HTML for the request listings
  // that the DRIVER sees
  // will have USER name on it
].join("");


var fetchApp = {
  urls: {
    // URL PATHS JAMES CREATES WILL GO HERE

    // driversUrl: '/drivers',
    // usersUrl: '/users',
    // requestsUrl: '/requests',      ..or something along these lines
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
      $('#loginPage').removeClass('active');
      if ($('select[name=userType]').val() === 'user') {
        $('#userPage').addClass('active');
        // fetchApp.getUserId()
        // add only this user's open requests to DOM
      }
      else {
        $('#driverPage').addClass('active');
        // fetchApp.getDriverId(username)
        // add this driver's accepted requests
        // followed by all open requests to DOM
      }
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
  getDriverId: function(driverName) {
      // ajax GET call to driversUrl
      // will find a driver object matching driverName in JSON
      // will get driver_id from driver object
           // then will call getDriverRequests matching that driver_id
  },

  getDriverRequests: function(driverId) {
      // will filter requests matching the driverId
      // these will be requests the driver has committed to
      // fulfill, but has not yet completed
  },

  getUserId: function(userName) {
    // ajax GET call to usersUrl
    // will find a user object matching userName in JSON
    // will get user_id from driver object
  },

  getUserRequests: function(userId) {
      // will filter requests matching the userId
      // these will be requests the user has posted,
      // but have not yet had delivered
  },

  getRequests: function() {
    // ajax GET call to requestsUrl
    // will return ALL existing requests to be filtered by other functions
  },

  addRequest: function(request) {
    // ajax POST call to requestsUrl
    // will add new request object to JSON object
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

};
