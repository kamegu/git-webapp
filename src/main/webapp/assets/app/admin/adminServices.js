(function(){
  'use strict';
  angular.module('defaultApp')
  .factory('User', ['$resource', 'gwContext', User])
  .factory('Group', ['$resource', 'gwContext', Group])
  .service('SharedService', [SharedService])
  .service('UserService', ['gwContext', '$location', '$routeParams', UserService]);

  function User($resource, gwContext){
    return $resource(gwContext.apiPath + '/settings/admin/users/:userName', {}, {
      query: {method:'GET', url: gwContext.apiPath + '/settings/admin/users/list.json', isArray:true}
    });
  }

  function Group($resource, gwContext){
    return $resource(gwContext.apiPath + '/settings/admin/groups/:userName');
  }

  function SharedService() {
    this.menu = 'profile';
  }

  function UserService(gwContext, $location, $routeParams) {
    this._gwContext = gwContext;
    this._location = $location;
    this._routeParams = $routeParams;
  }
  UserService.prototype.goListPage = function() {
    this._location.path(this._gwContext.appPath + '/settings/admin/users');
  }
  UserService.prototype.getUserName = function() {
    return this._routeParams.userName;
  }
})();
