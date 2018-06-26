<?php
$db = null;
init();
function init(){
    global $db;
    $myfile = fopen("database.json", "r") or die("Unable to get details");
    $text =  fread($myfile,filesize("database.json"));
    $json = json_decode($text);
    fclose($myfile);
    $servername = $json->{'servername'};
    $username = $json->{'username'};
    $password = $json->{'password'};
    $dbname = $json->{'dbname'};
    //$db=new mysqli($servername, $username, $password, "astonevents");
    $db = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);
    $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
}

function login($username, $password){
    global $db;
    try {
        $hashedPw = hashPw($password);
        $sql = $db->prepare("SELECT user_id FROM users  WHERE username = :usernm AND password = :passwd");
        $sql->bindParam(':usernm', $username, PDO::PARAM_STR, 20);
        $sql->bindParam(':passwd', $hashedPw, PDO::PARAM_STR, 128);
        $success = $sql->execute();
        if($sql->rowCount() == 1 and $success){
            return $sql->fetch(PDO::FETCH_ASSOC)['user_id'];
        } else {
            error_log($sql->fetch());
            return "Failed to login";
        }
    } catch(PDOException $e) {
        echo "Error: " . $e->getMessage();
    }
}

function registerUser($username, $password, $firstname, $lastname, $type, $email){
    global $db;
    try {
        if(checkUserExists($username)){
            return "Username already exists";
        }
        $hashedPw = hashPw($password);
        $sql = $db->prepare("INSERT INTO users (username, password, firstname, lastname) 
            VALUES (:usernm, :passwd, :first, :last)");
        $sql->bindParam(':usernm', $username, PDO::PARAM_STR, 20);
        $sql->bindParam(':passwd', $hashedPw, PDO::PARAM_STR, 128);
        $sql->bindParam(':first', $firstname, PDO::PARAM_STR, 20);
        $sql->bindParam(':last', $lastname, PDO::PARAM_STR, 20);
        $success = $sql->execute();
        if($success){
            $id = (int)$db->lastInsertId();
            if($type == "organiser"){
                $sql = $db->prepare("INSERT INTO organisers (user_id, email)
                    VALUES (:id, :email)");
                $sql->bindParam(':id', $id, PDO::PARAM_INT);
                $sql->bindParam(':email', $email, PDO::PARAM_STR, 50);
                $success = $sql->execute();
            }
            return $id;
        } else {
            return $sql->fetch();
         }
    } catch(PDOException $e) {
        echo "Error: " . $e->getMessage();
    }
}

function checkUserIsOrg($userID){
    global $db;
    try {
        $sql = $db->prepare("SELECT user_id FROM organisers WHERE user_id = :id ");
        $sql->bindParam(':id', $userID, PDO::PARAM_INT);
        $sql->execute();
        if($sql->rowCount() == 0){
            return false;
        } else {
            return true;
        }
    } catch(PDOException $e) {
        echo "Error: " . $e->getMessage();
    }
}

function checkUserExists($username){
    global $db;
    try {
        $sql = $db->prepare("SELECT user_id FROM users WHERE username = :usernm ");
        $sql->bindParam(':usernm', $username, PDO::PARAM_STR, 20);
        $sql->execute();
        if($sql->rowCount() == 0){
            return false;
        } else {
            return true;
        }
    } catch(PDOException $e) {
        echo "Error: " . $e->getMessage();
    }
}


// Reusable functions
function hashPw($password){
    return hash('sha512', $password);
}

function prune($data) {
    $data = trim($data);
    $data = stripslashes($data);
    $data = htmlspecialchars($data);
    return $data;
  }
?>