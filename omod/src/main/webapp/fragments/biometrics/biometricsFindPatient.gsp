<%
    ui.includeJavascript("pihcore", "biometrics/biometricsFindPatient.js")
%>

<div id="biometrics-find-patient" style="display:none">

    <h3>
        ${ui.message('pihcore.biometrics.matches')}
    </h3>

    <table id="biometrics-results">
        <thead>
            <tr>
                <td>${ui.message('registrationapp.patient.identifier.label')}</td>
                <td>${ui.message('coreapps.person.name')}</td>
                <td>${ui.message('coreapps.gender')}</td>
                <td>${ui.message('coreapps.age')}</td>
                <td>${ui.message('coreapps.birthdate')}</td>
            </tr>
        </thead>
        <tbody>

        </tbody>
    </table>
</div>


