<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <%@include file="WEB-INF/jspf/header.jspf" %>
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
                    <h3><fmt:message key="finalize.title" /> "<span id="contractTitle"></span>"</h3>
                    <hr>
                    <form action="PCFilingApp" method="post" class="form-horizontal" id="contractForm">
                        <input name="contractURL" id="contractURL" type="hidden" value="">	    
                        <input name="action" type="hidden" value="finalizeContract">
                        <input name="forward" type="hidden" value="buyer-completed.jsp">

                        <div class="control-group">
                            <label class="control-label"><fmt:message key="publishtenders" bundle="${cons}" /> <font color="red">*</font></label>
                            <div class="controls">
                                <label class="radio">
                                    <input type="radio" name="publishTenders" id="awarded" value="awarded" checked>
                                    <fmt:message key="awardedtender" bundle="${cons}" />
                                </label>
                                <label class="radio">
                                    <input type="radio" name="publishTenders" id="all" value="all">
                                    <fmt:message key="alltenders" bundle="${cons}" />
                                </label>
                                <label class="radio">
                                    <input type="radio" name="publishTenders" id="none" value="none">
                                    <fmt:message key="none" bundle="${cons}" />
                                </label>
                            </div>
                        </div>	

                        <div class="control-group">
                            <label class="control-label"><fmt:message key="actualprice" bundle="${cons}" /> <font color="red">*</font></label>

                            <div class="controls">
                                <input required step="0.01" id="actualPrice" name="actualPrice" type="text">
                                <select name="actualPriceCurrency">
                                    <option>USD</option>
                                    <option>EUR</option>
                                    <option>CZK</option>
                                </select>
                            </div>
                        </div>	

                        <div class="control-group">
                            <label class="control-label"><fmt:message key="actualcompletitiondate" bundle="${cons}" /> <font color="red">*</font></label>
                            <div class="controls">				
                                <input required id="actualEndDate" name="actualEndDate" type="text">
                            </div>
                        </div>	

                        <div class="control-group">
                            <label class="control-label"><fmt:message key="finalize.satisfied" /> <font color="red">*</font></label>
                            <div class="controls">
                                <label class="radio">
                                    <input type="radio" name="satisfiedField" id="satisfied" value="true" checked>
                                    <fmt:message key="satisfied" bundle="${cons}" />
                                </label>
                                <label class="radio">
                                    <input type="radio" name="satisfiedField" id="unsatisfied" value="false">
                                    <fmt:message key="unsatisfied" bundle="${cons}" />
                                </label>
                            </div>
                        </div>				

                        <div class="control-group">
                            <div class="form-actions">
                                <input type="submit" class="btn btn-primary" title="Finalize" value="Finalize">
                                <a href="awarded-calls-for-tenders.jsp" class="btn"><fmt:message key="cancel" bundle="${cons}" /></a>
                            </div>
                        </div>
                    </form>
                </div>
                <%@include file="WEB-INF/jspf/stats-buyer.jspf" %>
            </div>
        </div>

        <%@include file="WEB-INF/jspf/footer.jspf" %>

        <script src="js/functions.js"></script>
        <script src="js/sessionstorage.1.4.js"></script>
        <script src="js/script.js"></script>
        <script src="js/toolsBuyer.js"></script>
        <script src="js/table.js"></script>

        <script type="text/javascript">

            var contractURL = sessionStorage.contractURL;
            var title = sessionStorage.contractTitle;

            $("#actualEndDate").datepicker({dateFormat: 'yy-mm-dd'});

            $('#contractTitle').append(title);
            $('#contractURL').attr('value', contractURL);

        </script>
    </body>
</html>
