'use strict';

(function(){
  angular.module('repoApp')
  .factory('Issue', ['$resource', 'repoContext', Issue])
  .factory('Comment', ['$resource', 'repoContext', Comment])
  .factory('Label', ['$resource', 'repoContext', Label]);

  function Issue($resource, repoContext) {
    return $resource(repoContext.api + '/issues/:issueId', {}, {
      query: {method: 'GET', url: repoContext.api + '/issues/list.json', isArray: true},
      save: {method: 'POST', url: repoContext.api + '/issues'},
      saveLabels: {method: 'POST', url: repoContext.api + '/issues/:issueId/labels'},
      merge: {method: 'POST', url: repoContext.api + '/pulls/:issueId/merge'},
      close: {method: 'POST', url: repoContext.api + '/issues/:issueId/close'}
    });
  }

  function Comment($resource, repoContext) {
    return $resource(repoContext.api + '/issues/:issueId/comments');
  }

  function Label($resource, repoContext) {
    // use query, save
    return $resource(repoContext.api + '/labels/:name');
  }
})();
