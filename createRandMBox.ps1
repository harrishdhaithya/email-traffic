param (
    [Parameter(Mandatory=$true)]
    $emails
)
$AdminEmail = $Env:adminemail
$password = ConvertTo-SecureString $Env:adminpassword -AsPlainText -Force
$cred = New-Object System.Management.Automation.PSCredential($AdminEmail, $password)
Connect-ExchangeOnline -Credential $cred
$emails = $emails.split(',')
Write-Output $emails[0]
foreach($email in $emails) {
    $pass = "m365Password@123"
    $mpass = ConvertTo-SecureString $pass -AsPlainText -Force
    New-Mailbox -Shared -Name $email -DisplayName $email -Alias $email -Password $mpass
}