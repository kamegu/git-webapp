'use strict';

(function(){
  angular.module('repoApp')
  .service('SettingContext', [SettingContext])
  .factory('Collaborator', ['$resource', 'repoContext', Collaborator]);

  function SettingContext() {
    this.selectedMenu = 'options';
  }

  function Collaborator($resource, repoContext){
    return $resource(repoContext.api + '/settings/collaborators', {}, {
      del: {method: 'POST', url: repoContext.api + '/settings/collaborators/:collaboratorName/delete'}
    });
  }
})();

