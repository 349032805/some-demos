angular.module('app')

.directive('accordion', function() {
    return {
        transclude: true,
        template: '<div class="accordion" ng-transclude></div>',
        scope: {
        },
        controller: [function() {
            var vm = this;

            // This array keeps track of the accordion sections
            vm.sections = [];

            // Ensure that all the groups in this accordion are closed
            this.closeOthers = function(openSection) {
                angular.forEach(vm.sections, function(section) {
                    if (section !== openSection) {
                        section.close();
                    }
                });
            };

            // This is called from the accordion-section directive to add itself to the accordion
            this.addSection = function(sectionScope) {
                vm.sections.push(sectionScope);

                sectionScope.$on('$destroy', function() {
                    vm.removeSection(sectionScope);
                });
            };

            // This is called from the accordion-section directive when to remove itself
            vm.removeSection = function(section) {
                var index = vm.sections.indexOf(section);
                if (index !== -1) {
                    vm.sections.splice(index, 1);
                }
            };
        }]
    };
})

.directive('accordionSection', function() {
    return {
        require: '^accordion',     // We need this directive to be inside an accordion
        transclude: true,
        template: '<div class="accordion-section"><div class="title" ng-click="toggleOpen()">{{sectionTitle}}</div><div class="content animate-if" ng-if="isOpen">test</div></div>',
        scope: {
            sectionTitle: '@',
            isOpen: '=?'
        },
        controller: function($scope) {
            var vm = this;

            $scope.close = function() {
                $scope.isOpen = false;
            };
        },
        link: function(scope, element, attrs, accordionCtrl) {
            accordionCtrl.addSection(scope);

            scope.toggleOpen = function() {
                scope.isOpen = !scope.isOpen;

                if (scope.isOpen) {
                    accordionCtrl.closeOthers(scope);
                }
            };
        }
    };
});