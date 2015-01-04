'use strict';

(function(){
  angular.module('repoApp')
  .controller('MainCtrl', ['CompareService', 'Git', MainCtrl])
  .controller('CompareInitCtrl', ['CompareService', CompareInitCtrl])
  .controller('CompareBranchCtrl', ['$routeParams', 'gwContext', 'Git', 'CompareService', 'PullRequest', CompareBranchCtrl])
  .config(['$routeProvider', 'gwContext',
    function($routeProvider, gwContext) {
      var pathPrefix = gwContext.appPath + '/:userName/:reposName';
      var templatePrefix = gwContext.path + '/assets/app/repository/compare';

      //how to include '...' for route path??
      $routeProvider
      .when(pathPrefix + '/compare', {
        templateUrl: templatePrefix + '/compare-init.tmpl.html',
        controller: 'CompareInitCtrl'
      })
  /*
      .when(context.app + '/:userName/:reposName/compare/:ref1*\...:ref2', {
        template: '',
        controller: 'CompareBranchController'
      })
  */
      .when(pathPrefix + '/compare/:ref*', {
        templateUrl: templatePrefix + '/compare.tmpl.html',
        controller: 'CompareBranchCtrl as Comp'
      });
    }
  ]);

  function MainCtrl(CompareService, Git) {
    var vm = this;
    vm.compare = CompareService;
    vm.branchs = Git.branch();
  }

  function CompareInitCtrl(CompareService){
  }

  function CompareBranchCtrl($routeParams, gwContext, Git, CompareService, PullRequest){
    var vm = this;
    vm._gwContext = gwContext;
    vm._PullRequest = PullRequest;

    vm._repoParams = {
     // change when support forked branch
        owner: $routeParams.userName,
        repo: $routeParams.reposName
    };
    vm.pull = {
        title: '',
        content: '',
        baseBranch: CompareService.baseBranch,
        requestUserName: $routeParams.userName,
        requestRepoName: $routeParams.reposName,
        requestBranch: CompareService.compareBranch
    };

    CompareService.edit = false;
    if (!CompareService.isSame()) {
      loadDiffResult();
    }

    function loadDiffResult() {
      vm.commits = Git.log({ref1: CompareService.baseBranch, ref2: CompareService.compareBranch});
      vm.diffs = Git.diffMergeBase({baseRef: CompareService.baseBranch, compareRef: CompareService.compareBranch});

      vm.mergeCheckResult = Git.mergecheck({baseBranch: CompareService.baseBranch, compareBranch: CompareService.compareBranch});
    }
  }
  CompareBranchCtrl.prototype.createPullRequest = function() {
    var vm = this;
    vm.post = vm._PullRequest.save(vm._repoParams, vm.pull, function(data){
      var issuePk = data.object;
      window.location.href = vm._gwContext.appPath + '/' + issuePk.accountName + '/' + issuePk.repositoryName + '/issues/' + issuePk.issueId;
    });
  }

})();
