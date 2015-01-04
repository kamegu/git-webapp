<#import "/WEB-INF/templates/settings/macro.ftl" as settings/>

<@layout.html jsFiles=['app/admin-app.js']>
  <@settings.menu selectedMenu='users'>
    <div ng-app="adminApp">
      <div ng-view></div>
    </div>
  </@settings.menu>
</@layout.html>
