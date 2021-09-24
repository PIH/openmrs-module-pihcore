<table class="results-table">
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