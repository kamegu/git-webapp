(function(){
  'use strict';
  angular.module('defaultApp')
  .controller('MainCtrl', ['gwContext', 'SharedService', MainCtrl])
  .config(function($routeProvider, gwContext) {
    var templatePrefix = gwContext.path + '/assets/app/admin/';

    $routeProvider
    .when(context.app + '/settings/admin/users', {
      templateUrl: templatePrefix + 'user-list.tmpl.html',
      controller: 'UserListCtrl as ListCtrl',
    })
    .when(context.app + '/settings/admin/users/new', {
      templateUrl: templatePrefix + 'user-input.tmpl.html',
      controller: 'UserInputCtrl as InputCtrl'
    })
    .when(context.app + '/settings/admin/users/:userName/edit', {
      templateUrl: templatePrefix + 'user-input.tmpl.html',
      controller: 'UserInputCtrl as InputCtrl'
    })
    .when(context.app + '/settings/admin/groups/new', {
      templateUrl: templatePrefix + 'group-input.tmpl.html',
      controller: 'GroupInputCtrl as GroupCtrl'
    })
    .when(context.app + '/settings/admin/groups/:userName/edit', {
      templateUrl: templatePrefix + 'group-input.tmpl.html',
      controller: 'GroupInputCtrl as GroupCtrl'
    })
    .when(context.app + '/settings/admin/system', {
      templateUrl: templatePrefix + 'system.tmpl.html',
      controller: 'SystemCtrl as SystemCtrl'
    })
    .when(context.app + '/settings/admin/mail', {
      templateUrl: templatePrefix + 'mail.tmpl.html',
      controller: 'MailCtrl as MailCtrl'
    });
  });

  function MainCtrl(gwContext, SharedService) {
    var vm = this;
    vm.appPath = gwContext.appPath;
    vm.settingPath = gwContext.appPath + '/settings';
    vm.adminPath = gwContext.appPath + '/settings/admin';
    vm.shared = SharedService;
  }

})();

(function(){
  'use strict';
  angular.module('defaultApp')
  .controller('UserListCtrl', ['User', 'SharedService', 'gwContext', UserListCtrl]);

  function UserListCtrl(User, SharedService, gwContext) {
    SharedService.menu = 'users';
    this._User = User;
    this._gwContext = gwContext;
    this.reloadAccounts();
  }
  UserListCtrl.prototype.getEditUrl = function(account) {
    if (account.group) {
      return this._gwContext.appPath + '/settings/admin/groups/' + account.account.name + "/edit";
    } else {
      return this._gwContext.appPath + '/settings/admin/users/' + account.account.name + "/edit";
    }
  }
  UserListCtrl.prototype.reloadAccounts = function() {
    var vm = this;
    vm.accounts = vm._User.query({'deleted': !!vm.showDeleted});
  }
})();

(function(){
  'use strict';
  angular.module('defaultApp')
  .controller('UserInputCtrl', ['User', 'SharedService', 'UserService', UserInputCtrl]);

  function UserInputCtrl(User, SharedService, UserService) {
    SharedService.menu = 'users';
    var vm = this;
    vm._User = User;
    vm._userService = UserService;

    var userName = UserService.getUserName();
    vm.params = {};
    if (!!userName) {
      vm.params.userName = userName;
      vm.user = vm._User.get(vm.params, function(){}, function(){vm.errors = ['load error'];});
    }

    vm.isNew = function() {
      return !userName;
    };
  }
  UserInputCtrl.prototype.saveUser = function() {
    var vm = this;
    vm.post = vm._User.save(vm.params, vm.user, function(){
      vm._userService.goListPage();
    });
  }
})();

(function(){
  'use strict';
  angular.module('defaultApp')
  .controller('GroupInputCtrl', ['User', 'Group', 'SharedService', 'UserService', GroupInputCtrl])

  function GroupInputCtrl(User, Group, SharedService, UserService) {
    SharedService.menu = 'users';
    this._userService = UserService;
    var vm = this;
    vm._Group = Group;

    var userName = UserService.getUserName();
    vm.params = {};
    if (!!userName) {
      vm.params.userName = userName;
    }
    vm.group = vm._Group.get({userName: (userName || 'new')}, function(){}, function(){vm.errors = ['load error'];});

    vm.isNew = function() {
      return !userName;
    };

    vm.addMember = function() {
      if (alreadyAdded()) {
        vm.memberError = 'User has been already added.';
      } else {
        var user = User.get({userName: vm.memberName});
        user.$promise.then(function(data){
          if (data && data.account.name) {
            vm.group.members.push({name: vm.memberName});
            vm.memberName = '';
            vm.memberError = '';
          } else {
            vm.memberError = 'User does not exist.';
          }
        }, function(){
          vm.memberError = 'User does not exist.';
        });
      }
    }

    function alreadyAdded() {
      var has = false;
      angular.forEach(vm.group.members, function(member){
        if (member.name === vm.memberName) {
          has = true;
          return;
        }
      });
      return has;
    }
  }
  GroupInputCtrl.prototype.saveGroup = function() {
    var vm = this;
    vm.post = vm._Group.save(vm.params, vm.group, function(){
      vm._userService.goListPage();
    });
  }
})();

(function(){
  'use strict';
  angular.module('defaultApp')
  .factory('Mail', ['$resource', 'gwContext', Mail])
  .controller('MailCtrl', ['Mail', 'SharedService', MailCtrl]);

  function Mail($resource, gwContext){
    return $resource(gwContext.apiPath + '/settings/admin/mail', {}, {
      test: {method: 'POST', url: gwContext.apiPath + '/settings/admin/mail/test'}
    });
  }

  function MailCtrl(Mail, SharedService) {
    SharedService.menu = 'mail';
    var vm = this;
    this._Mail = Mail;
    this.settings = this._Mail.get();

    this.testMailAddress = '';
  }
  MailCtrl.prototype.save = function(){
    var vm = this;
    vm.savePost = this._Mail.save(this.settings, function(){
      window.location.reload();
    });
  }
  MailCtrl.prototype.sendTestMail = function(){
    var vm = this;
    vm.testing = true;
    var testMail = {
        to: vm.testMailAddress,
        mailSetting: this.settings
    };
    var post = this._Mail.test(testMail, function(){
      vm.testing = false;
      alert('ok');
    }, function(){
      vm.testing = false;
      alert('ng');
    });
  }
  
})();

(function(){
  'use strict';
  angular.module('defaultApp')
  .factory('System', ['$resource', 'gwContext', System])
  .controller('SystemCtrl', ['System', 'SharedService', SystemCtrl]);

  function System($resource, gwContext){
    return $resource(gwContext.apiPath + '/settings/admin/system');
  }

  function SystemCtrl(System, SharedService) {
    SharedService.menu = 'system';
    var vm = this;
    this._System = System;
    this.settings = this._System.get();
  }
  SystemCtrl.prototype.save = function(){
    var vm = this;
    vm.savePost = this._System.save(this.settings, function(){
      window.location.reload();
    });
  }
})();
