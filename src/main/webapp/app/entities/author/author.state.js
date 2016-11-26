(function() {
    'use strict';

    angular
        .module('mediumApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('author', {
            parent: 'entity',
            url: '/author',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Authors'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/author/authors.html',
                    controller: 'AuthorController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('author-detail', {
            parent: 'entity',
            url: '/author/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Author'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/author/author-detail.html',
                    controller: 'AuthorDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Author', function($stateParams, Author) {
                    return Author.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'author',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('author-detail.edit', {
            parent: 'author-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/author/author-dialog.html',
                    controller: 'AuthorDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Author', function(Author) {
                            return Author.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('author.new', {
            parent: 'author',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/author/author-dialog.html',
                    controller: 'AuthorDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                email: null,
                                password: null,
                                description: null,
                                birthday: null,
                                urlAvatar: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('author', null, { reload: 'author' });
                }, function() {
                    $state.go('author');
                });
            }]
        })
        .state('author.edit', {
            parent: 'author',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/author/author-dialog.html',
                    controller: 'AuthorDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Author', function(Author) {
                            return Author.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('author', null, { reload: 'author' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('author.delete', {
            parent: 'author',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/author/author-delete-dialog.html',
                    controller: 'AuthorDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Author', function(Author) {
                            return Author.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('author', null, { reload: 'author' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
