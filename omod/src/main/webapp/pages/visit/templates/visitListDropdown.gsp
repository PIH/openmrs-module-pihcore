<span dropdown dropdown-append-to-body>
    <button dropdown-toggle>${ ui.message("pihcore.visitNote.changeVisit") } <i class="icon-caret-down"></i></button>
    <ul id="visit-list-dropdown" class="dropdown-menu" aria-labelledby="visit-list-dropdown">
        <li id="visit-list-full-details-element" class="list-element selectable" ng-click="goToVisitList()">${ ui.message('pihcore.visitNote.fullDetails') }</li>
        <li class="list-element selectable" ng-repeat="v in visits" ng-click="goToVisit(v)" ng-class="{ 'selected-visit': v.uuid===visit.uuid }">
            <span class="visit-list-date">{{ v.startDatetime | serverDate : 'dd-MMM-yyyy' }}</span>
        </li>
    </ul>
</span>