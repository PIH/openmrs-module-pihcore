angular.module('configService', ['ngResource'])

    .factory('ConfigResource', [ "$resource", function($resource) {
        return $resource("/" + OPENMRS_CONTEXT_PATH + "/module/pihcore/config.json", {
        },{
            query: { method:'GET', isArray:false, cache:true }
        });
    }])

    .factory('ConfigService', [ "ConfigResource", function(ConfigResource) {
        return {
            // returns a promise
            getConfig: function() {
                return ConfigResource.query().
                    $promise.then(function(response) {
                        return response;
                    });
            },
            getVisitTemplates:function() {
                return ConfigResource.query().
                    $promise.then(function(response) {
                        return response.visitTemplates;
                    });
            }
        }
    }]);