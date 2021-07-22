<div class="dialog-header">
    <h3> ${ ui.message("pihcore.visitNote.editVisitDetails.header") }</h3>
</div>
<div class="dialog-content">
    <table>
        <thead>
        <tr>
            <th></th>
            <th>${ ui.message("uicommons.old") }</th>
            <th>${ ui.message("uicommons.new") }</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td>${ ui.message("uicommons.start") }</td>
            <td>{{ visit.startDatetime | serverDate }}</td>
            <td>
                <date-with-popup ng-model="newStartDatetime" min-date="startDateLowerLimit" max-date="(startDateUpperLimit && (startDateUpperLimit < newStopDatetime) ? startDateUpperLimit : newStopDatetime) || now"></date-with-popup>
            </td>
        </tr>
        <tr>
            <td>${ ui.message("uicommons.end") }</td>
            <td>{{ visit.stopDatetime | serverDate }}</td>
            <td>
                <date-with-popup type="text" size="20" ng-model="newStopDatetime" min-date="(endDateLowerLimit && (endDateLowerLimit > newStartDatetime) ? endDateLowerLimit : newStartDatetime)" max-date="endDateUpperLimit || now" clear-button="endDateUpperLimit ? false: true"></date-with-popup>
            </td>
        </tr>
        <tr>
            <td>${ ui.message("uicommons.location") }</td>
            <td>{{ visit.location | omrsDisplay }}</td>
        </tr>
        </tbody>
    </table>
    <br/>
    <div>
        <button class="confirm right" ng-click="confirm({ start: newStartDatetime, stop: newStopDatetime, location: newLocation  })">${ ui.message("uicommons.save") }</button>
        <button class="cancel" ng-click="closeThisDialog()">${ ui.message("uicommons.cancel") }</button>
    </div>
</div>
