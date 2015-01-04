'use strict';

(function(){
  angular.module('repoApp', ['gwRepo', 'ngRoute', 'ngResource'])
  .config(['$locationProvider', function($locationProvider) {
    $locationProvider.html5Mode(true);
  }])
  .run(['$rootScope', 'gwContext', 'repoContext', function($rootScope, gwContext, repoContext){
    $rootScope.appPath = gwContext.appPath;
    $rootScope.repoPath = repoContext.path;
    $rootScope.repoContext = repoContext;
  }]);

  var repoContext = {
      owner: window.context.repo.owner,
      name: window.context.repo.name,
      path: window.context.repo.path,
      api: window.context.app + '/api/' + window.context.repo.owner + '/' + window.context.repo.name,
      allOwners: window.context.repo.allOwners,
      collaborator: window.context.repo.collaborator
  };

  angular.module('gwRepo', ['gwService', 'ngResource'])
  .constant('repoContext', repoContext)
  .factory('Git', ['$resource', 'gwContext', 'repoContext', Git])
  .directive('gwRepositorySidemenu', ['$window', 'repoContext', gwRepositorySidemenu])
  .directive('gwCollaboratorOnly', ['repoContext', gwCollaboratorOnly])
  .directive('gwRepositoryDiff', ['$window', 'repoContext', gwRepositoryDiff])
  ;

  function Git($resource, gwContext, repoContext) {
    return $resource(gwContext.appPath + '/:owner/:repo/git', {owner: repoContext.owner, repo: repoContext.name}, {
      branch: {method: 'GET', url: gwContext.apiPath + '/:owner/:repo/git/branchs', isArray: true},
      log: {method: 'GET', url: gwContext.apiPath + '/:owner/:repo/git/log', isArray: true}, //{ref1, ref2(optional), n(optional)}
      diff: {method: 'GET', url: gwContext.apiPath + '/:owner/:repo/git/diff', isArray: true}, //{ref1, ref2}
      diffMergeBase: {method: 'GET', url: gwContext.apiPath + '/:owner/:repo/git/diff-merge-base', isArray: true}, //{baseRef, compareRef}
      filesAsDiff: {method: 'GET', url: gwContext.apiPath + '/:owner/:repo/git/filesAsDiff', isArray: true}, //{ref}
      mergecheck: {method: 'GET', url: gwContext.apiPath + '/:owner/:repo/git/mergecheck'}
    });
  }

  function gwRepositorySidemenu($window, repoContext) {
    return {
      scope: {
        current: '=gwCurrent'
      },
      link: function(scope, element) {
        scope.repoContext = repoContext;
      },
      templateUrl: $window.context.path + '/assets/app/common/gw-repository-sidemenu.html'
    };
  }

  function gwCollaboratorOnly(repoContext) {
    return {
      link: function(scope, element) {
        element.toggle(repoContext.collaborator);
      }
    };
  }

  function gwRepositoryDiff($window, repoContext) {
    return {
      scope: {
        diffs: '=gwRepositoryDiff'
      },
      link: function(scope, element) {
        scope.repoContext = repoContext;
        scope.changeTypeMap = {
          'ADD': 'added',
          'MODIFY': 'modified',
          'DELETE': 'removed',
          'RENAME': 'renamed'
        };
      },
      templateUrl: $window.context.path + '/assets/app/common/gw-repository-diff.html'
    };
  }

})();
