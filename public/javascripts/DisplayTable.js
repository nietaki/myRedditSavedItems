window.DisplayTable = function() {
    var app = angular.module('savedItems', ["ngTable"]);

    app.filter('ellipsis', function () {
        return function (text, length) {
            if(!text) {
                return "";
            }
            if (text.length > length) {
                return text.substr(0, length) + '...';
            }
            return text;
        }
    });
    
    app.filter('yesNo', function() {
        return function(bool) {
            if(!!bool) {
                return "yes";
            }
            return "no";
        }  
    });
    
    app.filter('orDefault', function() {
        return function(value, defaultValue) {
            if(!!value) {
                return value;
            }
            return defaultValue; 
        }
    });
    
    app.controller("displayTableCtrl", function($scope, NgTableParams) {
        var self = this;
        $scope.loaded = false;
        var data = [{name: "Moroni", age: 50} /*,*/];
        self.tableParams = new NgTableParams({}, { 
            dataset: [] 
        });
        
         
        $(function() {
            RedditCommunicator.retrieveSavedItemsWithCallback(function(data) {
                self.tableParams.settings({dataset: data});
                self.tableParams.reload();
                $scope.loaded = true;
            });
        }); 
        
    });
    
    return {app: app};
}();