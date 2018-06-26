<?php
    // Start the session
    session_start();
    require('parts/db.php');
    if(!isset($_SESSION['user_id'])){
        $_SESSION['user_id'] = 0;
    }
    $page = basename($_SERVER['PHP_SELF']);
    if($_SESSION['user_id'] == 0 and $page <> "login.php" and $page <> "register.php"){
        $_SESSION['message'] = "Please log in first";
        header('Location: '."login.php");
    }

    function setSessionID($id){
        $_SESSION['user_id'] = $id;
        $_SESSION['user_type'] = checkUserIsOrg($id) ? 'org' : 'usr';
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

<ul class='nav'>
    <li class='nav'><a class='nav' href="index.php">Home</a></li>
    <li class='nav'><a class='nav' href="events.php">Events</a></li>
    <li class='nav'>
        <?php if($_SESSION['user_type'] == "org"){ ?>
            <a class="nav" href="create.php" >Create Event</a>
        <?php }?>
    </li>
    <li class='nav login'>
        <?php if($_SESSION['user_id'] == 0){ ?>
            <a class="nav login" href="login.php">Login/Register</a>
        <?php }else{ ?>
            <a class="nav login" href="logout.php">Logout</a>
        <?php } ?>
    </li>
</ul>

<div class='main'>