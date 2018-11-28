const createScheduleURL = "";
const viewScheduleURL = "";
const pageURL = window.location.href.split("?")[0];

let schedule = "";
let secretCode = "";

function clickme() {
  console.log("test");
}

function handleCreate(e){
  var form = document.createForm;
  var name = form.name.value;
  var author = form.author.value;
  var startDate = form.startDate.value;
  var endDate = form.endDate.value;
  var startTime = form.startTime.value;
  var endTime = form.endTime.value;
  var duration = form.duration.value;
  
  
  
  var xhr = new XMLHttpRequest();
  xhr.open("POST", createScheduleURL, true); 
  
  // TODO: write code to add params
  // uses URLSearchParams()
  let url = new URL(window.location.href);
  let urlprot = url.protocol;
  let urlpath = url.pathname;
  let params = url.searchParams;
  params.set("name", name);
  params.set("author", author);
  params.set("startDate", startDate);
  params.set("endDate", endDate);
  params.set("startTime", startTime);
  params.set("endTime", endTime);
  params.set("duration", duration);

  updateURL(params.toString());

  
  // window.location.href = pageURL + "?name=" + name + "&author=" + author + "&startDate=" + startDate + 
  // "&endDate=" + endDate + "&startTime=" + startTime + "&endTime=" + endTime + "&duration=" + duration;
  
  // xhr.send();
  
  // xhr.onloadend = function () {
  //   console.log(xhr);
  //   console.log(xhr.request);
  //   if (xhr.readyState == XMLHttpRequest.DONE) {
  //     console.log ("XHR:" + xhr.responseText);
  //     processCreateResponse(xhr.responseText);
  //   } else {
      
  //   }
  // };
}

function handleRetrieve(){

}

function processCreateResponse(response){
  console.log("response:" + response);
  var js = JSON.parse(response);
  
  var secretCode = js["secretCode"];
  var releaseCode = js["releaseCode"];
  
  alert("Successfully created the schedule");
}

function viewSchedule() {
  
}

function refreshSchedule() {
  var schedule = JSON.parse(response);

  var days = schedule["days"];
  var startTime = schedule["startTime"];
  var endTime = schedule["endTime"];
  var period = schedule["timePeriod"];


}

function addRow(id, row, timeString) {
  // Get a reference to the table
  var table = document.getElementById(id);

  // Insert a row in the table at row index 0
  var newRow = table.insertRow(row);

  // Insert a cell in the row at index 0
  var newCell = newRow.insertCell(5);

  // Append a text node to the cell
  var newText = document.createTextNode('New top row');
  newCell.appendChild(newText);
}

function updateURL(params) {
  if (history.pushState) {
      var newurl = window.location.port + "//" + window.location.host + window.location.pathname + "?" + params;
      window.history.pushState({path:newurl},'',newurl);
  }
}