function generateTraffic(event) {
    event.preventDefault();
    const startdate = document.getElementById('startdate').value;
    const enddate = document.getElementById('enddate').value;
    const taskname = document.getElementById('taskname').value;
    const schedulename = document.getElementById('schedulename').value;
    const tenantid = document.getElementById('tenant').value;
    const count = document.getElementById('count').value;
    if(
        !startdate ||
        !enddate ||
        !taskname ||
        !schedulename ||
        !tenant ||
        !count
    ){
        alert('All the fields are required...');
        return;
    }
    const param = new URLSearchParams({startdate,enddate,taskname,schedulename,tenantid,count})
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