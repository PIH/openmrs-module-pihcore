# openmrs-module-pihcore
Core metadata and configuration included in all PIH distributions

Legacy means for code level configuration of metadata is found in this 
project (we are in the progress of migrating much of this to
[Iniz](https://github.com/mekomsolutions/openmrs-module-initializer)-based 
deployment in the [openmrs-config-pihemr](https://github.com/PIH/openmrs-config-pihemr/)
project). Metadata is set up programmatically in pih-core via methods
provided by the [metadata deploy module](https://wiki.openmrs.org/display/docs/Metadata+Deploy+Module).

Some metadata which is shared across all sites is in
[metadata/core](https://github.com/PIH/openmrs-module-pihcore/tree/master/api/src/main/java/org/openmrs/module/pihcore/metadata/core).

Some site-specific metadata is under directories organized by country. For example,
Location and Patient Identifier Type for Mexico is under
[metadata/mexico](https://github.com/PIH/openmrs-module-pihcore/tree/master/api/src/main/java/org/openmrs/module/pihcore/metadata/mexico).

### HTMLFormEntry Features

This module also defines a few HTML Form Entry features.

#### Tag familyHistoryRelativeCheckboxes
Usage:
```
<familyHistoryRelativeCheckboxes concept="uuid-of-asthma" label="Asthma" relatives="uuid-of-father,uuid-of-mother"/>
```
produces `Asthma: [ ]Father  [ ]Mother`
```
<familyHistoryRelativeCheckboxes concept="uuid-of-cancer" label="Cancer" relatives="uuid-of-father,uuid-of-mother" specify="true"/>
```
produces `Cancer, specify:________   [ ]Father  [ ]Mother`

For each relative box that is checked, an obs group will be stored with concept CIEL:160593 and members:
* which disease? CIEL:160592 = @concept
* which relative? CIEL:1560 = @relatives[i]
* present? CIEL:1729 = YES
* comments? CIEL:160618 = comment text


#### Tag pastMedicalHistoryCheckbox
Usage:
```
<pastMedicalHistoryCheckbox concept="uuid-of-asthma" />
```
produces `[ ] Asthma`
```
<pastMedicalHistoryCheckbox concept="uuid-of-STI" label="STI" specify="true"/>
```
produces `[ ] STI, specify:_______`

If you don't specify `label`, the preferred concept name in the context locale 
will be used.

If checked, will be stored as an obs group with concept CIEL:1633, and members:
- which? CIEL:1628 = @concept
- present? CIEL:1729 = YES
- comments: CIEL:160221 = comment text

#### Tag causeOfDeathList
?

#### RedirectToDeathNoteIfMissing

When included in a form, upon any save (of a new or edited form) if all the following are true, then the user is
redirected to fill out a new death certificate:
* this form's encounter's disposition is Death
* the user has the privilege to enter a Death Certificate
* the patient has no non-voided Death Certificate encounter

Usage within an HTML Form:
```
<postSubmissionAction class="org.openmrs.module.pihcore.htmlformentry.RedirectToDeathNoteIfMissing"/>
```

#### Velocity context

The PIH Config is added to the Velocity context as `config`.