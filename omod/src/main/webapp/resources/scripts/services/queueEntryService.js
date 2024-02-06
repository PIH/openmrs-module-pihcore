angular.module('queueEntryService', ['ngResource', 'uicommons.common'])
    .factory('QueueEntry', function($resource) {
        return $resource("/" + OPENMRS_CONTEXT_PATH  + "/ws/rest/v1/queue-entry/:uuid", {
            uuid: '@uuid'
        },{
            query: { method:'GET', isArray:false } // OpenMRS RESTWS returns { "results": [] }
        });
    })
    .factory('QueueEntryService', function(QueueEntry, $resource) {
        return {
            getQueueEntries: function(params) {
                return QueueEntry.query(params).$promise.then(function(res) {
                    return res.results;
                });
            },
            saveQueueEntry: function(queueEntry) {
                return new QueueEntry(queueEntry).$save();
            }
        }
    });