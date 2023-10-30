
<div>
<div id="hiv-program-location-info" style="background-color: lightcoral; padding: 10px; border-radius: 5px;">
      <ul>
          <% if (hivProgramLocation == null) { %>
          <li>No location found for this patient</li>
         <% } else { %>
            <% if (hivProgramLocation.name != sessionLocation.name) { %>
             <li>${ui.message("pihcode.alert") }</li>
          <% } %>
        <% }%>
      </ul>
</div>
</div>
