
<div class="visit-location">
    <i class="icon-hospital small"></i>
    {{ visit.location | omrsDisplay }}

    <span class="actions">
        <i class="icon-pencil edit-action" ng-click="edit()" ng-hide="editing"></i>
    </span>
</div>

<div class="visit-dates">
    <span>
        <i class="icon-time small"></i>
        {{ visit.startDatetime | serverDateLocalized:DatetimeFormats.dateLocalized }}
        <span ng-show="visit.stopDatetime">- {{ visit.stopDatetime | serverDateLocalized:DatetimeFormats.dateLocalized  }}</span>
        <span ng-hide="visit.stopDatetime" class="lozenge active">
            (${ ui.message("uicommons.active") })
        </span>
    </span>
</div>
