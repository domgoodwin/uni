<?php require('parts/header.php'); ?>

<?php 
    if($_SERVER["REQUEST_METHOD"] == "POST"){
        $type = prune($_POST["type"]);
        $name = prune($_POST["name"]);
        $desc = prune($_POST["desc"]);
        $date = prune($_POST["date"]);
        $pic = prune(basename($_FILES["picture"]["name"]));
        $venue = prune($_POST["venue"]);
        $created = createEvent($type, $name, $desc, $date, $pic, $venue, $_SESSION['user_id']);
        error_log("created/".$created);
        if(is_numeric($created)){
            upload();
            header('Location: '."event.php?".$created);
        } else {
            echo $created;
        }
    }

    function upload(){
        $target = "img/" . basename($_FILES["picture"]["name"]);
        $ext = strtolower(pathinfo($target, PATHINFO_EXTENSION));
        if(! ($ext == "png" or $ext == "jpg" or $ext == "jpeg")){
            return "Invalid file extension";
        }
        if (move_uploaded_file($_FILES["picture"]["tmp_name"], $target)) {
            return true;
        } else {
            return "Unknown error occured uploading file";
        }
    }


?>

<h1>Create event</h1>

<div class="container">
    <form action="<?php echo htmlspecialchars($_SERVER["PHP_SELF"]);?>" method="post" enctype="multipart/form-data">
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
        Photo:
        <input type="file" name="picture" id="picture">
        Venue:
        <input type="text" name="venue" id="venue">

        <input type="submit" name="create" value="Create">
    </form>
</div>
<?php require('parts/footer.php'); ?>