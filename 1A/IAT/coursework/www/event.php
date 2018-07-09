<?php require('parts/header.php'); ?>
<?php 
    // Get event requested
    $event = "";
    $message = "";
    $script = "";

    if ($_SERVER["REQUEST_METHOD"] == "POST") {
        // REGISTER INTEREST
        $registered = registerInterest(getId(), $_SESSION['user_id']);

        if($registered){
            $message = "Interest has been noted!";
            $script = "<script>document.getElementById('register').disabled = true;</script>";
        } else {
            $message = "Error: " + $registered;
        }
    } 
    $event = getEvent(getId()); 

    if(checkIfPrevInterested(getId(), $_SESSION['user_id'])){
        $message = "Your interest has been registered!";
        $script = "<script>document.getElementById('register').disabled = true;</script>";
    }

    function getId(){
        return $_SERVER['QUERY_STRING'];
    }

    function formatStudent($row){
        $html = "<tr class='student'>";
        $html .= "<td class='student'>".$row['firstname']." ".$row['lastname']."</td>";
        $html .= "<td class='student'>".$row['username']."</td>";
        $html .= "</tr>";
        return $html;
    }
?>
<div class='eventView'>
<a class='back' href='index.php'>Back</a>
<div class='message'><?php echo $message; ?> </div>
<h1 class='event'><?php echo $event['name'] ?></h1>
<h2 class='event'><?php echo $event['date'] ?></h2>
<p><?php echo $event['description']; ?></p>
<p><strong>Venue: </strong> <?php echo $event['venue']; ?> </strong></p>
<div class='event-image'><img src="img/<?php echo $event['picture'] ?>"></div>
</div>
<div class='students'>
    <h3>Interested students </h3>
    <?php if($_SESSION['user_type'] == "org" and $_SESSION['user_id'] == $event['organiser_id']){ ?>
        <?php
            $students = getInterestedStudents($event['event_id']);
            $table = "<table class='students'>";
            $table .= "<tr ><th class='student'>Name</th><th class='student'>Username</th></tr>";
            foreach ($students as &$student) {
                $table .= formatStudent($student);
            }
            $table .= "</table>";
            echo $table;
        ?>
    <?php } ?>
    </div>
<div class="container">

    <form action="<?php echo htmlspecialchars("event.php") ."?".$event['event_id'];?>" method="post">
        <input type="submit" value="Register Interest" id='register'>
    </form>
    <?php if($_SESSION['user_type'] == "org" and $_SESSION['user_id'] == $event['organiser_id']){ ?>

            <a href="eventu.php?<?php echo $event['event_id']?>">Update</a>
    <?php } ?>


</div>
<?php echo $script ?>



<?php require('parts/footer.php'); ?>