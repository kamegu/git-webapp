(function(){
  'use strict';

  angular.module('defaultApp')
  .factory('Login', ['$resource', 'gwContext', Login]);

  function Login($resource, gwContext){
    return $resource(gwContext.apiPath + '/login', {}, {
      login: {method:'POST', url: gwContext.apiPath + '/login'}
    });
  }
})();
