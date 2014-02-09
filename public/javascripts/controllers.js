var sharedTextApp = angular.module('sharedTextApp', ["xeditable"]);

sharedTextApp.run(function(editableOptions) {
  editableOptions.theme = 'bs3'; // bootstrap3 theme. Can be also 'bs2', 'default'
});

sharedTextApp.controller('SharedTxtCtrl', function($scope, $http) {
	
	// Event Listeners
    var source = new EventSource('data/get');

	source.addEventListener('message', function(e) {
	    $scope.$apply(function() {
	        $scope.receivedText = e.data;
	        //$scope.editables.push({name: e.data});
	    });
	}, false);

	source.addEventListener('open', function(e) {
	}, false);

	source.addEventListener('error', function(e) {
	}, false);

	// scopes
    $scope.sendData = function(data) {
        $http({
            method: 'POST',
            headers: {
                'Content-Type': 'text/plain',
            },
            url: 'data/receive',
            data: data
        });
    };

    $scope.editables = [
    	{name: 'Edit Me'},
    	{name: 'Edit Me too'}
    ];

});
