param (
    [Parameter(Mandatory=$true)]
    $emails,
    $adminemail,
    $adminpassword,
    $operation
)

Write-Output $adminemail
Write-Output $adminpassword

$emails = $emails.split(',')
$password = ConvertTo-SecureString $adminpassword -AsPlainText -Force
$cred = New-Object System.Management.Automation.PSCredential($adminemail, $password)
Connect-ExchangeOnline -Credential $cred



if($operation -eq 'create'){
    foreach($email in $emails) {
        $pass = "{{PASSWORD}}"
        $mpass = ConvertTo-SecureString $pass -AsPlainText -Force
        New-Mailbox -Shared -Name $email -DisplayName $email -Alias $email -Password $mpass
    }
}



if($operation -eq 'delete'){
    foreach ($email in $args) {
        Remove-Mailbox -Identity $email -confirm:$false
    }    
}
