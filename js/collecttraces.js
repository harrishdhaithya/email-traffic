function collectTraces(event) {
    event.preventDefault();
    let tenantid = document.getElementById('tenant').value;
    $.ajax('/mailtraffic/api/mailtrace?tenant='+tenantid,{
        method:'PUT'
    })
    .then(resp=>{
        alert(resp.message);
        location.href = '/mailtraffic/settings/mailtracestatus.jsp';
    }).catch(err=>{
        console.log(err.responseText);
        alert('Not able collect MailTraces...');
    })
}