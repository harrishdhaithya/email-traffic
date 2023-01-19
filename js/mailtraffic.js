function generateTraffic(event) {
    event.preventDefault();
    const time = document.getElementById('time').value;
    const schedulename = document.getElementById('schedulename').value;
    const tenantid = document.getElementById('tenant').value;
    const count = document.getElementById('count').value;
    if(
        !time ||
        !schedulename ||
        !tenant ||
        !count
    ){
        alert('All the fields are required...');
        return;
    }
    const param = new URLSearchParams({time,schedulename,tenantid,count})
    $.get('/mailtraffic/api/schedule?'+param.toString())
    .then(resp=>{
        alert('Email Traffic Sheduled Successfully...')
        location.reload();
    })
    .catch(err=>alert(err.responseText));
}

function observeCount(){
    const count = document.getElementById('count');
    if(count.value<=0){
        count.value = 1;
    }
}