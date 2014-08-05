<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <%@include file="WEB-INF/jspf/header.jspf" %>
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.Index" />
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.Constants" var="cons" />
    </head>
    <body>
        <%@include file="WEB-INF/jspf/header-public.jspf" %>
        <div class="container">

            <!-- Main hero unit for a primary marketing message or call to action -->
            <div class="hero-unit">
                <h1><fmt:message key="body.title.welcome" /> <sup><i>BETA</i></sup> !</h1>
                <p><fmt:message key="body.title.description" /></p>
                <p><a class="btn btn-primary btn-large"><fmt:message key="body.title.learnmore" /> &raquo;</a></p>
            </div>

            <!-- Message from query string -->
            <div id="message" class="alert fade in hide">
                <button type="button" class="close" data-dismiss="alert">&times;</button>
            </div>

            <div class="row">
                <div class="span12">
                    <h2><fmt:message key="obtain.title" /></h2>
                    <p><fmt:message key="obtain.received" /> <span id="invitation">#</span></p>
                </div>
            </div>

            <div class="row">
                <div class="span12">
                    <p class="logged-in hide"><fmt:message key="obtain.loggedas" /> <span id="username"></span></p>   
                    <p><input type="submit" form="obtain-invitation" class="btn btn-primary logged-in hide" name="submit" value="<fmt:message key="obtain.submit" />">
                        <a id="not-logged-in" href="#login-supplier" role="button" class="btn btn-primary" data-toggle="modal"><fmt:message key="body.bidders.login" /></a> 
                        <a class="btn add-inv-href" href="register-supplier.jsp?inv_id={inv_id}&email={con}"><fmt:message key="body.bidders.register" /></a></p>                 
                </div>
            </div>

            <div class="row">
                <div class="span12">
                    <p>
                    </p>
                </div>
            </div>

            <hr>

            <form class="hide" id="obtain-invitation" action="InvitationComponent" method="post">
                <input name="action" type="hidden" value="obtain">
                <input name="inv_id" class="add-inv-val" type="hidden" value="{inv_id}">
                <input name="email" class="add-inv-val" type="hidden" value="{con}">		
                <input name="forward" id="forward" type="hidden" value="supplier-invitations.jsp">
                <input name="forward-if-fail" type="hidden" value="./?t=error&m=<strong>Oops. Something went wrong.</strong>">		
            </form>

            <!-- Modal -->
            <div id="login-supplier" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="supplier-label" aria-hidden="true">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true"><i class="icon-remove"></i></button>
                    <h3 id="supplier-label"><fmt:message key="body.bidders.login" /></h3>
                </div>
                <div class="modal-body">

                    <form class="form-horizontal" id="login-supplier-form" action="SystemManager" method="post">
                        <input name="action" type="hidden" value="login">
                        <input type="hidden" name="role" value="2">
                        <input name="forward" id="forward" class="add-inv-val" type="hidden" value="InvitationComponent?action=obtain&forward=supplier-invitations.jsp&inv_id={inv_id}&email={con}">
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
                    <input form="login-supplier-form" class="btn btn-primary" type="submit" name="submit" value="<fmt:message key="body.bidders.login" />" default>
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

            function $_GET(variable) {
                var query = window.location.search.substring(1);
                var vars = query.split("&");
                for (var i = 0; i < vars.length; i++) {
                    var pair = vars[i].split("=");
                    if (pair[0] == variable) {
                        return decodeURIComponent(pair[1]);
                    }
                }
            }

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

                invitation = $_GET("inv_id");
                email = $_GET("email");
                if (invitation != null) {
                    $('#invitation').append(invitation);
                }

                checkUser();
                addHrefVal();

            });

// 	$(window).ready(function() {		
// 		initPages();
// 		loadContracts();
// 	});
            var invitation;
            var email;
            function addHrefVal() {
                $(".add-inv-href").each(function() {
                    var href = $(this).attr('href');
                    $(this).attr('href', href.replace('{inv_id}', invitation));
                    var href = $(this).attr('href');
                    $(this).attr('href', href.replace('{con}', email));
                });

                $(".add-inv-val").each(function() {
                    var val = $(this).attr('value');
                    $(this).attr('value', val.replace('{inv_id}', invitation));
                    var val = $(this).attr('value');
                    $(this).attr('value', val.replace('{con}', email));
                });

            }

            function checkUser() {
                $.getJSON("SystemManager?action=getuser", function(data)
                {
                    if (data == null || data.length == 0) {
                        sessionStorage.clear();
                    } else {
                        sessionStorage.username = data;
                    }

                    if (sessionStorage.username != undefined) {
                        $(".logged-in").show();
                        $("#username").append(sessionStorage.username);
                        $("#not-logged-in").removeClass("btn-primary");
                    }
                });

            }

        </script>
    </body>
</html>
