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
                      <button class="close"
                        onclick="userHelper('off')"
                        title="<fmt:message key="disableguide" bundle="${cons}" />" >×</button>
                        <fmt:message key="published.help" />
                    </div>

                    <h4><fmt:message key="published.title" /></h4>

                    <table class="table table-striped table-bordered" style="display: none;" id="contractTable">
                        <thead>
                            <tr>
                                <th><fmt:message key="title" bundle="${cons}" /></th>
                                <th><fmt:message key="estimatedprice" bundle="${cons}" /></th>
                                <th><fmt:message key="cpvcodes" bundle="${cons}" /></th>
                                <th><fmt:message key="tenderstocontract" bundle="${cons}" /></th>
                                <th><fmt:message key="lastupdate" bundle="${cons}" /></th>
                                <th><fmt:message key="tenderdeadline" bundle="${cons}" /></th>
                                <th class="col-span-210"><fmt:message key="action" bundle="${cons}" /></th>
                                <th class="col-span-170"><fmt:message key="matchmaker" bundle="${cons}" /></th>
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
                <%@include file="WEB-INF/jspf/predict-bidders-modal.jspf" %>
                <%@include file="WEB-INF/jspf/stats-buyer.jspf" %>
            </div>
        </div>

        <%@include file="WEB-INF/jspf/footer.jspf" %>

        <script id="tablePage" type="x-tmpl-mustache">
          {{#rows}}
          <tr data-contract-uri="{{contractURI}}"
              data-contract-title="{{title}}"
              data-contract-description="{{description}}"
              data-contract-price="{{price}}"
              data-contract-currency="{{currency}}"
              data-contract-cpv="{{cpv1URL}}"
              data-contract-place="{{place}}">
            <td>
              <a class="contract-link"
                 href="buyer-view-event.jsp">{{title}}</a>
            </td>
            <td>{{price}} {{currency}}</td>
            <td>{{{cpvs}}}</td>
            <td>
              <div class="btn-group">
                <a class="btn btn-small {{#sealed}}{{^openingTime}}disabled{{/openingTime}}{{/sealed}} view-tenders"
                   href="buyer-submitted-tenders.jsp">
                  {{tendersCount}}
                </a>
                {{#sealed}}{{^openingTime}}
                  <a class="btn btn-small {{#openDisabled}}disabled{{/openDisabled}} open-tenders"
                     data-confirmation="<fmt:message key="published.open.confirm" />"
                     data-unable-to-open="<fmt:message key="published.open.unableopen" />"
                     data-unable-to-process="<fmt:message key="published.open.unableprocess" />"
                     href="#">
                    <fmt:message key="opentenders" bundle="${cons}" />
                  </a>
                {{/openingTime}}{{/sealed}}
              </div>
            </td>
            <td>{{modified}}</td>
            <td>{{deadline}}</td>
            <td>
              <div class="btn-group btn-group-vertical">
                <a class="btn btn-small btn-danger confirm"
                  href="PCFilingApp?action=cancelContract&forward=buyer-cancelled.jsp&contractURL={{encodedContractURI}}"
                  data-confirmation="<fmt:message key="published.cancel.confirm" />">
                  <i class="icon-remove-sign"></i>
                  <fmt:message key="cancel" bundle="${cons}" />
                </a>
                <a class="btn btn-small send-notification"
                  href="#"
                  data-prompt="<fmt:message key="published.notification" />">
                  <i class="icon-envelope"></i>
                  <fmt:message key="published.invite" />
                </a>
                <a class="btn btn-small predict-bidders"
                   data-toggle="modal"
                   data-target="#predict-bidders"
                   href="#">
                  <i class="icon-question-sign"></i>
                  <fmt:message key="predictbidders" bundle="${cons}" />
                </a>
              </div>
            </td>
            <td>
              <div class="btn-group btn-group-vertical">
                <a class="btn btn-small save-contract"
                   href="buyer-similar-events.jsp?private=true">
                  <i class="icon-search"></i>
                  <fmt:message key="similarevents" bundle="${cons}" />
                </a>
                <a class="btn btn-small save-contract"
                   href="buyer-suitable-suppliers.jsp?private=true">
                  <i class="icon-magnet"></i>
                  <fmt:message key="suitablesuppliers" bundle="${cons}" />
                </a>
              </div>
            </td>
          </tr>
          {{/rows}}
        </script>

        <script src="js/jquery.twbsPagination.min.js"></script>
        <script src="js/jquery.mustache.js"></script>
        <script src="js/date.format.js"></script>		
        <script src="js/application.js"></script>
        
        <script src="js/functions.js"></script>
        <script src="js/sessionstorage.1.4.js"></script>
        <script src="js/cpv-codes-${pageContext.request.locale}.js"></script>
        <script src="js/script.js"></script>
        <script src="js/toolsBuyer.js"></script>
        <script src="js/table.js"></script>
        <script src="js/services.js"></script>

        <script type="text/javascript">
          (function ($) {
            $(document).ready(function () {
              var tableName = "PublishedCalls";
              TABLE.init(tableName);
            });
          })(jQuery);
        </script>
    </body>
</html>
