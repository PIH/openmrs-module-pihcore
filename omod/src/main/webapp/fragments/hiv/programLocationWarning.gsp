<div>
<div id="hiv-program-location-info" style="background-color: lightcoral; padding: 10px; border-radius: 5px;">
      <ul>
          <li>Session Location: <b>${sessionLocation.name}</b></li>
          <li>
              <% if (hivProgramLocation.name !=null ) { %> HIV Program Location: <b>${hivProgramLocation.name} <% } else { %> This Patient has no location yet <% } %>
          </li>
          <% if (sessionLocation.name != hivProgramLocation.name ) {%>
          <li>You modify the dossier of a patient who is not on your site</li>
          <% } %>
      </ul>

</div>
</div>

