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
                  <h3 class="matchmakerResultsTitle">
                    <fmt:message key="suitablesuppliers.title" /> '<span id="contractTitle"></span>'.
                  </h3>

                  <table class="table table-striped table-bordered" id="matchResultsTable">
                    <thead>
                      <tr>
                        <th><fmt:message key="rank" bundle="${cons}" /></th>
                        <th><fmt:message key="suppliername" bundle="${cons}" /></th>
                        <th><fmt:message key="location" bundle="${cons}" /></th>
                        <th class="<c:if test="${pageContext.request.getParameter('invite') eq 'false'}">hidden</c:if>"><fmt:message key="action" bundle="${cons}" /></th>
                      </tr>
                    </thead>
                    <tbody>
                    </tbody>
                  </table>

                  <div id="progressbar"></div>

                  <%--
                  <div id="additionalMetrics" class="pull-right" style="display:none;">
                    <a onclick="additionalMetrics = true;
                      $('#additionalMetrics').hide();
                      $('#progressbar').show();
                      loadContracts();" href="#">
                      <fmt:message key="suitablesuppliers.compute" /></a></div>
                  --%>
                  
                  <div class="pagination pagination-centered">
                    <ul id="pagination"></ul>
                  </div>
                </div>
                <%@include file="WEB-INF/jspf/stats-buyer.jspf" %>
            </div>
        </div>

        <%@include file="WEB-INF/jspf/footer.jspf" %>
        
        <%--
        <script src="js/cpvs.js"></script>
        <script src="js/functions.js"></script>
        <script src="js/script.js"></script>
        <script src="js/date.format.js"></script>	
        <script src="js/toolsBuyer.js"></script>
        <script src="js/table.js"></script>
        --%>

        <script src="js/toolsBuyer.js"></script>
        <script src="js/main.js"></script>
        <script src="js/sessionstorage.1.4.js"></script>
        <script src="js/jquery.mustache.js"></script>
        <script src="js/jquery.twbsPagination.min.js"></script>
        <script src="js/matchmaker.js"></script>
        <script id="matchmaker-results-template" type="x-tmpl-mustache">
          {{#matches}}
            <tr>
              <td>{{rank}}</td>
              <td><a href="{{uri}}">{{label}}</a></td>
              <td>{{addressLocality}}</td>
              <td class="<c:if test="${pageContext.request.getParameter('invite') eq 'false'}">hidden</c:if>">
                {{#email}}
                <a class="notificationButton btn btn-small"
                   title="<fmt:message key="suitablesuppliers.notification.invite" />"
                   href="javascript:void(0);"
                   data-email="{{.}}"
                   data-email-prompt="<fmt:message key="suitablesuppliers.notification" />"
                   data-subject="<fmt:message key="suitablesuppliers.notification.invite.subject" />"
                   data-template="<fmt:message key="suitablesuppliers.notification.invite.template" />">
                  <i class="icon-envelope"></i>
                  &nbsp;<fmt:message key="invite" bundle="${cons}" />
                </a>
                {{/email}}
              </td>
            </tr>
          {{/matches}}
        </script>
        <script type="text/javascript">
          (function ($) {
           $(document).ready(function () {
              // Boilerplate
              checkUser();
              $("#contractTitle").text(sessionStorage.contractTitle); 

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
                  prev: "<fmt:message key="prev" bundle="${cons}" />"
                },
                resourceUri: sessionStorage.contractURL,
                source: "contract",
                target: "business-entity"
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

            var contractURL = sessionStorage.contractURL;
            
            var title = sessionStorage.contractTitle;
            //var cpvString = sessionStorage.contractCpvString;
            var price = sessionStorage.contractPrice;
            var currency = sessionStorage.contractCurrency;

            var additionalMetrics = false;

            function initPages()
            {
                $.getJSON("Matchmaker?action=getSuitableSuppliersPages&contractURL=" + encodeURIComponent(contractURL) + "&additionalMetrics=" + additionalMetrics, function(data)
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
                $.getJSON("Matchmaker?action=getSuitableSuppliers&contractURL=" + encodeURIComponent(contractURL) + "&from="
                        + 10 * (currentPage - 1) + "&to=" + 10 * currentPage + "&additionalMetrics=" + additionalMetrics,
                        function(data)
                        {
                            $('#progressbar').hide();
                            $('#additionalMetrics').show()
                            $('#contractTable').fadeIn('slow');
                            $('#contractTable tbody').remove();
                            $.each(data, function(i, data) {
                                var div_data = '<tr style="vertical-align: middle;"><td style="text-align: center;">' +
                                        htmlEncode(data.percent.toFixed(1)) + '%</td>                  <td>' + htmlEncode(data.name) +
                                        '</td><td>' + htmlEncode(data.place) +
                                        '<br></td><td style="vertical-align:middle;"><a onclick="sendNotification();" href="#" ' +
                                        ' class="btn btn-small"><i class="icon-envelope"></i></a> &nbsp;&nbsp;&nbsp; <a href="' +
                                        htmlEncode(data.triplesURL) + '" target="_blank">RDF (N3)</a>' +
                                        (additionalMetrics ? ' &nbsp;&nbsp;&nbsp;<span title="' +
                                                'contracts: ' + htmlEncode(data.contracts) +
                                                '\ncontractsSameCPV: ' + htmlEncode(data.contractsSameCPV) +
                                                '\nvolumeOfContracts: ' + htmlEncode(data.volumeOfContracts) +
                                                '\nvolumeOfContractsSameCPV: ' + htmlEncode(data.volumeOfContractsSameCPV) +
                                                '\ncontractingAuthorities: ' + htmlEncode(data.contractingAuthorities) +
                                                '\ncontractingAuthoritiesSameCPV: ' + htmlEncode(data.contractingAuthoritiesSameCPV) +
                                                '">additional metrics</span>' : '') + '</td>                </tr>';
                                $(div_data).appendTo("#contractTable");
                            });
                            $('td').tooltip();
                            //$('a').tooltip();
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

            function checkUser() {
                if (sessionStorage.username != undefined) {
                    $("#username").append(sessionStorage.username);
                }
                $.getJSON("SystemManager?action=getuser", function(data)
                {
                    if (data == null || data.length == 0) {
                        sessionStorage.clear();
                        window.location.href = "./";
                    } else {
                        $("#username").html(data);
                        sessionStorage.username = data;
                    }
                });
            }

            function sendNotification() {
                var recipient = prompt("<fmt:message key="suitablesuppliers.notification" />", "@");
                if (recipient == null) {
                    return;
                }
                var description = "undefined"; // todo get from sessionStore
                $.getJSON("Utils?action=sendNotification&contractName=" + encodeURIComponent(title) + "&contractDescription=" + encodeURIComponent(description) + "&recipient=" + encodeURIComponent(recipient), function(data)
                {
                    if (data) {
                        alert("<fmt:message key="suitablesuppliers.notification.sent" />");
                    } else {
                        alert("<fmt:message key="suitablesuppliers.notification.error" /> " + recipient);
                    }
                });
            }

            $(window).ready(function() {
                $('#contractTitle').append(title);
                checkUser();
                initPages();
                loadContracts();
            });

            $("a").tooltip();
        </script>
        --%>
    </body>
</html>
