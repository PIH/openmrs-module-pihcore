angular.module("waitingforConsultService", [])
    .service('WaitingForConsultService', ['$q', '$http',
        function($q, $http) {
            var CONSTANTS = {
                URLS: {
                    CONCEPT: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/concept",
                    OBS: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/obs",
                    ACTIVE_VISIT: "/" + OPENMRS_CONTEXT_PATH  + "/ws/rest/emrapi/activevisit"
                },
                WAITING_FOR_CONSULT_STATUS_CONCEPT_UUID: "dee1b1ba-b82a-41b9-897b-28d7868c4bcd" ,
                BEGIN_CONSULTATION: "3cdc871e-26fe-102b-80cb-0017a47871b2",
                REMOVE: "45d0c3d2-2188-4186-8a19-0063b92914ee"
            };

            this.beginConsultation = function (patientId) {
                //check if patient already has a WAITING_FOR_CONSULT_STATUS
                return $http.get(CONSTANTS.URLS.OBS + "/" + "?patient=" + patientId + "&concept=" + CONSTANTS.WAITING_FOR_CONSULT_STATUS_CONCEPT_UUID)
                    .then( function (response) {
                         if (response.status == 200 ) {
                             var obs = {
                                 person:  patientId,
                                 concept:  CONSTANTS.WAITING_FOR_CONSULT_STATUS_CONCEPT_UUID,
                                 value: CONSTANTS.BEGIN_CONSULTATION,
                                 obsDatetime: new Date()
                             };
                             if (response.data && response.data.results && response.data.results.length > 0 ) {
                                 //update patient current status
                                 var obs = response.data.results[0];
                                 if (obs) {
                                     return saveObs(obs, obs.uuid);
                                 }
                             } else {
                                 // create BEGIN_CONSULT status
                                 return saveObs(obs, null);
                             }
                         }
                    }, function (error) {
                            console.log("Error searching for WAITING FOR CONSULT CONCEPT")
                    });
            };

            this.removeFromQueue = function (patientId) {
                //check if patient already has a WAITING_FOR_CONSULT_STATUS
                return $http.get(CONSTANTS.URLS.OBS + "/" + "?patient=" + patientId + "&concept=" + CONSTANTS.WAITING_FOR_CONSULT_STATUS_CONCEPT_UUID)
                    .then( function (response) {
                        if (response.status == 200 ) {
                            var obs = {
                                person:  patientId,
                                concept:  CONSTANTS.WAITING_FOR_CONSULT_STATUS_CONCEPT_UUID,
                                value: CONSTANTS.REMOVE,
                                obsDatetime: new Date()
                            };
                            if (response.data && response.data.results && response.data.results.length > 0 ) {
                                //update patient current status
                                var obs = response.data.results[0];
                                if (obs) {
                                    return saveObs(obs, obs.uuid);
                                }
                            } else {
                                // create BEGIN_CONSULT status
                                return saveObs(obs, null);
                            }
                        }
                    }, function (error) {
                        console.log("Error searching for WAITING FOR CONSULT CONCEPT")
                    });
            };

            function saveObs(obs, uuid) {
                var postUrl = CONSTANTS.URLS.OBS;
                if (uuid) {
                    postUrl += "/" + uuid;
                }
                return $http.post(postUrl, obs);
            }
        }]);