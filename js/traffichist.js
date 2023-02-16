function getHistory(event) {
    event.preventDefault();
    const tenantid = document.getElementById('tenant').value;
    const param = new URLSearchParams({tenantid});
    $.get('/mailtraffic/api/traffic/history?'+param.toString())
    .then(resp=>{
        renderTable(resp.data);
    })
    .catch(err=>{
        alert('Not able to get History...')
    })
}


function renderTable(datas){
    let tbody = document.getElementById('table-body');
    document.getElementById('table-container').classList.remove('hidden');
    tbody.innerHTML = "";
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
        startTime.textContent = data.startTime;
        tr.appendChild(startTime);

        let endTime = document.createElement('td');
        endTime.textContent = data.endTime;
        tr.appendChild(endTime);

        tbody.appendChild(tr);
        
    })
}