'use strict';

/**
 * @ngdoc function
 * @name paymentOrganizerApp.controller:DashboardCtrl
 * @description
 * # DashboardCtrl
 * Controller of the paymentOrganizerApp
 */
angular.module('paymentOrganizerApp')
    .controller('DashboardCtrl', function ($scope, $stateParams, Group) {
        if (!$stateParams.id) return;
        $scope.is_loading = true;
        $scope.group = Group.get({id: $stateParams.id});
        $scope.group.$promise.then(function(){
            $scope.is_loading = false;
        });

        $scope.acceptSuggestion = function(suggestion){
            var type = 'Exchange',
                groupId = $scope.group.id,
                fromUserId = suggestion.from.id,
                toUserId = suggestion.to.id,
                ammount = suggestion.ammount,
                date = new Date();
        };

        $scope.getPaymentData = function(payment){
            var data = "???";
            if (payment.type == "Payment") {
                data = payment.event.user.name;
            } else if (payment.type == "Exchange") {
                data = payment.event.from.name + " &rarr; " + payment.event.to.name;
            } else if (payment.type == "Expense") {
                var users = [];
                for (var i in payment.event.userRatios){
                    var ratio = payment.event.userRatios[i];
                    users.push(ratio.user.name + "(" + Math.round(ratio.ratio * 100) + "%)");
                }
                data = "<strong>" + payment.event.name + "</strong><br/>" + users.join(',');
            }
            return data;
        };
        $scope.abs = Math.abs;
        $scope.isArray = angular.isArray;
    });
