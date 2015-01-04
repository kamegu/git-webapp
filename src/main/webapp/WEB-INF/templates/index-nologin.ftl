<@layout.html>
nologin
  <div class="row">
    <div class="col-xs-6">
    </div>
    <div class="col-xs-3">
      <div class="panel panel-default">
        <div class="panel-heading">
          Recent Repositories
        </div>
        <div class="list-group">
          <#list recentRepositories as repos>
            <a class="list-group-item" href="${context.appPath}/${repos.pk.accountName}/${repos.pk.repositoryName}">${repos.pk.accountName}/${repos.pk.repositoryName}</a>
          </#list>
        </div>
      </div>
    </div>
    <div class="col-xs-3">
    </div>
  </div>
</@layout.html>
