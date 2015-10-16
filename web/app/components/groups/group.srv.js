'use strict';

/**
 * @ngdoc service
 * @name paymentOrganizerApp.group
 * @description
 * # group
 * Factory in the paymentOrganizerApp.
 */
angular.module('paymentOrganizerApp')
    .factory('Group', function ($resource, configuration) {
        return $resource(configuration.url + "/groups/:id");
    });
