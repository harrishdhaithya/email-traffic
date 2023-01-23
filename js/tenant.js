function createTenant(event) {
    event.preventDefault();
    const tenant = document.getElementById('tenant').value;
    const app_clientid = document.getElementById('app_clientid').value;
    const admin_email = document.getElementById('admin_email').value;
    const admin_password = document.getElementById('admin_password').value;
    if(
        !tenant || tenant.trim()==''||
        !app_clientid || app_clientid.trim()==''
    ){
        alert('Please enter all the required fields...');
        return;
    }
    const param = new URLSearchParams({tenant,app_clientid,admin_email,admin_password});
    $.get('/mailtraffic/api/addtenant?'+param.toString())
    .then(resp=>{
        alert(resp.message);
        location.reload();
    }).catch(err=>{
        alert("Not able to add Tenant")
        location.reload();
    })
}

function showpassword() {
    let passwordBox = document.getElementById('admin_password');
    if(passwordBox.type=='password'){
        passwordBox.type='text';
    }else{
        passwordBox.type='password';
    }
}