var sharedTextApp = angular.module('sharedTextApp', []);

sharedTextApp.controller('SharedTxtCtrl', function($scope, $http) {
	
	// Event Listeners
    var source = new EventSource('data/get');

	source.addEventListener('message', function(e) {
	    $scope.$apply(function() {
	        $scope.receivedText = e.data;
	    });
	}, false);

	source.addEventListener('open', function(e) {
	}, false);

	source.addEventListener('error', function(e) {
	}, false);

	// scopes
    $scope.sendData = function() {
        $http({
            method: 'POST',
            headers: {
                'Content-Type': 'text/plain',
            },
            url: 'data/receive',
            data: $scope.inputText
        });
    };

});