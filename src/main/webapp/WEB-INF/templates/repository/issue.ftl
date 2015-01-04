<#include "./macro.ftl"/>

<@layout.html ngFiles=['repository/issue/issueControllers.js', 'repository/issue/issueServices.js']>
  <@repoHead context repo/>
  
  <div ng-app="repoApp">
    <div class="repo-body row">
      <div class="col-xs-11">
        <div ng-view></div>
      </div>
      <div class="col-xs-1" gw-repository-sidemenu gw-current="'issue'"></div>
    </div>
  </div>

</@layout.html>
