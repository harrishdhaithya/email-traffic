function updateTenant(event) {
    event.preventDefault();
    let id = document.getElementById('id').value;
    let name = document.getElementById('tenant').value;
    let app_clientid = document.getElementById('app_clientid').value;
    let admin_email = document.getElementById('admin_email').value;
    let admin_password = document.getElementById('admin_password').value;
    if(
        !id ||
        !name ||
        !app_clientid 
    ){
        alert('Enter All the required fields...');
        return;
    }
    $.ajax({
        url:'/mailtraffic/api/updatetenant',
        type:'POST',
        data: JSON.stringify({id,name,app_clientid,admin_email,admin_password}),
        processData: false,
        contentType: false,
        cache: false
    }).then(resp=>alert(resp.message))
    .catch(err=>alert('Not able to update Tenant...'));
}

function showpassword() {
    let passwordBox = document.getElementById('admin_password');
    if(passwordBox.type=='password'){
        passwordBox.type='text';
    }else{
        passwordBox.type='password';
    }
}