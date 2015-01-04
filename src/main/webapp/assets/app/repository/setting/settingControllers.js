'use strict';

(function(){
  angular.module('repoApp')
  .controller('SettingMainCtrl', ['repoContext', 'SettingContext', SettingMainCtrl])
  .controller('OptionCtrl', ['SettingContext', OptionCtrl])
  .controller('CollaboratorCtrl', ['SettingContext', 'Collaborator', '$route', CollaboratorCtrl])
  .config(['$routeProvider', 'gwContext', 'repoContext',
    function($routeProvider, gwContext, repoContext) {
      var templatePrefix = gwContext.path + '/assets/app/repository/setting';
      var pathPrefix = gwContext.path + '/:userName/:reposName/settings';

      $routeProvider
      .when(pathPrefix, {
        templateUrl: templatePrefix + '/option.tmpl.html',
        controller: 'OptionCtrl as Option'
      })
      .when(pathPrefix + '/options', {
        templateUrl: templatePrefix + '/option.tmpl.html',
        controller: 'OptionCtrl as Option'
      })
      .when(pathPrefix + '/collaborators', {
        templateUrl: templatePrefix + '/collaborator.tmpl.html',
        controller: 'CollaboratorCtrl as CollaboratorCtrl'
      });
    }
  ]);

  function SettingMainCtrl(repoContext, SettingContext) {
    var vm = this;
    vm.repoContext = repoContext;
    vm.settingContext = SettingContext;
  }

  function OptionCtrl(SettingContext) {
    SettingContext.selectedMenu = 'options';
    
  }

  function CollaboratorCtrl(SettingContext, Collaborator, $route) {
    SettingContext.selectedMenu = 'collaborators';
    var vm = this;
    vm.Collaborator = Collaborator;
    vm._$route = $route;

    vm.collaborators = Collaborator.query();
  }
  CollaboratorCtrl.prototype.add = function() {
    var vm = this;
    vm.saving = true;
    var post = vm.Collaborator.save({}, vm.name, function(){
      vm._$route.reload();
    }, function(){
      vm.saving = false;
    });
  }
  CollaboratorCtrl.prototype.del = function(collaboratorName) {
    var vm = this;
    vm.saving = true;
    var post = vm.Collaborator.del({collaboratorName: collaboratorName}, {}, function(){
      vm._$route.reload();
    }, function(){
      vm.saving = false;
    });
  }

})();

