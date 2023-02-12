function downloadSampleFile(event) {
    event.preventDefault();
    $.get('/mailtraffic/api/samplefile')
    .then(resp=>{
        console.log(resp);
        const blob = new Blob([resp],{type:'text/csv'});
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.style.display="none";
        a.href = url;
        a.download = 'samplefile.csv';
        a.click();
        URL.revokeObjectURL(url);
    }).catch(err=>{
        console.log(err);
        alert('Not able to download file...');
    })
}