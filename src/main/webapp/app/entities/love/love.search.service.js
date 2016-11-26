(function() {
    'use strict';

    angular
        .module('mediumApp')
        .factory('LoveSearch', LoveSearch);

    LoveSearch.$inject = ['$resource'];

    function LoveSearch($resource) {
        var resourceUrl =  'api/_search/loves/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
