<div ng-hide="anyOrders()">
    ${ ui.message("uicommons.none") }
</div>
<ul>
    <li ng-repeat="order in orderList()">
        {{ order.dateActivated | serverDate:DatetimeFormats.date }}
        {{ order | orderInstructions }}
    </li>
</ul>