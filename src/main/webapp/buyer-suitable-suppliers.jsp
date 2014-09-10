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
                      <fmt:message key="suitablesuppliers.help" />
                  </div>

                  <button class="btn btn-small" href="#" id="back">
                    <i class="icon-backward"></i>
                    <fmt:message key="goback" bundle="${cons}" /> 
                  </button>

                  <h3 class="matchmakerResultsTitle">
                    <fmt:message key="suitablesuppliers.title" /> '<span id="contractTitle"></span>'.
                  </h3>

                  <table class="table table-striped table-bordered" id="matchResultsTable">
                    <thead>
                      <tr>
                        <th><fmt:message key="rank" bundle="${cons}" /></th>
                        <th><fmt:message key="suppliername" bundle="${cons}" /></th>
                        <th><fmt:message key="location" bundle="${cons}" /></th>
                        <th><fmt:message key="action" bundle="${cons}" /></th>
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
            </div>
        </div>

        <%@include file="WEB-INF/jspf/footer.jspf" %>
        
        <script src="js/toolsBuyer.js"></script>
        <script src="js/main.js"></script>
        <script src="js/sessionstorage.1.4.js"></script>
        <script src="js/jquery.mustache.js"></script>
        <script src="js/jquery.twbsPagination.min.js"></script>
        <script src="js/application.js"></script>
        <script src="js/matchmaker.js"></script>
        <script src="js/services.js"></script>
        <script id="matchmaker-results-template" type="x-tmpl-mustache">
          {{#matches}}
            <tr>
              <td>{{rank}}</td>
              <td><a class="uri" href="{{uri}}">{{label}}</a></td>
              <td>{{addressLocality}}</td>
              <td>
                <div class="btn-group">
                  <a class="btn notificationButton <c:if test="${pageContext.request.getParameter('invite') eq 'false'}">hidden</c:if>"
                    title="<fmt:message key="suitablesuppliers.notification.invite" />"
                    href="javascript:void(0);"
                    data-email="{{email}}"
                    data-email-prompt="<fmt:message key="suitablesuppliers.notification" />"
                    data-subject="<fmt:message key="suitablesuppliers.notification.invite.subject" />"
                    data-template="<fmt:message key="suitablesuppliers.notification.invite.template" />">
                    <i class="icon-envelope"></i>
                    &nbsp;<fmt:message key="invite" bundle="${cons}" />
                  </a>
                  <a class="btn payolaView"
                    href="javascript:void(0);"
                    data-endpoint="${pageContext.                                                                                 getAttribute("payolaEndpoint")}">
                    <i class="icon-arrow-right"></i>
                    <fmt:message key="visualization.view" /></a>
                </div>
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
              $("#back").click(function (e) {
                window.history.back();
              });

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
    </body>
</html>
