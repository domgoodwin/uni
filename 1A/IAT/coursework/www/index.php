<?php require('parts/header.php'); ?>

<?php 
    $results;
    if ($_SERVER["REQUEST_METHOD"] == "POST" and ! isset($_POST['clear'])) {
        $type = prune($_POST["type-filter"]);
        $dateF = prune($_POST["date-from"]);
        $dateT = prune($_POST["date-to"]);
        $popularity = (prune($_POST["popularity-filter"]) == "ascending") ? true : false ;
        error_log($type.$dateF.$popularity);
        $results = getEvents($type, $dateF, $dateT, $popularity, null);
    } else {
        $results = getEvents("all", null, null, true, null);
    }

?>
<h1>Events</h1>

<?php if($_SESSION['user_id'] <> 0){ ?>
    <div class="search">
        <h2>Filters</h2>
        <form action="<?php echo htmlspecialchars($_SERVER["PHP_SELF"]);?>" method="post" onSubmit="return clearFilters();">
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
                <input type="submit" name="filter" value="Filter">
            </div>
            <div class="filter">
                <input type="submit" name="clear" value="Clear" >
            </div>
        </form>
    </div>
    <hr>
<?php } ?>

<!-- Events views -->

<?php
    if(sizeof($results) == 0){
        echo "<div class='warning'>No events found for criteria</div>";
    } else {
        foreach ($results as &$row) {
            echo createEvent($row);
        }
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