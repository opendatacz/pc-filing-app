<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <%@include file="WEB-INF/jspf/header.jspf" %>
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.buyer" />
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.constants" var="cons" />
        <link href="./bootstrap/css/won.css" rel="stylesheet" />
    </head>
    <body>
        <%@include file="WEB-INF/jspf/header-private.jspf" %>
        <div class="container-fluid">
            <div class="row-fluid">
                <%@include file="WEB-INF/jspf/menu-buyer.jspf" %>
                <div class="span8">
                    <div class="alert hide" id="userHelper">
                        <button class="close" onclick="userHelper('off')" title="Disable guide" >×</button>
                        <fmt:message key="submittedtenders.help" />
                    </div>

                    <h4><fmt:message key="submittedtenders.title" /> '<span id="contractTitle"></span>'</h4>

                    <table class="table table-striped table-bordered" style="display: none;" id="contractTable">
                        <thead>
                            <tr>
                                <th><fmt:message key="ranking" bundle="${cons}" /></th>
                                <th><fmt:message key="seller" bundle="${cons}" /></th>
                                <th><fmt:message key="offeredprice" bundle="${cons}" /></th>
                                <th><fmt:message key="submissiondate" bundle="${cons}" /></th>
                                <th><fmt:message key="action" bundle="${cons}" /></th>							
                            </tr>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>

                    <div style="text-align: center;" id="progressbar">
                        <br> <img src="images/progressbar.gif" />
                    </div>

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

        <script src="js/functions.js"></script>
        <script src="js/sessionstorage.1.4.js"></script>
        <script src="js/script.js"></script>
        <script src="js/date.format.js"></script>
        <script src="js/toolsBuyer.js"></script>
        <script src="js/table.js"></script>

        <script type="text/javascript">

                                        var title = sessionStorage.contractTitle;
                                        var contractURI = sessionStorage.contractURL;
                                        $('#contractTitle').append(title);

                                        var pagesTotal = 1;
                                        var currentPage = 1;
                                        var windowSize = 3; // in each direction from the current page
                                        var tableItemsPerPage = 10;
                                        var tableAddress = "PCFilingApp?action=table&tableName=ContractSubmittedTenders&contractURI=" + contractURI;

                                        function fillTable() {

                                            $('#progressbar').hide();
                                            $('#contractTable').fadeIn('slow');
                                            $('#contractTable tbody').remove();
                                            $.each(
                                                    tableData[currentPage],
                                                    function(i, data) {
                                                        newRow = $('<tr>');

                                                        // Ranking
                                                        $('<td>').appendTo(newRow);

                                                        // Supplier
                                                        newSupplier = $('<a>');
                                                        newSupplier.attr('href', 'entity-supplier.jsp');
                                                        newSupplier.click(function() {
                                                            showEntity(data.grSupplier);
                                                        });
                                                        newSupplier.append(data.supplierName).appendTo($('<td>').appendTo(newRow));

                                                        // Price								
                                                        newPrice = $('<td>');
                                                        if (data.price != undefined && data.currency != undefined)
                                                            newPrice.append(Number(data.price).toFixed(2) + " " + data.currency);
                                                        newRow.append(newPrice);

                                                        // Modified
                                                        newSubmitted = $('<td>').append(formatDate(data.submitted)).appendTo(newRow);

                                                        // Actions								
                                                        newActions = $('<div>');
                                                        newActions.addClass('btn-group');

                                                        // Award
                                                        newAward = $('<a>');
                                                        newAward.attr('href', 'PCFilingApp?action=awardTender&forward=buyer-awarded.jsp&contractURL=' + data.contractURI + '&tenderURL=' + data.tenderURL);
                                                        newAward.click(function() {
                                                            return confirm("<fmt:message key="submittedtenders.award.confirm" />");
                                                        });
                                                        newAward.append('<fmt:message key="award" bundle="${cons}" />').appendTo(newActions);

                                                        // View
                                                        newView = $('<a>');
                                                        newView.attr('href', 'buyer-view-tender.jsp');
                                                        newView.click(function() {
                                                            showTender(title, data.tenderURL);
                                                        });
                                                        newView.append('<fmt:message key="view" bundle="${cons}" />').appendTo(newActions);

                                                        // Reject
                                                        newReject = $('<a>');
                                                        newReject.attr('href', 'PCFilingApp?action=rejectTender&forward=buyer-submitted-tenders.jsp&contractURL=' + data.contractURI + '&tenderURL=' + data.tenderURL);
                                                        newReject.click(function() {
                                                            return confirm("<fmt:message key="submittedtenders.reject.confirm" />");
                                                        });
                                                        newReject.append('<fmt:message key="reject" bundle="${cons}" />').appendTo(newActions);

                                                        newActions.children('a').addClass('btn');
                                                        newRow.append($('<td>').append(newActions));

                                                        newRow.appendTo("#contractTable");

                                                    }
                                            );

                                        }

                                        $(window).ready(function() {
                                            loadPage(true);
                                        });

                                        $('a').tooltip();
        </script>
    </body>
</html>
