function disableTask(event) {
    let id = event.target.value;
    if(
        !id
    ){
        alert('Unable to Disable...');
        return;
    }
    $.get('/mailtraffic/api/schedule/disable?id='+id)
    .then(resp=>{
        alert(resp.message);
        location.reload();
    }).catch(err=>alert('Unable to Disable...'))
}
function enableTask(event) {
    let id = event.target.value;
    if(
        !id
    ){
        alert('Unable to Enable...');
        return;
    }
    $.get('/mailtraffic/api/schedule/enable?id='+id)
    .then(resp=>{
        alert(resp.message);
        location.reload();
    }).catch(err=>alert('Unable to Enable...'))
}