var templates = {};

templates.user = [
  '<div class="userRequest">',
    '<h4><%= request %></h4>',
    '<p><span class="bold">Driver: </span><%= driverName %></p>',
    '<div class="deleteButton" data-id= <%= id %>>Complete</div>',
    '<div class="deleteButton" data-id= <%= id %>>Cancel</div>',
  '</div>'
].join("");

templates.userOpen = [
  '<div class="userOpenRequest">',
    '<h4><%= request %></h4>',
    '<div class="deleteButton" data-id=<%= id %>>Cancel</div>',
  '</div>',
].join("");

  templates.accepted = [
    '<div class="acceptedRequest">',
      '<h4><%= request %></h4>',
      '<p><span class="bold">User: </span><%= userName %></p>',
    '</div>',
   ].join("");

 templates.open = [
   '<div class="openRequest">',
    '<h4> <%= request %></h4>',
     '<p><span class="bold">User: </span><%= userName %></p>',
     '<div class="acceptButton" data-id= <%= id %>>Accept</div>',
   '</div>',
  ].join("");
