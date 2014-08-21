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
                        <fmt:message key="prepared.help" />
                    </div>

                    <h4><fmt:message key="prepared.title" /></h4>

                    <table class="table table-striped table-bordered" style="display: none;" id="contractTable">
                        <thead>
                            <tr>
                                <th><fmt:message key="title" bundle="${cons}" /></th>
                                <th><fmt:message key="estimatedprice" bundle="${cons}" /></th>
                                <th><fmt:message key="cpvcodes" bundle="${cons}" /></th>
                                <th><fmt:message key="lastupdate" bundle="${cons}" /></th>
                                <th><fmt:message key="creationdate" bundle="${cons}" /></th>
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

                    <div class="pull-right">
                      <a href="buyer-create-event.jsp" class="btn"
                        style="text-align: left; margin-left: 0px; margin-right: 10px; margin-top: 5px; padding-left: 0px;"
                        title="<fmt:message key="prepared.create.title" />">&nbsp;&nbsp;<i class="icon-plus"></i>&nbsp;&nbsp;<fmt:message key="prepared.create" /></a>
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
            <td>{{modified}}</td>
            <td>{{created}}</td>
            <td>
              <div class="btn-group btn-group-vertical">
                <a class="btn btn-small confirm"
                  href="PCFilingApp?forward=buyer-published.jsp&action=publishPrivateContract&contractURI={{encodedContractURI}}"
                  data-confirmation="<fmt:message key="prepared.publish.confirm" />">
                  <i class="icon-globe"></i>
                  <fmt:message key="publish" bundle="${cons}" />
                </a>
                <a class="btn btn-small contract-edit-link"
                  href="buyer-edit-event.jsp">
                  <i class="icon-edit"></i>
                  <fmt:message key="edit" bundle="${cons}" />
                </a>
                <a class="btn btn-small btn-danger confirm"
                  href="PCFilingApp?forward=buyer-prepared.jsp&action=deletePrivateContract&contractURI={{encodedContractURI}}"
                  data-confirmation="<fmt:message key="prepared.delete.confirm" />">
                  <i class="icon-trash"></i>
                  <fmt:message key="delete" bundle="${cons}" />
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
                   href="buyer-suitable-suppliers.jsp?private=true&invite=false">
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
        <script src="js/cpv-codes-<c:out value="${pageContext.request.locale.language}" />.js"></script>
        <script src="js/script.js"></script>
        <script src="js/toolsBuyer.js"></script>
        <script src="js/table.js"></script>
        <script src="js/services.js"></script>
        
        <script type="text/javascript">
          (function ($) {
            $(document).ready(function () {
              var tableName = "PrivateContracts";
              sessionStorage.public = false;
              TABLE.init(tableName);
            });
          })(jQuery);
        </script>
    </body>
</html>
