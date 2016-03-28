window.DisplayTable = function() {
    var app = angular.module('savedItems', ["ngTable"]);
    app.controller("displayTableCtrl", function($scope, NgTableParams) {
        var self = this;
        var data = [{name: "Moroni", age: 50} /*,*/];
        self.tableParams = new NgTableParams({}, { 
            dataset: [] 
        });
        
         
        $(function() {
            RedditCommunicator.retrieveSavedItemsWithCallback(function(data) {
                self.tableParams.settings({dataset: data});
                self.tableParams.reload();
            });
        }); 
    });
    
    return {app: app};
}();