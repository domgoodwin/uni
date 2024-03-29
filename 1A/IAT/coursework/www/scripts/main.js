function updateField(){
    if(document.getElementById('type-select').value == "organiser"){
        document.getElementById('email-box').style.display = "block";
        document.getElementById('email-field').required  = true;
    } else {
        document.getElementById('email-box').style.display = "none";
        document.getElementById('email-field').required  = false;
    }
}


function clearFilters(){
    document.getElementById('date-to').value = "";
    document.getElementById('date-from').value = "";
    document.getElementById('type-filter').value = "all";
    document.getElementById('popularity-filter').value = "ascending";
    return true;
}


function validateEmail(){
    email = document.getElementById("email-field").value;
    rx = /[A-Za-z0-9]*@[A-Za-z0-9]*[\.]{1}[A-Za-z.]*/;
    if(rx.test(String(email).toLowerCase())){
        document.getElementById("email-field").style.borderColor = "green";
        return true;
    } else {
        document.getElementById("email-field").style.borderColor = "red";
        return false;
    }
}