<div class="dialog-header">
    <h3>Edit Visit Details</h3>
</div>
<div class="dialog-content">
    <table>
        <thead>
        <tr>
            <th></th>
            <th>Old</th>
            <th>New</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td>Start</td>
            <td>{{ visit.startDatetime | serverDate }}</td>
            <td>
                <date-with-popup ng-model="newStartDatetime" min-date="startDateLowerLimit" max-date="startDateUpperLimit || now"></date-with-popup>
            </td>
        </tr>
        <tr>
            <td>End</td>
            <td>{{ visit.stopDatetime | serverDate }}</td>
            <td>
                <date-with-popup type="text" size="20" ng-model="newStopDatetime" min-date="endDateLowerLimit" max-date="endDateUpperLimit || now"></date-with-popup>
            </td>
        </tr>
        <tr>
            <td>Location</td>
            <td>{{ visit.location | omrs.display }}</td>
        </tr>
        </tbody>
    </table>
    <br/>
    <div>
        <button class="confirm right" ng-click="confirm({ start: newStartDatetime, stop: newStopDatetime, location: newLocation  })">${ ui.message("uicommons.save") }</button>
        <button class="cancel" ng-click="closeThisDialog()">${ ui.message("uicommons.cancel") }</button>
    </div>
</div>
