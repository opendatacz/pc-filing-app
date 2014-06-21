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
                    <h4>Activities</h4>
                    <table class="table activities" style="display: none;" id="activityTable">					
                        <tbody>
                        </tbody>
                    </table>
                    <div style="text-align: center;" id="progressbar">
                        <br><img src="images/progressbar.gif" />
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
                                    href="#">show all pages</a></li>
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
                                                                newAction.append("You created a new call for tenders to ").append(linkEvent(dataRow.subjectTitle, dataRow.subject));
                                                                break;
                                                            case "published":
                                                                newAction.append("You published a call for tenders to ").append(linkEvent(dataRow.subjectTitle, dataRow.subject));
                                                                break;
                                                            case "cancelled":
                                                                newAction.append("You cancelled a call for tenders to ").append(linkEvent(dataRow.subjectTitle, dataRow.subject));
                                                                break;
                                                            case "withdrawn":
                                                                newAction.append("You withdrew from ").append(linkEvent(dataRow.subjectTitle, dataRow.subject));
                                                                break;
                                                            case "awarded":
                                                                newAction.append("You awarded the contract ").append(linkEvent(dataRow.subjectTitle, dataRow.subject));
                                                                newAction.append(" to ").append(linkEntity(dataRow.entityName, dataRow.entity));
                                                                newAction.append(" on the base of their ").append(linkTender(dataRow.subjectTitle, dataRow.object));
                                                                break;
                                                            case "completed":
                                                                newAction.append("You finalized the contract ").append(linkEvent(dataRow.subjectTitle, dataRow.subject));
                                                                break;

                                                            case "tenderSubmitted":
                                                                newAction.append("A new ").append(linkTender(dataRow.objectTitle, dataRow.subject)).append(" to ");
                                                                newAction.append(linkEvent(dataRow.objectTitle, dataRow.object)).append(' (<strong>' + Number(data.price).toFixed(2) + ' ' + dataRow.currency + '</strong>)');
                                                                newAction.append(' was submitted by ').append(linkEntity(dataRow.entityName, dataRow.entity));
                                                                break;

                                                            case "tenderWithdrawn":
                                                                newAction.append(linkEntity(dataRow.entityName, dataRow.entity));
                                                                newAction.append(" withdrew ").append(linkTender(dataRow.objectTitle, dataRow.subject)).append(" from ");
                                                                newAction.append(linkEvent(dataRow.objectTitle, dataRow.object));
                                                                break;

                                                            case "tenderRejected":
                                                                newAction.append("You rejected ").append(linkTender(dataRow.objectTitle, dataRow.subject)).append(" to ");
                                                                newAction.append(linkEvent(dataRow.objectTitle, dataRow.object)).append(' submitted by ').append(linkEntity(dataRow.entityName, dataRow.entity));
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