<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <%@include file="WEB-INF/jspf/header.jspf" %>
        <%@include file="WEB-INF/jspf/header-supplier.jspf" %>
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.Supplier" />
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.Constants" var="cons" />
        <link href="bootstrap/css/won.css" rel="stylesheet" />
    </head>
    <body>
        <%@include file="WEB-INF/jspf/header-private.jspf" %>
        <div class="container-fluid">
            <div class="row-fluid">
                <%@include file="WEB-INF/jspf/menu-supplier.jspf" %>
                <div class="span10">	        

                    <div class="alert hide" id="userHelper">
                      <button class="close"
                        onclick="userHelper('off')"
                        title="<fmt:message key="disableguide" bundle="${cons}" />" >×</button>
                        <fmt:message key="awarded.help" />
                    </div>
                    
                    <%--
                    <%@include file="WEB-INF/jspf/list-tools.jspf" %>
                    --%>

                    <h3 style="margin-bottom: 20px;"><fmt:message key="awarded.title" /></h3>

                    <table class="table table-striped table-bordered" style="display:none;" id="contractTable">
                        <thead>
                            <tr>
                                <th><fmt:message key="title" bundle="${cons}" /></th>
                                <th><fmt:message key="buyer" bundle="${cons}" /></th>
                                <th><fmt:message key="agreedprice" bundle="${cons}" /></th>
                                <th><fmt:message key="specifications" bundle="${cons}" /></th>
                                <th><fmt:message key="awarded" bundle="${cons}" /></th>
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

                    <div id="showAllPages" class="hide pagination pull-right" style="margin:0; margin-top:-16px;">
                        <ul>
                            <li><a onclick="$('.3dots').remove();
                                        $('#pages li').removeClass('reallyhide');
                                        $('#showAllPages').remove();
                                        " href="#"><fmt:message key="showallpages" bundle="${cons}" /></a>
                            </li>
                        </ul>
                    </div>

                </div>        
            </div>
        </div>

        <%@include file="WEB-INF/jspf/footer.jspf" %>
	<script src="js/cpv-codes-<c:out value="${pageContext.request.locale.language}" />.js"></script>                        
        <script src="js/sessionstorage.1.4.js"></script>
        <script src="js/functions.js"></script>
        <script src="js/date.format.js"></script>
        <script src="js/toolsSupplier.js"></script>
        <script src="js/table.js"></script>
        <script src="js/script.js"></script>

        <script type="text/javascript">
                                    var pagesTotal = 1;
                                    var currentPage = 1;
                                    var windowSize = 3; // in each direction from the current page
                                    var tableItemsPerPage = 10;
                                    var tableAddress = "PCFilingApp?action=table&tableName=AwardedTenders";

                                    function fillTable() {

                                        $('#progressbar').hide();
                                        $('#contractTable').removeClass('hide');
                                        $('#contractTable').fadeIn('slow');
                                        $('#contractTable tbody').remove();
                                        $.each(
                                                tableData[currentPage],
                                                function(i, data) {
                                                    newRow = $('<tr>');

                                                    // Title
                                                    newTitle = $('<a>');
                                                    newTitle.attr('href', 'supplier-view-event.jsp');
                                                    newTitle.click(function() {
                                                        showEvent(htmlEncode(data.contractURI), htmlEncode(data.buyerURI));
                                                    });
                                                    newTitle.html(data.title);
                                                    newRow.append($('<td>').append(newTitle));

                                                    // Buyer
                                                    newRow.append($('<td>').append(linkBuyer(data.buyerName, data.buyerEntity)));

                                                    // Price								
                                                    newPrice = $('<td>');
                                                    if (data.price != undefined && data.currency != undefined)
                                                        newPrice.append(Number(data.price).toFixed(2) + " " + data.currency);
                                                    newRow.append(newPrice);

                                                    // CPVs
                                                    newCPVs = $('<td>').append(
                                                            CPVs(data.cpv1URL, data.cpvAdd)).appendTo(newRow);

                                                    // Awarded
                                                    newAwarded = $('<td>').append(formatDate(data.awarded)).appendTo(newRow);

                                                    // Created
                                                    newCreated = $('<td>').append((data.deadline).substring(0, 7)).appendTo(newRow);

                                                    // Actions								
                                                    newActions = $('<div>');
                                                    newActions.addClass('btn-group');

                                                    // View
                                                    newView = $('<a>');
                                                    newView.attr('href', 'supplier-view-tender.jsp');
                                                    newView.click(function() {
                                                        showTender(data.title, data.tenderURI);
                                                    });
                                                    newView.append('view').appendTo(newActions);

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

        <script>
            $("a").tooltip();
        </script>
    </body>
</html>
