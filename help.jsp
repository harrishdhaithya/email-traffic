<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Help Document</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="nav-bar flex" style="align-items: center;">
        <img src="img/logo.png" style="height: 5rem;margin-left: 5px;">
        <div class="nav-title">EMail Traffic Generator</div>
        <div class="flex-right">
            <button class="nav-btn" onclick="location.href='/mailtraffic'" >Home</button>
        </div>
    </div>
    <div class="help-box">
        <div class="header-box">
            Help Document
        </div>
        <div class="help-content-box">
            <div class="font-bold fs-4">Email Traffic Generator</div>
            <div class="font-bold fs-3">Purpose: </div>
            <div class="fs-2">
                The purpose of this application is to Generate bulk email traffic to mimic a mail flow in a large organization from an M365 tenant using Exchange Web Service.
            </div>
            <div class="font-bold fs-3">Creating Bulk Shared Mail Box: </div>
            <div class="fs-2">
                This application requires a huge number of shared mail box to send and recieve bulk emails. These shared mail boxes can be generated using a powershell 
                script as shown belowThis application requires a huge number of shared mailboxes to send and receive bulk emails. These shared mailboxes can be generated using a PowerShell script as shown below
                <div style="background-color: white;width: 60%; margin-left: auto; margin-right: auto; padding: 4px; border-radius: 10px; border: 1px solid black;">
                    <code style="white-space: pre-line;">
                        $email = Read-Host -Prompt "Enter Your Global Admin Account"
                        $password = Read-Host -Prompt "Enter Your Password" -AsSecureString
                        $tenant = $email.Split('@')[1]
                        $cred = New-Object System.Management.Automation.PSCredential($email, $password)
                        Connect-ExchangeOnline -Credential $cred
                        $MailBoxPrefix = "testing_mailbox_"
                        for($i=1;$i -le 10000;$i++){
                            $MailBoxName = $MailBoxPrefix+$i
                            $MailBoxDisplayName = $MailBoxPrefix+$i
                            $MailBoxAlias = $MailBoxPrefix+$i
                            $mpass = ConvertTo-SecureString "m365Password@123" -AsPlainText -Force
                            New-Mailbox -Shared -Name $MailBoxName -DisplayName $MailBoxDisplayName -Alias $MailBoxAlias -Password $mpass
                            Add-Content -Path .\mailboxes.csv -Value "$MailBoxName@$tenant , $mpass"
                            Write-Output "MailBox Created - $MailBoxName"
                        }
                    </code>
                </div>
                <div class="font-bold fs-3">Supported Data Formats: </div>
                <br>
                <div class="fs-2">
                    1) CSV File: <br> 
                    The Credentials can be uploaded using a CSV File. The CSV File Should contain Header i.e. Email and Password. It should contain 
                    two chunks of data i.e. with password and without password. The emails that contains password will be considered as sender and those which does
                    not contain password will be considered as receiver.This option requires to configure the tenant information in settings->tenconf.jsp for clientid.<br>
                    <br>
                    2) Generated Sequence(Not Supported): <br>
                    This data format is for those Credentials that contains similar patterns of email id and same password. This format does not separate senders and receiver. It takes the senders and 
                    receiver credentials from same chunk of data. <br>
                    <b>Fields: </b> 
                    <ul>
                        <li>Prefix</li>
                        <li>Suffix(Optional)</li>
                        <li>Starting Number of Sequence</li>
                        <li>Ending Number of Sequence</li>
                        <li>Tenant</li>
                        <li>Password</li>
                    </ul>
                    3) Database: <br>
                    This data format is very similar to csv but it takes the credentials from a pre-populated data base. The database can be populated by uploading a csv file in the Populate Database Page <br> i.e. settings->Populate Database.
                    Similar to csv data format this file also should contain two chunks of data i.e. sender and receiver. This data format can be used when we need to upload large number of data that cannot be populated in java memory.

                </div>
                <div class="font-bold fs-3">Settings and Configurations: </div>
                <div class="fs-2">
                    1) Database population: <br>
                    This setting is availabe to populate the credentials in database. <br>
                    2) Tenant Configuration: <br>
                    This setting is used to add tenant for uploading credentials in the database. <br>
                    Fields: <br>
                    <ul>
                        <li>Tenant Name: In the context of this application tenant name refers to primary domain of the m365 tenant eg. 1wqf3d.onmicrosoft.com.</li>
                        <li>
                            Client ID: A Client ID is an identifier associated with an application that assists with client / server OAuth 2.0 authentication. To register a public client application and get client id 
                            refer <a href="https://learn.microsoft.com/en-us/azure/healthcare-apis/azure-api-for-fhir/register-public-azure-ad-client-app" target="_blank">this link.</a> 
                        </li>
                        <li>Admin Email(Optional): Email of the global administrator of tenant.</li>
                        <li>Admin Password(Optional): Password of the global administrator of tenant.</li>
                    </ul>
                    
                    3) Threadpool Configuration: <br>
                    This setting is used to configure concurrency limit to send emails. <br>
                    4) Schedule Mail Traffic: <br>
                    This setting allows to automatically schedule email traffic on daily basis.
                </div>
            </div>
        </div>
    </div>
</body>
</html>