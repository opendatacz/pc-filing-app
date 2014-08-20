<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <%@include file="WEB-INF/jspf/header.jspf" %>
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.Register" />
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.Constants" var="cons" />
        <script src="js/functions.js"></script>    
        <script src="js/sessionstorage.1.4.js"></script>
    </head>
    <body>
        <%@include file="WEB-INF/jspf/header-public.jspf" %>

        <div class="container">

            <div class="alert hide" id="userHelper">
                <button class="close"
                        onclick="hideHint()"
                        title="<fmt:message key="disableguide" bundle="${cons}" />" >×</button>		
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
                <input name="forward" type="hidden" value="./">
                <input name="forward-message" type="hidden" value="<fmt:message key="buyer.body.success" />">
                <input name="forward-if-fail" type="hidden" value="<fmt:message key="buyer.body.error" />">
                <input name="role" type="hidden" value="1">
                <input name="active" type="hidden" value="1">

                <h4><fmt:message key="buyer.body.setting.account" />:</h4>
                <br>
                <div class="control-group required">
                    <label class="control-label" for="inputFn"><fmt:message key="email" bundle="${cons}" /></label>
                    <div class="controls">
                      <input required
                             name="username"
                             placeholder="<fmt:message key="body.form.email" />"
                             type="text"
                             id="inputFn">
                    </div>
                </div>
                <div class="control-group required">
                    <label class="control-label"><fmt:message key="password" bundle="${cons}" /></label>
                    <div class="controls">
                      <input required
                             name="password"
                             placeholder="<fmt:message key="body.form.password" />"
                             type="password">
                    </div>
                </div>
                <hr>
                <h4><fmt:message key="buyer.body.setting.entity" />:</h4>
                <br>
                <div class="control-group required">
                    <label class="control-label"><fmt:message key="name" bundle="${cons}" /></label>
                    <div class="controls">
                      <input required
                             name="businessName"
                             placeholder="<fmt:message key="body.form.name" />"
                             type="text">
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label"><fmt:message key="identifier" bundle="${cons}" /></label>
                    <div class="controls">
                      <input name="businessIC"
                             placeholder="<fmt:message key="body.form.identifier" />"
                             type="text">
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
        <script src="js/application.js" type="text/javascript"></script>

        <script>
                            $("a").tooltip();

                            // ?m=lorem ipsum&t=info
                            $(window).ready(function() {
                                APP.dom.normalizeInputValidity("<fmt:message key="pleasefill" bundle="${cons}" />");

                                var mtype = '${mt}';
                                if (mtype != null && mtype.length > 0) {
                                    $('#message').addClass('alert-' + mtype);
                                }
                                var mtext = '${message}';
                                if (mtext != null && mtext.length > 0) {
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
