var sharedTextApp = angular.module('sharedTextApp', ["xeditable"]);

sharedTextApp.run(function(editableOptions) {
    editableOptions.theme = 'bs3'; // bootstrap3 theme. Can be also 'bs2', 'default'
});

sharedTextApp.factory('db', function() {
    var items = [];
    var modify = {};
    modify.addItem = function(index, item) {
        if (index > items.length - 1)
            items.push({name: item});
        else if (items[index].name != item)
            items[index].name = item;
        return 'added item';
    };
    modify.getItems = function() {
        return items;
    };
    return modify;
});

sharedTextApp.controller('SharedTxtCtrl', function($scope, $http, $filter, db) {
	
	// Event Listeners
    var source = new EventSource('api/stream');

	source.addEventListener('message', function(e) {
	    $scope.$apply(function() {
	        //$scope.receivedText = e.data;
            var index = 0;
            JSON.parse(e.data).forEach(function(text) {
                db.addItem(index, text);
                index++;
            });
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

    
    $scope.sendDataFromEditables = function(index, text) {
        $http({
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            url: 'api/modify',
            data: [index, text]
        });
    };

    $scope.editables = db.getItems();

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
