<div>
<div id="hiv-program-location-info" style="background-color: lightcoral">
    <p class="left">Session Location: ${sessionLocation.name}</p><br/>
    <p class="left">HIV Program Location: ${hivProgramLocation.name}</p><br>
    <% if (sessionLocation.name != hivProgramLocation.name ) {%>
    <span>You modify the dossier of a patient who is not on your site</span>
    <%}%>
</div>

</div>