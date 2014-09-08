<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <%@include file="WEB-INF/jspf/header.jspf" %>
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.Buyer" />
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.Constants" var="cons" />
    </head>
    <body>
        <%@include file="WEB-INF/jspf/header-public.jspf" %>
        <div class="container">
            <div class="row">        
                <div class="span12">          

                    <h3 style="margin-bottom: 20px;"><fmt:message key="buyer" bundle="${cons}" /></h3>

                    <div id="message" class="alert fade in hide">
                        <button type="button" class="close" data-dismiss="alert">&times;</button>
                    </div>

                    <table class="table table-striped table-bordered" id="entityTable">            
                    </table>

                </div>
            </div>
        </div>

        <%@include file="WEB-INF/jspf/footer.jspf" %>
        <script src="js/sessionstorage.1.4.js"></script>
        <script src="js/script.js"></script>

        <script type="text/javascript">

            function initPage()
            {
                $.getJSON("PCFilingApp?action=getBusinessEntityBuyer&entity=" + sessionStorage.entity, function(data)
                {
                    if (data.success == true) {
                        $("#entityTable").append("<tr><th><fmt:message key="name" bundle="${cons}" /></th><td>" + data.name + "</td></tr>");
                    }
                    else
                    {
                        $('#message').addClass('alert-error');
                        $('#message').append("<fmt:message key="entity.notfound" />").fadeIn('slow');
                    }
                }
                );
            }

            $(window).ready(function() {
                checkUser();
                initPage();
            });

            function checkUser() {
                if (sessionStorage.username != undefined) {
                    $("#username").append(sessionStorage.username);
                }
                $.getJSON("SystemManager?action=getuser", function(data)
                {
                    if (data == null || data.length == 0) {
                        sessionStorage.clear();
                        window.location.href = "./";
                    } else {
                        $("#username").html(data);
                        sessionStorage.username = data;
                    }
                });
            }
        </script>

        <script>
            $("a").tooltip();
        </script>
    </body>
</html>
