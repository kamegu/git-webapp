<#include "./macro.ftl"/>

<@layout.html ngFiles=['repository/commitController.js']>
  <@repoHead context repo/>

  <div class="repo-body row" ng-app="repoApp">

    <div class="col-xs-11">
      <div ng-view></div>
    </div>

    <div class="col-xs-1" gw-repository-sidemenu gw-current="'code'"></div>
  </div>
</@layout.html>
