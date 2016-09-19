angular.module("waitingforConsultService", [])
    .service('WaitingForConsultService', ['$q', '$http',
        function($q, $http) {
            var CONSTANTS = {
                URLS: {
                    CONCEPT: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/concept",
                    OBS: "/" + OPENMRS_CONTEXT_PATH + "/ws/rest/v1/obs",
                    ACTIVE_VISIT: "/" + OPENMRS_CONTEXT_PATH  + "/ws/rest/emrapi/activevisit"
                },
                WAITING_FOR_CONSULT_STATUS_CONCEPT_UUID: "c45892e5-c715-4923-a27e-9c242e5a1a23" ,
                BEGIN_CONSULTATION: "2ead5913-633f-45af-9b47-41087722019e",
                BEGIN_CONSULTATION_CONCEPT_NAME: "160c3cc8-b771-4321-bbd4-4602e7b646b2",
                REMOVE: "45d0c3d2-2188-4186-8a19-0063b92914ee",
                REMOVE_CONCEPT_NAME: "81086bbb-cb1b-42e6-9845-9fe6ccd5fad6"
            };

            this.beginConsultation = function (patientId) {
                //check if patient already has a WAITING_FOR_CONSULT_STATUS
                return $http.get(CONSTANTS.URLS.OBS + "/" + "?patient=" + patientId + "&concept=" + CONSTANTS.WAITING_FOR_CONSULT_STATUS_CONCEPT_UUID)
                    .then( function (response) {
                         if (response.status == 200 ) {
                             var obs = {
                                 person:  patientId,
                                 concept:  CONSTANTS.WAITING_FOR_CONSULT_STATUS_CONCEPT_UUID,
                                 valueCodedName: CONSTANTS.BEGIN_CONSULTATION_CONCEPT_NAME,
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
                                valueCodedName: CONSTANTS.REMOVE_CONCEPT_NAME,
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