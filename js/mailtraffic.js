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
    const loading = document.getElementById('loading');
    loading.classList.remove('hidden');
    let params = new URLSearchParams({time,schedulename,tenantid,count});
    $.ajax('/mailtraffic/api/schedule',{
        method:'POST',
        ContentType: 'application/x-www-form-urlencoded',
        data: params.toString(),
        processData: false,
        cache: false
    })
    .then(resp=>{
        loading.classList.remove('hidden');
        alert('Email Traffic Sheduled Successfully...')
        location.href = '/mailtraffic/settings/schedules.jsp';
    })
    .catch(err=>{
        loading.classList.remove('hidden');
        alert(err.responseText)
    });
}

function observeCount(){
    const count = document.getElementById('count');
    if(count.value<=0){
        count.value = 1;
    }
}