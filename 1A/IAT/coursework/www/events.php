<?php require('parts/header.php'); ?>

<h1>Events</h1>

<?php if($_SESSION['user_id'] <> 0){ ?>
    SHOW FILTERS: type, popularity, date
    Also allow clicking
<?php } ?>

<!-- Events views -->

<?php 
    $results = getEvents(null, null, null, null);
    foreach ($results as &$row) {
        echo createEvent($row);
    }

    function createEvent($row){
        $eventHtml = "<div class='event'><h1 class='event'>";
        $eventHtml .= $_SESSION['user_id'] == 0 ? $row['name'] : "<a href='event.php?".$row['event_id']."'>".$row['name']."</a>";
        $eventHtml .= "</h1><h2 class='event'>".$row['date']."</h2>";
        $eventHtml .= "<p class='event'>".$row['description']."</p>";
        $eventHtml .= "</div>";
        return $eventHtml;
    }
?>

<?php require('parts/footer.php'); ?>