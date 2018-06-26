<?php
    // Start the session
    session_start();
    if(!isset($_SESSION['user_id'])){
        $_SESSION['user_id'] = 0;
    }

    $page = basename($_SERVER['PHP_SELF']);
    if($_SESSION['user_id'] == 0 and $page <> "login.php"){
        $_SESSION['message'] = "Please log in first";
        header('Location: '."login.php");
    }

?>

<!-- Header components -->
</<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Home</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" type="text/css" media="screen" href="css/main.css" />
    <script src="scripts/main.js"></script>
</head>
<body>

<?php include('parts/navigation.php'); ?>