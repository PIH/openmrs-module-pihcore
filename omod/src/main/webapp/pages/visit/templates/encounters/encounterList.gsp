<table id="encounter-list">
    <tr>
        <th>${ ui.message("Encounter.datetime") }</th>
        <th>${ ui.message("Encounter.type") }</th>
        <th>${ ui.message("Encounter.provider") }</th>
        <th>${ ui.message("pihcore.visitNote.clinicalNotes") }</th>
        <th>${ ui.message("uicommons.location") }</th>
    </tr>

    <tr ng-repeat="e in encounters" ng-click="goToEncounter(e)" class="list-element selectable" ng-class="{ 'selected-visit': e.uuid===encounter.uuid }">
        <td>
            <nobr><span class="visit-list-start-date">{{ e.encounterDatetime | serverDate : 'dd-MMM-yyyy'}}</span>
            </nobr>
        </td>
        <td>
            {{ e.encounterType.display }}
        </td>
        <td>
            <span ng-repeat="p in e.encounterProviders">
                {{ p | getProviderName }}{{ \$last ? "" : "," }}
            </span>
        </td>
        <td>
            <span ng-repeat="obs in e.obs | byConcept:Concepts.clinicalNotes">
                    {{ obs |  obs:"value" }}{{ \$last ? "" : "," }}
            </span>
        </td>
        <td>
            <span>
                {{e.location | omrsDisplay}}
            </span>
        </td>
    </tr>
</table>

<br/>

<button ng-click="back()" class="cancel">
    ${ ui.message("uicommons.return") }
</button>
