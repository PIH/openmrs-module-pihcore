
function printWristband(patientId) {
    emr.getFragmentActionWithCallback('pihcore', 'wristband', 'printWristband'
        , { patientId: patientId ?? patient.id }
        , function(data) {
            if(data.success) {
                emr.successMessage(data.message);
            } else {
                emr.errorMessage(data.message);
            }
        }
    );
}