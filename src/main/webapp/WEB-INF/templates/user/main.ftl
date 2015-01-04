<#assign userPath>${context.appPath}/${account.name}</#assign>

<@layout.html>
  <div class="row">
    <div class="col-xs-4">
      <div>
        <span>${account.name}</span>
      </div>
    </div>
    <div class="col-xs-8">
      <ul class="nav nav-tabs list-header" role="tablist">
        <li<#if (tab!'repository')=='repository'> class="active"</#if>><a href="${userPath}">Repository</a></li>
        <li<#if (tab!'repository')=='activity'> class="active"</#if>><a href="${userPath}?tab=activity">Public Activity</a></li>
      </ul>
      <#if repositories??>
        <#list repositories as repos>
          <div class="clearfix">
            <div class="pull-left" style="color: #bbb;">
              <span class="mega-octicon octicon-repo"></span>
            </div>
            <div class="pull-left" style="margin-left: 10px;">
              <div>
                <a href="${userPath}/${repos.pk.repositoryName}" style="font-size: 20px;"><b>${repos.pk.repositoryName}</b></a>
                <#if repos.privateRepo><span class="octicon octicon-lock"></span></#if>
              </div>
              <#if repos.description?has_content>
                <div>${repos.description}</div>
              </#if>
              <div class="text-muted small">
                Last updated: 
              </div>
            </div>
          </div>
        </#list>
      </#if>
    </div>
  </div>
</@layout.html>
