function updateUser(event){
    event.preventDefault();
    let email = document.getElementById('email').value;
    let password = document.getElementById('password').value;
    if(!password){
        alert('Please Enter a valid password...');
        return;
    }
    let param = new URLSearchParams({email,password});
    $.ajax('/mailtraffic/api/cred?'+param.toString(),{
        method:'PUT'
    }).then(resp=>{
        alert(resp.message);
        history.back();
    }).catch(err=>alert('Not able to update Credential...'));
}

function showpassword() {
    let passwordBox = document.getElementById('password');
    if(passwordBox.type=='password'){
        passwordBox.type='text';
    }else{
        passwordBox.type='password';
    }
}