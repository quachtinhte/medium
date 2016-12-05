(function() {
    'use strict';

    angular
        .module('mediumApp')
        .controller('StoryDialogController', StoryDialogController);

    StoryDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Story','$resource'];

    function StoryDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Story,$resource) {
        var vm = this;

        vm.story = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
          var contentLength=vm.story.content.length;
          var titleLength=vm.story.title.length;
          //
          if(titleLength<=0);
          //
          var User = $resource('api/account',{},{'charge':{method:'GET'}});
      		$scope.user=User.get({activated: true});
      		$scope.user.$promise.then(function(data){

            vm.story.author=data.login;
            vm.story.authorName=data.firstName+" "+data.lastName;
            vm.story.summary="xxx";//lấy câu đầu tiên||đoạn văn đầu tiên
            //vm.story.urlImage= "";
            //timeCreated
            var d = new Date();
            //vm.story.timeCreated=d.getFullYear()+"-"+d.getMonth()+"-"+d.getDate();
            vm.story.numberOfLove=0;
            vm.story.numberOfComment=0;
            vm.isSaving = true;
            if (vm.story.id !== null) {
                Story.update(vm.story, onSaveSuccess, onSaveError);
            } else {
                Story.save(vm.story, onSaveSuccess, onSaveError);
            }
          });
        }

        function onSaveSuccess (result) {
            $scope.$emit('mediumApp:storyUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.timeCreated = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
