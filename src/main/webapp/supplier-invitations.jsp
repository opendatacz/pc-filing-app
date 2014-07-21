<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <%@include file="WEB-INF/jspf/header.jspf" %>
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.supplier" />
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.constants" var="cons" />
        <link href="bootstrap/css/won.css" rel="stylesheet" />
    </head>
    <body>
        <%@include file="WEB-INF/jspf/header-private.jspf" %>
        <div class="container-fluid">
            <div class="row-fluid">
                <%@include file="WEB-INF/jspf/menu-supplier.jspf" %>

                <div class="span10">	

                    <div class="alert hide" id="userHelper">
                        <button class="close" onclick="userHelper('off')" title="Disable guide" >×</button>
                        <fmt:message key="invitations.help" />
                    </div>

                    <%@include file="WEB-INF/jspf/list-tools.jspf" %>

                    <h3 style="margin-bottom: 20px;"><fmt:message key="invitations.title" /></h3>

                    <table class="table table-striped table-bordered" style="display:none;" id="contractTable">
                        <thead>
                            <tr>
                                <th><fmt:message key="title" bundle="${cons}" /></th>
                                <th><fmt:message key="buyer" bundle="${cons}" /></th>
                                <th><fmt:message key="estimatedprice" bundle="${cons}" /></th>
                                <th><fmt:message key="specifications" bundle="${cons}" /></th>
                                <th><fmt:message key="publicationdate" bundle="${cons}" /></th>
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
        <script src="js/sessionstorage.1.4.js"></script>
        <script src="js/functions.js"></script>
        <script src="js/toolsSupplier.js"></script>
        <script src="js/script.js"></script>

        <script type="text/javascript">
                                var pagesTotal = 1;
                                var currentPage = 1;
                                var windowSize = 3;	// in each direction from the current page

                                function initPages()
                                {
                                    $.getJSON("InvitationComponent?action=getInvitationsPages", function(data)
                                    {
                                        pagesTotal = data;

                                        if (pagesTotal <= 1)
                                            return;

                                        if (pagesTotal > windowSize + 2) {
                                            $('#showAllPages').removeClass("hide");
                                        }

                                        $("#pages").append('<li><a href="#" id="loadPreviousContracts"><fmt:message key="prev" bundle="${cons}" /></a></li>');
                                        $("#loadPreviousContracts").click(function() {
                                            if ($(this).parent().hasClass("disabled")) {
                                                return false;
                                            }
                                            ;
                                            currentPage--;
                                            togglePageButtons();
                                            loadContracts();
                                        });

                                        for (var i = 1; i <= pagesTotal; i++) {
                                            $("#pages").append('<li><a href="#" id="loadContractsPage' + i + '">' + i + '</a></li>');
                                            $("#loadContractsPage" + i).on('click', function() {
                                                if ($(this).parent().hasClass("active")) {
                                                    return false;
                                                }
                                                ;
                                                currentPage = parseInt($(this).attr("id").match(/(\d+)$/)[0], 10);
                                                togglePageButtons();
                                                loadContracts();
                                            });
                                        }

                                        $("#pages").append('<li><a href="#" id="loadNextContracts"><fmt:message key="next" bundle="${cons}" /></a></li>');
                                        $("#loadNextContracts").click(function() {
                                            if ($(this).parent().hasClass("disabled")) {
                                                return false;
                                            }
                                            ;
                                            currentPage++;
                                            togglePageButtons();
                                            loadContracts();
                                        });

                                        togglePageButtons();
                                    }
                                    );
                                }

                                function loadContracts()
                                {
                                    $.getJSON("InvitationComponent?action=getInvitations&from=" + 10 * (currentPage - 1) + "&to=" + 10 * currentPage, function(data)
                                    {
                                        $('#progressbar').hide();
                                        $('#contractTable').fadeIn('slow');
                                        $('#contractTable tbody').remove();
                                        $.each(data, function(i, data) {
                                            var div_data = '<tr style="vertical-align: middle;"><td><a href="supplier-view-event.jsp" onclick="showEvent(\'' + encodeURIComponent(data.contractURL) + '\',\'' + encodeURIComponent(data.buyerURL) + '\')">' + data.title + '</a></td>         <td><a href="entity-buyer.jsp" onclick="showEntity(\'' + data.ownerEntity + '\')">' + data.ownerName + '</a></td>         <td>' + data.price + ' ' + data.currency + '</td>                  <td>' + data.cpv + '<br>                  </td>                  <td>' + htmlEncode(data.published) + '</td>                  <td>' + data.deadline + '</td> <td><div class="btn-group"> <a class="btn " href="supplier-create-tender.jsp" onclick="saveTenderInfo(\'' + encodeURIComponent(data.contractURL) + '\', \'' + encodeURIComponent(data.title) + '\', \'' + encodeURIComponent(data.buyerURL) + '\')"  title="<fmt:message key="accept" bundle="${cons}" />"><fmt:message key="accept" bundle="${cons}" /></a> <a class="btn " href="#" onclick="deleteInv(\'' + encodeURIComponent(data.invitationURL) + '\')"  title="<fmt:message key="delete" bundle="${cons}" />"><fmt:message key="delete" bundle="${cons}" /></a></div></td>  </tr>';
                                            $(div_data).appendTo("#contractTable");
                                        });
                                        $('td').tooltip();
                                    });
                                }

                                function deleteInv(contractURL) {
                                    if (!confirm('<fmt:message key="invitations.delete.confirm" />')) {
                                        return;
                                    }

                                    $.getJSON("InvitationComponent?action=deleteInvitation&invURL=" + contractURL, function(data)
                                    {
                                        window.location.reload();
                                    });
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

                                    for (var i = currentPage - windowSize; i <= currentPage + windowSize; i++) {
                                        if (i > 1 && i < pagesTotal) {
                                            $("#loadContractsPage" + i).parent().removeClass("reallyhide");
                                        }
                                    }

                                    $(".3dots").remove();
                                    if ((currentPage - windowSize - 1 >= 1) && $("#loadContractsPage" + (currentPage - windowSize - 1)).parent().hasClass('reallyhide')) {
                                        $("#loadContractsPage" + (currentPage - windowSize - 1)).parent().after('<li class="3dots disabled"><a href="#">...</a></li>');
                                    }
                                    if ((currentPage + windowSize + 1 <= pagesTotal) && $("#loadContractsPage" + (currentPage + windowSize + 1)).parent().hasClass('reallyhide')) {
                                        $("#loadContractsPage" + (currentPage + windowSize + 1)).parent().before('<li class="3dots disabled"><a href="#">...</a></li>');
                                    }
                                }

                                $(window).ready(function() {
                                    initPages();
                                    loadContracts();
                                });

                                function saveTenderInfo(contractURL, title, buyerURL) {
                                    sessionStorage.contractURL = contractURL;
                                    sessionStorage.contractTitle = title;
                                    sessionStorage.buyerURL = buyerURL;
                                }

        </script>

        <script>
            $("a").tooltip();
        </script>
    </body>
</html>
