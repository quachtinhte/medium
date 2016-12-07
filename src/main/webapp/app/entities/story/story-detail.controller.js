(function() {


    angular
        .module('mediumApp')
        .controller('StoryDetailController', StoryDetailController)
		;
	/*function($routeProvider) {
    // configure the routes here
})*/

    StoryDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'ParseLinks', 'previousState', 'DataUtils', 'entity', 'Story','Comment','$resource'];

    function StoryDetailController($scope, $rootScope, $stateParams, ParseLinks, previousState, DataUtils, entity, Story, Comment, $resource) {
        var vm = this;

        vm.story = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
    		vm.save=save;
    		vm.comments=[];
        vm.loadPage=loadPage;
		vm.last=0;
		vm.links = {
			last:0
		};
	//loadMore();
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
                  if (vm.comment.id !== null) {//id=>id==null
                      Comment.update(vm.comment, onSaveSuccess, onSaveError);
                      console.log("update");
      		        console.log(vm.comment);
                  } else {
                      Comment.save(vm.comment, onSaveSuccess, onSaveError);
                      console.log("save");
  		                console.log(vm.comment);
                  }
		          //document.getElementById("editForm").reset();
        			function onSaveSuccess (result) {
        				//$routes.reload()
        				console.log("success");
        				loadPage(vm.order+1);
        				//vm.story.numberOfComment+=1;
					var urlStoryUpdate='api/stories/increase/:id';
					var updateSt=$resource(urlStoryUpdate,{});
					updateSt.save(vm.story,function(){
						
						console.log("push success");
						Story.get({id : $stateParams.id}).$promise.then(function(data){
							vm.story=data;
						});
						console.log(Story.get({id : $stateParams.id}).$promise);
          //						
					});
					function onSuccess(){}
					function onError(){}
  					//$route.reload();
            	}
        			function onSaveError () {
                vm.isSaving = false;
                console.log("fail");
              }
      			//console.log(data);--done
      			}
    	    );
        }

        function loadPage(order){
          vm.order = order;
          loadMore();
        }
        //
		function loadIhihi(){
			var commentURL = 'api/comments/'+vm.story.id+'/'+0;

		}
        function loadMore(){
	if(vm.order>0){
          var commentURL = 'api/comments/'+vm.story.id+'/'+vm.order;
          var comment=$resource(commentURL,{},{ charge:{method: 'get', isArray:true}});
			comment.get(function(data){

				vm.comments.push(data);
				//vm.links = ParseLinks.parse(headers('link'));
				//vm.last=vm.comments.length;
				console.log(data);
			}, onSuccess, onError
						);
			function onSuccess(data, headers) {
				//vm.links = ParseLinks.parse(headers('link'));
			}
			function onError(error) {
                  //AlertService.error(error.data.message);
                  console.log(error.data.message);
              }
        }
	}
    }
})();
