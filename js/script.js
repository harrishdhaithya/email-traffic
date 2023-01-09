function generateTraffic(event){
    event.preventDefault();
    let datasource = document.getElementById('datasource').value;
    let formData = new FormData();
    formData.append('datasource',datasource);
    if(datasource=='csv'){
        let file = document.getElementById('file');
        if(
            !file.value
        ){
            alert('Please attach a csv file...');
            return;
        }
        formData.append('file',file.files[0]);
    }else if(datasource=='sequence'){
        let prefix = document.getElementById('prefix').value;
        let suffix = document.getElementById('suffix').value;
        let seqstart = document.getElementById('seqstart').value;
        let seqend = document.getElementById('seqend').value;
        let tenant = document.getElementById('tenant').value;
        let password = document.getElementById('password').value;
        formData.append('prefix',prefix);
        formData.append('suffix',suffix);
        formData.append('seqstart',seqstart);
        formData.append('seqend',seqend);
        formData.append('tenant',tenant);
        formData.append('password',password);
    }else{
        let tenant = document.getElementById('tenantid').value;
        formData.append("tenant",tenant)
    }
    let count = document.getElementById('count').value;
    formData.append('count',count);
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
        const json = JSON.parse(err.responseText);
        alert(json.error);
    });
}

function changeOpt(){
    const datasource = document.getElementById('datasource');
    const seqopt =  document.getElementById('seq-opt');
    const csvopt = document.getElementById('csv-opt');
    const dbopt = document.getElementById('db-opt');
    const common = document.getElementById('common');
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

