var templates = {};

templates.open = [
      "<div class = 'userRequest'>",
      "<h3><%=items%</h3>",
      "<p><%=userName %></p>",
      "<div class =acceptButton ",
    ].join("");

  templates.accept = [
    "<div class = 'acceptedRequest'>",
     "<h3><%=items%</h3>",
     "<p><%=userName %></p>",
   ].join("");

 templates.request = [
   "<div class = 'userRequest'>",
    "<p><%=status%></p>",
    "<p><%=drivername%></p>",
    "div class = deleteButton>",
  ].join("");
}
