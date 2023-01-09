$email = Read-Host -Prompt "Enter Your Global Admin Account"
$password = Read-Host -Prompt "Enter Your Password" -AsSecureString
$tenant = $email.Split('@')[1]
$cred = New-Object System.Management.Automation.PSCredential($email, $password)
Connect-ExchangeOnline -Credential $cred
$MailBoxPrefix = "testing_mailbox__"
for($i=1;$i -le 10000;$i++){
    $MailBoxName = $MailBoxPrefix+$i
    $MailBoxDisplayName = $MailBoxPrefix+$i
    $MailBoxAlias = $MailBoxPrefix+$i
    $mpass = ConvertTo-SecureString "m365Password@123" -AsPlainText -Force
    New-Mailbox -Shared -Name $MailBoxName -DisplayName $MailBoxDisplayName -Alias $MailBoxAlias -Password $mpass
    Add-Content -Path .\mailboxes.csv -Value "$MailBoxName@$tenant , $mpass"
    Write-Output "MailBox Created - $MailBoxName"
}