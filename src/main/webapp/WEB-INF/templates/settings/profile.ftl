<#import "/WEB-INF/templates/settings/macro.ftl" as settings/>

<@layout.html>
  <@settings.menu selectedMenu='profile'>
    <div class="panel panel-default">
      <div class="panel-heading">Profile</div>
      <div class="panel-body">
<#-->
        <form action="${context.appPath}/settings/profile" method="POST">
          <div class="form-group">
            <label for="name">Name</label>
            <input type="text" name="name" id="name" style="width: 95%"/>
          </div>
          <div class="form-group">
            <label for="email">Email</label>
            <input type="text" name="email" id="email" style="width: 95%"/>
          </div>
          <div class="form-group">
            <input type="submit" class="btn btn-success" value="Update"/>
          </div>
        </form>
</#-->
      </div>
    </div>
  </@settings.menu>
</@layout.html>
