(function() {
    'use strict';
    angular
        .module('mediumApp')
        .factory('Love', Love);

    Love.$inject = ['$resource'];

    function Love ($resource) {
        var resourceUrl =  'api/loves/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
