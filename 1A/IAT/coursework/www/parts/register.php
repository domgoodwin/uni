<?php require('parts/header.php'); ?>

<?php 
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $username = prune($_POST["username"]);
    $password = prune($_POST["password"]);
    $firstname = prune($_POST["firstname"]);
    $lastname = prune($_POST["lastname"]);
    $type = prune($_POST["user_type"]);
    $returned = registerUser($username, $password, $firstname, $lastname, $type);
    if(is_numeric($returned)){
        $_SESSION['message'] = "Account has been created";
        $_SESSION['user_id'] = $returned;
        header('Location: '."index.php");
    } else{
        $_SESSION['message'] = "Register failed due to: ".$returned;
        header('Location: '."login.php");
    }
  }

?>