$(document).ready(function() {
  fetchApp.init();
});

var fetchApp = {
  urls: {
    // URL PATHS JAMES CREATES WILL GO HERE

    // driversUrl: '/drivers',
    // usersUrl: '/users',
    // requestsUrl: '/requests',      ..or something along these lines
  },

  init: function(){
    fetchApp.initStyling();
    fetchApp.initEvents();
  },

  initStyling: function(){
    // ANY PAGE-LOAD STYLING WILL GO HERE
  },

  initEvents: function(){
    // ALL OUR CLICK EVENTS WILL LIVE HERE

    // ON LOGIN FORM SUBMISSION
      // remove visible class from landing page
      // IF driver
        // add visible class to driver page
        // fetchApp.getDriverId(username)
        // filter requests by driver_id and add to DOM
      // ELSE (user)
        // add visible class to user page
        // fetchApp.getUserId()
        // filter user requests by user_id and add to DOM
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

  changeRequestStatus: function(requestId, driverId) {
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
