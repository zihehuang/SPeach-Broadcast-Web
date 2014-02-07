var sharedTextApp = angular.module('sharedTextApp', []);

sharedTextApp.controller('SharedTxtCtrl', function($scope, $http) {
	
	// Event Listeners
	if (!!window.EventSource) {
		var source = new EventSource('data/receive');
	}

	source.addEventListener('message', function(e) {
		$scope.receivedText = e.data;
	}, false);

	source.addEventListener('open', function(e) {
	}, false);

	source.addEventListener('error', function(e) {
	}, false);

	// scopes
    $scope.sendData = function() {
        $http.post('data/receive', $scope.inputText);
    };

});