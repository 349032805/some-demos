var myModule = angular.module('myModule',[]);

myModule.controller('myCtrl',['$scope',function($scope){
	$scope.expanders = [{
		title: 'Click me to expander',
		text: 'Hi there folks,I am the content that what'	
	},{
		title: 'Click this',
		text: 'angular is so powerful.'
	},{
		title: 'Click that',
		text: 'the npm of node.js is so useful.'
	}];
}]);

myModule.directive('accordion',function(){
	return {
		restrict: 'AE',
		replace: true,
		transclude: true,
		template: '<div ng-transclude></div>',
		controller: function(){
			var expanders = [];
			this.gotOpened = function(selectedExpander){
				angular.forEach(expanders,function(expander){
					if(selectedExpander != expander){
						expander.show = false;
					}
				});
			};
			this.addExpander = function(expander){
				expanders.push(expander);
			};
		}
	}
});

myModule.directive('expander',function(){
	return {
		restrict: 'AE',
		replace: true,
		transclude: true,
		require: '^?accordion',
		scope: {
			title: '=expanderTitle'
		},
		template: '<div>' +
				  	'<div class="title" ng-click="toggle()">{{title}}</div>' +
				  	'<div class="body" ng-show="show" ng-transclude></div>' +
				  '</div>',
		link: function(scope,elem,attrs,accordionCtrl){
			scope.show = false;
			accordionCtrl.addExpander(scope);
			scope.toggle = function(){
				scope.show = !scope.show;
				accordionCtrl.gotOpened(scope);
			};
		}
	}
});