<table id="visit-list">
    <tr>
        <th>${ ui.message("uicommons.start") }</th>
        <th>${ ui.message("uicommons.end") }</th>
        <th>${ ui.message("pihcore.visitNote.visitTemplate") }</th>
        <th>${ ui.message("pihcore.diagnoses.label") }</th>
        <th>${ ui.message("pihcore.visitNote.clinicalNotes") }</th>
    </tr>

    <tr ng-repeat="v in visits" ng-click="goToVisit(v)" class="list-element selectable" ng-class="{ 'selected-visit': v.uuid===visit.uuid, active: !v.stopDatetime }">
        <td>
            {{ v.startDatetime | serverDate }}
        </td>
        <td>
            {{ v.stopDatetime | serverDate }}
            <span ng-hide="v.stopDatetime">
                (${ ui.message("uicommons.active") })
            </span>
        </td>
        <td>
           {{ v.visitTemplate.label | translate }}
        </td>
        <td>
            <span ng-repeat="diag in v | diagnosesInVisitShort">
                {{ diag }}{{ \$last ? '' : ',' }}
            </span>
        </td>
        <td>
            {{ v.encounters | encounterTypesForVisitList }}
        </td>
    </tr>
</table>