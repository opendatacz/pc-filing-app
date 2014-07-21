<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <%@include file="WEB-INF/jspf/header.jspf" %>
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.buyer" />
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.constants" var="cons" />
        <link href="bootstrap/css/won.css" rel="stylesheet" />
    </head>
    <body>
        <%@include file="WEB-INF/jspf/header-private.jspf" %>
        <div class="container-fluid">
            <div class="row-fluid">
                <%@include file="WEB-INF/jspf/menu-buyer.jspf" %>

                <div class="span8">

                    <div class="alert hide" id="userHelper">
                        <button class="close" onclick="userHelper('off')" title="Disable guide" >×</button>
                        <fmt:message key="prepared.help" />
                    </div>

                    <h4><fmt:message key="prepared.title" /></h4>

                    <table class="table table-striped table-bordered" style="display: none;" id="contractTable">
                        <thead>
                            <tr>
                                <th><fmt:message key="title" bundle="${cons}" /></th>
                                <th><fmt:message key="estimatedprice" bundle="${cons}" /></th>
                                <th><fmt:message key="specifications" bundle="${cons}" /></th>
                                <th><fmt:message key="lastupdate" bundle="${cons}" /></th>
                                <th><fmt:message key="creationdate" bundle="${cons}" /></th>
                                <th><fmt:message key="action" bundle="${cons}" /></th>
                                <th><fmt:message key="matchmaker" bundle="${cons}" /></th>
                            </tr>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>

                    <div id="progressbar"></div>

                    <div class="pagination pagination-centered">
                        <ul id="pages">

                        </ul>

                        <div class="pull-right">
                            <a href="buyer-create-event.jsp" class="btn"
                               style="text-align: left; margin-left: 0px; margin-right: 10px; margin-top: 5px; padding-left: 0px;"
                               title="<fmt:message key="prepared.create.title" />">&nbsp;&nbsp;<i class="icon-plus"></i>&nbsp;&nbsp;<fmt:message key="prepared.create" /></a>
                        </div>

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
        <script src="js/cpv-codes.js"></script>
        <script src="js/cpvs.js"></script>
        <script src="js/script.js"></script>
        <script src="js/toolsBuyer.js"></script>
        <script src="js/date.format.js"></script>		
        <script src="js/table.js"></script>

        <script type="text/javascript">
                                        var pagesTotal = 1;
                                        var currentPage = 1;
                                        var windowSize = 3; // in each direction from the current page
                                        var tableItemsPerPage = 10;
                                        var tableAddress = "PCFilingApp?action=table&tableName=PrivateContracts";

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
                                                        newTitle.attr('href', 'buyer-view-event.jsp');
                                                        newTitle.click(function() {
                                                            showEvent(htmlEncode(data.contractURI));
                                                        });
                                                        newTitle.html(data.title);
                                                        newRow.append($('<td>').append(newTitle));
                                                        // Price								
                                                        newPrice = $('<td>');
                                                        if (data.price != undefined && data.currency != undefined)
                                                            newPrice.append(Number(data.price).toFixed(2) + " " + data.currency);
                                                        newRow.append(newPrice);

                                                        // CPVs
                                                        newCPVs = $('<td>').append(
                                                                CPVs(data.cpv1URL, data.cpvAdd)).appendTo(newRow);

                                                        // Modified
                                                        newModified = $('<td>').append(formatDate(data.modified)).appendTo(newRow);

                                                        // Created
                                                        newCreated = $('<td>').append(formatDate(data.created)).appendTo(newRow);

                                                        // Actions								
                                                        newActions = $('<div>');
                                                        newActions.addClass('btn-group');

                                                        // Publish
                                                        newPublish = $('<a>');
                                                        newPublish.attr('href', 'PCFilingApp?forward=buyer-published.jsp&action=publishPrivateContract&contractURI=' + htmlEncode(data.contractURI));
                                                        newPublish.click(function() {
                                                            return confirm('<fmt:message key="prepared.publish.confirm" />');
                                                        });
                                                        newPublish.append('<fmt:message key="publish" bundle="${cons}" />').appendTo(newActions);

                                                        // Edit
                                                        newEdit = $('<a>');
                                                        newEdit.attr('href', 'buyer-edit-event.jsp');
                                                        newEdit.click(function() {
                                                            editEvent(htmlEncode(data.contractURI));
                                                        });
                                                        newEdit.append('<fmt:message key="edit" bundle="${cons}" />').appendTo(newActions);

                                                        // Delete
                                                        newPublish = $('<a>');
                                                        newPublish.attr('href', 'PCFilingApp?forward=buyer-prepared.jsp&action=deletePrivateContract&contractURI=' + htmlEncode(data.contractURI));
                                                        newPublish.click(function() {
                                                            return confirm('<fmt:message key="prepared.delete.confirm" />');
                                                        });
                                                        newPublish.append('<fmt:message key="delete" bundle="${cons}" />').appendTo(newActions);

                                                        newActions.children('a').addClass('btn');
                                                        newRow.append($('<td>').append(newActions));

                                                        // Matchmaker								
                                                        newMatchmaker = $('<div>');
                                                        newMatchmaker.addClass('btn-group');

                                                        // Similar
                                                        newSimilar = $('<a>');
                                                        newSimilar.attr('href', 'buyer-similar-events.jsp?private=true');
                                                        newSimilar.click(function() {
                                                            saveEventInfo(i);
                                                        });
                                                        newSimilar.append('<fmt:message key="similarevents" bundle="${cons}" />').appendTo(newMatchmaker);

                                                        // Suppliers
                                                        newSuppliers = $('<a>');
                                                        newSuppliers.attr('href', 'buyer-suitable-suppliers.jsp?private=true');
                                                        newSuppliers.click(function() {
                                                            saveEventInfo(i);
                                                        });
                                                        newSuppliers.append('<fmt:message key="suitablesuppliers" bundle="${cons}" />').appendTo(newMatchmaker);

                                                        newMatchmaker.children('a').addClass('btn');
                                                        newRow.append($('<td>').append(newMatchmaker));

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
