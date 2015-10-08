<span dropdown dropdown-append-to-body>
    <a dropdown-toggle>${ ui.message("pihcore.visitNote.goToAnotherVisit") }</a>
    <ul id="visit-list-dropdown" class="dropdown-menu" aria-labelledby="visit-list-dropdown">
        <li class="list-header">
            <span class="visit-list-date-header">${ ui.message('uicommons.startDate') }</span>
            <span class="visit-list-encounters-header">${ ui.message('pihcore.visitList.encounters') }</span>
        </li>
        <li class="list-element" ng-repeat="v in visits" ng-click="goToVisit(v)" class="selectable" ng-class="{ 'selected-visit': v.uuid===visit.uuid, active: !v.stopDatetime }">
            <span class="visit-list-date">{{ v.startDatetime | serverDate : 'dd-MMM-yyyy' }}</span>
            <span class="visit-list-encounters">{{ v.encounters | encounterTypesForVisitList }}</span>
        </li>
    </ul>
</span>