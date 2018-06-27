<?php require('parts/header.php'); ?>
<?php 
    // Get event requested
    $event;
    $message;
    $script;

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

    function getId(){
        return $_SERVER['QUERY_STRING'];
    }
?>

<a class='back' href='events.php'>Back</a>
<div class='message'><?php echo $message; ?> </div>
<h1 class='event'><?php echo $event['name'] ?></h1>
<h2 class='event'><?php echo $event['date'] ?></h2>
<p><?php echo $event['description']; ?></p>
<p><strong>Venue: </strong> <?php echo $event['venue']; ?> </strong></p>
<div class='event-image'><img src="img/<?php echo $event['picture'] ?>"></div>

<div class="container">
    <form action="<?php echo htmlspecialchars("event.php") ."?".$event['event_id'];?>" method="post">
        <input type="submit" value="Register Interest" id='register'>
    </form>
</div>
<?php echo $script ?>



<?php require('parts/footer.php'); ?>