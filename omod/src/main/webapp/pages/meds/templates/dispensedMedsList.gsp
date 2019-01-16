<h2>
    ${ ui.message(mirebalais.dispensing.dispensing) }
</h2>

<table id="dispense-med-list">
    <tr>
        <th>${ ui.message("zl.date") }</th>
        <th>${ ui.message("mirebalais.dispensing.medication") }</th>
        <th>${ ui.message("mirebalais.dispensing.medicationFrequency") }</th>
    </tr>

</table>

<br/>

<button ng-click="back()" class="cancel">
    ${ ui.message("uicommons.return") }
</button>