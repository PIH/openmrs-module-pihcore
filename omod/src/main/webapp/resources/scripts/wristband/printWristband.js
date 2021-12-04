
function printWristband() {
    emr.getFragmentActionWithCallback('pihcore', 'wristband', 'printWristband'
        , { patientId: patient.id }
        , function(data) {
            if(data.success) {
                emr.successMessage(data.message);
            } else {
                emr.errorMessage(data.message);
            }
        }
    );
}