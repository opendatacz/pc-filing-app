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
          <h3 class="matchmakerResultsTitle">
            <fmt:message key="similarevents.title" /> '<span id="contractTitle"></span>'.
          </h3>

          <!--
          <div class="btn-group btn-group">
            <a href="mycontracts-step-05.html" class="btn btn-small" title="Click to compare your contract with this one.">compare</a>
          </div>
          -->

          <table class="table table-striped table-bordered" id="matchResultsTable">
            <thead>
              <tr>
                <th><fmt:message key="similarity" bundle="${cons}" /></th>
                <th><fmt:message key="contracttitle" bundle="${cons}" /></th>
                <!--
                <th><fmt:message key="place" bundle="${cons}" /></th>
                <th><fmt:message key="action" bundle="${cons}" /></th>
                -->
              </tr>
            </thead>
            <tbody>
            </tbody>
          </table>

          <div id="progressbar"></div>

          <div class="pagination pagination-centered">
            <ul id="pagination"></ul>
          </div>

        </div>
        <%@include file="WEB-INF/jspf/stats-buyer.jspf" %>
        <div id="addedboxes">				
          <div class="box">
            <div id="refine" style="display:none;">
              <div class="box-header">
                <h2><fmt:message key="similarevents.results" /></h2>
              </div>
              <div class="box-content">
                <form id="refineForm" style="margin:5px">
                  <input type="hidden" name="refine" value="true">
                  <label><fmt:message key="age" bundle="${cons}" />:</label>
                  <input name="dateFrom" class="input-small hasDatePicker" type="text" placeholder="<fmt:message key="similarevents.filter.datefrom" /> ..." /> - <input name="dateTo" class="input-small hasDatePicker" type="text" placeholder="<fmt:message key="similarevents.filter.dateto" /> ..." />
                  <label><fmt:message key="price" bundle="${cons}" />:</label>
                  <input disabled name="priceFrom" class="input-small" type="text" placeholder="<fmt:message key="similarevents.filter.minprice" /> ..." /> - <input disabled name="priceTo" class="input-small" type="text" placeholder="<fmt:message key="similarevents.filter.maxprice" /> ..." />
                  <select disabled name="priceCurrency" style="width:60px">
                    <option>EUR</option>
                    <option>USD</option>
                  </select>
                  <label><fmt:message key="distance" bundle="${cons}" />:</label>
                  <input name="maxDistance" class="input-small" type="text" placeholder="<fmt:message key="similarevents.filter.maxdistance" /> ..." />
                  <label><fmt:message key="countries" bundle="${cons}" />:</label>
                  <select name="countries" style="width:160px" multiple="multiple">
                    <option value="-- any --">-- any --</option>
                    <option value="Austria">Austria</option>
                    <option value="Belgium">Belgium</option>
                    <option value="Bulgaria">Bulgaria</option>
                    <option value="Cyprus">Cyprus</option>
                    <option value="Czech Republic">Czech Republic</option>
                    <option value="Denmark">Denmark</option>
                    <option value="Estonia">Estonia</option>
                    <option value="Finland">Finland</option>
                    <option value="France">France</option>
                    <option value="Germany">Germany</option>
                    <option value="Greece">Greece</option>
                    <option value="Hungary">Hungary</option>
                    <option value="Ireland">Ireland</option>
                    <option value="Island">Island</option>
                    <option value="Italy">Italy</option>
                    <option value="Latvia">Latvia</option>
                    <option value="Lithuania">Lithuania</option>
                    <option value="Luxembourg">Luxembourg</option>
                    <option value="Macedonia">Macedonia</option>
                    <option value="Malta">Malta</option>
                    <option value="Netherlands">Netherlands</option>
                    <option value="Norway">Norway</option>
                    <option value="Poland">Poland</option>
                    <option value="Portugal">Portugal</option>
                    <option value="Romania">Romania</option>
                    <option value="Slovakia">Slovakia</option>
                    <option value="Slovenia">Slovenia</option>
                    <option value="Spain">Spain</option>
                    <option value="Sweden">Sweden</option>
                    <option value="Switzerland">Switzerland</option>
                    <option value="United Kingdom">United Kingdom</option>
                    <option value="United States">United States</option>
                  </select>
                  <label><fmt:message key="similarity" bundle="${cons}" />:</label>
                  <input name="minScore" class="input-small" type="text" placeholder="<fmt:message key="similarevents.filter.minsimilarity" /> ..." />% - <input name="maxScore" class="input-small" type="text" placeholder="<fmt:message key="similarevents.filter.maxsimilarity" /> ..." />%
                  <button name="submit" onclick='refineResults();
                    return false;' class="btn btn-primary"><fmt:message key="refine" bundle="${cons}" />!</button>
                </form>
              </div>
            </div>
          </div>
        </div>	
      </div>
    </div>

    <%@include file="WEB-INF/jspf/footer.jspf" %>

    <!--<script src="js/cpvs.js"></script>-->
    <script src="js/functions.js"></script>
    <script src="js/jquery-ui.js"></script>
    <script src="js/script.js"></script>
    <script src="js/date.format.js"></script>	
    <script src="js/toolsBuyer.js"></script>
    <script src="js/table.js"></script>

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
        </tr>
      {{/matches}}
    </script>
    <script type="text/javascript">
      (function ($) {
        $(document).ready(function () {
          // Boilerplate
          $(".hasDatePicker").datepicker({dateFormat: 'yy-mm-dd'});
          //$("#addedboxes .box").appendTo($("#statsbuyer"));
          checkUser();
          $("#contractTitle").text(sessionStorage.contractTitle); 

          var config = {
            contractUri: //sessionStorage.contractURL,
              "http://linked.opendata.cz/resource/vestnikverejnychzakazek.cz/public-contract/484169-7403010084169",
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
            source: "contract",
            target: "contract"
          };

          if (config.contractUri) {
            MATCHMAKER.getMatches(config);
          }
        });
      })(jQuery);
    </script>

    <script type="text/javascript">
      /*
                                        var pagesTotal = 1;
                                        var currentPage = 1;
                                        var windowSize = 3;	// in each direction from the current page

                                        var arrdata = [];

                                        var contractURL = sessionStorage.contractURL;
                                        var title = sessionStorage.contractTitle;
                                        // var cpvString = sessionStorage.contractCpvString; // todo remove all traces?
                                        var price = sessionStorage.contractPrice;
                                        var currency = sessionStorage.contractCurrency;

                                        var refineParams = "";

                                        function initRefineParams() {
                                            refineParams = "";
                                            $("form#refineForm :input").each(function() {
                                                var input = $(this);
                                                refineParams += "&" + input.attr("name") + "=" + encodeURIComponent(input.val());
                                            });
                                            //alert(refineParams);
                                        }

                                        function refineResults() {
                                            $('#progressbar').show();
                                            $('#contractTable').hide();
                                            $('#refine').hide();
                                            initRefineParams();
                                            initPages();
                                            loadContracts();
                                        }

                                        function initPages()
                                        {
                                            $.getJSON("Matchmaker?action=getSimilarContractsPages&contractURL=" + encodeURIComponent(contractURL) + refineParams, function(data)
                                            {
                                                pagesTotal = data;

                                                if (pagesTotal > windowSize + 2) {
                                                    $('#showAllPages').removeClass("hide");
                                                }

                                                $("#pages").empty();

                                                $("#pages").append('<li><a href="#" id="loadPreviousContracts"><fmt:message key="prev" bundle="${cons}" /></a></li>');
                                                $("#loadPreviousContracts").off('click');
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
                                                $("#loadNextContracts").off('click');
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
                                            $.getJSON("Matchmaker?action=getSimilarContracts&contractURL=" + encodeURIComponent(contractURL) + "&from=" + 10 * (currentPage - 1) + "&to=" + 10 * currentPage + refineParams, function(data)
                                            {
                                                $('#progressbar').hide();
                                                $('#contractTable').fadeIn('slow');
                                                $('#refine').fadeIn('slow');
                                                $('#contractTable tbody').remove();
                                                $.each(data, function(i, data) {
                                                    arrdata[i] = data;
                                                    var div_data = '<tr style="vertical-align: middle;"><td style="text-align: center;"> ' +
                                                            '<a onclick="compareWith(\'' + i + '\');" href="compare-contracts.jsp?mm" data-original-title="">' +
                                                            htmlEncode(data.percent.toFixed(1)) +
                                                            '%</a></td><td title="' + htmlEncode(data.description) + '">' + htmlEncode(data.title) +
                                                            '</td><td>' + htmlEncode(data.place) + '<br></td><td style="vertical-align:middle;">' +
                                                            '<div class="btn-group btn-group"><a onclick="compareWith(\'' + i +
                                                            '\');" class="btn btn-small" href="compare-contracts.jsp" data-original-title="">' +
                                                            '<fmt:message key="compare" bundle="${cons}" /></a> </div></td></tr>';
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
                                            if (currentPage <= 1) {
                                                $("#loadPreviousContracts").parent().addClass("disabled");
                                            } else {
                                                $("#loadPreviousContracts").parent().removeClass("disabled");
                                            }
                                            ;
                                            if (currentPage >= pagesTotal) {
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
                                            $(".hasDatePicker").datepicker({dateFormat: 'yy-mm-dd'});
                                            $('#contractTitle').append(title);
                                            checkUser();
                                            initPages();
                                            loadContracts();
                                            $("#addedboxes .box").appendTo($("#statsbuyer"));
                                        });


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

                                        function compareWith(i) {
                                            var data = arrdata[i];
                                            sessionStorage.contractURL2 = data.contractURL;
                                            sessionStorage.contractTitle2 = data.title;
                                            sessionStorage.contractDescription2 = data.description;
                                            sessionStorage.contractPrice2 = data.price;
                                            sessionStorage.contractCurrency2 = data.currency;
                                            //sessionStorage.contractCpvString2 = data.cpvString;
                                            sessionStorage.contractPlace2 = data.place;
                                            sessionStorage.comparerMessages = JSON.stringify(data.comparerMessages);
                                            sessionStorage.contractTriplesURL2 = data.triplesURL;
                                        }

                                        $("a").tooltip();
          */
        </script>
    </body>
</html>
