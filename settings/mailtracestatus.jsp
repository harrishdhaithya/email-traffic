<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mail Trace Status</title>
    <link rel="stylesheet" href="../css/style.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.1/jquery.min.js"></script>
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css" rel="stylesheet">
    <script src="../js/mailtracestatus.js"></script>
</head>
<body>
    <div class="nav-bar flex" style="align-items: center;">
        <img src="../img/logo.png" style="height: 5rem;margin-left: 5px;">
        <div class="nav-title">EMail Traffic Generator</div>
        <div class="flex-right">
            <button class="nav-btn" onclick="location.href='/mailtraffic'" >Home</button>
            <button class="nav-btn" onclick="location.href='settings'" >Settings</button>
            <button class="nav-btn" onclick="location.href='mailtraces.jsp'" >Back</button>
        </div>
    </div>
    <div class="container">
        <div id="status-box">
            Mail Trace Status
            <hr>
            <div id="mailcount"></div>
            <div id="status"></div>
            <div id="starttime"></div>
            <div id="endtime"></div>
        </div>
        <div style="text-align: center;" id="loading">
            <img src="../img/loading.gif" style="height: 50px;width: 50px;border-radius: 50%;">
        </div>
    </div>
</body>
</html>