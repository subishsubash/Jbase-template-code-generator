'use strict';

var singleUploadForm = document.querySelector('#singleUploadForm');
var singleFileUploadInput = document.querySelector('#singleFileUploadInput');
var singleFileUploadError = document.querySelector('#singleFileUploadError');
var singleFileUploadSuccess = document.querySelector('#singleFileUploadSuccess');

function uploadSingleFile(file) {
    var formData = new FormData();
    formData.append("template", file);

    function handleFile(data) {
        console.log(this.response || data);
        var file = URL.createObjectURL(this.response || data);
        var fileName = request.getResponseHeader("fileName");
        var response = request.getResponseHeader("responseMsg");
        var errorMsg = request.getResponseHeader("errorMsg");
        var a = document.createElement("a");
        // if `a` element has `download` property
        if(response != "Successful") {
        	console.log(errorMsg);
        	alert(response);
        }
        else{
	        if ("download" in a) {
	          a.href = file;
	          a.download = fileName;
	          document.body.appendChild(a);
	          a.click();
	          document.body.removeChild(a);
	        } else {
	          // use `window.open()` if `download` not defined at `a` element
	          window.open(file)
	        }
        }
      }
    
    var request = new XMLHttpRequest();
    request.responseType = "blob";
    request.onload = handleFile;
    request.open("POST","/template/uploadexcel");
    request.send(formData);
}


$('#formSubmit').bind('click', function(event) {
    var files = singleFileUploadInput.files;
    uploadSingleFile(files[0]);
    event.preventDefault();
    $("#singleUploadFormId").trigger('reset');
    $("form p").text("Drag your files here or click here to upload Excel.");
});

$('#singleFileUploadInput').change(function() {
	 var files = singleFileUploadInput.files;
	 $('form p').text(singleFileUploadInput.files[0].name + " file selected");
});
