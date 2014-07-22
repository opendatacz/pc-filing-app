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
                    <h4><fmt:message key="dashboard.activities.title" /></h4>
                    <table class="table activities" style="display: none;" id="activityTable">					
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
        <script src="js/functions.js"></script>
        <script src="js/sessionstorage.1.4.js"></script>
        <script src="js/jquery.timeago.js"></script>
        <script src="js/toolsBuyer.js"></script>
        <script src="js/script.js"></script>		
        <script src="js/table.js"></script>
        <script type="text/javascript">
                                        var pagesTotal = 1;
                                        var currentPage = 1;
                                        var windowSize = 3; // in each direction from the current page
                                        var tableItemsPerPage = 8;
                                        var tableAddress = "PCFilingApp?action=table&tableName=Dashboard";

                                        function fillTable() {

                                            $('#progressbar').hide();

                                            $('#activityTable tbody').remove();
                                            $.each(
                                                    tableData[currentPage],
                                                    function(i, data) {
                                                        newRow = $('<tr>');
                                                        dataRow = tableData[currentPage][i];

                                                        var newAction = $('<td>');
                                                        switch (dataRow.type) {
                                                            case "created":
                                                                newAction.append("<fmt:message key="dashboard.activities.created" />").append(linkEvent(dataRow.subjectTitle, dataRow.subject));
                                                                break;
                                                            case "published":
                                                                newAction.append("<fmt:message key="dashboard.activities.published" />").append(linkEvent(dataRow.subjectTitle, dataRow.subject));
                                                                break;
                                                            case "cancelled":
                                                                newAction.append("<fmt:message key="dashboard.activities.cancelled" />").append(linkEvent(dataRow.subjectTitle, dataRow.subject));
                                                                break;
                                                            case "withdrawn":
                                                                newAction.append("<fmt:message key="dashboard.activities.withdrawn" />").append(linkEvent(dataRow.subjectTitle, dataRow.subject));
                                                                break;
                                                            case "awarded":
                                                                var awarded = "<fmt:message key="dashboard.activities.awarded" />";
                                                                awarded = awarded.replace("{0}", $('<div>').append(linkEvent(dataRow.subjectTitle, dataRow.subject).clone()).html());
                                                                awarded = awarded.replace("{1}", $('<div>').append(linkEntity(dataRow.entityName, dataRow.entity).clone()).html());
                                                                awarded = awarded.replace("{2}", $('<div>').append(linkTender(dataRow.subjectTitle, dataRow.object).clone()).html());
                                                                newAction.append(awarded);
                                                                break;
                                                            case "completed":
                                                                newAction.append("<fmt:message key="dashboard.activities.completed" />").append();
                                                                break;

                                                            case "tenderSubmitted":
                                                                var tenderSubmitted = "<fmt:message key="dashboard.activities.tenderSubmitted" />";
                                                                tenderSubmitted = tenderSubmitted.replace("{0}", $('<div>').append(linkTender(dataRow.objectTitle, dataRow.subject).clone()).html());
                                                                tenderSubmitted = tenderSubmitted.replace("{1}", $('<div>').append(linkEvent(dataRow.objectTitle, dataRow.object).clone()).html());
                                                                tenderSubmitted = tenderSubmitted.replace("{2}", Number(data.price).toFixed(2) + ' ' + dataRow.currency);
                                                                tenderSubmitted = tenderSubmitted.replace("{3}", $('<div>').append(linkEntity(dataRow.entityName, dataRow.entity).clone()).html());
                                                                newAction.append(tenderSubmitted);
                                                                break;

                                                            case "tenderWithdrawn":
                                                                var tenderWithdrawn = "<fmt:message key="dashboard.activities.tenderWithdrawn" />";
                                                                tenderWithdrawn = tenderWithdrawn.replace("{0}", $('<div>').append(linkEntity(dataRow.entityName, dataRow.entity).clone()).html());
                                                                tenderWithdrawn = tenderWithdrawn.replace("{1}", $('<div>').append(linkTender(dataRow.objectTitle, dataRow.subject).clone()).html());
                                                                tenderWithdrawn = tenderWithdrawn.replace("{2}", $('<div>').append(linkEvent(dataRow.objectTitle, dataRow.object).clone()).html());
                                                                newAction.append(tenderWithdrawn);
                                                                break;

                                                            case "tenderRejected":
                                                                var tenderRejected = "<fmt:message key="dashboard.activities.tenderRejected" />";
                                                                tenderRejected = tenderRejected.replace("{0}", $('<div>').append(linkTender(dataRow.objectTitle, dataRow.subject).clone()).html());
                                                                tenderRejected = tenderRejected.replace("{1}", $('<div>').append(linkEvent(dataRow.objectTitle, dataRow.object).clone()).html());
                                                                tenderRejected = tenderRejected.replace("{2}", $('<div>').append(linkEntity(dataRow.entityName, dataRow.entity).clone()).html());
                                                                newAction.append(tenderRejected);
                                                                break;
                                                        }
                                                        newAction.append('<br/><abbr class="timeago" title="' + dataRow.date + '">' + (new Date(dataRow.date)) + '</abbr>');
                                                        newRow.append(newAction);
                                                        newRow.appendTo("#activityTable");

                                                    }
                                            );

                                            jQuery("abbr.timeago").timeago();
                                            $('#activityTable').fadeIn('slow');

                                        }


                                        $(window).ready(function() {
                                            loadPage(true);

                                        });

                                        $('a').tooltip();
        </script>
    </body>
</html>
