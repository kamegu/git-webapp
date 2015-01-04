<#include "./macro.ftl"/>

<#assign repoPath>${context.appPath}/${repo.path}</#assign>

<@layout.html>
  <@repoHead context repo/>
  
  <div class="repo-body row">
    <div class="col-xs-10">
      <div class="list-header clearfix">
        <div class="dropdown pull-left">
          <#assign refs=fileList.refs![]/>
          <button class="btn btn-default btn-small dropdown-toggle" type="button" data-toggle="dropdown">
            <#assign currentRef={'type':'tree', 'shortName':refPath.refName}/>
            <#list refs as ref>
              <#if ref.current>
                <#assign currentRef=ref/>
              </#if>
            </#list>
            <span class="octicon octicon-${(currentRef.type=='tag')?string('tag', 'git-branch')}"></span>
            <span class="type">${currentRef.type}:</span>
            <span class="name"><#if currentRef.type=='tree' && (currentRef.shortName?length > 10)>${currentRef.shortName?substring(0, 10)}<#else>${currentRef.shortName}</#if></span>
            <span class="caret"></span>
          </button>
          <ul class="dropdown-menu">
            <#list refs as ref>
              <li <#if ref.current> class="selected"</#if>><a href="${repoPath}/tree/${ref.name}"><span class="octicon octicon-check"></span><#if ref.type=='tag'><span class="octicon octicon-tag"></span></#if>${ref.shortName}</a></li>
            </#list>
          </ul>
        </div>
        <div class="pull-left repo-current-paths">
          <span class="path"><a href="${repoPath}/tree/${refPath.refName}">${repo.name}</a></span>
          <#assign absolutePath=""/>
          <#list refPath.path?split('/') as path>
            <#if path?has_content>
              <#assign absolutePath=absolutePath+"/"+path/>
              <span class="path"><a href="${repoPath}/tree/${refPath.refName}${absolutePath}">${path}</a></span>
            </#if>
          </#list>
        </div>
      </div>

      <table class="table repo-files">
        <thead>
          <tr>
            <td colspan="4" class="commit-message">
              ${fileList.commit.message}
            </td>
          </tr>
          <tr>
            <td colspan="4" class="commit-detail">
              <b>${fileList.commit.author}</b>
              <span class="text-muted">authored on ${fileList.commit.timestamp?string('yyyy-MM-dd HH:mm:ss')}</span>
              <div class="pull-right"><span class="text-muted">latest commit: </span>${fileList.commit.getId(10)}</div>
            </td>
          </tr>
        </thead>
        <tbody>
          <#if refPath.path?has_content>
            <tr>
              <td class="icon"></td>
              <td class="content" title="parent directory">
                <#if refPath.parentPath?? || (refPath.refName != repo.defaultBranchName)>
                  <a href="${repoPath}/tree/${refPath.refName}/${refPath.parentPath!''}">..</a>
                <#else>
                  <a href="${repoPath}">..</a>
                </#if>
              </td>
              <td class="message"></td>
              <td class="timestamp"></td>
            </tr>
          </#if>
          <#list fileList.files as file>
            <tr>
              <td class="icon">
                <#if file.directory><span class="octicon octicon-file-directory"></span>
                <#else><span class="octicon octicon-file-text"></span></#if>
              </td>
              <td class="content">
                <#if file.directory><a href="${repoPath}/tree/${refPath.refName}/${file.path}">${file.name}</a>
                <#else><a href="${repoPath}/blob/${refPath.refName}/${file.path}">${file.name}</a></#if>
              </td>
              <td class="message text-muted"><a href="${repoPath}/commit/${file.commit.commitId}">${file.commit.message}</a></td>
              <td class="timestamp text-muted" title="${file.commit.timestamp?string('yyyy-MM-dd HH:mm:ss')}">${file.commit.latest}</td>
            </tr>
          </#list>
        </tbody>
      </table>
    </div>
    <div class="col-xs-2">
      <div class="list-group">
        <a class="list-group-item active" href="${repoPath}"><span class="octicon octicon-code"></span>Code</a>
        <a class="list-group-item" href="${repoPath}/issues"><span class="octicon octicon-issue-opened"></span>Issue</a>
        <a class="list-group-item" href="${repoPath}/pulls"><span class="octicon octicon-git-pull-request"></span>Pull Requests</a>
        <#if repo.collaborator>
          <a class="list-group-item" href="${repoPath}/settings"><span class="octicon octicon-tools"></span>Settings</a>
        </#if>
      </ul>
    </div>
  </div>
  
</@layout.html>
