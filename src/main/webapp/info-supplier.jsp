<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <%@include file="WEB-INF/jspf/header.jspf" %>
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.Constants" var="cons" />
    </head>
    <body>
        <%@include file="WEB-INF/jspf/header-public.jspf" %>
        <div class="container">
            <div class="row">
                <div class="span12">
                    <div id="progressbar"></div>
                    <h3 style="margin-bottom: 20px;"><fmt:message key="supplier" bundle="${cons}" /></h3>
                    <div id="message" class="alert fade in hide">
                        <button type="button" class="close" data-dismiss="alert">&times;</button>
                    </div>
                    <div id="view hide">
                        <table class="table table-striped table-bordered" id="entityTable">
                        </table>


                    </div>
                </div>
            </div>
        </div>

        <%@include file="WEB-INF/jspf/footer.jspf" %>
        <script src="js/sessionstorage.1.4.js"></script>
        <script src="js/script.js"></script>
        <script src="js/cpvs.js"></script>
        <script src="js/table.js"></script>

        <script type="text/javascript">
            function initPage() {
                $.getJSON("PCFilingApp?action=getBusinessEntitySupplier&entity="
                        + sessionStorage.entity,
                        function(data) {
                            if (data.success == true) {
                                $("#entityTable").append(
                                        "<tr><th><fmt:message key="name" bundle="${cons}" /></th><td>" + data.name
                                        + "</td></tr>");
                            } else {
                                $('#message').addClass('alert-error');
                                $('#message').append("Entity not found").fadeIn(
                                        'slow');
                            }

                            $("#progressbar").hide();
                            $("#view").fadeIn();
                        });
            }



            $(window).ready(function() {
                checkUser();
                initPage();
            });
        </script>

        <script>
            $("a").tooltip();
        </script>
    </body>
</html>
