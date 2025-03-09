$(document).ready(function() {
	$(document).on("click", "#collecion", function(e) {
		e.preventDefault();
		$("#dynamic-content").load("content/collection.html", function() {
			$("#sidebar, nav").fadeIn();
		});
	});
	
	$(document).on("click", "#create", function(e) {
		e.preventDefault();
		$("#dynamic-content").load("content/create.html", function() {
			$("#sidebar, nav").fadeIn();
		});
	});
	
	$(document).on("click", "#edit", function(e) {
		e.preventDefault();
		$("#dynamic-content").load("content/edit.html", function() {
			$("#sidebar, nav").fadeIn();
		});
	});
});