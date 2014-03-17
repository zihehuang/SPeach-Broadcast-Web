var sharedTextApp = angular.module('sharedTextApp', ["monospaced.elastic"]);

sharedTextApp.controller('SharedTxtViewCtrl', function($scope, $http, $location, $anchorScroll) {
    // Event Listeners
    var source = new EventSource('api/transcript');

    // Update from Server's event
    source.addEventListener('message', function(e) {
        $scope.$apply(function() {
            // hacky solution for SSE not sending newlines: use tabs instead, so we need to replace tabs here.
            var transcriptWithNewLines = e.data.replace(/\t/g, "\n");

            $scope.transcript = transcriptWithNewLines;
            if ($scope.autoscroll) {
                $location.hash('bottom');
                $anchorScroll();
            }
        });
    }, false);

    source.addEventListener('open', function(e) {
    }, false);

    source.addEventListener('error', function(e) {
    }, false);

    $scope.transcript = "";
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
sharedTextApp.controller('SharedTxtCtrl', function($scope, $http, $timeout, db) {
    // boolean that stops the initial modification http request from occuring.
    var isLoading = true;
    
    // Event Listeners
    var source = new EventSource('api/stream');

    // Update from Server's event
    source.addEventListener('message', function(e) {
        var caretPos = getCaret('volunteer');

        $scope.$apply(function() {
            // hacky solution for SSE not sending newlines: use tabs instead, so we need to replace tabs here.
            var transcriptWithNewLines = e.data.replace(/\t/g, "\n");

            db.append(transcriptWithNewLines);
            // var index = 0;

            // var dataJSON = JSON.parse(e.data);

            // for (var utteranceId in dataJSON) {
            //     var utterance = dataJSON[utteranceId];
            //     // add in this code for when we have options.
            //    for (var optionId in utterance) {
            //         var option = utterance[optionId];
            //         db.append(option.text);
            //    }
            // }

            $scope.htmlcontent = db.getString();
            $timeout(function() {}, 500);

        });
        setCaretPosition('volunteer', caretPos);
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
                'Content-Type': 'text/plain',
            },
            url: 'api/modify',
            data: text
        });
    };

    $scope.$watch('htmlcontent', function(newVal){
        if (!isLoading) {
            console.log(newVal);
            db.store(newVal);
            $scope.htmlcontent = db.getString();
            $scope.sendData2(newVal);
        }
        isLoading = false;
    });

    // Initialization of variables
    $scope.htmlcontent = "";

    function setCaretPosition(elemId, caretPos) {
        var elem = document.getElementById(elemId);

        if(elem != null) {
            if(elem.createTextRange) {
                var range = elem.createTextRange();
                range.move('character', caretPos);
                range.select();
            }
            else {
                if(elem.selectionStart) {
                    elem.focus();
                    elem.setSelectionRange(caretPos, caretPos);
                }
                else
                    elem.focus();
            }
        }
    }

    function getCaret(elemId) {
        var el = document.getElementById(elemId);

        if (el.selectionStart) { 
            return el.selectionStart; 
        }
        else if (document.selection) { 
            el.focus(); 

            var r = document.selection.createRange(); 
            if (r == null) { 
                return 0; 
            } 

            var re = el.createTextRange(), 
            rc = re.duplicate(); 
            re.moveToBookmark(r.getBookmark()); 
            rc.setEndPoint('EndToStart', re); 

            return rc.text.length; 
        }  
        return 0; 
    }

});
