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
    if(datas.length==0){
        document.getElementById('nodata').classList.remove('hidden');
        document.getElementById('table').classList.add('hidden');
        return;
    }
    document.getElementById('nodata').classList.add('hidden');
    document.getElementById('table').classList.remove('hidden');
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
        timestamp.textContent = formatDate(data.timestamp);
        tr.appendChild(timestamp);
        tbody.appendChild(tr);
        
    })
}

function formatDate(datestr) {
    var month = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul","Aug", "Sep", "Oct", "Nov", "Dec"];
    let date = new Date(datestr);
    return date.getDate()+"-"+month[date.getMonth()]+"-"+date.getFullYear()+" "+date.toLocaleTimeString();
}

function updatePageinationBtn(){
    const pagenum = document.getElementsByClassName('pagenum')
    const prevbtn = document.getElementsByClassName('prevbtn');
    const firstpage = document.getElementsByClassName('firstpage');
    const lastpage = document.getElementsByClassName('lastpage');
    const nextbtn = document.getElementsByClassName('nextbtn');
    for(let i=0;i<prevbtn.length;i++){
        pagenum[i].textContent = page+"/"+totalpages;
        console.log(pagenum[i].textContent);
        if(page==1){
            prevbtn[i].classList.add('hidden');
            firstpage[i].classList.add('hidden');
            if(page!=totalpages){
                lastpage[i].classList.remove('hidden');
                nextbtn[i].classList.remove('hidden');
            }else{
                lastpage[i].classList.add('hidden');
                nextbtn[i].classList.add('hidden');
            }
        }else if(page==totalpages){
            nextbtn[i].classList.add('hidden');
            lastpage[i].classList.add('hidden');
            firstpage[i].classList.remove('hidden');
            prevbtn[i].classList.remove('hidden');
        }else if(page!=1&&page!=totalpages){
            nextbtn[i].classList.remove('hidden');
            firstpage[i].classList.remove('hidden');
            prevbtn[i].classList.remove('hidden');
            lastpage[i].classList.remove('hidden');
        }
    }
    
}

function loadTable(){
    if(!tenant){
        alert('Please select Tenant...');
        return;
    }
    count = document.getElementById('count')?.value?Number(document.getElementById('count')?.value):100;
    const loading = document.getElementById('loading');
    loading.classList.remove('hidden');
    let param = new URLSearchParams({lowerbound,count,tenant});
    $.get('/mailtraffic/api/mailtrace?'+param.toString())
    .then(resp=>{
        loading.classList.add('hidden');
        totalpages = Math.ceil(Number(resp.totalcount)/count);
        renderTable(resp.data);
        page=Math.ceil(resp.rangestart/count);
        updatePageinationBtn();
        const pageinfo = document.getElementsByClassName('pageinfo');
        const totalDataCol = document.getElementsByClassName('total-data');
        for(let i=0;i<pageinfo.length;i++){
            pageinfo[i].textContent = "Range: "+resp.rangestart+" to "+resp.rangeend;
            totalDataCol[i].textContent ="Total Count: "+ resp.totalcount;
        }
        
        totalData = resp.totalcount;
    })
    .catch(err=>{
        loading.classList.add('hidden');
        console.log(err.responseText)
    })
}

function nextPage(event) {
    if(page==totalpages){
        alert('Last Page Reached...');
        return;
    }
    console.log(count);
    lowerbound+=count;
    console.log(lowerbound);
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