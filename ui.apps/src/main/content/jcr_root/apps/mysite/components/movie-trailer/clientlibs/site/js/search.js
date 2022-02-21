window.onload = () => {
    let searchButton = document.querySelector("#submit_search");
	searchButton.onclick = searchRequest;
}

function searchRequest(){

    let searchParam = document.querySelector("#search_param").value.trim();

    var url= "/bin/mysite/movie-results?fulltext=" + searchParam;

    fetch(url)
    .then(response => response.json())
    .then(data => {


    if(data){
    
        displayResultList(data["results"]);
    }
  })
}
