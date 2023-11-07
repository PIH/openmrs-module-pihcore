<% if (hivProgramLocation != null && hivProgramLocation.name != sessionLocation.name ) { %>
  <div>
  <div id="hiv-program-location-info" style="background-color: lightcoral; padding: 10px; border-radius: 5px;">
        <ul>
              <li>${ui.message("pihcode.alert") }</li>
        </ul>
  </div>
  </div>
<% }%>