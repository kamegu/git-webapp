'use strict';

(function(){
  angular.module('defaultApp', ['gwService', 'ngRoute', 'ngResource'])
  .config(['$locationProvider', function($locationProvider) {
    $locationProvider.html5Mode(true);
  }])
  .run(['$rootScope', 'gwContext', function($rootScope, gwContext){
    $rootScope.appPath = gwContext.appPath;
  }]);

  var gwContext = {
    user : window.context.user,
    path : window.context.path,
    appPath : window.context.app,
    apiPath : window.context.app + '/api',
    tmplBase : window.context.path + '/assets/html/',
    redirect : function(path) {
      window.location.href = window.context.app + path;
    },
    getUrl : function(path) {
      return window.context.app + (path || '');
    },
    alert : function(obj) {
      window.alert(obj);
    },
    log : function(obj) {
      window.console.log(obj);
    }
  };

  angular.module('gwService', [])
  .constant('gwContext', gwContext)
  .directive('gwLoginOnly', ['gwContext', '$window', gwLoginOnly])
  .directive('gwLoginOnlyAlternative', ['gwContext', gwLoginOnlyAlternative])
  .directive('gwPostButton', [gwPostButton])
  .directive('gwMarkdownEditor', ['$http', '$sce', '$window', gwMarkdownEditor])
  .factory('GwHttpInterceptor', ['$q', '$window', 'gwContext', GwHttpInterceptor])
  .config(['$httpProvider', function($httpProvider){
    $httpProvider.interceptors.push('GwHttpInterceptor');
  }]);

  function GwHttpInterceptor($q, $window, gwContext) {
    function _showUncontrollableError(errors) {
      $window.alert(errors);
    }

    return {
      'response': function(response) {
        if (response.config.method !== 'GET') {
          var status = response.data.status;
          if (status === 'success') {
          } else {
            _showUncontrollableError('unknown status');
            return $q.reject(response);
          }
        }
        return response;
      },

      // when controllable, set response.customErrors
      // else show message as alert
      'responseError': function(rejection) {
        var status = rejection.data.status;

        if (status === 'forbidden') {
          _showUncontrollableError('not allowed!! ' + rejection.data.messages[0]);
        } else if (status === 'not_found') {
          _showUncontrollableError('no data');
        } else if (status === 'nologin') {
          if (rejection.config.method === 'GET') {
            $window.location.reload();
          } else {
            _showUncontrollableError('please login!!');
          }
        } else if (rejection.config.method === 'GET') {
          _showUncontrollableError('Unexpected error ' + rejection.statusText);
        } else {//POST(, PUT, DELETE)
          // controllable only in this block
          if (status === 'error') {
            rejection.customErrors = rejection.data.errors || 'error occured';
          } else if (status === 'validation') {
            var errors = [];
            angular.forEach(rejection.data.errors, function(err){
              errors.push(err.name + ' ' + err.message);
            });
            rejection.customErrors = errors;
            rejection.validationErrors = rejection.data.errors;
          } else {
            _showUncontrollableError('Unexpected error ' + rejection.statusText);
          }
          if (rejection.customErrors) {
            if (!rejection.config.customErrorHandler) {
              _showUncontrollableError(rejection.customErrors);
            } else if (typeof rejection.config.customErrorHandler == 'function') {
              rejection.config.customErrorHandler(rejection.customErrors);
            }
          }
        }
        return $q.reject(rejection);
      }
    };
  }

  function gwLoginOnly(gwContext, $window) {
    return {
      link: function(scope, element, attrs) {
        var logined = !!gwContext.user;
        element.toggle(logined);
        if (attrs.gwLoginOnly === 'reload' && !logined) {
          $window.location.reload();
        }
      }
    };
  }
  function gwLoginOnlyAlternative(gwContext) {
    return {
      link: function(scope, element) {
        element.toggle(!gwContext.user);
      }
    };
  }


  function gwPostButton() {
    return {
      scope: {
        postingResource: '=gwPostButton',
        alternativeText: '@gwAlternativeText',
        postForm: '=gwPostForm',
        gwDisabled: '=gwDisabled'
      },
      link: function(scope, element, attrs) {
        var originalText = element.html();
        scope.postButtonSaving = function() {
          return !!(scope.postingResource && !scope.postingResource.$resolved);
        }
        scope.formValid = function() {
          var ngForm = scope.postForm || getForm(scope, element.parents('form').attr('name'));
          return !ngForm || ngForm.$valid;
        }
        function getForm(sc, formName) {
          if (sc) {
            return sc[formName] || getForm(sc.$parent, formName)
          } else {
            return undefined;
          }
        }

        scope.$watch("gwDisabled", function(dis){
          setDisabled();
        });
        scope.$watch("formValid()", function(formValid){
          setDisabled();
        });
        scope.$watch("postButtonSaving()", function(saving){
          setDisabled();
          if (saving) {
            element.text(scope.alternativeText || 'updating...');
          } else {
            element.html(originalText);
          }
        });

        function setDisabled() {
          var disabled = scope.gwDisabled || !scope.formValid() || scope.postButtonSaving();
          attrs.$set('disabled', !!disabled);
        }
      }
    };
  }

  function gwMarkdownEditor($http, $sce, $window) {
    return {
      scope: {
        content: '=gwContent',
        gwRequired: '=gwRequired'
      },
      link: function(scope, element) {
        scope.preview = function(){
          scope.previewing = true;
          scope.previewHtml = $sce.trustAsHtml('loading...');
          $http.post($window.context.app + '/api/markdown', scope.content)
          .success(function(data){
            scope.previewHtml = $sce.trustAsHtml(data);
          });
        }
      },
      templateUrl: $window.context.path + '/assets/app/common/gw-markdown-editor.html'
    };
  }
})();
