'use strict';

describe('Controller: GroupsCtrl', function () {

  // load the controller's module
  beforeEach(module('paymentOrganizerApp'));

  var GroupsCtrl,
      Group,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope, _Group_) {
    scope = $rootScope.$new();
    Group = _Group_
    GroupsCtrl = $controller('GroupsCtrl', {
      $scope: scope,
      Group: Group
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(scope.groups.lenght).toBe(2);
  });
});
