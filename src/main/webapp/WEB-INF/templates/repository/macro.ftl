<#macro repoHead context repo>
  <#local repoPath>${context.appPath}/${repo.path}</#local>
  <div class="repo-header">
    <span class="octicon octicon-repo"></span>
    <a href="${context.appPath}/${repo.ownerName}">${repo.ownerName}</a> / 
    <b><a href="${repoPath}">${repo.name}</a></b>
    <#if repo?? && repo.collaborator>
      <div class="pull-right">
        <button class="btn btn-danger btn-xs" onclick="deleteRepository();">delete</button>
      </div>
    </#if>
  </div>
  <hr>
  <script>
  function deleteRepository() {
    $.ajax({
      url: '${repoPath}/delete',
      type: 'POST',
      dataType: 'json',
      success: function(data) {
        if (data.status==='success') {
          window.location.href = "${context.appPath}/${repo.ownerName}";
        } else {
          alert('error');
        }
      },
      error: function() {
        alert('error');
      }
    });
  }
  </script>
</#macro>
