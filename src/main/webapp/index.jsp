<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <%@include file="WEB-INF/jspf/header.jspf" %>
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.index" />
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.constants" var="cons" />
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
                <p><a class="btn btn-primary btn-large"><fmt:message key="body.title.learmore" /> &raquo;</a></p>
            </div>

            <!-- Message from query string -->
            <div id="message" class="alert fade in hide">
                <button type="button" class="close" data-dismiss="alert">&times;</button>
            </div>

            <!-- Example row of columns -->
            <div class="row">
                <div class="span4">
                    <h2><fmt:message key="body.buyers" /></h2>
                    <p>Donec id elit non mi porta gravida at eget metus. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui. </p>
                    <p><a href="#login-buyer" role="button" class="btn btn-primary" data-toggle="modal"><fmt:message key="body.buyers.login" /></a>
                        <a class="btn" href="register-buyer.jsp"><fmt:message key="body.buyers.register" /></a>
                    </p>
                </div>
                <div class="span4">
                    <h2><fmt:message key="body.sellers" /></h2>
                    <p>Donec id elit non mi porta gravida at eget metus. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui. </p>
                    <p><a href="#login-supplier" role="button" class="btn btn-primary" data-toggle="modal"><fmt:message key="body.sellers.login" /></a>
                        <a class="btn" href="register-supplier.jsp"><fmt:message key="body.sellers.register" /></a>
                </div>
                <div class="span4">
                    <h2><fmt:message key="body.public" /></h2>
                    <p>Donec sed odio dui. Cras justo odio, dapibus ac facilisis in, egestas eget quam. Vestibulum id ligula porta felis euismod semper. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus.</p>
                    <p><a class="btn" href="#"><fmt:message key="body.public.details" /> &raquo;</a></p>
                </div>
            </div>

            <hr>


            <!-- Modal -->
            <div id="login-buyer" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="buyer-label" aria-hidden="true">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true"><i class="icon-remove"></i></button>
                    <h3 id="buyer-label"><fmt:message key="body.buyers.login" /></h3>
                </div>
                <div class="modal-body">

                    <form class="form-horizontal" id="login-buyer-form" action="SystemManager" method="post">
                        <input name="action" type="hidden" value="login">
                        <input name="forward" id="forward" type="hidden" value="buyer-dashboard.jsp">
                        <input name="forward-if-fail" type="hidden" value="./?t=error&m=<fmt:message key="body.form.email.error" />">
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
                    </form>

                </div>

                <div class="modal-footer">
                    <button class="btn" data-dismiss="modal" aria-hidden="true"><fmt:message key="cancel" bundle="${cons}" /></button>
                    <input form="login-buyer-form" class="btn btn-primary" type="submit" name="submit" value="<fmt:message key="body.buyers.login" />" default>
                    <!-- HTML < 5 : <button onclick="$('#login-buyer-form').submit();" class="btn btn-primary">Login</button>-->
                </div>
            </div>


            <!-- Modal -->
            <div id="login-supplier" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="supplier-label" aria-hidden="true">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true"><i class="icon-remove"></i></button>
                    <h3 id="supplier-label"><fmt:message key="body.sellers.login" /></h3>
                </div>
                <div class="modal-body">

                    <form class="form-horizontal" id="login-supplier-form" action="SystemManager" method="post">
                        <input name="action" type="hidden" value="login">
                        <input name="forward" id="forward" type="hidden" value="supplier-calls.jsp">
                        <input name="forward-if-fail" type="hidden" value="./?t=error&m=<fmt:message key="body.form.email.error" />">
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
                    </form>

                </div>

                <div class="modal-footer">
                    <button class="btn" data-dismiss="modal" aria-hidden="true"><fmt:message key="cancel" bundle="${cons}" /></button>
                    <input form="login-supplier-form" class="btn btn-primary" type="submit" name="submit" value="<fmt:message key="body.sellers.login" />" default>
                    <!-- HTML < 5 : <button onclick="$('#login-supplier-form').submit();" class="btn btn-primary">Login</button>-->
                </div>
            </div>	  

            <!--<footer>
              <div class="container">
                <p class="pull-right"><a href="#">Back to top</a></p>
                <p>Designed and built with <a href="http://twitter.github.com/bootstrap/index.html" target="_blank">Twitter Bootstrap</a> by <a href="http://www.opendata.cz" target="_blank">OpenData.cz</a> and <a href="http://www.tenderstats.com" target="_blank">PC Filing App</a>.</p>
                <p>Code licensed under the <a href="http://www.apache.org/licenses/LICENSE-2.0" target="_blank">Apache License v2.0</a>. Documentation licensed under <a href="http://creativecommons.org/licenses/by/3.0/">CC BY 3.0</a>.</p>
                <ul class="footer-links">
                  <li><a href="http://blog.tenderstats.com">Read the blog</a></li>
                  <li><a href="about.html">About PC Filing App</a></li>
                </ul>
              </div>
            </footer>-->

        </div> <!-- /container -->

        <%@include file="WEB-INF/jspf/footer.jspf" %>

        <script>

            // ?m=lorem ipsum&t=info
            $(window).ready(function() {
                var mtype = $_GET("t");
                if (mtype !== null) {
                    $('#message').addClass('alert-' + mtype);
                }
                var mtext = $_GET("m");
                if (mtext !== null) {
                    $('#message').append(mtext).fadeIn('slow');
                }
            });

        </script>
    </body>
</html>
