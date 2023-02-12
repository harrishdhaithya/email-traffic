let lowerbound = 1;
let count = 100;
let tenant = null;
let currPage = 1;
let page = 1;
let totalpages = 1;
let totalData = 0;


function getTraces(event) {
    event.preventDefault();
    tenant = document.getElementById('tenant').value;
    lowerbound=1;
    count=100;
    currPage=1;
    page=1;
    totalpages=1;
    loadTable();
}



function renderTable(respData){
    const datas = JSON.parse(respData);
    let tbody = document.getElementById('table-body');
    document.getElementById('table-container').classList.remove('hidden');
    tbody.innerHTML = "";
    datas.forEach(data=>{
        let tr = document.createElement('tr');

        let sender = document.createElement('td');
        sender.textContent = data.sender;
        tr.appendChild(sender);
        
        let receiver = document.createElement('td');
        receiver.textContent = data.receiver;
        tr.appendChild(receiver);

        let subject = document.createElement('td');
        subject.textContent = data.subject;
        tr.appendChild(subject);
        
        let status = document.createElement('td');
        status.textContent = data.status;
        tr.appendChild(status);

        let timestamp = document.createElement('td');
        timestamp.textContent = data.timestamp;
        tr.appendChild(timestamp);
        tbody.appendChild(tr);
        
    })
}

function updatePageinationBtn(){
    document.getElementById('pagenum').textContent = page+"/"+totalpages;
    if(page==1){
        document.getElementById('prevbtn').classList.add('hidden');
        document.getElementById('firstpage').classList.add('hidden');
        document.getElementById('lastpage').classList.remove('hidden');
        document.getElementById('nextbtn').classList.remove('hidden');
    }
    if(page==totalpages){
        document.getElementById('nextbtn').classList.add('hidden');
        document.getElementById('lastpage').classList.add('hidden');
        document.getElementById('firstpage').classList.remove('hidden');
        document.getElementById('prevbtn').classList.remove('hidden');
    }
    if(page!=1&&page!=totalpages){
        document.getElementById('prevbtn').classList.remove('hidden');
        document.getElementById('nextbtn').classList.remove('hidden');
        document.getElementById('firstpage').classList.remove('hidden');
        document.getElementById('lastpage').classList.remove('hidden');
    }
}

function loadTable(){
    if(tenant==null){
        alert('Please select Tenant...');
    }
    let param = new URLSearchParams({lowerbound,count,tenant});
    $.get('/mailtraffic/api/mailtrace?'+param.toString())
    .then(resp=>{
        totalpages = Math.ceil(Number(resp.totalcount)/count);
        renderTable(resp.data);
        page=Math.ceil(resp.rangestart/100);
        updatePageinationBtn();
        document.getElementById('pageinfo').textContent = "Range: "+resp.rangestart+" to "+resp.rangeend;
        document.getElementById('total-data').textContent ="Total Count: "+ resp.totalcount;
        totalData = resp.totalcount;
    })
    .catch(err=>console.log(err.responseText))
}

function nextPage(event) {
    if(page==totalpages){
        alert('Last Page Reached...');
        return;
    }
    lowerbound+=count;
    loadTable();
}

function prevPage(event) {
    if(page==1){
        alert('This is your first page...');
        return;
    }
    lowerbound-=count;
    loadTable();
}

function firstPage(event) {
    lowerbound=1;
    loadTable();
}
function lastPage(event){
    lowerbound = (totalData%count==0)?(totalData-count):(totalData-(totalData%count))+1;
    loadTable();
}