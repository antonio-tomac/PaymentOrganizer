/**
 * Created by alen on 16.10.15..
 */
'use strict';

/**
 * @ngdoc overview
 * @name webApp
 * @description
 * # webApp
 *
 * Main module of the application.
 */
angular
    .module('paymentOrganizerApp')
    .config(function ($urlRouterProvider, $stateProvider) {
        $urlRouterProvider.otherwise('/groups');
        $stateProvider
            .state('groups', {
                url: '/groups',
                templateUrl: 'components/groups/groups.html',
                controller: 'GroupsCtrl'
            })
            .state('dashboard', {
                url: '/dashboard/:id',
                templateUrl: 'components/dashboard/dashboard.html',
                controller: 'DashboardCtrl'
            });
    });