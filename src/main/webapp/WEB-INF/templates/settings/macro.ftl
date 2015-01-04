<#macro menu selectedMenu=''>
  <div class="row">
    <div class="col-xs-3">
      <div class="list-group">
        <a class="list-group-item<#if selectedMenu=='profile'> active</#if>" href="${context.appPath}/settings/profile">Profile</a>
      </div>
      <#if session.USER.admin>
        <div class="list-group">
          <a class="list-group-item<#if selectedMenu=='system'> active</#if>" href="${context.appPath}/settings/admin/system">System Settings</a>
          <a class="list-group-item<#if selectedMenu=='users'> active</#if>" href="${context.appPath}/settings/admin/users">User Management</a>
          <a class="list-group-item<#if selectedMenu=='mail'> active</#if>" href="${context.appPath}/settings/admin/mail">Notification Mail Settings</a>
        </div>
      </#if>
    </div>
    <div class="col-xs-9">
      <#nested/>
    </div>
  </div>
</#macro>
