<div ng-hide="anyOrders()">
    None
</div>
<ul>
    <li ng-repeat="order in orderList()">
        {{ order.dateActivated | serverDate:DatetimeFormats.date }}
        {{ order | orderInstructions }}
    </li>
</ul>