<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <%@include file="WEB-INF/jspf/header.jspf" %>
        <%@include file="WEB-INF/jspf/header-buyer.jspf" %>
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.Buyer" />
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.Constants" var="cons" />
        <link href="bootstrap/css/won.css" rel="stylesheet" />
    </head>
    <body>
        <%@include file="WEB-INF/jspf/header-private.jspf" %>
        <div class="container-fluid">
		<div class="row-fluid">
			<%@include file="WEB-INF/jspf/menu-buyer.jspf" %>
			<div class="span8">
				<div class="alert hide" id="userHelper">
          <button class="close"
            onclick="userHelper('off')"
            title="<fmt:message key="disableguide" bundle="${cons}" />" >×</button>
					<fmt:message key="cancelled.help" />
		  		</div>
				<h4><fmt:message key="cancelled.title" /></h4>
				<table class="table table-striped table-bordered" style="display: none;" id="contractTable">
					<thead>
						<tr>
							<th><fmt:message key="title" bundle="${cons}" /></th>
							<th><fmt:message key="estimatedprice" bundle="${cons}" /></th>
							<th><fmt:message key="cpvcodes" bundle="${cons}" /></th>							
							<th><fmt:message key="lastupdate" bundle="${cons}" /></th>
							<th><fmt:message key="tenderdeadline" bundle="${cons}" /></th>
							<th><fmt:message key="action" bundle="${cons}" /></th>							
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
				<div id="progressbar"></div>
				<div class="pagination pagination-centered">
					<ul id="pages">
					</ul>
				</div>
				<div id="showAllPages" class="hide pagination pull-right" style="margin: 0; margin-top: -16px;">
					<ul>
						<li><a
							onclick="$('.3dots').remove(); $('#pages li').removeClass('reallyhide'); $('#showAllPages').remove(); "
							href="#"><fmt:message key="showallpages" bundle="${cons}" /></a></li>
					</ul>
				</div>
			</div>
                        <%@include file="WEB-INF/jspf/stats-buyer.jspf" %>
		</div>
	</div>
	<%@include file="WEB-INF/jspf/footer.jspf" %>
	<script src="js/functions.js"></script>
	<script src="js/sessionstorage.1.4.js"></script>
	<script src="js/script.js"></script>
	<script src="js/date.format.js"></script>
	<script src="js/toolsBuyer.js"></script>
	<script src="js/table.js"></script>

	<script type="text/javascript">
		var pagesTotal = 1;
		var currentPage = 1;
		var windowSize = 3; // in each direction from the current page
		var tableItemsPerPage = 10;
		var tableAddress = "PCFilingApp?action=table&tableName=CanceledCalls";

		function fillTable() {

			$('#progressbar').hide();
			$('#contractTable').fadeIn('slow');
			$('#contractTable tbody').remove();
			$.each(
					tableData[currentPage],
					function(i, data) {
						newRow = $('<tr>');

						// Title
						newTitle = $('<a>');
						newTitle.attr('href', 'buyer-view-event.jsp');
						newTitle.click(function() {
							showEvent(htmlEncode(data.contractURI));
						});
						newTitle.html(data.title);
						newRow.append($('<td>').append(newTitle));
						
							// Price								
						newPrice = $('<td>');
						if (data.price != undefined	&& data.currency != undefined)
							newPrice.append(Number(data.price).toFixed(2) + " " + data.currency);
						newRow.append(newPrice);

						// CPVs
						newCPVs = $('<td>').append(
						CPVs(data.cpv1URL, data.cpvAdd)).appendTo(newRow);
						
						// Modified
						newModified = $('<td>').append(formatDate(data.modified)).appendTo(newRow);

						// Deadline
						newDeadline = $('<td>').append(formatDate(data.deadline)).appendTo(newRow);

						// Actions								
						newActions = $('<div>');
						newActions.addClass('btn-group');
							
							// Withdraw
							newWithdraw = $('<a>');
							newWithdraw.attr('href', 'buyer-copy-event.jsp');
							newWithdraw.click(function() { copyEvent(data.contractURI); });
							newWithdraw.append('copy as new').appendTo(newActions);

						newActions.children('a').addClass('btn');
						newRow.append($('<td>').append(newActions));
						
						newRow.appendTo("#contractTable");

					}
				);

		}
		
		function copyEvent(contractURL) {
			sessionStorage.copyContractURL = contractURL;
			return;
			
		}
		
		$(window).ready(function() {
			loadPage(true);
		});

		$('a').tooltip();
	</script>
    </body>
</html>
