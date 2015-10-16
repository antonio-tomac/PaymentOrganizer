'use strict';

/**
 * @ngdoc function
 * @name paymentOrganizerApp.controller:GroupsCtrl
 * @description
 * # GroupsCtrl
 * Controller of the paymentOrganizerApp
 */
angular.module('paymentOrganizerApp')
    .controller('GroupsCtrl', function ($scope, Group) {
        $scope.is_loading = true;
        $scope.groups = Group.query();
        $scope.groups.$promise.then(function(){
            $scope.is_loading = false;
        });
    });
