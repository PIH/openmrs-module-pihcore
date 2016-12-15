
// TODO: no longer used, keeping around in case we bring it back, but can be deleted eventually

angular.module("primaryCareWaitingForConsultController", [])
    .controller("WaitingForConsultController", ["$scope", "WaitingForConsultService","ngDialog",
    function($scope, WaitingForConsultService, ngDialog) {

        $scope.beginConsult = function (patientId, visitId) {

            ngDialog.openConfirm({
                showClose: true,
                closeByEscape: true,
                template: "confirmBeginConsult.page"
            }).then(function() {
                $scope.isSaving = true;

                return WaitingForConsultService.beginConsultation(patientId).then(function(res){
                    $scope.isSaving = false;
                    if( (res.status == 200) || (res.status == 201) ){
                        var url = "/pihcore/visit/visit.page?patient=" + patientId + "&visit=" + visitId;
                        emr.navigateTo({ applicationUrl: url});
                    }
                    else{
                        alert("The system was not able to update the record");
                        $scope.message = {type: 'danger', text: "Failed to change patient waiting status"};
                    }
                });
            })
        };

        $scope.removeFromQueue = function (patientId, visitId) {

            ngDialog.openConfirm({
                showClose: true,
                closeByEscape: true,
                template: "confirmRemoveFromQueue.page"
            }).then(function() {
                $scope.isSaving = true;

                return WaitingForConsultService.removeFromQueue(patientId).then(function(res){
                    $scope.isSaving = false;
                    if( (res.status == 200) || (res.status == 201)){
                        $scope.message = {type: 'danger', text: res.data};
                        var url = "/pihcore/visit/waitingForConsult.page" ;
                        emr.navigateTo({ applicationUrl: url});
                    }
                    else{
                        alert("The system was not able to remove patient from queue");
                        $scope.message = {type: 'danger', text: "Failed to remove patient from queue"};
                    }
                });
            })
        };
    }]);