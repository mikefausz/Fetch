var templates = {};

templates.user = [
  '<div class="userRequest">',
    '<h4> <%= request %></h4>',
    '<p>Status: <%= status %></p>',
    '<p>Driver: <%= driverId %></p>',
    '<div class="deleteButton">Completed</div>',
    '<div class="deleteButton">Cancel</div>',
  '</div>'
].join("");

  templates.accepted = [
    '<div class="acceptedRequest">',
      '<h4> <%= request %></h4>',
      '<p>  <%= userId %></p>',
    '</div>',
   ].join("");

 templates.request = [
   '<div class="openRequest">',
    '<h4> <%= request %></h4>',
     '<p> <%= userId %></p>',
     '<div class="acceptButton">Accept</div>',
   '</div>',
  ].join("");

