<?php require('parts/header.php'); ?>

    <?php
        $newUrl = "http://www.google.com";
        //header('Location: '.$newUrl);
        echo $_SESSION['user_id'];
        echo "//";
        echo $_SESSION['user_type'];
    ?>

<?php require('parts/footer.php'); ?>