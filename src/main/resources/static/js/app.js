function loadTable(url){
    $(".table-block").html("<h1>Loading...</h1>");
    $.get(url +"/table", function(data){
        $(".table-block").html(data);
    })
}


