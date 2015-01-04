(function(){
  'use strict';

  angular.module('defaultApp')
  .controller('LoginCtrl', ['Login', '$window', LoginCtrl]);

  function LoginCtrl(Login, $window) {
    this.data = {};
    this._Login = Login;
    this._window = $window;
  }
  LoginCtrl.prototype.login = function() {
    var vm = this;
    vm.loginPost = vm._Login.login(vm.data, function(data){
      var result = data.object;
      if (result === 'ok') {
        vm._window.location.href = vm.redirectUrl || vm._window.context.appPath;
      } else {
        vm.formError = 'Username or Password is invalid';
      }
    });
  }

})();
