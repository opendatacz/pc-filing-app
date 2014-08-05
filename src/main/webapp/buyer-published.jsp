<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <%@include file="WEB-INF/jspf/header.jspf" %>
        <%@include file="WEB-INF/jspf/header-buyer.jspf" %>
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.Buyer" />
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.Constants" var="cons" />
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
                        <fmt:message key="published.help" />
                    </div>

                    <h4><fmt:message key="published.title" /></h4>

                    <table class="table table-striped table-bordered" style="display: none;" id="contractTable">
                        <thead>
                            <tr>
                                <th><fmt:message key="title" bundle="${cons}" /></th>
                                <th><fmt:message key="estimatedprice" bundle="${cons}" /></th>
                                <th><fmt:message key="specifications" bundle="${cons}" /></th>
                                <th><fmt:message key="tenderstocontract" bundle="${cons}" /></th>
                                <th><fmt:message key="lastupdate" bundle="${cons}" /></th>
                                <th><fmt:message key="tenderdeadline" bundle="${cons}" /></th>
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
        <script src="js/date.format.js"></script>	
        <script src="js/toolsBuyer.js"></script>
        <script src="js/table.js"></script>
        <script src="js/services.js"></script>

        <script type="text/javascript">
                                        var pagesTotal = 1;
                                        var currentPage = 1;
                                        var windowSize = 3; // in each direction from the current page
                                        var tableItemsPerPage = 10;
                                        var tableAddress = "PCFilingApp?action=table&tableName=PublishedCalls";

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
                                                        if (data.price != undefined && data.currency != undefined)
                                                            newPrice.append(Number(data.price).toFixed(2) + " " + data.currency);
                                                        newRow.append(newPrice);

                                                        // CPVs
                                                        newCPVs = $('<td>').append(
                                                                CPVs(data.cpv1URL, data.cpvAdd)).appendTo(newRow);

                                                        // Tenders

                                                        var newCol = $('<div class="btn-group">');
                                                        if (data.sealed == "true" && data.openingTime === undefined) {

                                                            newTenders = $('<a class="btn disabled">');
                                                            newTenders.append(data.tendersCount);
                                                            newTenders.appendTo(newCol);

                                                            newOpen = $('<a class="btn">');
                                                            newOpen.append('<fmt:message key="opentenders" bundle="${cons}" />');
                                                            if (new Date(data.deadline) <= new Date()) {
                                                                newOpen.on('click', function() {
                                                                    openTenders($(this), data.contractURI);
                                                                });
                                                            }
                                                            else {
                                                                newOpen.addClass('disabled');
                                                            }
                                                            newOpen.appendTo(newCol);
                                                            $('<td>').append(newCol).appendTo(newRow);
                                                        }
                                                        else
                                                        {
                                                            newTenders = $('<a>');
                                                            newTenders.addClass('btn');
                                                            newTenders.click(function() {
                                                                saveEventInfo(i);
                                                            });
                                                            newTenders.attr('href', 'buyer-submitted-tenders.jsp');
                                                            
                                                            // Predict number of bidders
                                                            newPredictBidders = $('<a href="javascript:void(0);" title="<fmt:message key="predictbidders" bundle="${cons}" />">');
                                                            newPredictBidders.addClass("btn");
                                                            predictBiddersIcon = $('<i class="icon-question-sign"></i>');
                                                            newPredictBidders.click(function () {
                                                                services.predictBidders(data.contractURI);
                                                            });
                                                            newPredictBidders.append(predictBiddersIcon).appendTo(newCol);
                                                            
                                                            var td = $('<td>');
                                                            td.append(newTenders.append(data.tendersCount));
                                                            td.append(newPredictBidders);
                                                            td.appendTo(newRow);
                                                        }



                                                        // Modified
                                                        newModified = $('<td>').append(formatDate(data.modified)).appendTo(newRow);

                                                        // Deadline
                                                        newDeadline = $('<td>').append(formatDate(data.deadline)).appendTo(newRow);

                                                        // Actions								
                                                        newActions = $('<div>');
                                                        newActions.addClass('btn-group');

                                                        // Cancel
                                                        newEdit = $('<a>');
                                                        newEdit.attr('href', 'PCFilingApp?action=cancelContract&forward=buyer-cancelled.jsp&contractURL=' + data.contractURI);
                                                        newEdit.click(function() {
                                                            return cancelContract();
                                                        });
                                                        newEdit.append('<fmt:message key="cancel" bundle="${cons}" />').appendTo(newActions);

                                                        // Notify
                                                        newNotify = $('<a>');
                                                        newNotify.click(function() {
                                                            sendNotification(data.title, data.contractURI);
                                                        });
                                                        newNotify.append($('<i>').addClass("icon-envelope")).appendTo(newActions);

                                                        newActions.children('a').addClass('btn');
                                                        newRow.append($('<td>').append(newActions));

                                                        // Matchmaker								
                                                        newMatchmaker = $('<div>');
                                                        newMatchmaker.addClass('btn-group');

                                                        // Similar
                                                        newSimilar = $('<a>');
                                                        newSimilar.attr('href', 'buyer-similar-events.jsp');
                                                        newSimilar.click(function() {
                                                            saveEventInfo(i);
                                                        });
                                                        newSimilar.append('<fmt:message key="similarevents" bundle="${cons}" />').appendTo(newMatchmaker);

                                                        // Suppliers
                                                        newSuppliers = $('<a>');
                                                        newSuppliers.attr('href', 'buyer-suitable-suppliers.jsp');
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

                                        function cancelContract() {
                                            return confirm('<fmt:message key="published.cancel.confirm" />');
                                        }

                                        function sendNotification(title, contract) {
                                            var recipient = prompt("<fmt:message key="published.notification" />", "@");
                                            if (recipient == null) {
                                                return;
                                            }
                                            var description = "undefined"; // todo get from sessionStore			
                                            $.getJSON("InvitationComponent?action=send&contractURL=" + encodeURIComponent(contract) + "&name=" + encodeURIComponent(sessionStorage.username) + "&contract=" + encodeURIComponent(title) + "&email=" + encodeURIComponent(recipient), function(data)
                                            {
                                                if (data.sent) {
                                                    alert(data.message);
                                                }
                                            });
                                        }

                                        function openTenders(caller, contract) {

                                            if (!confirm("<fmt:message key="published.open.confirm" />"))
                                                return false;

                                            $.getJSON("PCFilingApp?action=openTenders&contractURL=" + encodeURIComponent(contract), function(data)
                                            {
                                                if (data.success == true) {
                                                    var link = caller.prev();
                                                    caller.fadeOut(function() {
                                                        $(this).remove();
                                                    });
                                                    link.removeClass("btn disabled");
                                                    link.addClass("btn");
                                                    link.on('click', function() {
                                                        saveEventInfo(i);
                                                    });
                                                    link.attr('href', 'buyer-submitted-tenders.jsp');

                                                }
                                                else
                                                {
                                                    alert('Unable to open tenders.');
                                                }
                                            }).error(function() {
                                                alert('Unable to process request.');
                                            });
                                        }

                                        $(window).ready(function() {
                                            loadPage(true);
                                        });

                                        $('a').tooltip();
        </script>
    </body>
</html>
