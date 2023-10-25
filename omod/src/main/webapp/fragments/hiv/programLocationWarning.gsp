
<div>
<div id="hiv-program-location-info" style="background-color: lightcoral; padding: 10px; border-radius: 5px;">
      <ul>
          <li>Session Location: <b>${sessionLocation.name}</b></li>
          <% if (hivProgramLocation == null) { %>
          <li>No location found for this patient</li>
         <% } else { %>
            <% if (hivProgramLocation.name != sessionLocation.name) { %>
             <li>You're trying to modify a patient of location <b>${hivProgramLocation.name}</b> who is not in your location</li>
          <% } %>
        <% }%>
      </ul>
</div>
</div>
