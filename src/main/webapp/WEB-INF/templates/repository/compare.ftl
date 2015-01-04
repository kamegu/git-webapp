<#include "./macro.ftl"/>

<@layout.html ngFiles=['repository/compare/compareController.js', 'repository/compare/compareService.js']>
  <@repoHead context repo/>

  <div ng-app="repoApp">
    <div class="repo-body row" ng-controller="MainCtrl as Main" ng-init="Main.compare.init('${compare.baseBranch}', '${compare.compareBranch}');">
      <div class="col-xs-11">
        <div class="panel panel-default compare-range-editor">
          <div class="panel-body" style="padding: 6px 15px;">
            <div class="pull-left" style="margin-right: 10px;">
              <span class="octicon octicon-git-compare"></span>
            </div>
            <div class="pull-left" ng-hide="Main.compare.edit">
              <span class="icon-label label"><span class="octicon octicon-git-branch"></span>{{Main.compare.baseBranch}}</span>
              ...
              <span class="icon-label label"><span class="octicon octicon-git-branch"></span>{{Main.compare.compareBranch}}</span>
            </div>
            <div class="pull-left" ng-show="Main.compare.edit">
              <div class="btn-group">
                <button class="btn btn-default dropdown-toggle" type="button" data-toggle="dropdown">
                  base: {{Main.compare.baseBranch}}
                  <span class="caret"></span>
                </button>
                <ul class="dropdown-menu">
                  <li ng-repeat="branch in Main.branchs" ng-class="{selected: Main.compare.baseBranch==branch.name}">
                    <a href="#" ng-click="Main.compare.setBaseBranch(branch);"><span class="octicon octicon-check"></span>{{branch.name}}</a>
                  </li>
                </ul>
              </div>
              ...
              <div class="btn-group">
                <button class="btn btn-default dropdown-toggle" type="button" data-toggle="dropdown">
                  compare: {{Main.compare.compareBranch}}
                  <span class="caret"></span>
                </button>
                <ul class="dropdown-menu" role="menu" >
                  <li ng-repeat="branch in Main.branchs" ng-class="{selected: Main.compare.compareBranch==branch.name}">
                    <a href="#" ng-click="Main.compare.setCompareBranch(branch);"><span class="octicon octicon-check"></span>{{branch.name}}</a>
                  </li>
                </ul>
              </div>
            </div>
            <div class="pull-right">
              <span class="octicon octicon-x" style="cursor: pointer;" ng-show="Main.compare.edit" ng-click="Main.compare.edit=false;"></span>
              <a class="btn btn-default btn-sm" ng-hide="Main.compare.edit" ng-click="Main.compare.edit=true;">Edit</a>
            </div>
          </div>
        </div><#-->range-editor</#-->
          
        <div ng-view></div>

      </div>

      <div class="col-xs-1" gw-repository-sidemenu gw-current="''"></div>
    </div>
  </div>

</@layout.html>
