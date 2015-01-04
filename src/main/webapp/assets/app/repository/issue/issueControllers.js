'use strict';

(function(){
  angular.module('repoApp')
  .controller('IssueListCtrl', ['repoContext', 'Issue', IssueListCtrl])
  .controller('PullListCtrl', ['repoContext', 'Issue', PullListCtrl])
  .controller('IssueInputCtrl', ['repoContext', '$location', 'Issue', IssueInputCtrl])
  .config(function($routeProvider, gwContext) {
    var pathPrefix = gwContext.appPath + '/:userName/:reposName';
    var templatePrefix = gwContext.path + '/assets/app/repository/issue/';

    $routeProvider
    .when(pathPrefix + '/issues', {
      templateUrl: templatePrefix + '/issue-list.tmpl.html',
      controller: 'IssueListCtrl as list'
    })
    .when(pathPrefix + '/pulls', {
      templateUrl: templatePrefix + '/issue-list.tmpl.html',
      controller: 'PullListCtrl as list'
    })
    .when(pathPrefix + '/issues/new', {
      templateUrl: templatePrefix + '/issue-input.tmpl.html',
      controller: 'IssueInputCtrl as Input'
    })
    .when(pathPrefix + '/issues/:issueId', {
      templateUrl: templatePrefix + '/issue-detail.tmpl.html',
      controller: 'IssueDetailCtrl as Detail'
    })
    .when(pathPrefix + '/labels', {
      templateUrl: templatePrefix + '/label-list.tmpl.html',
      controller: 'LabelListCtrl as Labelctrl'
    });
  });

  function IssueListCtrl(repoContext, Issue) {
    var vm = this;
    vm.config = {
        pull: false
    };
    vm.repoContext = repoContext;
    
    vm.issues = Issue.query(vm.config);
  }

  function PullListCtrl(repoContext, Issue) {
    var vm = this;
    vm.config = {
        pull: true
    };
    vm.repoContext = repoContext;

    vm.issues = Issue.query(vm.config);
  }

  function IssueInputCtrl(repoContext, $location, Issue) {
    var vm = this;
    vm.repoContext = repoContext;
    vm._Issue = Issue;
    vm._$location = $location;
  }
  IssueInputCtrl.prototype.saveIssue = function() {
    var vm = this;
    console.log('test');

    vm.post = vm._Issue.save(vm.issue, function(data){
      var issuePK = data.object;
      vm._$location.path(vm.repoContext.path + '/issues/' + issuePK.issueId);
    });
  };

})();

(function(){
  angular.module('repoApp')
  .controller('LabelListCtrl', ['repoContext', 'Label', LabelListCtrl]);

  function LabelListCtrl(repoContext, Label) {
    var vm = this;
    vm.repoContext = repoContext;
    vm._Label = Label;

    var labelPath = vm.repoContext.path + '/labels';
    vm.newLabel = {
        name: '',
        color: '#bbffff'
    };

    vm._loadLabels();
  }
  LabelListCtrl.prototype.createLabel = function() {
    var vm = this;
    vm.createPost = vm._Label.save({}, vm.newLabel, function(){
      vm._loadLabels();
      vm.newLabel.name = '';
    });
  };
  LabelListCtrl.prototype.deleteLabel = function(label) {
    this._updateLabel(angular.extend(label, {'delete': true}));
  };
  LabelListCtrl.prototype.changeLabel = function(label) {
    this._updateLabel(label);
  };
  LabelListCtrl.prototype._loadLabels = function() {
    this.labels = this._Label.query();
  }
  LabelListCtrl.prototype._updateLabel = function(label) {
    var vm = this;
    var data = {name: label.name, color: label.color, 'delete': label['delete']};
    vm.updatePost = vm._Label.save({name: label.orgName}, data, function(){
      vm._loadLabels();
    });
  }

})();

(function(){
  angular.module('repoApp')
  .controller('IssueDetailCtrl', ['Git', 'Issue', 'Comment', 'Label', 'gwContext', '$sce', '$route', '$routeParams', IssueDetailCtrl]);
  
  function IssueDetailCtrl(Git, Issue, Comment, Label, gwContext, $sce, $route, $routeParams) {
    var vm = this;
    this._Issue = Issue;
    this._Comment = Comment;
    this._route = $route;
    this._routeParams = $routeParams;
    this.issue = Issue.get({issueId: $routeParams.issueId}, function(){
      vm.issue.labelNames = vm.issue.labels.map(function(l){
        return l.name;
      });
      if (vm.issue.pull) {
        vm.mergeCheckResult = Git.mergecheck({baseBranch: vm.issue.pullRequest.base.branchName, compareBranch: vm.issue.pullRequest.request.branchName});
      }
    });
    this.comments = Comment.query({issueId: $routeParams.issueId}, function(){
      angular.forEach(vm.comments, function(c) {
        c.content = $sce.trustAsHtml(c.content);
      });
    });
    this.newComment = {};
    this.allLabels = Label.query();
  }
  IssueDetailCtrl.prototype.hasLabel = function(labelName) {
    if (!this.issue || !this.issue.labelNames) {
      return false;
    }
    return this.issue.labelNames.indexOf(labelName) >= 0;
  }
  IssueDetailCtrl.prototype.addComment = function() {
    var vm = this;
    vm.post = vm._Comment.save({issueId: vm._routeParams.issueId}, vm.newComment, function(){
      vm._route.reload();
    });
  };
  IssueDetailCtrl.prototype.closeIssue = function() {
    var vm = this;
    vm.post = vm._Issue.close({issueId: vm._routeParams.issueId}, vm.newComment, function(){
      vm._route.reload();
    });
  };
  IssueDetailCtrl.prototype.merge = function() {
    var vm = this;
    vm.mergePost = vm._Issue.merge({issueId: vm._routeParams.issueId}, vm.mergeComment, function(){
      vm._route.reload();
    });
  };
  IssueDetailCtrl.prototype.registerLabels = function(label) {
    var vm = this;
    var index = this.issue.labelNames.indexOf(label);
    if (index >= 0) {
      vm.issue.labelNames.splice(index, 1);
    } else {
      vm.issue.labelNames.push(label);
    }
    vm.labelPost = this._Issue.saveLabels({issueId: vm._routeParams.issueId}, {labels: vm.issue.labelNames}, function(){
      vm._route.reload();
    });
  }
})();
