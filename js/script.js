function generateTraffic(event){
    event.preventDefault();
    let datasource = document.getElementById('datasource').value;
    let formData = new FormData();
    formData.append('datasource',datasource);
    let count = document.getElementById('count').value;
    formData.append('count',count);
    if(
        !count
    ){
        alert('Please Enter the number of Mails...');
        return;
    }
    if(datasource=='csv'){
        let file = document.getElementById('file');
        let tenant = document.getElementById('tenantid').value;
        if(
            !tenant
        ){
            alert('Enter all the required fields...');
            return;
        }
        if(
            !file.value
        ){
            alert('Please attach a csv file...');
            return;
        }
        formData.append('file',file.files[0]);
        formData.append("tenant",tenant)
    }else if(datasource=='sequence'){
        let prefix = document.getElementById('prefix').value;
        let suffix = document.getElementById('suffix').value;
        let seqstart = document.getElementById('seqstart').value;
        let seqend = document.getElementById('seqend').value;
        let tenant = document.getElementById('tenantid').value;
        let password = document.getElementById('password').value;
        if(
            !prefix ||
            seqstart == null ||
            seqend == null ||
            !tenant ||
            !password 
        ){
            alert('Enter all the required fields...')
        }
        formData.append('prefix',prefix);
        formData.append('suffix',suffix);
        formData.append('seqstart',seqstart);
        formData.append('seqend',seqend);
        formData.append('tenant',tenant);
        formData.append('password',password);
        formData.append("tenant",tenant)
    }else{
        let tenant = document.getElementById('tenantid').value;
        formData.append("tenant",tenant)
    }
    $.ajax({
        type: "POST",
        enctype: 'multipart/form-data',
        url: "/mailtraffic/api/generate",
        data: formData,
        processData: false,
        contentType: false,
        cache: false,
    }).then(resp=>{
        location.href='mailstatus.html';
    }).catch(err=>{
        alert(err.responseText);
    });
}

function changeOpt(){
    const datasource = document.getElementById('datasource');
    const seqopt =  document.getElementById('seq-opt');
    const csvopt = document.getElementById('csv-opt');
    const dbopt = document.getElementById('db-opt');
    const common = document.getElementById('common');
    const azureCreds = document.getElementById("azure-cred")
    if(datasource.value=='sequence'){
        seqopt.classList.remove('hidden');
        common.classList.remove('hidden');
        csvopt.classList.add('hidden');
        dbopt.classList.add('hidden')
    }else if(datasource.value=='csv'){
        csvopt.classList.remove('hidden');
        common.classList.remove('hidden');
        seqopt.classList.add('hidden');
        dbopt.classList.add('hidden');
    }else if(datasource.value=='db'){
        dbopt.classList.remove('hidden');
        common.classList.remove('hidden');
        seqopt.classList.add('hidden');
        csvopt.classList.add('hidden');
    }else{
        seqopt.classList.add('hidden');
        common.classList.add('hidden');
        csvopt.classList.add('hidden');
        dbopt.classList.add('hidden');
    }
}

function changeTenant(){
    const tenantid = document.getElementById('tenantid').value;
    const senders = document.getElementById('senders');
    const reciever = document.getElementById('recievers');
    if(
        !tenantid
    ){
        senders.classList.add('hidden');
        reciever.classList.add('hidden');
        return;
    }
    const param = new URLSearchParams({tenantid});
    $.get('/mailtraffic/api/getinfo?'+param.toString())
    .then(json=>{
        senders.classList.remove('hidden');
        reciever.classList.remove('hidden');
        senders.textContent="Senders: "+json.sender;
        reciever.textContent="Recievers: "+json.reciever;
    }).catch(err=>console.log("Not able to retrieve info..."));    
}