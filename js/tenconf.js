function deleteTen(event) {
    let id = event.target.value;
    if(
        !id
    ){
        alert('Invalid Tenant');
    }
    $.get('/mailtraffic/api/deletetenant?id='+id)
    .then(resp=>{
        alert(resp.message);
        location.reload();
    }).catch(err=>alert('Not able to Delete Tenant...'))
}