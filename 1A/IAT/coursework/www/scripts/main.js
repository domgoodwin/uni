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
