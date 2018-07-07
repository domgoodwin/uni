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


// User mgmt
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

// Events
function getEvents($filterType, $filterDateFrom, $filterDateTo, $filterPopularity, $filterOrganiser){
    global $db;
    error_log("dt".$filterDateTo);
    error_log("df".$filterDateFrom);
    $datesGiven = ! (empty($filterDateFrom) and empty($filterDateTo));
    //error_log(isset($filterDateFrom)."/" . isset($filterDateTo)."//" . (trim($filterDateFrom) <> '')."///" . (trim($filterDateTo) <> ''));
    $sqlStatement = "SELECT e.event_id, event_type, name, description, date, organiser_id FROM events e";
    // If any params are set
    $sqlAppend = "";
    if(isset($filterPopularity)) {
        $sqlStatement = str_replace("FROM", ", COUNT(ei.id) FROM", $sqlStatement);
        $sqlStatement .= " LEFT JOIN event_interest ei ON e.event_id=ei.event_id ";
        $sqlAppend .= "GROUP BY e.event_id ORDER BY IFNULL(COUNT(ei.id),0) ";
        $sqlAppend .= $filterPopularity ? "ASC" : "DESC";
    }
    if($datesGiven or isset($filterOrganiser) or $filterType <> "all"){
        $sqlStatement .= " WHERE e.event_id > 0 ";
        error_log($datesGiven);
        $sqlStatement .= ($filterType <> "all") ? " AND event_type = :type " : "";
        $sqlStatement .= ($datesGiven) ? " AND date BETWEEN :dateF AND :dateT " :  "";
        $sqlStatement .= (isset($filterOrganiser)) ? " AND organiser_id = :orgID " :  "";
        //$sqlStatement .= (isset($filterPopularity)) ? " AND organiser_id = :orgID " :  "";
    }
    $sqlStatement .= $sqlAppend;
    try {
        error_log($sqlStatement);
        $sql = $db->prepare($sqlStatement);
        if($datesGiven){
            $sql->bindParam(':dateF', $filterDateFrom, PDO::PARAM_STR, 20);
            $sql->bindParam(':dateT', $filterDateTo, PDO::PARAM_STR, 20);
        }
        if(isset($filterOrganiser)){
            $sql->bindParam(':orgID', $filterOrganiser, PDO::PARAM_INT);
        }
        if($filterType <> "all"){
            $sql->bindParam(':type', $filterType, PDO::PARAM_STR, 20);
        }
        $success = $sql->execute();
        if($success){
            return $sql->fetchAll();
        } else {
            return "Failed: ".$sql->fetch();;
        }
        
    } catch(PDOException $e) {
        echo "Error: ".$e->getMessage();
    }
}

function getEvent($id){
    global $db;
    $sqlStatement = "SELECT event_id, event_type, name, description, date, organiser_id, picture, venue
        FROM events e WHERE e.event_id=:id";
    $sqlStatement .= $sqlAppend;
    try {
        error_log($sqlStatement);
        $sql = $db->prepare($sqlStatement);
        $sql->bindParam(':id', $id, PDO::PARAM_INT);
        $success = $sql->execute();
        if($success){
            return $sql->fetch();
        } else {
            return "Failed: ".$sql->fetch();;
        }
        
    } catch(PDOException $e) {
        echo "Error: ".$e->getMessage();
    }
}

function registerInterest($id, $userID){
    return true;
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