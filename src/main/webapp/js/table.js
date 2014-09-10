var TABLE = {
  itemsPerPage: 10,
  dom: {
    $body: $('#contractTable tbody'),
    $contractTable: $('#contractTable'),
    $pagination: $("#pagination"),
    $predictBiddersModal: $("#predict-bidders"),
    $progressbar: $("#progressbar"),
    $template: $("#tablePage").html(),
  },

  display: function (data) {
    if (data["data"].length <= TABLE.itemsPerPage) {
      //TABLE.dom.$pagination.find(".next, .last").addClass("disabled");
      TABLE.dom.$pagination.hide();
    }
    TABLE.dom.$progressbar.hide();
    TABLE.dom.$contractTable.removeClass("hide").fadeIn("slow");
    result = Mustache.render(TABLE.dom.$template, {
        rows: jQuery.map(data["data"], function (item, i) {
                var row = jQuery.extend(true, {}, item);
                if (typeof row.deadline !== "undefined" && new Date(row.deadline) >= new Date()) {
                  row.openDisabled = true;
                }
                if (typeof row.price !== "undefined" && typeof row.currency !== "undefined") {
                  row.price = Number(row.price).toFixed(2);
                }
                row.cpvs = CPVs(row.cpv1URL, row.cpvAdd).html();
                if (typeof row.modified !== "undefined") {
                  row.modified = APP.util.dateFormat(row.modified);
                }
                if (typeof row.created !== "undefined") {
                  row.created = APP.util.dateFormat(row.created);
                }
                if (typeof row.deadline !== "undefined") {
                  row.deadline = APP.util.dateFormat(row.deadline);
                }
                row.encodedContractURI = encodeURIComponent(row.contractURI);
                return row;
              })
      });
    TABLE.dom.$body.html(result);
  },
  init: function (tableName) {
    Mustache.parse(TABLE.$template);
    TABLE.dom.$contractTable.tooltip({});

    TABLE.dom.$contractTable
      .delegate(".contract-link", "click", function (e) {
        sessionStorage.contractURL = TABLE.util.getClosestContractUri(e);
      })
      .delegate(".confirm", "click", function (e) {
        return confirm($(e.target).data("confirmation"));
      })
      .delegate(".contract-edit-link", "click", function (e) {
        sessionStorage.editContractURL = TABLE.util.getClosestContractUri(e); 
      })
      .delegate(".predict-bidders", "click", function (e) {
        services.predictBidders(TABLE.util.getClosestContractUri(e), function (data) {
          var $modal = TABLE.dom.$predictBiddersModal, 
            predictedNumberLabel = $modal.data("predicted-number"),
            template = "<p><strong>{{predictedNumberLabel}}:</strong> {{predictedNumber}}"
              + " ({{predictedMinimum}} - {{predictedMaximum}})"
              + "</p>";
          $modal.find("#progressbar").hide();

          $modal.find(".modal-body").html(
            Mustache.render(template, {
              predictedMaximum: data["topBoundary"],
              predictedMinimum: data["bottomBoundary"],
              predictedNumber: data["prediction"],
              predictedNumberLabel: predictedNumberLabel
            }));
        });
      })
      .delegate(".save-contract", "click", function (e) {
        TABLE.saveContractData(e);
      })
      .delegate(".open-tenders", "click", function (e) {
        var $target = $(e.target),
          confirmation = $target.data("confirmation"),
          unableToOpenMessage = $target.data("unable-to-open"),
          unableToProcessMessage = $target.data("unable-to-process");
        if (!$target.hasClass("disabled")) {
          if (confirm(confirmation)) {
            $.getJSON("PCFilingApp", {
              action: "openTenders",
              contractURL: TABLE.util.getClosestContractUri(e) 
            }, function (data) {
              if (data.success) {
                var tendersLink = $target.prev();
                $target.fadeOut(function () {
                  $(this).remove();
                });
                tendersLink.removeClass("disabled").on("click", function () {
                  TABLE.saveContractData(e); 
                });
              } else {
                alert(unableToOpenMessage);
              }
            }).error(function () {
              alert(unableToProcessMessage);
            });
          } else {
            return false;
          }
        }
      })
      .delegate(".view-tenders", "click", function (e) {
        if ($(e.target).hasClass("disabled")) {
          return false;
        } else {
          TABLE.saveContractData(e);
        }
      })
      .delegate(".send-notification", "click", function (e) {
        var $target = $(e.target),
          $row = TABLE.util.getClosestRow(e),
          recipient = prompt($target.data("prompt"));
        if (recipient) {
          $.getJSON("InvitationComponent", {
              action: "send",
              contract: $row.data("contract-title"), 
              contractURL: $row.data("contract-uri"),
              email: recipient,
              name: sessionStorage.username
            }, function (data) {
              if (data.sent) {
                alert(data.message);
              }
            });
        }
      });

    TABLE.dom.$pagination.twbsPagination({
      startPage: 1, 
      totalPages: TABLE.itemsPerPage, // FIXME: This needs to be present, but what to use for dynamic data?
      href: "#page={{number}}",
      visiblePages: 3,
      onPageClick: function (event, page) {
        TABLE.load(tableName, page);
      }
    });
  },
  load: function (tableName, page, itemsPerPage) {
    TABLE.dom.$body.empty();
    TABLE.dom.$contractTable.addClass("hide");
    TABLE.dom.$progressbar.show();

    var opts = {
      action: "table",
      items: itemsPerPage || TABLE.itemsPerPage,
      page: page - 1, // Server-side pagination is zero-offseted
      tableName: tableName
    };
    if (page === 1) {
      opts["reload"] = true;
    }
    $.getJSON("PCFilingApp", opts, TABLE.display);
  },
  saveContractData: function (e) {
    var $row = TABLE.util.getClosestRow(e);
    
    sessionStorage.contractURL = decodeURIComponent($row.data("contract-uri"));
    sessionStorage.contractTitle = decodeURIComponent($row.data("contract-title"));
    sessionStorage.contractDescription = decodeURIComponent($row.data("contract-description"));
    sessionStorage.contractPrice = decodeURIComponent($row.data("contract-price"));
    sessionStorage.contractCurrency = decodeURIComponent($row.data("contract-currency"));
    sessionStorage.contractCpvString = decodeURIComponent($row.data("contract-cpv"));
    sessionStorage.contractPlace = decodeURIComponent($row.data("place"));
  },
  util: {
    getClosestRow: function (e) {
      return $(e.target).closest("tr");
    },
    getClosestContractUri: function (e) {
      return TABLE.util.getClosestRow(e).data("contract-uri");
    }
  }
};

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
	$.each(cpv, function (index) {
		list.append(
      $('<li>').append($.grep(cpvCollection, function (item) {
        return item.indexOf(cpv[index]) === 0;
      }))
    );
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
	sessionStorage.contractURL = decodeURIComponent(contractURI);
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
