<%	
	ui.includeJavascript("coreapps", "fragments/patientdashboard/encountertemplate/defaultEncounterTemplate.js")
%>

<style>
	.printEncounter {
		vertical-align: top;
		padding: 0.2em;
	}
	.print-action {
		position: relative;
		color: #bbbbbb;
		font-size: 1em;
		cursor: pointer;
	}
	.print-action:hover {
		color: #007FFF;
	}
</style>

<script type="text/javascript">
	$(document).on('click', '.print-action', function(event) {
		var encounterId = $(event.target).attr("data-encounter-id");

	});
</script>

<script type="text/template" id="mexicoConsultEncounterTemplate">
<li>
	<div class="encounter-date">
	    <i class="icon-time"></i>
	    <strong>
	        {{- encounter.encounterTime }}
	    </strong>
	    {{- encounter.encounterDate }}
	</div>
	<ul class="encounter-details">
	    <li> 
	        <div class="encounter-type">
	            <strong>
	                <i class="{{- config.icon }}"></i>
	                <span class="encounter-name" data-encounter-id="{{- encounter.encounterId }}">{{- encounter.encounterType.name }}</span>
	            </strong>
	        </div>
	    </li>
	    <li>
	        <div>
	            ${ ui.message("coreapps.by") }
	            <strong class="provider">
	                {{- encounter.primaryProvider ? encounter.primaryProvider : '' }}
	            </strong>
	            ${ ui.message("coreapps.in") }
	            <strong class="location">{{- encounter.location }}</strong>
	        </div>
	    </li>
	    <li>
	        <div class="details-action">
	            <a class="view-details collapsed" href='javascript:void(0);' data-encounter-id="{{- encounter.encounterId }}" data-encounter-form="{{- encounter.form != null}}" data-display-with-html-form="{{- config.displayWithHtmlForm }}" data-target="#encounter-summary{{- encounter.encounterId }}" data-toggle="collapse" data-target="#encounter-summary{{- encounter.encounterId }}">
	                <span class="show-details">${ ui.message("coreapps.patientDashBoard.showDetails")}</span>
	                <span class="hide-details">${ ui.message("coreapps.patientDashBoard.hideDetails")}</span>
	                <i class="icon-caret-right"></i>
	            </a>
	        </div>
	    </li>
	</ul>

	<span>
		<i class="printEncounter print-action fas fa-fw fa-capsules" data-mode="view" data-patient-id="{{- patient.id }}" data-encounter-id="{{- encounter.encounterId }}" title="${ ui.message("pihcore.visitNote.printPrescriptions") }"></i>
		<i class="viewEncounter view-action icon-file-alt" data-mode="view" data-patient-id="{{- patient.id }}" data-encounter-id="{{- encounter.encounterId }}" {{ if (config.viewUrl) { }} data-view-url="{{- config.viewUrl }}" {{ } }} {{ if (config.uiStyle) { }} data-ui-style="{{- config.uiStyle }}" {{ } }} title="${ ui.message("coreapps.view") }"></i>
		{{ if ( (config.editable == null || config.editable) && encounter.canEdit) { }}
            <i class="editEncounter edit-action icon-pencil" data-patient-id="{{- patient.id }}" data-encounter-id="{{- encounter.encounterId }}" {{ if (config.editUrl) { }} data-edit-url="{{- config.editUrl }}" {{ } }} {{ if (config.uiStyle) { }} data-ui-style="{{- config.uiStyle }}" {{ } }} title="${ ui.message("coreapps.edit") }"></i>
        {{ } }}
        {{ if ( encounter.canDelete ) { }}
	       <i class="deleteEncounterId delete-action icon-remove" data-visit-id="{{- encounter.visitId }}" data-encounter-id="{{- encounter.encounterId }}" title="${ ui.message("coreapps.delete") }"></i>
        {{  } }}
	</span>

	<div id="encounter-summary{{- encounter.encounterId }}" class="collapse encounterview">
	    <div class="encounter-summary-container"></div>
	</div>
</li>
</script>
