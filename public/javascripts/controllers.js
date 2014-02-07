var sharedTextApp = angular.module('sharedTextApp', []);

sharedTextApp.controller('SharedTxtCtrl', function($scope, $http) {

    $scope.sendData = function() {
        // ... do $http.post
    };

});