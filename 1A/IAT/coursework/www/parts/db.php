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
    $_POST['cur-filter'] = "Current filters are: ";
    global $db;
    error_log("dt //".$filterDateTo);
    error_log("df //".$filterDateFrom);
    error_log("fil //".$filterType);
    error_log("org // ".$filterOrganiser);
    $datesGiven = ! (empty($filterDateFrom) and empty($filterDateTo));
    //error_log(isset($filterDateFrom)."/" . isset($filterDateTo)."//" . (trim($filterDateFrom) <> '')."///" . (trim($filterDateTo) <> ''));
    $sqlStatement = "SELECT e.event_id, event_type, name, description, date, organiser_id FROM events e";
    // If any params are set
    $sqlAppend = "";
    if(isset($filterPopularity)) {
        $_POST['cur-filter'] .= $filterPopularity ? " popularity ascending" : " popularity descending,";
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
            $_POST['cur-filter'] .= ", dates filter";
            $sql->bindParam(':dateF', $filterDateFrom, PDO::PARAM_STR, 20);
            $sql->bindParam(':dateT', $filterDateTo, PDO::PARAM_STR, 20);
        }
        if(isset($filterOrganiser)){
            $_POST['cur-filter'] .= ", only show org events";
            $sql->bindParam(':orgID', $filterOrganiser, PDO::PARAM_INT);
        }
        if($filterType <> "all"){
            $_POST['cur-filter'] .= ", type filter:" . $filterType;
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
    global $db;
    $sqlStatement = "INSERT INTO event_interest (event_id, user_id) VALUES (:eid, :uid)";
    $sql = $db->prepare($sqlStatement);
    $sql->bindParam(':eid', $id, PDO::PARAM_INT);
    $sql->bindParam(':uid', $userID, PDO::PARAM_INT);
    $success = $sql->execute();
    if($success){
        return true;
    } else {
       return $sql->fetch();
    }
}

function checkIfPrevInterested($id, $userID){
    global $db;
    $sqlStatement = "SELECT id FROM event_interest 
        WHERE event_id = :eid AND user_id = :uid";
    error_log($sqlStatement);
    try{
        $sql = $db->prepare($sqlStatement);
        $sql->bindParam(':eid', $id, PDO::PARAM_INT);
        $sql->bindParam(':uid', $userID, PDO::PARAM_INT);
        $success = $sql->execute();
        if($success){
            if($sql->rowCount() == 0){
                return false; // no not prev interested
            } else {
                return true; // yes prev interested
            }
        } else {
            return $sql->fetch();
        }
    } catch(PDOException $e) {
        echo "Error: ".$e->getMessage();
    }
}

function createEvent($type, $name, $desc, $date, $photo, $venue, $oid){
    global $db;
    error_log($type."//".$name."//".$desc."//".$date."//".$photo."//".$venue."//".$oid);
    $sqlStatement = "INSERT INTO events (event_type, name, description, date, picture, organiser_id, venue)
        VALUES (:type, :name, :desc, :date, :pic, :oid, :ven)";
    try{
        $sql = $db->prepare($sqlStatement);
        $sql->bindParam(':type', $type, PDO::PARAM_STR, 10);
        $sql->bindParam(':name', $name, PDO::PARAM_STR, 50);
        $sql->bindParam(':desc', $desc, PDO::PARAM_STR, 500);
        $sql->bindParam(':date', $date, PDO::PARAM_STR, 20);
        $sql->bindParam(':pic', $photo, PDO::PARAM_STR, 30);
        $sql->bindParam(':ven', $venue, PDO::PARAM_STR, 20);
        $sql->bindParam(':oid', $oid, PDO::PARAM_INT);
        $success = $sql->execute();
        if($success){
            $id = (int)$db->lastInsertId();
            return $id;
        } else {
           return $sql->fetch();
        }
    } catch(PDOException $e){
        echo "Error: ".$e->getMessage();
    }
}

function updateEvent($type, $name, $desc, $date, $venue, $eid){
    global $db;
    error_log($type."//".$name."//".$eid);
    $sqlStatement = "UPDATE events 
        SET event_type = :type, name = :name, description = :desc, date = :date, venue = :ven
        WHERE event_id = :eid";
    try{
        $sql = $db->prepare($sqlStatement);
        $sql->bindParam(':type', $type, PDO::PARAM_STR, 10);
        $sql->bindParam(':name', $name, PDO::PARAM_STR, 50);
        $sql->bindParam(':desc', $desc, PDO::PARAM_STR, 500);
        $sql->bindParam(':date', $date, PDO::PARAM_STR, 20);
        $sql->bindParam(':ven', $venue, PDO::PARAM_STR, 20);
        $sql->bindParam(':eid', $eid, PDO::PARAM_STR, 20);
        $success = $sql->execute();
        if($success){
            return true;
        } else {
           return $sql->fetch();
        }
    } catch(PDOException $e){
        echo "Error: ".$e->getMessage();
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