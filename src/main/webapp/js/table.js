var tableData;

function loadPage(init) {

	if (currentPage < 1) {
		currentPage = 1;
	} else if (currentPage > pagesTotal) {
		currentPage = pagesTotal;
	}

	var address = tableAddress;
	var page = currentPage;
	address += "&page=" + (page - 1);
	address += "&items=" + (tableItemsPerPage);
	if (init) {
		address += "&reload";
		tableData = new Array();
	}

	$.getJSON(address, function(data) {
		if (data.success) {
			if (data.pages != undefined)
				pagesTotal = data.pages;
			if (init)
				paging();
			togglePageButtons();
			tableData[page] = data.data;
			fillTable();
			} else {
			alert("An error occured, please reload the page.");
		}
	});

}

function CPVs(cpv1, cpvAll) {

	var cpv = new Array();

	if (cpv1 != undefined) {
		cpv.push(cpv1.replace(/\S+\//g, ""));						
	}

	if (cpvAll != undefined) {	
            acpvs = (cpvAll.replace(/\S+\//g, "")).split(" ");
            for (x in acpvs)
		cpv.push(acpvs[x]);
		
	}	
        
        
	var list = $("<ul>");	
	$.each(cpv,function(index){
		list.append( $('<li>').append(cpvs["cpv"+cpv[index]]));
	});
	
	list.addClass("table_cpvs");
	
	return list;
}

function paging() {

	if (pagesTotal <= 1)
		return;

	if (pagesTotal > windowSize + 2) {
		$('#showAllPages').removeClass("hide");
	}

	$("#pages").append(
			'<li><a href="#" id="loadPreviousContracts">Prev</a></li>');
	$("#loadPreviousContracts").click(function() {
		if ($(this).parent().hasClass("disabled")) {
			return false;
		}
		;
		currentPage--;
		loadPage();
	});

	for ( var i = 1; i <= pagesTotal; i++) {
		$("#pages").append(
				'<li><a href="#" id="loadContractsPage' + i + '">' + (i)
						+ '</a></li>');
		$("#loadContractsPage" + i).on('click', function() {
			if ($(this).parent().hasClass("active")) {
				return false;
			}
			;
			currentPage = parseInt($(this).attr("id").match(/(\d+)$/)[0], 10);
			loadPage();
		});
	}

	$("#pages").append('<li><a href="#" id="loadNextContracts">Next</a></li>');
	$("#loadNextContracts").click(function() {
		if ($(this).parent().hasClass("disabled")) {
			return false;
		}
		;
		currentPage++;
		loadPage();
	});

}

function editEvent(contract) {
	sessionStorage.editcontractURI = contract;
}

function formatDate(date) {
	var d = new Date(date);	
	return dateFormat(d,"yyyy-mm-dd");
	
}

function togglePageButtons() {
	$("#pages li").addClass("reallyhide");
	$("#loadPreviousContracts").parent().removeClass("reallyhide");
	$("#loadNextContracts").parent().removeClass("reallyhide");
	$("#loadContractsPage" + 1).parent().removeClass("reallyhide");
	$("#loadContractsPage" + pagesTotal).parent().removeClass("reallyhide");

	$("#pages li").removeClass("active");
	if (currentPage == 1) {
		$("#loadPreviousContracts").parent().addClass("disabled");
	} else {
		$("#loadPreviousContracts").parent().removeClass("disabled");
	}
	;
	if (currentPage == pagesTotal) {
		$("#loadNextContracts").parent().addClass("disabled");
	} else {
		$("#loadNextContracts").parent().removeClass("disabled");
	}
	;
	$("#loadContractsPage" + currentPage).parent().addClass("active");

	for ( var i = currentPage - windowSize; i <= currentPage + windowSize; i++) {
		if (i > 1 && i < pagesTotal) {
			$("#loadContractsPage" + i).parent().removeClass("reallyhide");
		}
	}

	$(".3dots").remove();
	if ((currentPage - windowSize - 1 >= 1)
			&& $("#loadContractsPage" + (currentPage - windowSize - 1))
					.parent().hasClass('reallyhide')) {
		$("#loadContractsPage" + (currentPage - windowSize - 1)).parent()
				.after('<li class="3dots disabled"><a href="#">...</a></li>');
	}
	if ((currentPage + windowSize + 1 <= pagesTotal)
			&& $("#loadContractsPage" + (currentPage + windowSize + 1))
					.parent().hasClass('reallyhide')) {
		$("#loadContractsPage" + (currentPage + windowSize + 1)).parent()
				.before('<li class="3dots disabled"><a href="#">...</a></li>');
	}
}

function saveEventInfo(contractURI, title, description, price, currency, cpvString, place) {
	sessionStorage.contractURI = decodeURIComponent(contractURI);
	sessionStorage.contractTitle = decodeURIComponent(title);
	sessionStorage.contractDescription = decodeURIComponent(description);
	sessionStorage.contractPrice = decodeURIComponent(price);
	sessionStorage.contractCurrency = decodeURIComponent(currency);
	sessionStorage.contractCpvString = decodeURIComponent(cpvString);
	sessionStorage.contractPlace = decodeURIComponent(place);
}

function saveEventInfo(i) {
	sessionStorage.contractURL = tableData[currentPage][i].contractURI;
	sessionStorage.contractTitle = tableData[currentPage][i].title;
	sessionStorage.contractDescription = tableData[currentPage][i].description;
	sessionStorage.contractPrice = tableData[currentPage][i].price;
	sessionStorage.contractCurrency = tableData[currentPage][i].currency;
	sessionStorage.contractCpvString = tableData[currentPage][i].cpv1URL.replace("http://purl.org/weso/cpv/2008/","");
	sessionStorage.contractPlace = tableData[currentPage][i].place;
}

function editEvent(contract) {
	sessionStorage.editContractURL = contract;
}
