function uploadData(event) {
    event.preventDefault();
    const formData = new FormData();
    const file = document.getElementById('file');
    const tenant = document.getElementById('tenant').value;
    if(file.value==null){
        alert('Please upload file...');
        return;
    }
    if(!tenant){
        alert('Please Select Tenant...');
        return;
    }
    formData.append('file',file.files[0]);
    formData.append('tenantid',tenant);
    $.ajax({
        type: "POST",
        enctype: 'multipart/form-data',
        url: "/mailtraffic/api/uploadcreds",
        data: formData,
        processData: false,
        contentType: false,
        cache: false,
    }).then(resp=>{
        alert('Data uploaded successfully...');
        location.reload();
    }).catch(err=>{
        // const json = JSON.parse(err.responseText);
        alert(err.responseText);
    });
}