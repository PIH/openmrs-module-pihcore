<table class="results-table" ng-show="hasAnyNonEmptyCohort(dataset)">
    <thead>
        <tr>
            <th ng-repeat="column in dataset.metadata.columns">
                {{ column.label | translate }}
            </th>
        </tr>
    </thead>
    <tbody>
        <tr ng-repeat="row in dataset.rows">
            <td ng-repeat="column in dataset.metadata.columns" ng-show="hasAnyNonEmptyCohort(row)">
                <span ng-hide="row[column.name].size >= 0">
                    {{ row[column.name] | translate }}
                </span>
                <a ng-show="row[column.name].size >= 0" ng-click="viewCohort(day, row, column)">
                    {{ row[column.name].size }}
                </a>
            </td>
        </tr>
    </tbody>
</table>