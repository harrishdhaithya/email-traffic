function getHistory(event) {
    event.preventDefault();
    let tenantid = document.getElementById('tenant').value;
    let param = new URLSearchParams({tenantid})
    $.get('/mailtraffic/api/trace/history?'+param.toString())
    .then(resp=>{
        renderTable(resp.data);
    }).catch(err=>{
        alert("Something went wrong...");
    });

}



function renderTable(datas){
    let tbody = document.getElementById('table-body');
    document.getElementById('table-container').classList.remove('hidden');
    tbody.innerHTML = "";
    datas.forEach(data=>{
        console.log(data)
        let tr = document.createElement('tr');
        
        let traceStart = document.createElement('td');
        traceStart.textContent = data.tracestart;
        tr.appendChild(traceStart);
        
        let recentTrace = document.createElement('td');
        recentTrace.textContent = data.recentTrace;
        tr.appendChild(recentTrace);
        
        let tenant = document.createElement('td');
        tenant.textContent = data.tenant;
        tr.appendChild(tenant);
        
        let status = document.createElement('td');
        status.textContent = data.status;
        tr.appendChild(status);
        
        tbody.appendChild(tr);
        
    })
}