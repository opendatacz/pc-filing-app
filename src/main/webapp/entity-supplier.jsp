<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <%@include file="WEB-INF/jspf/header.jspf" %>
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.Constants" var="cons" />
    </head>
    <body>
        <%@include file="WEB-INF/jspf/header-public.jspf" %>
        <div class="container">
            <div class="row">
                <div class="span12">

                    <div style="text-align: center;" id="progressbar">
                        <br> <img src="images/progressbar.gif" />
                    </div>

                    <div id="message" class="alert fade in hide">
                        <button type="button" class="close" data-dismiss="alert">&times;</button>
                    </div>

                    <div id="view" class="hide">				

                        <h3 style="margin-bottom: 20px;"><fmt:message key="supplier" bundle="${cons}" /></h3>					

                        <h4><fmt:message key="details" bundle="${cons}" /></h4>
                        <table class="table table-striped table-bordered" id="entityTable">
                        </table>

                        <hr>

                        <h4><fmt:message key="completedcontracts" bundle="${cons}" /></h4>	
                        <table class="table table-striped table-bordered" id="eventsTable">
                            <thead>
                                <tr>
                                    <th><fmt:message key="title" bundle="${cons}" /></th>							
                                    <th><fmt:message key="specifications" bundle="${cons}" /></th>
                                    <th><fmt:message key="actualprice" bundle="${cons}" /></th>							
                                </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>					

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
                                        href="#"><fmt:message key="show all pages" bundle="${cons}" /></a></li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <%@include file="WEB-INF/jspf/footer.jspf" %>

        <script src="js/sessionstorage.1.4.js"></script>
        <script src="js/script.js"></script>
        <script src="js/cpvs.js"></script>
        <script src="js/table.js"></script>

        <script type="text/javascript">

                                            var pagesTotal = 1;
                                            var currentPage = 1;
                                            var windowSize = 3; // in each direction from the current page
                                            var tableItemsPerPage = 10;
                                            var tableAddress = "PCFilingApp?action=table&tableName=PublicSupplierData&entity=" + sessionStorage.entity;
                                            var tableData = new Array();

                                            function fillTable() {

                                                $('#progressbar').hide();
                                                $('#contractTable').fadeIn('slow');
                                                $('#contractTable tbody').remove();
                                                $.each(
                                                        tableData[currentPage],
                                                        function(i, data) {
                                                            newRow = $('<tr>');
                                                            var link = $('<a>');
                                                            link.attr('href', 'view-public-event.jsp');
                                                            link.on('click', function() {
                                                                showEvent(data.contractURI);
                                                            });
                                                            link.html(data.title);
                                                            newRow.append($('<td>').append(link));
                                                            newRow.append($('<td>').append(CPVs(data.cpv1URL, data.cpvAdd)));
                                                            newRow.append($('<td>').append(data.price + " " + data.currency));
                                                            newRow.appendTo("#eventsTable");

                                                        }
                                                );

                                            }

                                            function initPage() {
                                                $.getJSON("PCFilingApp?action=getBusinessEntitySupplier&entity="
                                                        + sessionStorage.entity,
                                                        function(data) {
                                                            $("#progressbar").hide();
                                                            if (data.success == true) {
                                                                $("#entityTable").append(
                                                                        "<tr><th><fmt:message key="name" bundle="${cons}" /></th><td>" + data.name
                                                                        + "</td></tr>");

                                                                $("#view").fadeIn();
                                                            } else {

                                                                $('#message').addClass('alert-error');
                                                                $('#message').append("Entity not found").fadeIn(
                                                                        'slow');
                                                            }

                                                        });
                                            }

                                            $(window).ready(function() {
                                                initPage();
                                                loadPage(true);
                                            });
        </script>

        <script>
            $("a").tooltip();
        </script>
    </body>
</html>
