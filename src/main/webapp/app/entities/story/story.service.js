(function() {
    'use strict';
    angular
        .module('mediumApp')
        .factory('Story', Story);

    Story.$inject = ['$resource', 'DateUtils'];

    function Story ($resource, DateUtils) {
        var resourceUrl =  'api/stories/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.timeCreated = DateUtils.convertLocalDateFromServer(data.timeCreated);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.timeCreated = DateUtils.convertLocalDateToServer(copy.timeCreated);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.timeCreated = DateUtils.convertLocalDateToServer(copy.timeCreated);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
