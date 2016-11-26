(function() {
    'use strict';

    angular
        .module('mediumApp')
        .factory('StorySearch', StorySearch);

    StorySearch.$inject = ['$resource'];

    function StorySearch($resource) {
        var resourceUrl =  'api/_search/stories/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
