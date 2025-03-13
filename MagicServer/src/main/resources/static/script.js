const TokenStorage = {
    saveToken: function(token) {
        localStorage.setItem("jwt", token);
    },
    getToken: function() {
        return localStorage.getItem("jwt");
    },
    removeToken: function() {
        localStorage.removeItem("jwt");
    }
};
function escapeQuotes(str) {
    return str
        ? str.replace(/\\/g, "\\\\") // Escape backslashes
             .replace(/'/g, "\\'")  // Escape single quotes
             .replace(/"/g, '\\"')  // Escape double quotes
             .replace(/\r?\n/g, " ") // Remove newlines
        : "";
}

$(document).ready(function() {
	updateAuthUI()
	$("#searchButton").click(function () {
	    let query = $("#searchInput").val().trim();
	    if (query) {
	        $("#dynamic-content").load("content/collection.html", function () {
	            window.history.pushState({}, "", `?search=${encodeURIComponent(query)}`); 
				loadCards(query);
	        });
	    }
	});

	   $("#searchInput").keypress(function (e) {
	       if (e.which === 13) { // Enter key pressed
	           $("#searchButton").click();
	       }
	   });
	   
	   
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
	
	$(document).on("click", "#registration", function(e) {
		e.preventDefault();
		$("#dynamic-content").load("content/registration.html", function() {
			$("#sidebar, nav").fadeIn();
		});
	});
	
	$(document).on("click", "#favorites", function(e) {
		e.preventDefault();
		$("#dynamic-content").load("content/favorites.html", function() {
			$("#sidebar, nav").fadeIn();
		});
	});
	
	$(document).on("click", "#favorite-stats", function(e) {
		e.preventDefault();
		$("#dynamic-content").load("content/favorite-stats.html", function() {
			$("#sidebar, nav").fadeIn();
		});
	});
		
	function updateAuthUI() {
	    let token = TokenStorage.getToken();
	    console.log("Checking token on page load:", token);

	    if (token) {
	        try {
	            const decodedToken = jwt_decode(token);
	            console.log("Decoded Token:", decodedToken);

	            const username = decodedToken.sub || "Unknown User";
	            const role = decodedToken.role || "GUEST"; // Default role


	            // Show user info
	            $("#login-btn").hide();
	            $("#user-info").show();
	            $("#username").text(username);

	            // Hide all elements first
	            $(".admin-only, .user-only").hide();

	            if (role.includes("ADMIN")) {
	                $(".admin-only").show();
	                $(".user-only").show();
	            } else if (role.includes("USER")) {
	                $(".user-only").show();
	            } else {
	                console.log("User role not recognized ('" + role + "'), hiding all elements.");
	            }
	        } catch (error) {
	            console.error("Error decoding token:", error);
	            TokenStorage.removeToken();
	            updateAuthUI(); // Re-run with no token
	        }
	    } else {
	        console.log("No token found. Hiding all elements.");
	        $("#login-btn").show();
	        $("#user-info").hide();
	        $(".admin-only, .user-only").hide();
	    }
	}

	
	$('#login-btn').click(function() {
        $('#login-modal').show();
    });

    $('.close').click(function() {
        $('#login-modal').hide();
    });
	
	 $(document).on("click", "#submit-login", function(event) {
		event.preventDefault(); // Prevent form submission

		// Get values from form inputs
		var username = document.getElementById('username-input').value;
		var password = document.getElementById('password-input').value;

		// Send login request to backend
		loginUser(username, password);
	});

	function loginUser(username, password) {
		// Create the URL where the login validation will be processed
		const url = '/api/users/login';  // No need for username/password in URL anymore

		// Hide sidebar and navbar when on the login page
		$("#sidebar").hide();
		$("nav").hide(); // Optionally hide navbar as well

		// Prepare the login data to send as JSON in the request body
		const loginData = {
			username: username,
			password: password
		};

		// Fetch request to your Java backend endpoint
		fetch(url, {
			method: 'POST', // Changed to POST method
			headers: {
				'Content-Type': 'application/json',  // Make sure we're sending JSON
			},
			body: JSON.stringify(loginData)  // Send login data as JSON
		})
			.then(response => {
				if (response.ok) {  // Check if HTTP status is 200 OK
					return response.json();  // Parse response as JSON (if any)
				} else if (response.status === 401) {  // Unauthorized
					showAlert('Invalid username or password!', 'danger');
				} else {
					showAlert('Something went wrong. Please try again.', 'danger');
				}
			})
			.then(data => {
			    TokenStorage.saveToken(data.jwt);

			    const decodedToken = jwt_decode(data.jwt);
			    const role = decodedToken.role;
			    localStorage.setItem("role", role);

			    updateAuthUI(); // ✅ Update UI immediately after login

			    $("#login-modal").fadeOut();
			})
			.catch(error => {
				console.error('Error:', error);
				showAlert('Invalid password or username.', 'warning');
			});
	}

	// Function to show Bootstrap alert messages
	function showAlert(message, type) {
		const messages = document.getElementById("messages");
		// Clear any previous messages
		messages.innerHTML = "";

		// Create and display the Bootstrap alert
		const alertDiv = document.createElement('div');
		alertDiv.classList.add('alert', `alert-${type}`, 'alert-dismissible', 'fade', 'show');
		alertDiv.setAttribute('role', 'alert');
		alertDiv.innerHTML = `${message} <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>`;
		messages.appendChild(alertDiv);
	}
	
	$("#logout-btn").click(function() {
	    localStorage.removeItem("jwt");
	    TokenStorage.removeToken();
	    updateAuthUI(); // Ensure UI is reset
	});
});