(function(){
  'use strict';

  angular.module('repoApp')
  .controller('MainCtrl', [ MainCtrl])
  .config(['$routeProvider', 'gwContext', 'repoContext',
    function($routeProvider, gwContext, repoContext) {
      var templatePrefix = gwContext.path + '/assets/app/repository/';

      //how to include '...' for route path??
      $routeProvider
      .when(repoContext.path + '/commit/:commitId', {
        templateUrl: templatePrefix + '/commit.tmpl.html',
        controller: 'CommitCtrl as Commit'
      })
      .when(repoContext.path + '/tree/:ref*', {
        templateUrl: templatePrefix + '/file.tmpl.html',
        controller: 'FileCtrl as File'
      });
    }
  ]);

  function MainCtrl() {
  }

  function FileCtrl() {
    
  }
})();

(function(){
  'use strict';

  angular.module('repoApp')
  .controller('CommitCtrl', ['Git', '$routeParams', CommitCtrl]);

  function CommitCtrl(Git, $routeParams) {
    var vm = this;
    vm.commits = Git.log({
      ref1: $routeParams.commitId,
      n: 1
    }, function(){
      vm.commit = vm.commits[0];
      if (vm.commit.parentIds && vm.commit.parentIds.length != 0) {
        vm.diffs = Git.diff({
          ref1: vm.commit.parentIds[0],
          ref2: $routeParams.commitId
        });
      } else {
        vm.diffs = Git.filesAsDiff({
          ref: $routeParams.commitId
        });
      }
    });
  }

})();
