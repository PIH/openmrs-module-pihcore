<div class="dialog-header">
    <h3>${ ui.message("pihcore.visitNote.recordVaccination.header") }</h3>
</div>

<!-- TODO: finish translations -->
<div class="dialog-content">
    <h4>
        {{ vaccination.label }}, {{ sequence.label }}
    </h4>
    <div class="spaced-paragraphs">
        <h5>${ ui.message("pihcore.vaccination.whenWasItGiven") }</h5>

        <p ng-show="hasActiveVisit">
            <label>
                <input type="radio" ng-model="when" value="now"/> ${ ui.message("uicommons.today") }: {{ now | date }}
            </label>
        </p>

        <p>
            <label>
                <input type="radio" ng-model="when" value="visit"/> ${ ui.message("pihcore.visitNote.duringVisit.label") }
            </label>
        </p>

        <p>
            <label>
                <input type="radio" ng-model="when" value="no-visit"/>${ ui.message("pihcore.visitNote.notDuringVisit.label") }
            </label>
        </p>

        <p ng-show="when == 'visit'">
            <br/>
            ${ ui.message("pihcore.visitNote.whichVisit") }?

            <select id="visit-list-select" ng-model="whenVisit" ng-options="visit as visit | combinedVisitDates for visit in visits">
            </select>
        </p>

        <p ng-show="when != 'now'">
            <br/>
            ${ ui.message("uicommons.date") }: <date-with-popup ng-model="date" min-date="minDate" max-date="maxDate"></date-with-popup>
        </p>
        <br/>
    </div>
    <div>
        <button class="confirm right" ng-click="confirm({when: when, whenVisit: whenVisit, date: date})">${ ui.message("uicommons.save") }</button>
        <button class="cancel" ng-click="closeThisDialog()">${ ui.message("uicommons.cancel") }</button>
    </div>
</div>