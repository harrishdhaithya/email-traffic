let lowerbound = 1;
let count = 100;
let tenant = null;
let currPage = 1;
let page = 1;
let totalpages = 1;
let totalData = 0;

function getCredentials(event) {
    event.preventDefault();
    tenant = document.getElementById('tenant').value;
    lowerbound=1;
    count=100;
    currPage=1;
    page=1;
    totalpages=1;
    loadTable();
}

function renderTable(user){
    let tbody = document.getElementById('table-body');
    document.getElementById('table-container').classList.remove('hidden');
    tbody.innerHTML = "";
    if(user.length==0){
        document.getElementById('nodata').classList.remove('hidden');
        document.getElementById('table').classList.add('hidden');
        return;
    }
    document.getElementById('nodata').classList.add('hidden');
    document.getElementById('table').classList.remove('hidden');
    user.forEach(user=>{
        let tr = document.createElement('tr');
        let email = document.createElement('td');
        email.textContent = user.email;
        tr.appendChild(email);
        let status = document.createElement('td');
        status.textContent = user.status;
        tr.appendChild(status);
        let editBtn = document.createElement('button');
        editBtn.value = user.id;
        editBtn.style = "background-color: #5bc0de;color: white;";
        editBtn.onclick = (event)=>{
            let id = event.target.value;
            location.href = '/mailtraffic/settings/edituser.jsp?id='+id;
        }
        editBtn.textContent = "UPDATE"
        let editCol = document.createElement('td');
        editCol.appendChild(editBtn);
        tr.appendChild(editCol);
        let deleteBtn = document.createElement('button');
        deleteBtn.value = user.id;
        deleteBtn.style = "background-color: #d9534f;color:white;"
        deleteBtn.onclick = deleteUser;
        deleteBtn.textContent = "DELETE"
        let deleteCol = document.createElement('td');
        deleteCol.appendChild(deleteBtn);
        tr.appendChild(deleteCol);
        tbody.appendChild(tr);
    })
}

function updatePageinationBtn(){
    const pagenum = document.getElementsByClassName('pagenum')
    const prevbtn = document.getElementsByClassName('prevbtn');
    const firstpage = document.getElementsByClassName('firstpage');
    const lastpage = document.getElementsByClassName('lastpage');
    const nextbtn = document.getElementsByClassName('nextbtn');
    for(let i=0;i<prevbtn.length;i++){
        pagenum[i].textContent = page+"/"+totalpages;
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

function deleteUser(event) {
    event.preventDefault();
    let id = event.target.value;
    $.ajax('/mailtraffic/api/cred?id='+id,{
        method: 'DELETE'
    }).then(resp=>{
        alert(resp.message);
        loadTable();
    }).catch(err=>{
        alert('Not able to Delete Credential...');
    });
}

function loadTable(){
    if(tenant==null){
        alert('Please select Tenant...');
    }
    let param = new URLSearchParams({lowerbound,count,tenant});
    const loading = document.getElementById('loading');
    loading.classList.remove('hidden');
    $.get('/mailtraffic/api/cred?'+param.toString())
    .then(resp=>{
        
        loading.classList.add('hidden');
        totalpages = Math.ceil(Number(resp.totalcount)/count);
        renderTable(resp.data);
        page=Math.ceil(resp.rangestart/100);
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