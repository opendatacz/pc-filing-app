<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <%@include file="WEB-INF/jspf/header.jspf" %>
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.Index" />
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.Constants" var="cons" />
        <script src="js/functions.js"></script>    
        <script src="js/sessionstorage.1.4.js"></script>
    </head>
    <body>
        <div class="navbar navbar-inverse navbar-fixed-top">
            <div class="navbar-inner">
                <div class="container">
                    <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </a>
                    <a class="brand" href="./">PC Filing App <sup><i>BETA</i></sup></a>
                </div><!--/.nav-collapse -->
            </div>
        </div>

        <div class="container">

            <!-- Main hero unit for a primary marketing message or call to action -->
            <div class="hero-unit">
                <h1><fmt:message key="body.title.welcome" /> <sup><i>BETA</i></sup> !</h1>
                <p><fmt:message key="body.title.description" /></p>
                <%--
                <p>
                  <a class="btn btn-primary btn-large">
                    <fmt:message key="body.title.learnmore" /> &raquo;
                  </a>
                </p>
                --%>
            </div>

            <!-- Message from query string -->
            <div id="message" class="alert fade in hide">
                <button type="button" class="close" data-dismiss="alert">&times;</button>
            </div>

            <!-- Example row of columns -->
            <div class="row">
                <div class="span4">
                    <h2><fmt:message key="body.contractingauthorities" /></h2>
                    <p><fmt:message key="body.contractingauthorities.description" /></p>
                    <p class="btn-group btn-group-vertical"><a href="#login-buyer" role="button" class="btn btn-primary" data-toggle="modal"><fmt:message key="body.contractingauthorities.login" /></a>
                        <a class="btn" href="register-buyer.jsp"><fmt:message key="body.contractingauthorities.register" /></a>
                    </p>
                </div>
                <div class="span4">
                    <h2><fmt:message key="body.bidders" /></h2>
                    <p><fmt:message key="body.bidders.description" /></p>
                    <p class="btn-group btn-group-vertical"><a href="#login-supplier" role="button" class="btn btn-primary" data-toggle="modal"><fmt:message key="body.bidders.login" /></a>
                        <a class="btn" href="register-supplier.jsp"><fmt:message key="body.bidders.register" /></a>
                </div>
                <div class="span4">
                    <h2><fmt:message key="body.acknowledgements.title" /></h2>
                    <p>
                      <a href="http://lod2.eu" target="_blank">
                        <img src="${pageContext.request.contextPath}/images/LOD2_logo.png"
                             alt="<fmt:message key="body.acknowledgements.logo" />"/>
                      </a>
                    </p>
                    <p><fmt:message key="body.acknowledgements.desc" /></p>
                    <%--
                    <h2><fmt:message key="body.public" /></h2>
                    <p><fmt:message key="body.public.description" /></p>
                    <p><a class="btn" href="#"><fmt:message key="body.public.details" /> &raquo;</a></p>
                    --%>
                </div>
            </div>

            <hr>

            <!-- Modal -->
            <div id="login-buyer" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="buyer-label" aria-hidden="true">
                <form class="form-horizontal" id="login-buyer-form" style="margin-bottom: 0px;" action="SystemManager" method="post">
                    <div class="modal-header">
                        <a type="button" class="close" data-dismiss="modal" aria-hidden="true"><i class="icon-remove"></i></a>
                        <h3 id="buyer-label"><fmt:message key="body.contractingauthorities.login" /></h3>
                    </div>
                    <div class="modal-body">
                        <input name="action" type="hidden" value="login">
                        <input name="role" type="hidden" value="1">
                        <input name="forward" id="forward" type="hidden" value="buyer-dashboard.jsp">
                        <input name="forward-if-fail" type="hidden" value="<fmt:message key="body.form.email.error" />">
                        <div class="control-group">
                            <label class="control-label"><fmt:message key="email" bundle="${cons}" /></label>
                            <div class="controls">
                                <input name="username" class="input-xlarge" type="text" placeholder="<fmt:message key="body.form.email" />" autofocus required>
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label"><fmt:message key="password" bundle="${cons}" /></label>
                            <div class="controls">
                                <input name="password" class="input-xlarge" type="password" placeholder="<fmt:message key="body.form.password" />" required>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <a class="btn" data-dismiss="modal" aria-hidden="true"><fmt:message key="cancel" bundle="${cons}" /></a>
                        <input form="login-buyer-form"  class="btn btn-primary" type="submit" name="submit" value="<fmt:message key="body.contractingauthorities.login" />" default>
                        <!-- HTML < 5 : <button onclick="$('#login-buyer-form').submit();" class="btn btn-primary">Login</button>-->
                    </div>
                </form>
            </div>

            <!-- Modal -->
            <div id="login-supplier" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="supplier-label" aria-hidden="true">
                <form class="form-horizontal" id="login-supplier-form" style="margin-bottom: 0px;" action="SystemManager" method="post">
                    <div class="modal-header">
                        <a type="button" class="close" data-dismiss="modal" aria-hidden="true"><i class="icon-remove"></i></a>
                        <h3 id="supplier-label"><fmt:message key="body.bidders.login" /></h3>
                    </div>
                    <div class="modal-body">
                        <input name="action" type="hidden" value="login">
                        <input name="role" type="hidden" value="2">
                        <input name="forward" id="forward" type="hidden" value="supplier-calls.jsp">
                        <input name="forward-if-fail" type="hidden" value="<fmt:message key="body.form.email.error" />">
                        <div class="control-group">
                            <label class="control-label"><fmt:message key="email" bundle="${cons}" /></label>
                            <div class="controls">
                                <input name="username" class="input-xlarge" type="text" placeholder="<fmt:message key="body.form.email" />" autofocus required>
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label"><fmt:message key="password" bundle="${cons}" /></label>
                            <div class="controls">
                                <input name="password" class="input-xlarge" type="password" placeholder="<fmt:message key="body.form.password" />" required>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <a class="btn" data-dismiss="modal" aria-hidden="true"><fmt:message key="cancel" bundle="${cons}" /></a>
                        <input form="login-supplier-form" class="btn btn-primary" type="submit" name="submit" value="<fmt:message key="body.bidders.login" />" default>
                        <!-- HTML < 5 : <button onclick="$('#login-supplier-form').submit();" class="btn btn-primary">Login</button>-->
                    </div>
                </form>
            </div>	  

        </div> <!-- /container -->

        <%@include file="WEB-INF/jspf/footer.jspf" %>
        <script src="js/application.js"></script>

        <script type="text/javascript">
            $(window).ready(function() {
                APP.dom.normalizeInputValidity("<fmt:message key="pleasefill" bundle="${cons}" />");
                var mtype = '${mt}';
                if (typeof mtype !== "undefined" && mtype !== null && mtype.length > 0) {
                    $('#message').addClass('alert-' + mtype);
                }
                var mtext = '${message}';
                if (typeof mtext !== "undefined" && mtext !== null && mtext.length > 0) {
                    $('#message').append(mtext).fadeIn('slow');
                }
            });

        </script>
    </body>
</html>
