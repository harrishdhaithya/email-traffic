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
    $.get('/mailtraffic/api/deletetenant?id='+id)
    .then(resp=>{
        alert(resp.message);
        location.reload();
    }).catch(err=>alert('Not able to Delete Tenant...'))
}


function editTen(event) {
    let id = event.target.value;
    location.href = '/mailtraffic/settings/edittenant.jsp?id='+id;
}