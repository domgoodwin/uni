<?php require('parts/header.php'); ?>

<?php 
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $username = prune($_POST["username"]);
    $password = prune($_POST["password"]);
    $returned = login($username, $password);
    if(is_numeric($returned)){
        $_SESSION['message'] = "Login successful";
        setSessionID($returned);
        // $_SESSION['user_id'] = $returned;
        header('Location: '."index.php");
    } else{
        $_SESSION['message'] = "Login failed: ".$returned;
        header('Location: '."login.php");
    }
  }
?>

<span class='message'><?php echo $_SESSION['message'] ?> </span>
<h1>Login</h1>
<div class="container">
    <form action="<?php echo htmlspecialchars($_SERVER["PHP_SELF"]);?>" method="post">
        Username: <input class='login' type="text" name="username" required><br>
        Password: <input class='login' type="password" name="password" required><br>
        <input type="submit" value="Login">
    </form>
</div>

<h1>Sign up</h1>
<div class="container">
    <form action="<?php echo htmlspecialchars("register.php");?>" method="post">
        First Name: <input class='login' type="text" name="firstname" required><br>
        Last Name: <input class='login' type="text" name="lastname" required><br>
        Username: <input class='login' type="text" name="username" required><br>
        Password: <input class='login' type="password" name="password" required><br>
        Type: 
        <select class='login' name='user_type' onchange="updateField()" id='type-select'>
            <option value="student">Student</option>
            <option value="organiser">Organiser</option>
        </select><br>
        <div id='email-box'>Email: <input class='login' type="text" name="email" id="email-field"></div>
        <input type="submit" value="Register">
    </form>
</div>



<?php require('parts/footer.php'); ?>