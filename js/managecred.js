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
        if(page!=1){
            document.getElementById('firstpage').classList.remove('hidden');
            document.getElementById('prevbtn').classList.remove('hidden');
        }
    }
    if(page!=1&&page!=totalpages){
        document.getElementById('prevbtn').classList.remove('hidden');
        document.getElementById('nextbtn').classList.remove('hidden');
        document.getElementById('firstpage').classList.remove('hidden');
        document.getElementById('lastpage').classList.remove('hidden');
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
    $.get('/mailtraffic/api/cred?'+param.toString())
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