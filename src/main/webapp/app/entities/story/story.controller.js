(function() {
    'use strict';

    angular
        .module('mediumApp').controller('StoryController', StoryController);

    StoryController.$inject = ['$scope', '$state', 'DataUtils', 'Story', 'StorySearch', 'ParseLinks', 'AlertService','$resource','Principal'];

    function StoryController ($scope, $state, DataUtils, Story, StorySearch, ParseLinks, AlertService, $resource,Principal) {
        var vm = this;
        /*
        *
        *
        *
        */
        vm.stories = [];
        vm.loadPage = loadPage;
        vm.page = 0;
        vm.links = {
            last: 0
        };
        vm.predicate = 'id';
        vm.reset = reset;
        vm.reverse = true;
        vm.clear = clear;
        vm.loadAll = loadAll;
        vm.search = search;
        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;
        /*
        *new
        */
        vm.account = null;
        vm.isSaving=true;
    		vm.isAuthenticated=null;
    		vm.save = save;
    		//
    		vm.info= null;

    		//
        loadAll();
        //getAccount();

        function loadAll () {
            if (vm.currentSearch) {
                StorySearch.query({
                    query: vm.currentSearch,
                    page: vm.page,
                    size: 1,
                    sort: sort()
                }, onSuccess, onError);
            } else {
                Story.query({
                    page: vm.page,
                    size: 1,
                    sort: sort()
                }, onSuccess, onError);
            }
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }

            function onSuccess(data, headers) {

                vm.links = ParseLinks.parse(headers('link'));
                console.log(vm.links);
                vm.totalItems = headers('X-Total-Count');
                console.log(vm.totalItems);
                for (var i = 0; i < data.length; i++) {
                    vm.stories.push(data[i]);
                }
            }

            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function reset () {
            vm.page = 0;
            vm.stories = [];
            loadAll();
        }

        function loadPage(page) {
            vm.page = page;
            loadAll();
        }

        function clear () {
            vm.stories = [];
            vm.links = {
                last: 0
            };
            vm.page = 0;
            vm.predicate = 'id';
            vm.reverse = true;
            vm.searchQuery = null;
            vm.currentSearch = null;
            vm.loadAll();
        }//2016-11-28 09:30:34.933

        function search (searchQuery) {
            if (!searchQuery){
                return vm.clear();
            }
            vm.stories = [];
            vm.links = {
                last: 0
            };
            vm.page = 0;
            vm.predicate = '_score';
            vm.reverse = false;
            vm.currentSearch = searchQuery;
            vm.loadAll();
        }

        function getAccount(){
          Principal.identity().then(function(account){
            vm.account = account;
            vm.isAuthenticated = Principal.isAuthenticated;
            var Info= $resource('api/authors'+vm.account.id,{},{'charge':{method:'GET'}});
            $scope.info=Info.get({activated: true});
            $scope.info.$promise.then(function(data){
                vm.info=data;
              });
          });
        }

        function save(){
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

            //timeCreated
            var d = new Date();
            vm.story.timeCreated=d.getFullYear()+"-"+d.getMonth()+"-"+d.getDate();
            vm.story.numberOfLove=0;
            vm.story.numberOfComment=0;

            vm.isSaving = true;
            if (vm.story.id !== null) {

              Story.update(vm.story, onSaveSuccess, onSaveError);
			        console.log(vm.story);
            } else {
                  Story.save(vm.story, onSaveSuccess, onSaveError);
  		            console.log(vm.story);
            }
      			function onSaveSuccess (result) {
      				$scope.$emit('mediumApp:storyUpdate', result);
      				//$uibModalInstance.close(result);
      				$state.go('story', null, { reload: 'story' });
      				//vm.isSaving = false;
              	}

      			function onSaveError () {
      				vm.isSaving = false;
      			}
          });
          //
        }
    }
})();
