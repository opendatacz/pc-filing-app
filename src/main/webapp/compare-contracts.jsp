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
                    <a class="btn pull-right" onclick="/*history.back();*/" href="buyer-similar-events.jsp" data-original-title="Click to get back to the previous screen.">Back</a><br>		

                    <h3 style="margin-bottom: 20px;"><fmt:message key="comparecontracts.title" /></h3>

                    <!--
                    <div class="btn-group btn-group">
               <a href="mycontracts-step-05.html" class="btn btn-small" title="Click to compare your contract with this one.">compare</a>
            </div>
                    -->

                    <div style="text-align: center;" id="progressbar">
                        <br> <img src="images/progressbar.gif" />
                    </div>

                    <table class="table table-striped table-bordered" id="contractTable" style="display: none;">
                        <thead>
                            <tr>
                                <th><fmt:message key="property" bundle="${cons}" /></th>
                                <th id="title1"></th>
                                <th id="title2"></th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td><fmt:message key="estimatedprice" bundle="${cons}" /></td>
                                <td id="price1"></td>
                                <td id="price2"></td>
                            </tr>
                            <!-- <tr>
                              <td>Best offered price</td>
                              <td></td>
                              <td></td>
                            </tr>
                            <tr>
                              <td>Actual price</td>
                              <td></td>
                              <td></td>
                            </tr> -->
                            <tr>
                                <td><fmt:message key="maincpvcode" bundle="${cons}" /></td>
                                <td id="cpv1"></td>
                                <td id="cpv2"></td>
                            </tr>
                            <tr>
                                <td><fmt:message key="additionalcpvcode" bundle="${cons}" /></td>
                                <td id="cpva1"></td>
                                <td id="cpva2"></td>
                            </tr>
                            <tr>
                                <td><fmt:message key="description" bundle="${cons}" /></td>
                                <td id="description1"></td>
                                <td id="description2"></td>
                            </tr>
                            <tr>
                                <td><fmt:message key="location" bundle="${cons}" /></td>
                                <td id="place1"></td>
                                <td id="place2"></td>
                            </tr>
                            <tr>
                                <td><fmt:message key="contractingauthority" bundle="${cons}" /></td>
                                <td id="cauth1"></td>
                                <td id="cauth2"></td>
                            </tr>
                            <tr>
                                <td><fmt:message key="proceduretype" bundle="${cons}" /></td>
                                <td id="ptype1"></td>
                                <td id="ptype2"></td>
                            </tr>
                        </tbody>
                    </table>

                    <div id="triples2" style="color:grey;">

                    </div>

                    <br>

                    <div id="comparerMessages">
                        <a id="seeMm" onclick="$('#comparerMessagesTable').fadeIn('slow')" href="#"><fmt:message key="comparecontracts.seematchmaker" /></a><br>
                        <table id="comparerMessagesTable" class="table table-striped table-bordered" style="display:none;">
                            <thead>
                                <tr>
                                    <th><fmt:message key="comparecontracts.module.name" /></th>
                                    <th><fmt:message key="comparecontracts.module.weight" /></th>
                                    <th><fmt:message key="comparecontracts.module.score" /></th>
                                    <th class="matchmakerAttr2"><fmt:message key="details" bundle="${cons}" /></th>
                                </tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                    </div>

                </div>
            </div>
        </div>

        <%@include file="WEB-INF/jspf/footer.jspf" %>

        <script type="text/javascript">

            var contractURL = sessionStorage.contractURL;
            var title = sessionStorage.contractTitle;
            var description = sessionStorage.contractDescription;
            //var cpvString = sessionStorage.contractCpvString;
            var price = sessionStorage.contractPrice;
            var currency = sessionStorage.contractCurrency;
            var place = sessionStorage.contractPlace;

            var contractURL2 = sessionStorage.contractURL2;
            var title2 = sessionStorage.contractTitle2;
            var description2 = sessionStorage.contractDescription2;
            //var cpvString2 = sessionStorage.contractCpvString2;
            var price2 = sessionStorage.contractPrice2;
            var currency2 = sessionStorage.contractCurrency2;
            var place2 = sessionStorage.contractPlace2;
            var triplesURL2 = sessionStorage.contractTriplesURL2;

            var comparerMessages = JSON.parse(sessionStorage.comparerMessages);

            $(window).ready(function() {

                //$('#contractTitle').append(title);

                $('#title1').append(title);
                $('#title2').append(title2);


                if (price == "undefined" && currency == "undefined")
                {
                    $('#price1').append("undefined");
                }
                else
                    $('#price1').append(price + " " + currency);
                if (price2 == "undefined" && currency2 == "undefined")
                {
                    $('#price2').append("undefined");
                }
                else
                    $('#price2').append(price2 + " " + currency2);


                $('#place1').append(place);
                $('#place2').append(place2);

                //$('#cpv1').append(cpvString);
                //$('#cpv2').append(cpvString2);

                $('#description1').append(description);
                $('#description2').append(description2);

                $('#triples2').append('<a href="' + htmlEncode(triplesURL2) + '" target="_blank">RDF (N3)</a>');
                //$('#comparerMessages').append('<pre id="messagePre" style="display:none;">' + comparerMessages + '</pre>');

                $.each(comparerMessages, function(key, value) {
                    newRow = $('<tr>');
                    newRow.append($('<td>').append(key.replace("Comparer", "")));
                    newRow.append($('<td>').addClass('matchmakerAttr0').append(value[0]));
                    newRow.append($('<td>').addClass('matchmakerAttr1').append($('<meter>').val(value[1] / 100)).append("&nbsp;&nbsp;&nbsp;" + value[1] + "%"));
                    newRow.append($('<td>').addClass('matchmakerAttr2').append(value[2]));
                    /*for (var col = 0; col < value.length; col++ ) {
                     newRow.append($('<td>').addClass('matchmakerAttr' + col).append(value[col]));	
                     }*/
                    newRow.appendTo("#comparerMessagesTable");
                });

                populateContractDetails();
            });

            $(window).ready(function() {
                checkUser();
            });

            function populateContractDetails()
            {
                // for both contracts
                $.getJSON("ContractsComponent?action=getPrivateContract&contractURI=" + encodeURIComponent(contractURL), function(data)
                {
                    $.each(data, function(i, data) {

                        $('#cpv1').append(htmlEncode(data.mainCPV));
                        $('#cpva1').append(htmlEncode(data.additionalCPV));
                        try {
                            $('#cauth1').append(htmlEncode(data.contractingAuthority.name));
                        } catch (e) {
                        }
                        $('#ptype1').append(htmlEncode(data.procedureType));

                    });
                    $('td').tooltip();
                });

                $.getJSON("ContractsComponent?action=getPublicContract&contractURI=" + encodeURIComponent(contractURL2), function(data)
                {
                    $.each(data, function(i, data) {

                        $('#cpv2').append(htmlEncode(data.mainCPV));
                        $('#cpva2').append(htmlEncode(data.additionalCPV));
                        try {
                            $('#cauth2').append(htmlEncode(data.contractingAuthority.name));
                        } catch (e) {
                        }
                        $('#ptype2').append(htmlEncode(data.procedureType));

                    });
                    $('td').tooltip();

                    $('#progressbar').hide();
                    $('#contractTable').fadeIn('slow');
                    if ($_GET('mm')) {
                        $('#seeMm').hide();
                        $('#comparerMessagesTable').show();
                    }

                });

                // todo 2nd
            }

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
