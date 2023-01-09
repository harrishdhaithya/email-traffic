let timeout = null;
function getStatus(){
    $.get('/mailtraffic/api/status')
    .then(json=>{
        let success = document.getElementById('success');
        let failure = document.getElementById('failure');
        let processing = document.getElementById('processing');
        let timetaken = document.getElementById('timetaken');
        success.textContent="Success: "+json.success;
        failure.textContent="Failure: "+json.failure;
        processing.textContent="Pending: "+json.pending;
        if(Number(json.pending)==0){
            let loading = document.getElementById('loading');
            loading.classList.add('hidden');
            console.log(json.timetaken);
            let timetext = ""
            let seconds = Number(json.timetaken);
            if(seconds>3600){
                timetext+=Math.floor(seconds/3600)+" hours ";
                seconds = seconds%3600;
            }
            if(seconds>60){
                timetext+=Math.floor(seconds/60)+" minutes ";
                seconds = seconds%60;
            }
            if(seconds>0){
                timetext+=seconds+" seconds."
            }
            timetaken.textContent = "Timetaken: "+timetext;
            clearTimeout(timeout);
        }
    }).catch(err=>{
        location.href="/mailtraffic"
    })
    timeout = setTimeout(getStatus,3000);
}
getStatus();