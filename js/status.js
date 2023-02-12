let timeout = null;
function getStatus(){
    $.get('/mailtraffic/api/status')
    .then(json=>{
        let success = document.getElementById('success');
        let failure = document.getElementById('failure');
        let processing = document.getElementById('processing');
        let timetaken = document.getElementById('timetaken');
        let starttime = document.getElementById('starttime');
        let endtime = document.getElementById('endtime');
        console.log(starttime);
        console.log(endtime);
        success.textContent="Success: "+json.success;
        failure.textContent="Failure: "+json.failure;
        processing.textContent="Pending: "+json.pending;
        starttime.textContent="Start Time: "+formatDate(json.starttime);
        if(json.endtime&&json.timetaken){
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
            endtime.textContent = "End Time: "+formatDate(json.endtime);
            clearTimeout(timeout);
        }
    }).catch(err=>{
        console.log(err)
        location.href="/mailtraffic"
    })
    timeout = setTimeout(getStatus,3000);
}
getStatus();


function formatDate(datestr) {
    var month = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul","Aug", "Sep", "Oct", "Nov", "Dec"];
    let date = new Date(datestr);
    return date.getDate()+"-"+month[date.getMonth()]+"-"+date.getFullYear()+" "+date.toLocaleTimeString();
}



