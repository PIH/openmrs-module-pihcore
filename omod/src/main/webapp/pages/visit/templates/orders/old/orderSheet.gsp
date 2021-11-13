<div ng-hide="anyOrders()">
    ${ ui.message("uicommons.none") }
</div>
<ul>
    <li ng-repeat="order in orderList()">
        {{ order.dateActivated | serverDateLocalized:DatetimeFormats.dateLocalized }}
        {{ order | orderInstructions }}
    </li>
</ul>