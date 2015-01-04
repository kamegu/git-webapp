<#include "./macro.ftl"/>

<@layout.html ngFiles=['repository/setting/settingControllers.js', 'repository/setting/settingServices.js']>
  <@repoHead context repo/>
  
  <div ng-app="repoApp">
    <div class="repo-body row" ng-controller="SettingMainCtrl as Main">
      <div class="col-xs-3">
        <div class="list-group">
          <a class="list-group-item" ng-class="{active: Main.settingContext.selectedMenu=='options'}" href="{{Main.repoContext.path}}/settings/options">Options</a>
          <a class="list-group-item" ng-class="{active: Main.settingContext.selectedMenu=='collaborators'}" href="{{Main.repoContext.path}}/settings/collaborators">Collaborators</a>
        </div>
      </div>
      <div class="col-xs-8">
        <div ng-view></div>
      </div>
      <div class="col-xs-1" gw-repository-sidemenu gw-current="'setting'"></div>
    </div>
  </div>

</@layout.html>
