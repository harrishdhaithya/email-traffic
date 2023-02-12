function toggleTask(event) {
    let name = event.target.name;
    let id = event.target.value;
    let status = name=="enable";
    let params = new URLSearchParams({id,status})
    $.ajax('/mailtraffic/api/schedule?'+params.toString(),{
        method: 'PUT'
    })
    .then(resp=>{
        alert(resp.message);
        location.reload();
    }).catch(err=>{
        if(name=='enable'){
            alert('Unable to Enable...');
        }else{
            alert('Unable to Disable...')
        }
    })
}