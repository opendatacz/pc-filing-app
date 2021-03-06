function editTender(tender,title) {
		sessionStorage.editTenderURL = tender;
		sessionStorage.contractTitle = title;
}	


function linkTender(tenderTitle,tenderURI) {
	var link = $('<a>');
	link.attr('href','buyer-view-tender.html');
	link.on('click',function(){ showTender(tenderTitle,tenderURI); });
	link.append("tender");
	return link;
}

function linkBuyer(entityName,entityURI) {
	
	var buyer = $('<a>');	
	buyer.attr('href','entity-buyer.html');
	buyer.on('click',function(){ showEntity(entityURI);  });
	buyer.append(entityName);
	return buyer;
}

var statsLoaded = false;

function userStatsLoad() {

	$.getJSON("PCFilingApp?action=buyerStats", function(data) {
		if (data != null) {			
			for ( var type in data.data )
			{				
				$("#stats"+type+" td:last-child").html(data.data[type]);
			}
			statsLoaded = true;
			showStats();
		}
	});
}

function showStats() {
	if ( !statsLoaded ) {
		userStatsLoad(); 
	}
	else
	{		
		$("#statsTable").fadeIn('fast').removeClass('hide');
		$("#statsHide i.icon-plus").addClass('hide');
		$("#statsHide i.icon-minus").removeClass('hide');		
	}
}

function hideStats() {
	$("#statsTable").fadeOut('fast').addClass("hide");
	$("#statsHide i.icon-plus").removeClass('hide');
	$("#statsHide i.icon-minus").addClass('hide');	
}

function userStats(value) {
	
	$.getJSON("SystemManager?action=updateUserPreference&preference=userStats&value="+ value,
			function(data) {
				if (data != null) {
					if (data.success) {
						if (value == 'true') {
							showStats();
						} else {
							hideStats();
						}
					}
				}
			});		
}

function newDoc(inputName,name) {
	var newD = $('<li class="addDocLI hide">');
	var newClose = $('<a>').append($('<i>').addClass('icon-remove-sign'));
	newClose.css('cursor','pointer');
	newClose.on('click',function() {				
		$(this).closest('.addDocLI').fadeOut(function(){ $(this).remove();});
	});
	newD.append(newClose);
	newD.append($('<label for="'+inputName+'">').append(name));
	newD.append($('<br>'));
	newD.append($('<input type="file" name="'+inputName+'">'));
	return newD;
} 
