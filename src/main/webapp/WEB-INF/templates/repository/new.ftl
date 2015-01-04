<@layout.html>
  <div>
    <form action="${context.appPath}/api/repository/create" method="POST">
      <div class="form-inline form-group">
        <div class="form-group">
          <label for="owner">Owner</label>
          <div class="dropdown">
            <button class="btn btn-default btn-sm dropdown-toggle" data-toggle="dropdown" style="padding: 3px 10px;">
              <span>${session.USER.name}</span>
              <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
              <li><a href="#" data-name="${session.USER.name}">${session.USER.name}</a></li>
            </ul>
            <input type="hidden" name="owner" id="owner" value="${session.USER.name}"/>
          </div>
        </div>
        <div class="form-group" style="margin: 5px; vertical-align: bottom;">/</div>
        <div class="form-group">
          <label for="name">Repository name</label>
          <input type="text" name="name" id="name" style="width: 95%"/>
        </div>
      </div>
      <div class="form-group">
        <label for="description">Description (optional)</label>
        <input type="text" name="description" id="description" style="width: 95%"/>
      </div>
      <div class="radio">
        <label>
          <input type="radio" name="privateRepo" value="false" checked="checked">Public
          <div class="small">All users and guests can read this repository.</div>
        </label>
      </div>
      <div class="radio">
        <label>
          <input type="radio" name="privateRepo" value="true">Private
          <div class="small">Only collaborators can read this repository.</div>
        </label>
      </div>
      <div class="form-group">
        <input type="submit" class="btn btn-success" value="Create repository"/>
        <span class="form-error"></span>
      </div>
    </form>
  </div>

  <script>
  $('form').submit(function(){
    var $form = $(this);
    var form = $(this)[0];
    $.ajax({
      url: form.action,
      type: form.method,
      contentType: 'application/json', // リクエストの Content-Type
      dataType: 'json',
      data: JSON.stringify({
        owner: $('#owner').val(),
        name: $('#name').val(),
        description: $('#description').val(),
        privateRepo: $('input[name=privateRepo]:checked').val()
      }),
      success: function(data) {
        if (data.status == 'success') {
          var repositoryPk = data.object;
          window.location.href = "${context.appPath}/" + repositoryPk.accountName + "/" + repositoryPk.repositoryName;
        } else {
          $form.find('.form-error').html('Error occured');
        }
      },
      error: function(data) {
        console.log(data.responseJSON);
        alert(data.responseJSON.errors || 'error');
      }
    });
    return false;
  });
  </script>
</@layout.html>
