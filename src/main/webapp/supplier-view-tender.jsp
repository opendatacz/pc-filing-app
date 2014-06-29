<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <%@include file="WEB-INF/jspf/header.jspf" %>
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.supplier" />
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.constants" var="cons" />
        <link href="./bootstrap/css/won.css" rel="stylesheet" />
    </head>
    <body>
        <%@include file="WEB-INF/jspf/header-private.jspf" %>
        <div class="container-fluid">
            <div class="row-fluid">
                <%@include file="WEB-INF/jspf/menu-supplier.jspf" %>

                <div class="span10">	        


                    <div style="text-align: center;" id="progressbar">
                        <br> <img src="images/progressbar.gif" />
                    </div>

                    <div id="view" style="display:none">

                        <h3><fmt:message key="viewtender.title" /> '<span style="display: inline;" id="tenderTitle"></span>'</h3>

                        <hr>

                        <div class="control-group">
                            <h4><fmt:message key="createtender.basicinfo" /></h4>
                            <div class="control-group">
                                <label class="control-label" for="buyer"><fmt:message key="buyer" bundle="${cons}" /></label>
                                <div class="controls">
                                    <span id="buyer"></span>
                                </div>
                            </div>
                        </div>
                        <div class="control-group">

                            <div class="control-group">
                                <div class="control-group">
                                    <label class="control-label" for="inputDescription"><fmt:message key="description" bundle="${cons}" /></label>
                                    <div class="controls">
                                        <span id="inputDescription"></span>
                                    </div>
                                </div>
                            </div>

                            <hr>

                            <div class="control-group">
                                <h4><fmt:message key="createtender.pricingtiming" /></h4>

                                <label class="control-label" for="inputPrice"><fmt:message key="offeredprice" bundle="${cons}" /></label>
                                <div class="controls">
                                    <span id="inputPrice"></span> <span id="inputCurrency"></span>
                                </div>
                            </div>

                            <div class="control-group">
                                <label class="control-label" for="inputStartDate"><fmt:message key="createtender.startend" /></label>
                                <div class="controls">
                                    <span id="inputStartDate"></span> - <span id="inputEndDate"></span>
                                </div>
                            </div>

                            <hr>

                            <h4 id="docsHeader"><fmt:message key="documents" bundle="${cons}" /></h4>

                            <div class="control-group" style="display:none">								
                                <label class="control-label" for="inputFileOffer"><fmt:message key="createtender.doc.tender" /></label>
                                <div class="controls">
                                    <ul id="fileOffer" class="docList"></ul>

                                </div>
                            </div>

                            <div class="control-group" style="display:none">
                                <label class="control-label" for="inputFileTechSpecs"><fmt:message key="createtender.doc.specs" /></label>
                                <div class="controls">
                                    <ul id="fileTechSpecs" class="docList"></ul>

                                </div>
                            </div>

                            <div class="control-group" style="display:none">
                                <label class="control-label" for="inputFilePriceDelivery"><fmt:message key="createtender.doc.price" /></label>
                                <div class="controls">
                                    <ul id="filePriceDelivery" class="docList"></ul>

                                </div>
                            </div>

                            <div class="control-group" style="display:none">
                                <label class="control-label" for="inputFileRequested"><fmt:message key="createtender.doc.forms" /></label>
                                <div class="controls">
                                    <ul id="fileRequested" class="docList"></ul>

                                </div>
                            </div>

                            <hr>

                            <h4 id="attaHeader"><fmt:message key="attachments" bundle="${cons}" /></h4>

                            <div class="control-group" style="display:none">

                                <label class="control-label" for="inputFileCerts"><fmt:message key="createtender.attachments.certificates" /></label>
                                <div class="controls">
                                    <ul id="fileCerts" class="docList"></ul>
                                </div>
                            </div>

                            <div class="control-group" style="display:none">
                                <label class="control-label" for="inputFileProfile"><fmt:message key="createtender.attachments.profile" /></label>
                                <div class="controls">
                                    <ul id="fileProfile" class="docList"></ul>
                                </div>
                            </div>

                            <div class="control-group" style="display:none">
                                <label class="control-label" for="inputFinStatements"><fmt:message key="createtender.attachments.financial" /></label>
                                <div class="controls">
                                    <ul id="fileFinStatements" class="docList"></ul>

                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </div>

        </div>

        <%@include file="WEB-INF/jspf/footer.jspf" %> 
        <script src="js/functions.js"></script>
        <script src="js/sessionstorage.1.4.js"></script>
        <script src="js/toolsBuyer.js"></script>
        <script src="js/script.js"></script>
        <script src="js/table.js"></script>

        <script type="text/javascript">

            var tenderURL = sessionStorage.tenderURL;
            var title = sessionStorage.contractTitle;
            var buyerURL = sessionStorage.buyerURL;

            $(window).ready(function() {
                checkUser();
                $('#tenderTitle').append(title);
                $('#buyerURL').attr('value', buyerURL);
                $('#tenderURL').attr('value', tenderURL);
                console.log(sessionStorage);
                loadForm();
            });


            function loadForm() {

                $.getJSON("PCFilingApp?action=getTenderJson&editTenderURL=" + encodeURIComponent(sessionStorage.tenderURL), function(data)
                {
                    if (data == null || data.length == 0) {
                        sessionStorage.clear();
                        window.location.href = "./";
                    } else {

                        $("#buyer").html('<a onclick="showEntity(\'' + data.buyer.entity + '\')" href="entity-buyer.jsp">' + data.buyer.name + '</a>');
                        if (data.description) {
                            $("#inputDescription").html(data.description);
                        } else {
                            $("#inputDescription").closest('.control-group').hide();
                        }
                        if (data.currency || data.price) {
                            $("#inputCurrency").html(data.currency);
                            $("#inputPrice").html(data.price);
                        } else {
                            $("#inputCurrency").closest('.control-group').hide();
                        }

                        if (data.startDate || data.endDate) {
                            $("#inputStartDate").html(data.startDate);
                            $("#inputEndDate").html(data.endDate);
                        } else {
                            $("#inputStartDate").closest('.control-group').hide();
                        }

                        var docsEnabled = false;
                        var attaEnabled = false;
                        $.each(data.documents, function() {
                            var item;
                            switch (this.docType) {
                                case "Offer":
                                    docsEnabled = true;
                                    item = $("#fileOffer");
                                    break;

                                case "TechSpecs":
                                    docsEnabled = true;
                                    item = $("#fileTechSpecs");
                                    break;

                                case "PriceDelivery":
                                    docsEnabled = true;
                                    item = $("#filePriceDelivery");
                                    break;

                                case "Requested":
                                    docsEnabled = true;
                                    item = $("#fileRequested");
                                    break;

                                case "QualityCertificate":
                                    attaEnabled = true;
                                    item = $("#fileCerts");
                                    break;

                                case "CompanyProfile":
                                    attaEnabled = true;
                                    item = $("#fileProfile");
                                    break;

                                case "FinancialStatements":
                                    attaEnabled = true;
                                    item = $("#fileFinStatements");
                                    break;
                            }
                            item.append('<li id="doc-' + this.token + '"><a href="PCFilingApp?action=document&token=' + this.token + '"><i class="icon-download"></i> ' + this.fileName + '</a></li>');
                            item.closest('.control-group').show();
                        });

// 						if ( !docsEnabled ) $('#docsHeader').css('display','none');
// 						if ( !attaEnabled ) $('#attaHeader').css('display','none');

                    }

                    $("#progressbar").hide();
                    $("#view").fadeIn();

                });
            }

        </script>
    </body>
</html>
