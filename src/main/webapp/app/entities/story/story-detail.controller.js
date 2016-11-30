(function() {
    'use strict';

    angular
        .module('mediumApp')
        .controller('StoryDetailController', StoryDetailController);

    StoryDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'Story','Comment','$resource'];

    function StoryDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, Story, Comment, $resource) {
        var vm = this;
		
        vm.story = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
		vm.save=save;
		vm.comments=null;
		
		
        var unsubscribe = $rootScope.$on('mediumApp:storyUpdate', function(event, result) {
            vm.story = result;
        });
        $scope.$on('$destroy', unsubscribe);
        //
        function save(){
    			var contentLength=vm.comment.content.length;
    			if (contentLength>0);
    			var User = $resource('api/account',{},{'charge':{method:'GET'}});
          		//console.log(User);
        		$scope.user=User.get({activated: true});
        		$scope.user.$promise.then(function(data){
    				vm.comment.userName=data.firstName+' '+data.lastName;
    				vm.comment.userID=data.id;
    				vm.comment.storyID=vm.story.id;
					var d = new Date();
					//vm.comment.timeCommented=d.getFullYear()+'-'+d.getMonth()+'-'+d.getDate()+' '+ d.getHours() +':'+ d.getMinutes()+':'+d.getSeconds()+'.'+d.getMilliseconds();
    				//console.log(vm.comment);
    				//console.log(data);
    				vm.isSaving = true;
                if (vm.comment.id !== null) {
                    Comment.update(vm.comment, onSaveSuccess, onSaveError);
    		        console.log(vm.comment);
                } else {
                    Comment.save(vm.comment, onSaveSuccess, onSaveError);
    		        console.log(vm.comment);
                }
      			function onSaveSuccess (result) {
      				//$routes.reload()
					console.log("success");
          	}
      			function onSaveError () {
              vm.isSaving = false;
					console.log("fail");
            }
      			//console.log(data);--done
      			}
    	    );
        }
        //
        function loadComment(){
          var order=1;
          var commentURL= 'api/comments/'+vm.story.id+'/'+order;
          var comment = $resource(commentURL,{});
          comment.query({activated:true}).$promise.then(
            function(data){
              vm.comments=data;
              console.log(vm.comments);
            });
    		}
    }
})();
