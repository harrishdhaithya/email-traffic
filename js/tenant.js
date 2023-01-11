function createTenant(event) {
    event.preventDefault();
    const tenant = document.getElementById('tenant').value;
    if(
        !tenant || tenant.trim()==''
    ){
        alert('Please enter name of the tenant');
        return;
    }
    const param = new URLSearchParams({tenant});
    $.get('/mailtraffic/api/addtenant?'+param.toString())
    .then(resp=>{
        alert(resp.message);
        location.reload();
    }).catch(err=>{
        alert("Not able to add Tenant")
        location.reload();
    })
}