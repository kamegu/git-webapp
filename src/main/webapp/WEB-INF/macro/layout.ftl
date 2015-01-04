<#macro html jsFiles=[] ngFiles=[]>
<!DOCTYPE html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>${context.appName!'Git webapp'}<#if repo??> ${repo.ownerName}/${repo.name}</#if></title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<base href="${context.appPath}"/>

<link rel="stylesheet" href="${context.path}/assets/lib/bootstrap-3.2.0/css/bootstrap.css">
<link rel="stylesheet" href="${context.path}/assets/lib/octicons/octicons.css">
<link rel="stylesheet" href="${context.path}/assets/css/gitapp.css">

<!--[if lt IE 9]>
<script src="//cdn.jsdelivr.net/html5shiv/3.7.2/html5shiv.min.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->

<script>
var context = {
  <#if repo??>
    repo: {
      owner: '${repo.ownerName}',
      name: '${repo.name}',
      path: '${context.appPath}/${repo.path}',
      collaborator: ${repo.collaborator?string},
      allOwners: [<#list repo.allOwners as owner>"${owner}"<#if owner_has_next>, </#if></#list>]
    },
  </#if>
  path: '${context.path}',
  app: '${context.appPath}',
  user: '${(session.USER.name)!''}'
};
</script>
<script src="${context.path}/assets/lib/jquery-1.11.1.min.js"></script>
<script src="${context.path}/assets/lib/bootstrap-3.2.0/js/bootstrap.min.js"></script>
<script src="${context.path}/assets/lib/angular-1.3.3/angular.min.js"></script>
<script src="${context.path}/assets/lib/angular-1.3.3/angular-resource.min.js"></script>
<script src="${context.path}/assets/lib/angular-1.3.3/angular-route.min.js"></script>

<script src="${context.path}/assets/js/gitapp.js"></script>
<script src="${context.path}/assets/app/angular-gitwebapp.js"></script>
<#if repo??>
  <script src="${context.path}/assets/app/angular-gitwebapp-repo.js"></script>
</#if>
<#list jsFiles![] as jsFile>
  <script src="${context.path}/assets/js/${jsFile}"></script>
</#list>
<#list ngFiles![] as ngFile>
  <script src="${context.path}/assets/app/${ngFile}"></script>
</#list>
</head>
<body>
  <nav class="navbar navbar-default gw-header" role="navigation">
    <div class="container-fluid">
      <div class="navbar-header">
        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
          <span class="sr-only">Toggle navigation</span>
          <span class="icon-bar"></span>
          <span class="icon-bar"></span>
          <span class="icon-bar"></span>
        </button>
        <a class="navbar-brand" href="${context.path}/">${context.appName!'Git webapp'}</a>
      </div>
      <div class="collapse navbar-collapse">
        <#if (session.USER.name)??>
          <ul class="nav navbar-nav navbar-right">
            <li><a href="${context.path}/${session.USER.name}">
              <span class="glyphicon glyphicon-user"></span>${session.USER.name}
            </a></li>
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown"><span class="octicon octicon-plus"></span><span class="caret"></span></a>
              <ul class="dropdown-menu" role="menu">
                <li><a href="${context.path}/repository/new">New Repository</a></li>
                <li><a href="#">New Group</a></li>
              </ul>
            </li>
            <li><a href="${context.path}/settings/profile" data-toggle="tooltip" data-placement="bottom" title="settings">
              <span class="octicon octicon-gear"></span>
            </a></li>
            <li><a href="${context.path}/logout" data-toggle="tooltip" data-placement="bottom" title="logout">
              <span class="octicon octicon-sign-out"></span>
            </a></li>
          </ul>
        <#else>
          <div class="nav navvar-nav navbar-right">
            <a href="${context.path}/login?url=${context.fullPathWithQuery?url}" class="btn btn-default navbar-btn">Sign In</a>
          </div>
        </#if>
      </div>
    </div>
  </nav>

  <div class="container">
    <#nested/>
  </div>
</body>
</html>
</#macro>
