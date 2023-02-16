let timeout = null;
function getStatus() {
    $.get('/mailtraffic/api/mailtrace/status')
    .then(json=>{
        document.getElementById('mailcount').textContent = "Total Number of Mail Traces: "+json.mailcount;
        document.getElementById('status').textContent = "Mail Status: "+json.status;
        document.getElementById('starttime').textContent = "Start Time: "+json.starttime;
        document.getElementById('tenant').textContent = "Tenant: "+json.tenant;
        if(json.endtime){
            document.getElementById('endtime').textContent = "End Time: "+json.endtime;
            document.getElementById('loading').classList.add('hidden');
            if(json.status=='FAILED'){
                document.getElementById('error').textContent = "Error Message: "+json.stacktrace;
            }
            clearTimeout(timeout);
        }
    })
    .catch(err=>{
        alert(err.responseText);
        // location.href = '/mailtraffic/settings/mailtraces.jsp'
    })

    timeout = setTimeout(getStatus,5000);
}
getStatus();