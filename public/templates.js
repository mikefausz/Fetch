var templates = {};

templates.user = [
  '<div class="userRequests">',
    '<h4> <%= request %></h4>',
    '<p>Status: <%= status %></p>',
    '<p>Driver: <%= driverId %></p>',
    '<div class="completeButton" data-id= <%= id %>>Complete</div>',
    '<div class="deleteButton" data-id= <%= id %>>Cancel</div>',
  '</div>'
].join("");

templates.userOpen = [
  '<div class="openUserRequests">',
    '<h4>Get me 2 beef Doritos Locos tacos</h4>',
    '<div class="deleteButton">Cancel</div>',
  '</div>',
].join("");

  templates.accepted = [
    '<div class="acceptedRequest">',
      '<h4> <%= request %></h4>',
      '<p>  <%= userId %></p>',
    '</div>',
   ].join("");

 templates.open = [
   '<div class="openRequest">',
    '<h4> <%= request %></h4>',
     '<p> <%= userId %></p>',
     '<div class="acceptButton">Accept</div>',
   '</div>',
  ].join("");
