<#include "./macro.ftl"/>

<#assign repoPath>${context.appPath}/${repo.path}</#assign>

<@layout.html >
  <@repoHead context repo/>

  <div class="repo-body row" ng-app="gwRepo">

    <div class="col-xs-11">
      <div class="list-header clearfix">
        <div class="pull-left repo-current-paths">
          <span class="path"><a href="${repoPath}/tree/${refPath.refName}">${repo.name}</a></span>
          <#assign absolutePath=""/>
          <#list refPath.path?split('/') as path>
            <#if path?has_content>
              <#if path_has_next>
                <#assign absolutePath=absolutePath+"/"+path/>
                <span class="path"><a href="${repoPath}/tree/${refPath.refName}${absolutePath}">${path}</a></span>
              <#else>
                <span>${path}</span>
              </#if>
            </#if>
          </#list>
        </div>
      </div>

      <div class="repo-file">
        <div class="meta clearfix">
          <span></span>
          <div class="pull-right">
            <a href="#" class="btn btn-default btn-xs">History</a>
          </div>
        </div>
        <div class="blob">
          <#if fileContent.binary>
            this is binary file
          <#else>
            <pre>${fileContent.text?html}</pre>
          </#if>
        </div>
      </div>

      <div ng-view></div>
    </div>

    <div class="col-xs-1" gw-repository-sidemenu gw-current="'code'"></div>
  </div>
</@layout.html>
