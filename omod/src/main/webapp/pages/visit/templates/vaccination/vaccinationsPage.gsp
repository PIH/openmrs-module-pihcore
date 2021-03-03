<!-- stand-alone page to back the "vaccinations" state used when during the "Next" workflow -->

<div class="visit-element">
    <vaccination-table encounter="encounter" patient="patient" visit="visit" expand-on-load="true"></vaccination-table>

    <div id="buttons">
        <button id="next" ng-click="goToNextSection('vaccinations')" type="button" class="submitButton confirm right">${ ui.message("coreapps.next") }<i class="icon-spinner icon-spin icon-2x" style="display: none; margin-left: 10px;"></i></button>
        <button id="return" ng-click="goToVisit()" type="button" class="cancel">${ ui.message("coreapps.return") }</button>
    </div>

</div>
