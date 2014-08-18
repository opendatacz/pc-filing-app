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
                        <fmt:message key="calls.help" />
                    </div>

                    <%--
                    <%@include file="WEB-INF/jspf/list-tools.jspf" %>
                    --%>

                    <h3 class="matchmakerResultsTitle"><fmt:message key="calls.title" /></h3>          

                    <table class="table table-striped table-bordered" id="matchResultsTable">
                        <thead>
                            <tr>
                                <th><fmt:message key="rank" bundle="${cons}" /></th>  
                                <th><fmt:message key="title" bundle="${cons}" /></th>
                                <th><fmt:message key="description" bundle="${cons}" /></th>
                                    <%-- <th><fmt:message key="estimatedprice" bundle="${cons}" /></th> --%>
                                    <%-- <th><fmt:message key="specifications" bundle="${cons}" /></th> --%>
                                <th><fmt:message key="publicationdate" bundle="${cons}" /></th>
                                <th><fmt:message key="tenderdeadline" bundle="${cons}" /></th>
                                    <%-- <th><fmt:message key="action" bundle="${cons}" /></th> --%>
                            </tr>
                        </thead>
                        <tbody></tbody>
                    </table>

                    <div id="progressbar"></div>


                    <div class="pagination pagination-centered">
                        <ul id="pagination"></ul>
                    </div>
                </div>
            </div>
        </div>

        <%@include file="WEB-INF/jspf/footer.jspf" %> 
        <script src="js/date.format.js"></script>
        <script src="js/jquery.twbsPagination.min.js"></script>
        <script src="js/jquery.mustache.js"></script>
        <script src="js/jquery.jtruncate.pack.js"></script>
        <script src="js/sessionstorage.1.4.js"></script>
        <script src="js/application.js"></script>
        <script src="js/script.js"></script>
        <script src="js/matchmaker.js"></script>
        <script id="matchmaker-results-template" type="x-tmpl-mustache">
            {{#matches}}
            <tr>
            <td>{{rank}}</td>
            <td>
              <a class="contract-link"
                 href="supplier-view-event.jsp"
                 data-contract-uri="{{uri}}">{{label}}</a>
             </td>
            <td class="truncate fixedCol">{{description}}</td>
            <%-- <td>{{estimatedPrice}}</td> --%>
            <td>{{publicationDate}}</td>
            <td>{{tenderDeadline}}</td>
            </tr>
            {{/matches}}
        </script>
        <script type="text/javascript">
                            (function($) {
                                $(document).ready(function() {
                                    dateFormat.i18n.monthNames = [
                                        "<fmt:message key="januaryshort" bundle="${cons}" />",
                                        "<fmt:message key="februaryshort" bundle="${cons}" />",
                                        "<fmt:message key="marchshort" bundle="${cons}" />",
                                        "<fmt:message key="aprilshort" bundle="${cons}" />",
                                        "<fmt:message key="mayshort" bundle="${cons}" />",
                                        "<fmt:message key="juneshort" bundle="${cons}" />",
                                        "<fmt:message key="julyshort" bundle="${cons}" />",
                                        "<fmt:message key="augustshort" bundle="${cons}" />",
                                        "<fmt:message key="septembershort" bundle="${cons}" />",
                                        "<fmt:message key="octobershort" bundle="${cons}" />",
                                        "<fmt:message key="novembershort" bundle="${cons}" />",
                                        "<fmt:message key="decembershort" bundle="${cons}" />",
                                        "<fmt:message key="january" bundle="${cons}" />",
                                        "<fmt:message key="february" bundle="${cons}" />",
                                        "<fmt:message key="march" bundle="${cons}" />",
                                        "<fmt:message key="april" bundle="${cons}" />",
                                        "<fmt:message key="may" bundle="${cons}" />",
                                        "<fmt:message key="june" bundle="${cons}" />",
                                        "<fmt:message key="july" bundle="${cons}" />",
                                        "<fmt:message key="august" bundle="${cons}" />",
                                        "<fmt:message key="september" bundle="${cons}" />",
                                        "<fmt:message key="october" bundle="${cons}" />",
                                        "<fmt:message key="november" bundle="${cons}" />",
                                        "<fmt:message key="december" bundle="${cons}" />"
                                    ];
                                    var config = {
                                        dom: {
                                            $matchResultsTable: $("#matchResultsTable"),
                                            $pagination: $("#pagination"),
                                            $progressbar: $("#progressbar"),
                                            templateId: "#matchmaker-results-template"
                                        },
                                        labels: {
                                            first: "<fmt:message key="first" bundle="${cons}" />",
                                            last: "<fmt:message key="last" bundle="${cons}" />",
                                            notfound: "<fmt:message key="notfound" bundle="${cons}" />",
                                            prev: "<fmt:message key="prev" bundle="${cons}" />",
                                            truncate: {
                                                lessText: "<fmt:message key="lesstext" bundle="${cons}" />",
                                                moreText: "<fmt:message key="moretext" bundle="${cons}" />"
                                            }
                                        },
                                        resourceUri:  "<%=uc.getPreference("businessEntity")%>",
                                                //"http://linked.opendata.cz/resource/business-entity/e6e9258d-2602-45e6-829d-62a89a0e812d",
                                        source: "business-entity",
                                        target: "contract"
                                    };

                                    if (config.resourceUri) {
                                        MATCHMAKER.getMatches(config);
                                    }
                                });
                            })(jQuery);
        </script>

        <%--
        <script type="text/javascript">
                                var pagesTotal = 1;
                                var currentPage = 1;
                                var windowSize = 3;	// in each direction from the current page

                                function initPages()
                                {
                                    $.getJSON("Matchmaker-legacy?action=getSuitableTendersPages", function(data)
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
                                    $.getJSON("Matchmaker-legacy?action=getSuitableTenders&from=" + 10 * (currentPage - 1) + "&to=" + 10 * currentPage, function(data)
                                    {
                                        $('#progressbar').hide();
                                        $('#contractTable').fadeIn('slow');
                                        $('#contractTable tbody').remove();
                                        $.each(data, function(i, data) {
                                            var div_data = '<tr style="vertical-align: middle;">                  <td title="' + data.description + '">' + data.title + '</td>                  <td>' + data.price + ' ' + data.currency + '</td>                  <td>' + data.cpv + '<br>                  </td>                  <td>' + data.publicationDate + '</td>                  <td>' + data.deadline + '</td>                  <td>                    <div class="btn-group" style="display:none;">                      <a  href="PCFilingApp?forward=mycontracts.jsp&action=publishPrivateContract&contractURI=' + data.contractURL + '" onclick="return confirm(\'<fmt:message key="calls.publish.confirm" />\')" rel="tooltip" data-placement="bottom" class="btn" title="<fmt:message key="calls.publish" />"><i class="icon-globe"></i></a>                      <a href="mycontracts-step-01.jsp#" rel="tooltip" data-placement="bottom" class="btn disabled" title="<fmt:message key="calls.edit" />"><i class="icon-edit"></i></a>                      <a href="PCFilingApp?forward=mycontracts.jsp&action=deletePrivateContract&contractURL=' + data.contractURL + '" onclick="return confirm(\'<fmt:message key="calls.delete.confirm" />\')" rel="tooltip" data-placement="bottom" class="btn" title="<fmt:message key="calls.delete" />"><i class="icon-trash"></i></a>                      <button class="btn dropdown-toggle" data-toggle="dropdown" ><i class="icon-search"></i> <span class="caret"></span></button>                      <ul class="dropdown-menu">                        <li ><a onclick="findSimilarContracts(\'' + data.contractURL + '\', \'' + data.title + '\', \'' + data.price + '\', \'' + data.currency + '\', \'' + data.cpv + '\')" href="similar-contracts.jsp" data-original-title="Click to search for similar contracts."><fmt:message key="calls.findsimilar" /></a></li>                        <li ><a href="#" title="<fmt:message key="calls.findsuppliers.desc" />"><fmt:message key="calls.findsuppliers" /></a></li>                      </ul>                    </div>                  </td>                </tr>';
                                            $(div_data).appendTo("#contractTable");
                                        });
                                        $('td').tooltip();
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

        </script>

        <script>
            $("a").tooltip();
        </script>
        --%>
    </body>
</html>
