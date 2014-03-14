var sharedTextApp = angular.module('sharedTextApp', ['textAngular']);

sharedTextApp.controller('SharedTxtViewCtrl', function($scope, $http) {
    // Event Listeners
    var source = new EventSource('api/stream');

    // Update from Server's event
    source.addEventListener('message', function(e) {
        $scope.$apply(function() {
             var temp = JSON.parse(e.data);
             //temp.replace("\n", "<br>");
             $scope.text = temp;

        });
    }, false);

    source.addEventListener('open', function(e) {
    }, false);

    source.addEventListener('error', function(e) {
    }, false);

    $scope.text = "";
});

// Object to keep track of all the utterances
sharedTextApp.factory('db', function() {
    var str = "";
    var modify = {};

    modify.append = function(newVal) {
        str = str.concat(newVal);
        return "appended";
    };

    modify.store = function(newVal) {
        str = newVal;
        return "stored";
    };

    modify.getString = function() {
        return str;
    }

    return modify;
});

// Controller
sharedTextApp.controller('SharedTxtCtrl', function($scope, $http, $filter, $sce, db) {
    
    // Event Listeners
    var source = new EventSource('api/stream');

    // Update from Server's event
    source.addEventListener('message', function(e) {
        $scope.$apply(function() {
            //db.append(JSON.parse(e.data));
            var index = 0;

            var dataJSON = JSON.parse(e.data);

            for (var utteranceId in dataJSON) {
                var utterance = dataJSON[utteranceId];
                // add in this code for when we have options.
               for (var optionId in utterance) {
                    var option = utterance[optionId];
                    db.append(option.text);
               }
            }

            $scope.htmlcontent = db.getString();

        });
    }, false);

    source.addEventListener('open', function(e) {
    }, false);

    source.addEventListener('error', function(e) {
    }, false);

    // Function for the input box to send data to server
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

    // Function for the textAngular to send data to server
    $scope.sendData2 = function(text) {
        $http({
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            url: 'api/modify',
            data: text
        });
    };

    $scope.$watch('htmlcontent', function(newVal){
        console.log(newVal);
        db.store(newVal);
        //$scope.sendData2(newVal);
    });

    // Initialization of variables
    $scope.htmlcontent = "";

});
