function createTenant(event) {
    event.preventDefault();
    const tenant = document.getElementById('tenant').value;
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