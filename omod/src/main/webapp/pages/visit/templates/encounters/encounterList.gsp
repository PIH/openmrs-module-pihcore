<table id="encounter-list">
    <tr>
        <th>${ ui.message("Encounter.datetime") }</th>
        <th>${ ui.message("Encounter.type") }</th>
        <th>${ ui.message("Encounter.provider") }</th>
        <th>${ ui.message("pihcore.visitNote.clinicalNotes") }</th>
        <th>${ ui.message("uicommons.location") }</th>
        <th></th>
    </tr>

    <tr ng-repeat="e in encounters" class="list-element selectable" ng-class="{ 'selected-visit': e.uuid===encounter.uuid }">
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
        <td>
            <nobr><span>
                <a class="edit-encounter" ng-click="goToEncounter(e)"><i class="icon-pencil"></i></a>
                <a class="delete-action" ng-click="confirmDeleteEncounter(e)"><i class="icon-remove"></i></a>
            </span></nobr>
        </td>
    </tr>
</table>

<br/>

<button ng-click="back()" class="cancel">
    ${ ui.message("uicommons.return") }
</button>
