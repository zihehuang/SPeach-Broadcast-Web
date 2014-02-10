var sharedTextApp = angular.module('sharedTextApp', ["xeditable"]);

sharedTextApp.run(function(editableOptions) {
  editableOptions.theme = 'bs3'; // bootstrap3 theme. Can be also 'bs2', 'default'
});

sharedTextApp.controller('SharedTxtCtrl', function($scope, $http, $filter) {
	
	// Event Listeners
    var source = new EventSource('api/stream');

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
    $scope.sendData = function() {
        $http({
            method: 'POST',
            headers: {
                'Content-Type': 'text/plain',
            },
            url: 'api/add',
            data: $scope.inputText
        });

        $scope.inputText = ""
    };

    $scope.sendDataFromEditables = function(data) {
        $http({
            method: 'POST',
            headers: {
                'Content-Type': 'text/plain',
            },
            url: 'api/receive',
            data: data
        });
    };

    $scope.editables = [
    	{name: 'Edit Me'}
    ];

    $scope.user = {
        status: 2
    }; 

    $scope.statuses = [
        {value: 1, text: 'status1'},
        {value: 2, text: 'status2'},
        {value: 3, text: 'status3'},
        {value: 4, text: 'status4'}
    ]; 

    $scope.showStatus = function() {
        var selected = $filter('filter')($scope.statuses, {value: $scope.user.status});
        return ($scope.user.status && selected.length) ? selected[0].text : 'Not set';
    };
});
