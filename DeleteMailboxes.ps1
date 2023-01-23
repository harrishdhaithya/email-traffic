param(
    [Parameter(Mandatory=$true)]
    $emails,
    $adminemail,
    $adminpassword
)
$emails = $emails.split(',')
$password = ConvertTo-SecureString $adminpassword -AsPlainText -Force
$cred = New-Object System.Management.Automation.PSCredential($adminemail, $password)
Connect-ExchangeOnline -Credential $cred
foreach ($email in $args) {
    Remove-Mailbox -Identity $email -confirm:$false
}