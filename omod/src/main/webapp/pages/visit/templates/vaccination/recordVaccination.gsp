<div class="dialog-header">
    <h3>${ ui.message("pihcore.visitNote.recordVaccination.header") }</h3>
</div>

<div class="dialog-content">
    <h4>
        {{ vaccination.label | translate }}, {{ sequence.label | translate }}
    </h4>
    <div class="spaced-paragraphs">
        <h5>${ ui.message("pihcore.vaccination.whenWasItGiven") }</h5>

        <p ng-show="encounter">
            <label>
                <input type="radio" ng-model="when" value="encounter"/> ${ ui.message("pihcore.visitNote.duringCurrentConsult") }
            </label>
        </p>

        <p>
            <label>
                <input type="radio" ng-model="when" value="no-encounter"/>${ ui.message("pihcore.visitNote.notDuringVisit.label") }
            </label>
        </p>

        <p ng-show="when == 'no-encounter'">
            <br/>
            ${ ui.message("uicommons.date") }: <date-with-popup ng-model="date" min-date="minDate" max-date="maxDate"></date-with-popup>
        </p>
        <br/>
    </div>

    <div class="spaced-paragraphs">
        <p>
            <label>
                <!-- ToDo:  UHM-5674 Connect lot number data -->
                <input type="text" id="lotnumber" />${ ui.message("pihcore.lotNo") }
            </label>
        </p>
        <p>
            <label>
                <!-- ToDo:  UHM-5674 Connect manufacturer data -->
                <input type="text" id="manufacturer">${ ui.message("pihcore.manufacturer") }
            </label>
        </p>
    </div>
    <div>
        <!-- note: vaccination-confirm is simply a helper class for smoke tests -->
        <button class="vaccination-confirm confirm right" ng-disabled="when != 'encounter' && !date" ng-click="confirm({when: when, date: date})">${ ui.message("uicommons.save") }</button>
        <button class="cancel" ng-click="closeThisDialog()">${ ui.message("uicommons.cancel") }</button>
    </div>
</div>