function updatePoolSize(event) {
    event.preventDefault();
    const poolsize = document.getElementById('poolsize').value;
    if(
        !poolsize
    ){
        alert('Please enter a valid pool size...');
        return;
    }
    const param = new URLSearchParams({poolsize})
    $.ajax('/mailtraffic/api/updatepool?'+param.toString(),{
        method:'PUT'
    })
    .then(resp=>{
        alert(resp.message);
    })
    .catch(err=>{
        // const error = JSON.parse(error.resposeText);
        alert(err.resposeText);
    });
}