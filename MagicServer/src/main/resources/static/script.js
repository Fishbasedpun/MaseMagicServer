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
	
	function updateAuthUI() {
        const token = localStorage.getItem("jwt");
        if (token) {
            const decodedToken = jwt_decode(token);
            const username = decodedToken.username || "User";
            
            $("#login-btn").hide();
            $("#user-info").show();
            $("#username").text(username);
        } else {
            $("#login-btn").show();
            $("#user-info").hide();
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
					console.log(response);
					return response.json();  // Parse response as JSON (if any)
				} else if (response.status === 401) {  // Unauthorized
					showAlert('Invalid username or password!', 'danger');
				} else {
					showAlert('Something went wrong. Please try again.', 'danger');
				}
			})
			.then(data => {
				{
					console.log(data.jwt);
					TokenStorage.saveToken(data.jwt);

					// Decode the token to extract the role (requires jwt-decode library)
					const decodedToken = jwt_decode(data.jwt);
					const role = decodedToken.role;
					// Save the role in localStorage
					localStorage.setItem("role", role);
					updateAuthUI();
					$("#login-modal").fadeOut();
					// **Force content load after login**
					setTimeout(() => {
						window.location.reload(); // Refresh to trigger script.js
					}, 500);
				}
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
        updateAuthUI();
    });
	updateAuthUI();
});