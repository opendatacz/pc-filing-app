<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <%@include file="WEB-INF/jspf/header.jspf" %>
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.register" />
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.constants" var="cons" />
        <script src="js/functions.js"></script>    
        <script src="js/sessionstorage.1.4.js"></script>
    </head>
    <body>
        <%@include file="WEB-INF/jspf/header-public.jspf" %>

        <div class="container">

            <div class="alert hide" id="userHelper">
                <button class="close" onclick="hideHint()" title="Disable guide" >×</button>		
                <fmt:message key="buyer.body.welcome" />
            </div>

            <!-- Message from query string -->
            <div id="message" class="alert fade in hide">
                <button type="button" class="close" data-dismiss="alert">&times;</button>
            </div>

            <h1><fmt:message key="buyer.body.title" /></h1>
            <hr>

            <form action="SystemManager" class="form-horizontal" method="post">
                <input name="action" type="hidden" value="register">
                <input name="forward" type="hidden" value="./?t=success&m=<fmt:message key="buyer.body.success" />">
                <input name="forward-if-fail" type="hidden" value="register-buyer.jsp?t=error&m=<fmt:message key="buyer.body.error" />">
                <input name="role" type="hidden" value="1">
                <input name="active" type="hidden" value="1">

                <h4><fmt:message key="buyer.body.setting.account" />:</h4>
                <br>
                <div class="control-group">
                    <label class="control-label" for="inputFn"><fmt:message key="email" bundle="${cons}" /> <font color="red">*</font></label>
                    <div class="controls">
                        <input required name="username" type="text" id="inputFn">
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label"><fmt:message key="password" bundle="${cons}" /> <font color="red">*</font></label>
                    <div class="controls">
                        <input required name="password" type="password">
                    </div>
                </div>
                <hr>
                <h4><fmt:message key="buyer.body.setting.entity" />:</h4>
                <br>
                <div class="control-group">
                    <label class="control-label"><fmt:message key="name" bundle="${cons}" /> <font color="red">*</font></label>
                    <div class="controls">
                        <input required name="businessName" type="text">
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">IC (<fmt:message key="optional" bundle="${cons}" />)</label>
                    <div class="controls">
                        <input name="businessIC" type="text">
                    </div>
                </div>
                <hr>
                <!--
                <h4>Linked data settings  for your private contracts (optional):</h4>
                <br>
                     <div class="control-group">
                       <label class="control-label">Private graph URI</label>
                       <div class="controls">
                             <input name="privateGraphURI" type="text" disabled>
                       </div>
                     </div>
                     <div class="control-group">
                       <label class="control-label">SPARQL URI (query)</label>
                       <div class="controls">
                             <input name="sparqlQueryURI" type="text" disabled>
                       </div>
                     </div>
                     <div class="control-group">
                       <label class="control-label">SPARQL URI (update)</label>
                       <div class="controls">
                             <input name="sparqlUpdateURI" type="text" disabled>
                       </div>
                     </div>		
                -->
                <div class="control-group">
                    <div class="form-actions">
                        <input type="submit" id="submit" value="<fmt:message key="buyer.body.submit" />" class="btn btn-large btn-primary">
                    </div>
                </div>	  
            </form>


        </div> <!-- /container -->

        <%@include file="WEB-INF/jspf/footer.jspf" %>

        <script>
            $("a").tooltip();

            // ?m=lorem ipsum&t=info
            $(window).ready(function() {
                var mtype = $_GET("t");
                if (mtype != null) {
                    $('#message').addClass('alert-' + mtype);
                }
                var mtext = $_GET("m");
                if (mtext != null) {
                    $('#message').append(mtext).fadeIn('slow');
                }
            });

            function hideHint() {
                $("#userHelper").slideUp();
            }

            function showHint() {
                $("#userHelper").slideDown();
            }

        </script>
    </body>
</html>
