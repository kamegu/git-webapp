'use strict';

(function(){
  angular.module('repoApp')
  .factory('PullRequest', ['$resource', 'gwContext', PullRequest])
  .service('CompareService', ['$location', '$routeParams', 'repoContext', CompareService]);

  function PullRequest($resource, gwContext) {
    return $resource(gwContext.apiPath + '/:owner/:repo/pulls');
  }

  function CompareService($location, $routeParams, repoContext) {
    var self = this;
    this.edit = true;
    this.baseBranch = 'master';
    this.compareBranch = 'master';

    this.init = function(baseBranch, compareBranch) {
      self.baseBranch = baseBranch;
      self.compareBranch = compareBranch;
    }

    this.setBaseBranch = function(branch) {
      self.baseBranch = branch.name;
      $location.path(repoContext.path + '/compare/' + self.baseBranch + '...' + self.compareBranch);
    };
    this.setCompareBranch = function(branch) {
      self.compareBranch = branch.name;
      $location.path(repoContext.path + '/compare/' + self.baseBranch + '...' + self.compareBranch);
    };

    this.isSame = function() {
      return self.baseBranch == self.compareBranch;
    }
  }
})();
