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
                    <div class="alert hide" id="userHelper">
                      <button class="close"
                        onclick="userHelper('off')"
                        title="<fmt:message key="disableguide" bundle="${cons}" />" >×</button>
                        <fmt:message key="comparecontracts.help" />
                    </div>

                    <button class="btn btn-small" href="#" id="back">
                      <i class="icon-backward"></i>
                      <fmt:message key="goback" bundle="${cons}" />
                    </button>
                    
                    <h3><fmt:message key="comparecontracts.title" /></h3>

                    <div id="progressbar"></div>

                    <table class="table table-hover" id="contract-comparison-table">
                      <thead>
                        <tr>
                          <th></th>
                          <th><fmt:message key="comparecontracts.yourcontract" /></th>
                          <th><fmt:message key="comparecontracts.similarcontract" /></th>
                        </tr>
                      </thead>
                      <tbody>
                      </tbody>
                    </table>

                <%@include file="WEB-INF/jspf/stats-buyer.jspf" %>
            </div>
        </div>

        <%@include file="WEB-INF/jspf/footer.jspf" %>

        <script src="js/jquery.mustache.js"></script>
        <script id="contract-comparison-template" type="x-tmpl-mustache">
          <tr>
            <th><fmt:message key="title" bundle="${cons}" /></th>
            <td>{{source.title}}</td>
            <td>{{target.title}}</td>
          </tr>
          <tr>
            <th><fmt:message key="description" bundle="${cons}" /></th>
            <td>{{source.description}}</td>
            <td>{{target.description}}</td>
          </tr>
          <tr>
            <th><fmt:message key="contractingauthority" bundle="${cons}" /></th>
            <td>{{source.contractingAuthority.name}}</td>
            <td>{{target.contractingAuthority.name}}</td>
          </tr>
          <tr>
            <th><fmt:message key="cpvcodes" bundle="${cons}" /></th>
            <td>
              <ul>
                <li>{{source.mainCPV}}</li>
                {{#source.additionalCPV}}
                <li>{{.}}</li>
                {{/source.additionalCPV}}
              </ul>
            </td>
            <td>
              <ul>
                <li>{{target.mainCPV}}</li>
                {{#target.additionalCPV}}
                <li>{{.}}</li>
                {{/target.additionalCPV}}
              </ul>
            </td>
          </tr>
          <tr>
            <th><fmt:message key="procurementmethod" bundle="${cons}" /></th>
            <td>{{source.procedureType}}</td>
            <td>{{target.procedureType}}</td>
          </tr>
          <tr>
            <th><fmt:message key="eventtype" bundle="${cons}" /></th>
            <td>{{source.eventType}}</td>
            <td>{{target.eventType}}</td>
          </tr>
          <tr>
            <th><fmt:message key="createevent.tenders.deadline" /></th>
            <td>{{source.deadline}}</td>
            <td>{{target.deadline}}</td>
          </tr>
          <tr>
            <th><fmt:message key="estimatedprice" bundle="${cons}" /></th>
            <td>{{source.price}} {{source.currency}}</td>
            <td>{{target.price}} {{target.currency}}</td>
          </tr>
          <tr>
            <th><fmt:message key="createevent.startend" /></th>
            {{#source}}
              <td>
              {{#startDate}}{{#estimatedEndDate}}
                {{startDate}} &rarr; {{estimatedEndDate}}
              {{/estimatedEndDate}}{{/startDate}}
              </td>
            {{/source}}
            {{#target}}
              <td>
              {{#startDate}}{{#estimatedEndDate}}
                {{startDate}} &rarr; {{estimatedEndDate}}
              {{/estimatedEndDate}}{{/startDate}}
              </td>
            {{/target}}
          </tr>
          <tr>
            <th colspan="3"><h4><fmt:message key="location" bundle="${cons}" /></h4></th>
          </tr>
          <tr>
            <th><fmt:message key="createevent.locationrealization" /></th>
            <td>{{source.locationLabel}}</td>
            <td>{{target.locationLabel}}</td>
          </tr>
          <tr>
            <th>NUTS</th>
            <td>{{source.locationNUTS}}</td>
            <td>{{target.locationNUTS}}</td>
          </tr>
          <tr>
            <th colspan="3"><h4><fmt:message key="createevent.evaluationcriteria" /></h4></th>
          </tr>
          <tr>
            <th><fmt:message key="price" bundle="${cons}" /></th>
            <td>{{#source.criteria.LowestPrice}}{{.}} %{{/source.criteria.LowestPrice}}</td>
            <td>{{#target.criteria.LowestPrice}}{{.}} %{{/target.criteria.LowestPrice}}</td>
          </tr>
          <tr>
            <th><fmt:message key="technicalspecification" bundle="${cons}" /></th>
            <td>{{#source.criteria.TechnicalQuality}}{{.}} %{{/source.criteria.TechnicalQuality}}</td>
            <td>{{#target.criteria.TechnicalQuality}}{{.}} %{{/target.criteria.TechnicalQuality}}</td>
          </tr>
          <tr>
            <th><fmt:message key="deliverydate" bundle="${cons}" /></th>
            <td>{{#source.criteria.BestDate}}{{.}} %{{/source.criteria.BestDate}}</td>
            <td>{{#target.criteria.BestDate}}{{.}} %{{/target.criteria.BestDate}}</td>
          </tr>
        </script>
      
        <script src="js/date.format.js"></script>
        <script src="js/cpv-codes-<c:out value="${pageContext.request.locale.language}" />.js"></script>
        <script src="js/application.js"></script>
        <script src="js/matchmaker.js"></script>

        <script type="text/javascript">
          (function ($) {
            $(document).ready(function () {
              var formatData = function (data) {
                if (data.mainCPV) {
                  data.mainCPV = APP.cpv.getLabel(data.mainCPV);
                }
                if (data.additionalCPV) {
                  data.additionalCPV = jQuery.map(data.additionalCPV, function (cpv) {
                    return APP.cpv.getLabel(cpv);
                  });
                }
                if (data.startDate) {
                  data.startDate = APP.util.dateFormat(data.startDate);
                }
                if (data.estimatedEndDate) {
                  data.estimatedEndDate = APP.util.dateFormat(data.estimatedEndDate);
                }
                if (data.deadline) {
                  data.deadline = APP.util.dateFormat(data.deadline);
                }
                if (data.price) {
                  data.price = APP.util.priceFormat(Number(data.price)); 
                }
                return data;
              };

              var sourceUri = APP.util.getParameterByName("source"),
                targetUri = APP.util.getParameterByName("target"),
                template = $("#contract-comparison-template").html(),
                $comparisonTable = $("#contract-comparison-table tbody");

              Mustache.parse(template);

              $("#back").click(function (e) {
                window.history.back();
              });

              if (sourceUri && targetUri) {
                $.when(
                  $.getJSON("PCFilingApp", {action: "getContractJson", copyContractURL: sourceUri, public: false}),
                  $.getJSON("PCFilingApp", {action: "getContractJson", copyContractURL: targetUri, public: true})
                ).then(function (sourceData, targetData) {
                  var source = formatData(sourceData[0]),
                    target = formatData(targetData[0]);
                      
                  var html = Mustache.render(template, {
                     source: source,
                     target: target
                    });
                  $("#progressbar").hide();
                  $comparisonTable.html(html);
                }); 
              } else {
                console.log("Missing URIs to compare.");
              }
            });
          })(jQuery);
        </script>
    </body>
</html>
