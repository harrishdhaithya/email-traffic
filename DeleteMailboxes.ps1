param(
    [Parameter(Mandatory=$true)]
    $emails
)
$emails = $emails.split(',')
$AdminEmail = $Env:adminAdminEmail
$password = ConvertTo-SecureString $Env:adminpassword -AsPlainText -Force
$cred = New-Object System.Management.Automation.PSCredential($AdminEmail, $password)
Connect-ExchangeOnline -Credential $cred
foreach ($email in $args) {
    Remove-Mailbox -Identity $email -confirm:$false
}