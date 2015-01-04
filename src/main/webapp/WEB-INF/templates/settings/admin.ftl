<@layout.html ngFiles=['admin/adminControllers.js', 'admin/adminServices.js']>
<div ng-app="defaultApp">
  <div class="row" ng-controller="MainCtrl as Main">
    <div class="col-xs-3">
      <div class="list-group">
        <a class="list-group-item" ng-class="{'active': Main.shared.menu=='profile'}" href="{{Main.settingPath}}/profile" target="_self">Profile</a>
      </div>
      <#if session.USER.admin>
        <div class="list-group">
          <a class="list-group-item" ng-class="{'active': Main.shared.menu=='system'}" href="{{Main.settingPath}}/admin/system">System Settings</a>
          <a class="list-group-item" ng-class="{'active': Main.shared.menu=='users'}" href="{{Main.settingPath}}/admin/users">User Management</a>
          <a class="list-group-item" ng-class="{'active': Main.shared.menu=='mail'}" href="{{Main.settingPath}}/admin/mail">Notification Mail Settings</a>
        </div>
      </#if>
    </div>
    <div class="col-xs-9">
      <div ng-view></div>
    </div>
  </div>
</div>
</@layout.html>
