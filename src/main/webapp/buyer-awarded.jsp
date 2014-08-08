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
                        <fmt:message key="awarded.help" />
                    </div>
                    <h4><fmt:message key="awarded.title" /></h4>
                    <table class="table table-striped table-bordered" style="display: none;" id="contractTable">
                        <thead>
                            <tr>
                                <th><fmt:message key="title" bundle="${cons}" /></th>
                                <th><fmt:message key="agreedprice" bundle="${cons}" /></th>
                                <th><fmt:message key="cpvcodes" bundle="${cons}" /></th>							
                                <th><fmt:message key="supplier" bundle="${cons}" /></th>
                                <th><fmt:message key="expectedcompletiondate" bundle="${cons}" /></th>
                                <th><fmt:message key="lastupdate" bundle="${cons}" /></th>
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
                                    onclick="$('.3dots').remove();
                                                                $('#pages li').removeClass('reallyhide');
                                                                $('#showAllPages').remove();
                                                                "
                                    href="#"><fmt:message key="showallpages" bundle="${cons}" /></a></li>
                        </ul>
                    </div>
                </div>
                <%@include file="WEB-INF/jspf/stats-buyer.jspf" %>
            </div>
        </div>
        <%@include file="WEB-INF/jspf/footer.jspf" %>
        <script src="js/cpvs.js"></script>
        <script src="js/functions.js"></script>
        <script src="js/sessionstorage.1.4.js"></script>
        <script src="js/script.js"></script>
        <script src="js/toolsBuyer.js"></script>
        <script src="js/date.format.js"></script>
        <script src="js/table.js"></script>
        <script type="text/javascript">
                                                            var pagesTotal = 1;
                                                            var currentPage = 1;
                                                            var windowSize = 3; // in each direction from the current page
                                                            var tableItemsPerPage = 10;
                                                            var tableAddress = "PCFilingApp?action=table&tableName=AwardedContracts";

                                                            function withdrawContract() {
                                                                return confirm('<fmt:message key="awarded.cancel.confirm" />');
                                                            }

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
                                                                            if (data.aprice != undefined && data.acurrency != undefined)
                                                                                newPrice.append(Number(data.aprice).toFixed(2) + " " + data.acurrency);
                                                                            newRow.append(newPrice);

                                                                            // CPVs
                                                                            newCPVs = $('<td>').append(
                                                                                    CPVs(data.cpv1URL, data.cpvAdd)).appendTo(newRow);

                                                                            // Supplier
                                                                            newSupplier = $('<a>');
                                                                            newSupplier.attr('href', 'entity-supplier.jsp');
                                                                            newSupplier.click(function() {
                                                                                showEntity(data.supplierURI);
                                                                            });
                                                                            newSupplier.append(data.supplierName).appendTo($('<td>').appendTo(newRow));

                                                                            // Completion 
                                                                            newCompletion = $('<td>').append(formatDate(data.tenderEndDate)).appendTo(newRow);

                                                                            // Modified
                                                                            newModified = $('<td>').append(formatDate(data.modified)).appendTo(newRow);

                                                                            // Actions								
                                                                            newActions = $('<div>');
                                                                            newActions.addClass('btn-group');

                                                                            // View
                                                                            newView = $('<a>');
                                                                            newView.attr('href', 'buyer-view-tender.jsp');
                                                                            newView.click(function() {
                                                                                showTender(data.title, data.awarded);
                                                                            });
                                                                            newView.append('view tender').appendTo(newActions);

                                                                            // Withdraw
                                                                            newEdit = $('<a>');
                                                                            newEdit.attr('href', 'PCFilingApp?action=withdrawContract&forward=buyer-withdrawn.jsp&contractURL=' + data.contractURI);
                                                                            newEdit.click(function() {
                                                                                withdrawContract();
                                                                            });
                                                                            newEdit.append('withdraw').appendTo(newActions);

                                                                            // Finalize
                                                                            newFinalize = $('<a>');
                                                                            newFinalize.attr('href', 'buyer-finalize.jsp');
                                                                            newFinalize.click(function() {
                                                                                saveEventInfo(i);
                                                                            });
                                                                            newFinalize.append('finalize').appendTo(newActions);

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
