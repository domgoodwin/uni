<?php require('parts/header.php'); ?>

<?php 
    $results;
    if ($_SERVER["REQUEST_METHOD"] == "POST" and ! isset($_POST['clear'])) {
        $type = prune($_POST["type-filter"]);
        $dateF = prune($_POST["date-from"]);
        $dateT = prune($_POST["date-to"]);
        $alpha = (prune($_POST["alpha-filter"]) == "on") ? true : false;
        $popularity = (prune($_POST["popularity-filter"]) == "ascending") ? true : false ;
        $org = prune($_POST["org-only"]) == "on" ?  $_SESSION['user_id'] : null;
        error_log($type.$dateF.$popularity);
        $results = getEvents($type, $dateF, $dateT, $popularity, $org, $alpha);
    } else {
        $results = getEvents("all", null, null, true, null, false);
        $_POST['cur-filter'] = "";
        echo "<script> clearFilters(); </script>";
    }

?>
<h1>Events</h1>

<?php if($_SESSION['user_id'] <> 0){ ?>
    <div class="search">
        <h2>Filters</h2>
        <form action="<?php echo htmlspecialchars($_SERVER["PHP_SELF"]);?>" method="post">
            <div class="filter">
                Type: 
                <select class="filter" name='type-filter' id='type-filter'>
                    <option value='all'>All</option>
                    <option value='sport'>Sport</option>
                    <option value='culture'>Culture</option>
                    <option value='other'>Other</option>
                </select>
            </div>
            <div class="filter">
                Date From: 
                <input type="date" class="filter" name="date-from" id="date-from">
                To: 
                <input type="date" class="filter" name="date-to" id="date-to">
            </div>
            <div class="filter">
                Popularity
                <select class="filter" name='popularity-filter' id='popularity-filter'>
                    <option value='ascending'>Ascending</option>
                    <option value='descending'>Descending</option>
                </select>
            </div>
            <div class="filter">
                Alphabetical
                <input type="checkbox" class="filter" name='alpha-filter' id='alpha-filter'>
            </div>
            <?php if($_SESSION['user_type'] == "org"){ ?>
                <div class="filter">
                    Only show your events: 
                    <input type="checkbox" name='org-only' id='org-only'>
                </div>
            <?php } ?>
            <div class="filter">
                <input type="submit" name="filter" value="Filter">
            </div>
            <div class="filter">
                <input type="submit" name="clear" value="Clear" >
            </div>
        </form>
    </div>

    <div>
        <h3> <?php echo $_POST['cur-filter']; ?> </h3>
    </div>
    <hr>
<?php } ?>

<!-- Events views -->

<?php
    if(sizeof($results) == 0){
        echo "<div class='warning'>No events found for criteria</div>";
    } else {
        foreach ($results as &$row) {
            echo formatEvent($row);
        }
    }


    function formatEvent($row){
        $eventHtml = "<div class='event'><h1 class='event'>";
        $eventHtml .= $_SESSION['user_id'] == 0 ? $row['name'] : "<a href='event.php?".$row['event_id']."'>".$row['name']."</a>";
        $eventHtml .= "</h1><h2 class='event'>".$row['date']."</h2>";
        $eventHtml .= "<p class='event'>".$row['description']."</p>";
        $eventHtml .= "</div>";
        return $eventHtml;
    }
?>

<?php require('parts/footer.php'); ?>