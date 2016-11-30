(function() {
    'use strict';

    angular
        .module('mediumApp')
        .config(bootstrapMaterialDesignConfig);

     bootstrapMaterialDesignConfig.$inject = [];

    function bootstrapMaterialDesignConfig() {
        $.material.init();

    }
})();
