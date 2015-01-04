<@layout.html ngFiles=['login/loginController.js', 'login/loginService.js']>
  <div class="panel panel-default login-form center-block" ng-app="defaultApp">
    <div class="panel-body" ng-controller="LoginCtrl as Login" <#if url??>ng-init="Login.redirectUrl='${url}'"</#if>>
    <form name="loginForm" ng-submit="Login.login()" novalidate>
      <div class="form-group">
        <label for="loginid">Username:</label>
        <input type="text" class="form-control" name="loginid" ng-model="Login.data.loginid" required/>
      </div>
      <div class="form-group">
        <label for="password">Password:</label>
        <input type="password" class="form-control" name="password" ng-model="Login.data.password" required/>
      </div>
      <div class="form-group">
        <a class="btn btn-success" ng-click="Login.login()" gw-post-button="Login.loginPost">Sign in</a>
        <span class="error-message" ng-bind="Login.formError"></span>
      </div>
    </form>
    </div>
  </div>
</@layout.html>
