$cred = Get-Credential
$session = New-PSSession -ConfigurationName Microsoft.Exchange -ConnectionUri https://ps.outlook.com/powershell/ -Credential $cred -Authentication Basic -AllowRedirection
Import-PSSession $session

for ($i = 6647; $i -le 10000; $i++) {
    $MailBoxPrefix = "testing_mailbox_"
    $MailBoxName = $MailBoxPrefix + $i
    $MailBoxDisplayName = $MailBoxPrefix + $i
    $MailBoxAlias = $MailBoxPrefix + $i
    $passwordString = "m365password@123"
    $password = ConvertTo-SecureString $passwordString -AsPlainText -Force
    New-Mailbox -Shared -Name $MailBoxName -DisplayName $MailBoxDisplayName -Alias $MailBoxAlias -password $password
    Add-Content -Path .\mailboxes.csv -Value "$MailBoxName@1wqf3d.onmicrosoft.com , $passwordString"
    Write-Output "MailBox Created - $MailBoxName"
}