<span dropdown dropdown-append-to-body>
    <a dropdown-toggle>${ ui.message("pihcore.visitNote.goToAnotherVisit") }</a>
    <ul id="visit-list-dropdown" class="dropdown-menu" aria-labelledby="visit-list-dropdown">
        <li class="list-element" ng-repeat="v in visits" ng-click="goToVisit(v)" class="selectable" ng-class="{ 'selected-visit': v.uuid===visit.uuid, active: !v.stopDatetime }">
            <span class="visit-list-date">{{ v.startDatetime | serverDate : 'dd-MMM-yyyy' }}</span>
        </li>
    </ul>
</span>