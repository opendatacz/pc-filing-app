function userPreferences() {

	$.getJSON("SystemManager?action=getUserPreferences", function(data) {
		if (sessionStorage.username != undefined) {
			$("#username").append(sessionStorage.username);
		}
		if (data == null || data.length == 0) {
			sessionStorage.clear();
			window.location.href = "./";
		} else {		
			
			$("#username").html(data.username);
			sessionStorage.username = data.username;
			
			if (data.userHelper && data.userHelper == "on") {
				$("#userHelper").slideDown().removeClass("hide");
			}
			
			if (data.userStats && data.userStats == "true") {
				showStats();				
			}
		}
	});
	
}

function userHelper(value) {
	$.getJSON(
			"SystemManager?action=updateUserPreference&preference=userHelper&value="
					+ value, function(data) {
				if (data != null) {
					if (data.success) {
						if (value == "on") {
							$("#userHelper").slideDown().removeClass("hide");
						} else {
							$("#userHelper").slideUp().addClass("hide");
						}
					}
				}
			});
}

function checkUser() {
	userPreferences();
}

function showTender(title, tender) {		
	sessionStorage.tenderURL =  decodeURIComponent(tender);
	sessionStorage.contractTitle =  decodeURIComponent(title);
}

function showEvent(contract) {		
	sessionStorage.contractURL =  decodeURIComponent(contract);
	sessionStorage.buyerURL = "";	
}

function showEvent(contract,ns) {		
	sessionStorage.contractURL =  decodeURIComponent(contract);
	sessionStorage.buyerURL =  decodeURIComponent(ns);
	
}

function showEntity(entity) {
	sessionStorage.entity = decodeURIComponent(entity);
}

$(window).ready(function() {
	userPreferences();
});
