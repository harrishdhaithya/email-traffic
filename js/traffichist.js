function getHistory(event) {
    event.preventDefault();
    const tenantid = document.getElementById('tenant').value;
    const param = new URLSearchParams({tenantid});
    const loading = document.getElementById('loading');
    loading.classList.remove('hidden');
    $.get('/mailtraffic/api/traffic/history?'+param.toString())
    .then(resp=>{
        loading.classList.add('hidden');
        renderTable(resp.data);
    })
    .catch(err=>{
        loading.classList.add('hidden');
        alert('Not able to get History...')
    })
}


function renderTable(datas){
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
        
        let successCount = document.createElement('td');
        successCount.textContent = data.successCount;
        tr.appendChild(successCount);
        
        let failureCount = document.createElement('td');
        failureCount.textContent = data.failureCount;
        tr.appendChild(failureCount);
        
        let totalCount = document.createElement('td');
        totalCount.textContent = data.totalCount;
        tr.appendChild(totalCount);

        let tenant = document.createElement('td');
        tenant.textContent = data.tenant;
        tr.appendChild(tenant);

        let startTime = document.createElement('td');
        startTime.textContent = formatDate(data.startTime);
        tr.appendChild(startTime);

        let endTime = document.createElement('td');
        endTime.textContent = formatDate(data.endTime);
        tr.appendChild(endTime);

        tbody.appendChild(tr);
        
    })
}

function formatDate(datestr) {
    var month = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul","Aug", "Sep", "Oct", "Nov", "Dec"];
    let date = new Date(datestr);
    return date.getDate()+"-"+month[date.getMonth()]+"-"+date.getFullYear()+" "+date.toLocaleTimeString();
}