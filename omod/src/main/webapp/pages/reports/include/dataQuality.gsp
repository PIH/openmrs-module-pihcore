<table class="results-table" ng-show="hasAnyNonEmptyCohort(dataset)">
    <thead>
        <tr>
            <th colspan="2">
                <i class="icon-warning-sign"></i>
                ${ ui.message("mirebalaisreports.dailyReport.dataQuality.title") }
            </th>
        </tr>
    </thead>
    <tbody>
    <tr ng-repeat="column in dataset.metadata.columns">
        <th>
            {{ column.label | translate }}
        </th>
        <td>
            <a ng-click="viewCohort(day, dataset.rows[0], column)">
                {{ dataset.rows[0][column.name].size }}
            </a>
        </td>
    </tr>
    </tbody>
</table>