function getHistory(event) {
    event.preventDefault();
    let tenantid = document.getElementById('tenant').value;
    let param = new URLSearchParams({tenantid})
    const loading = document.getElementById('loading');
    loading.classList.remove('hidden')
    $.get('/mailtraffic/api/trace/history?'+param.toString())
    .then(resp=>{
        loading.classList.add('hidden');
        renderTable(resp.data);
    }).catch(err=>{
        loading.classList.add('hidden');
        alert("Something went wrong...");
    });
}



function renderTable(datas){
    console.log(datas)
    let tbody = document.getElementById('table-body');
    document.getElementById('table-container').classList.remove('hidden');
    tbody.innerHTML = "";
    if(datas.length==0){
        document.getElementById('nodata').classList.remove('hidden');
        document.getElementById('table').classList.add('hidden');
        return;
    }
    document.getElementById('nodata').classList.add('hidden');
    document.getElementById('table').classList.remove('hidden');
    datas.forEach(data=>{
        let tr = document.createElement('tr');
        
        let traceStart = document.createElement('td');
        traceStart.textContent = formatDate(data.tracestart);
        tr.appendChild(traceStart);
        
        let recentTrace = document.createElement('td');
        recentTrace.textContent = formatDate(data.recentTrace);
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

function formatDate(datestr) {
    var month = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul","Aug", "Sep", "Oct", "Nov", "Dec"];
    let date = new Date(datestr);
    return date.getDate()+"-"+month[date.getMonth()]+"-"+date.getFullYear()+" "+date.toLocaleTimeString();
}