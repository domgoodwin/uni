<?php require('parts/header.php'); ?>
<?php 
    // Get event requested
    $event = "";
    $message = "";
    $script = "";


    $event = getEvent(getId()); 
    if($_SERVER["REQUEST_METHOD"] == "POST"){
        $type = prune($_POST["type"]);
        $name = prune($_POST["name"]);
        $desc = prune($_POST["desc"]);
        $date = prune($_POST["date"]);
        $venue = prune($_POST["venue"]);
        $eid = getId();
        $updated = updateEvent($type, $name, $desc, $date, $venue, $eid);
        if($updated){
            header('Location: '."event.php?".$eid);
        } else {
            echo $updated;
        }
    }

    $script = "<script> ";
    $script .= "document.getElementById('type').value = '".$event['event_type']."';";
    $script .= "document.getElementById('name').value = '".$event['name']."';";
    $script .= "document.getElementById('desc').value = '".$event['description']."';";
    $script .= "document.getElementById('date').value = '".$event['date']."';";
    $script .= "document.getElementById('venue').value = '".$event['venue']."';";
    $script .= "</script>";

    
    function getId(){
        return $_SERVER['QUERY_STRING'];
    }
?>

<div class="container">
    <form action="<?php echo htmlspecialchars($_SERVER["PHP_SELF"])."?".getId();?>" method="post">
        Type: 
        <select class="filter" name='type' id='type'>
            <option value='sport'>Sport</option>
            <option value='culture'>Culture</option>
            <option value='other'>Other</option>
        </select>
        Event name:
        <input type="text" name="name" id='name'>
        Description:
        <textarea name='desc' id='desc'></textarea>
        Date:
        <input type="date" name="date" id='date'>
        Venue:
        <input type="text" name="venue" id="venue">

        <input type="submit" name="update" value="Update">
    </form>
</div>

<?php echo $script; ?>


<?php require('parts/footer.php'); ?>