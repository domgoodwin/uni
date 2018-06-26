<?php require('parts/header.php'); ?>

<?php 
    $_SESSION['user_id'] = 0;
    $_SESSION['message'] = "Successfully logged out!";
    header("Refresh:0; url=login.php");
?>