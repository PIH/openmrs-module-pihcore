<% if (hivProgramLocation != null && hivProgramLocation.name != sessionLocation.name ) { %>
  <div>
  <div id="hiv-program-location-info" style="background-color: lightcoral; padding: 10px; border-radius: 5px;">
        <ul>
              <li>${ui.message("pihcore.alert") }</li>
              <li>${ui.message("pihcore.sessionLocaton")} :<b>${sessionLocation.name}</b></li>
              <li>${ui.message("pihcore.currentUserLocation")} :<b>${hivProgramLocation.name}</b></li>
        </ul>
  </div>
  </div>
<% }%>