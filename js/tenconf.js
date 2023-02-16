function deleteTen(event) {
    let id = event.target.value;
    if(
        !id
    ){
        alert('Invalid Tenant');
    }
    let result = confirm('Are You Sure you want to delete?')
    if(!result){
        return;
    }
    $.ajax('/mailtraffic/api/tenant?id='+id,{
        method:'DELETE'
    })
    .then(resp=>{
        alert(resp.message);
        location.reload();
    }).catch(err=>alert('Not able to Delete Tenant...'))
}


function editTen(event) {
    let id = event.target.value;
    location.href = '/mailtraffic/settings/edittenant.jsp?id='+id;
}