<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <%@include file="WEB-INF/jspf/header.jspf" %>
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.Public" />
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.Supplier" var="supp" />
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
                    <div class="nav-collapse collapse">
                        <ul class="nav pull-right">
                            <li><a href="#"><i class="icon-search icon-white"></i></a></li>
                            <li><a href="#" id="enableGuide" onclick="userHelper('on')" title="Enable guide"><i class="icon-question-sign icon-white"></i></a></li>
                        </ul>
                    </div><!--/.nav-collapse -->
                </div>
            </div>
        </div>

        <div class="container">
            <div class="row">        
                <div class="span12">

                    <h1><fmt:message key="viewtender.title" /> '<span style="display:inline;" id="tenderTitle"></span>'</h1>

                    <hr>

                    <div class="view" >		    
                        <div class="control-group">
                            <h4><fmt:message key="createtender.basicinfo" bundle="${supp}" /></h4>
                            <div class="control-group">
                                <label class="control-label" for="input"><fmt:message key="supplier" bundle="${cons}" /></label>
                                <div class="controls">
                                    <span id="supplier"></span>
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
                            <div class="control-group">
                                <h4><fmt:message key="createtender.pricingtiming" bundle="${supp}" /></h4>

                                <label class="control-label" for="inputPrice"><fmt:message key="offeredprice" bundle="${cons}" /></label>
                                <div class="controls">              
                                    <span id="inputPrice"></span> <span id="inputCurrency"></span>                
                                </div>            
                            </div>

                            <div class="control-group">
                                <label title="Contract duration" class="control-label" for="inputStartDate"><fmt:message key="createtender.startend" bundle="${supp}" /></label>
                                <div class="controls">
                                    <span id="inputStartDate"></span> - <span id="inputEndDate"></span>
                                </div>
                            </div>

                            <div class="control-group">
                                <h4><fmt:message key="documents" bundle="${cons}" /></h4>
                                <label class="control-label" for="inputFileOffer"><fmt:message key="createtender.doc.tender" bundle="${supp}" /></label>
                                <div class="controls">       
                                    <ul id="fileOffer" class="docList"></ul>                                       

                                </div>
                            </div>

                            <div class="control-group">			
                                <label class="control-label" for="inputFileTechSpecs"><fmt:message key="createtender.doc.specs" bundle="${supp}" /></label>
                                <div class="controls">     
                                    <ul id="fileTechSpecs" class="docList"></ul>                                         

                                </div>
                            </div>

                            <div class="control-group">			
                                <label class="control-label" for="inputFilePriceDelivery"><fmt:message key="createtender.doc.price" bundle="${supp}" /></label>
                                <div class="controls">           
                                    <ul id="filePriceDelivery" class="docList"></ul>                                   

                                </div>
                            </div>

                            <div class="control-group">			
                                <label class="control-label" for="inputFileRequested"><fmt:message key="createtender.doc.forms" bundle="${supp}" /></label>
                                <div class="controls">      
                                    <ul id="fileRequested" class="docList"></ul>                                        

                                </div>
                            </div>

                            <div class="control-group">
                                <h4><fmt:message key="attachments" bundle="${cons}" /></h4>
                                <label class="control-label" for="inputFileCerts"><fmt:message key="createtender.attachments.certificates" bundle="${supp}" /></label>
                                <div class="controls">               
                                    <ul id="fileCerts" class="docList"></ul>                                                              
                                </div>
                            </div>

                            <div class="control-group">			
                                <label class="control-label" for="inputFileProfile"><fmt:message key="createtender.attachments.profile" bundle="${supp}" /></label>
                                <div class="controls">                          
                                    <ul id="fileProfile" class="docList"></ul>                                                    
                                </div>
                            </div>

                            <div class="control-group">			
                                <label class="control-label" for="inputFinStatements"><fmt:message key="createtender.attachments.financial" bundle="${supp}" /></label>
                                <div class="controls">      
                                    <ul id="fileFinStatements" class="docList"></ul>                                        

                                </div>
                            </div>


                        </div>
                    </div>
                </div>
            </div>

            <%@include file="WEB-INF/jspf/footer.jspf" %>
            <script src="js/cpv-codes-<c:out value="${pageContext.request.locale.language}" />.js"></script>  
            <script src="js/jquery-ui.js"></script>
            <script src="js/main.js"></script>    

            <script>
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

                                            //$("#tenderURL").val(sessionStorage.tenderURL);
                                            $("#supplier").html('<a onclick="showEntity(\'' + data.supplier.entity + '\')" href="info-supplier.jsp">' + data.supplier.name + '</a>');
                                            $("#inputDescription").html(data.description);
                                            $("#inputCurrency").html(data.currency);
                                            $("#inputPrice").html(data.price);
                                            $("#inputStartDate").html(data.startDate);
                                            $("#inputEndDate").html(data.endDate);

                                            $.each(data.documents, function() {
                                                switch (this.docType) {
                                                    case "Offer":
                                                        $("#fileOffer").append('<li id="doc-' + this.token + '"><a href="PCFilingApp?action=document&token=' + this.token + '"><i class="icon-download"></i> ' + this.fileName + '</a></li>');
                                                        break;

                                                    case "TechSpecs":
                                                        $("#fileTechSpecs").append('<li id="doc-' + this.token + '"><a href="PCFilingApp?action=document&token=' + this.token + '"><i class="icon-download"></i> ' + this.fileName + '</a></li>');
                                                        break;

                                                    case "PriceDelivery":
                                                        $("#filePriceDelivery").append('<li id="doc-' + this.token + '"><a href="PCFilingApp?action=document&token=' + this.token + '"><i class="icon-download"></i> ' + this.fileName + '</a></li>');
                                                        break;

                                                    case "Requested":
                                                        $("#fileRequested").append('<li id="doc-' + this.token + '"><a href="PCFilingApp?action=document&token=' + this.token + '"><i class="icon-download"></i> ' + this.fileName + '</a></li>');
                                                        break;

                                                    case "QualityCertificate":
                                                        $("#fileCerts").append('<li id="doc-' + this.token + '"><a href="PCFilingApp?action=document&token=' + this.token + '"><i class="icon-download"></i> ' + this.fileName + '</a></li>');
                                                        break;

                                                    case "CompanyProfile":
                                                        $("#fileProfile").append('<li id="doc-' + this.token + '"><a href="PCFilingApp?action=document&token=' + this.token + '"><i class="icon-download"></i> ' + this.fileName + '</a></li>');
                                                        break;

                                                    case "FinancialStatements":
                                                        $("#fileFinStatements").append('<li id="doc-' + this.token + '"><a href="PCFilingApp?action=document&token=' + this.token + '"><i class="icon-download"></i> ' + this.fileName + '</a></li>');
                                                        break;
                                                }
                                            });

                                        }
                                    });
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
    </body>
</html>
