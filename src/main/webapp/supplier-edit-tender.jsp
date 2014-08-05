<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <%@include file="WEB-INF/jspf/header.jspf" %>
        <%@include file="WEB-INF/jspf/header-supplier.jspf" %>
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.Supplier" />
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.Constants" var="cons" />
        <link href="./bootstrap/css/won.css" rel="stylesheet" />
    </head>
    <body>
        <%@include file="WEB-INF/jspf/header-private.jspf" %>
        <div class="container-fluid">
            <div class="row-fluid">
                <%@include file="WEB-INF/jspf/menu-supplier.jspf" %>

                <div class="span10">

                    <h3><fmt:message key="edittender.title" /> '<span style="display:inline;" id="tenderTitle"></span>'</h3>

                    <hr>

                    <!-- <form action="PCFilingApp" method="post" class="form-horizontal" id="contractForm" enctype="multipart/form-data"> -->
                    <form action="PCFilingApp" method="post" enctype="multipart/form-data" class="form-horizontal" id="contractForm">
                        <input name="action" type="hidden" value="editTender">
                        <input name="forward" type="hidden" value="supplier-edit-tender.jsp">
                        <input name="buyerURL" id="buyerURL" type="hidden" value="">
                        <input name="tenderURL" id="tenderURL" type="hidden" value="">

                        <div class="control-group">
                            <h4><fmt:message key="createtender.basicinfo" /></h4>              
                            <div class="control-group">
                                <label class="control-label" for="inputDescription"><fmt:message key="description" bundle="${cons}" /></label>
                                <div class="controls">
                                    <textarea name="description" id="inputDescription"></textarea>
                                </div>
                            </div>
                        </div>
                        <hr>            
                        <div class="control-group">                        
                            <h4><fmt:message key="createtender.pricingtiming" /></h4>

                            <label class="control-label" for="inputPrice"><fmt:message key="offeredprice" bundle="${cons}" /> <font color="red">*</font></label>
                            <div class="controls">              
                                <input type="number" step="0.01" name="price" id="inputPrice">
                                <select name="currency" id="inputCurrency">
                                    <option>USD</option>
                                    <option>EUR</option>
                                    <option>CZK</option>
                                </select>                
                            </div>            
                        </div>

                        <div class="control-group">
                            <label title="Contract duration" class="control-label" for="inputStartDate"><fmt:message key="createtender.startend" /> <font color="red">*</font></label>
                            <div class="controls">
                                <input required name="startDate" type="text" id="inputStartDate"> - <input required id="inputEndDate" name="endDate" type="text">
                            </div>
                        </div>

                        <hr>

                        <h4><fmt:message key="documents" bundle="${cons}" /></h4>

                        <div class="control-group hide">			
                            <label class="control-label" for="inputFileOffer"><fmt:message key="createtender.doc.tender" /></label>
                            <div class="controls">       
                                <ul id="fileOffer" class="docList"></ul>                                       
                            </div>
                        </div>

                        <div class="control-group hide">			
                            <label class="control-label" for="inputFileTechSpecs"><fmt:message key="createtender.doc.specs" /></label>
                            <div class="controls">     
                                <ul id="fileTechSpecs" class="docList"></ul>                                         
                            </div>
                        </div>

                        <div class="control-group hide">			
                            <label class="control-label" for="inputFilePriceDelivery"><fmt:message key="createtender.doc.price" /></label>
                            <div class="controls">           
                                <ul id="filePriceDelivery" class="docList"></ul>                                   
                            </div>
                        </div>

                        <div class="control-group hide">			
                            <label class="control-label" for="inputFileRequested"><fmt:message key="createtender.doc.forms" /></label>
                            <div class="controls">      
                                <ul id="fileRequested" class="docList"></ul>                
                            </div>
                        </div>

                        <div class="control-group">			
                            <label class="control-label" for="addDocument"><fmt:message key="createtender.doc.add" /></label>
                            <div class="controls">                                              
                                <select id="addDocSelect">
                                    <option value="Offer"><fmt:message key="createtender.doc.tender" /></option>
                                    <option value="TechSpecs"><fmt:message key="createtender.doc.specs" /></option>
                                    <option value="PriceDelivery"><fmt:message key="createtender.doc.price" /></option>
                                    <option value="Requested"><fmt:message key="createtender.doc.forms" /></option>
                                </select>                
                                <button name="addDocument" id="addDoc" class="btn" type="button"><fmt:message key="add" bundle="${cons}" /></button>
                                <ul id="docsList">                
                                </ul>
                            </div>
                        </div>

                        <hr>

                        <div class="control-group">
                            <h4><fmt:message key="attachments" bundle="${cons}" /></h4>
                            <label class="control-label" for="inputFileCerts"><fmt:message key="createtender.attachments.certificates" /></label>
                            <div class="controls">               
                                <ul id="fileCerts" class="docList"></ul>                               
                                <select id="inputFileCerts" name="inputFileCerts">
                                    <option value="">---select---</option>
                                </select>                
                            </div>
                        </div>

                        <div class="control-group">			
                            <label class="control-label" for="inputFileProfile"><fmt:message key="createtender.attachments.profile" /></label>
                            <div class="controls">                          
                                <ul id="fileProfile" class="docList"></ul>                    
                                <select id="inputFileProfile" name="inputFileProfile">
                                    <option value="">---select---</option>
                                </select>                
                            </div>
                        </div>

                        <div class="control-group">			
                            <label class="control-label" for="inputFinStatements"><fmt:message key="createtender.attachments.financial" /></label>
                            <div class="controls">      
                                <ul id="fileFinStatements" class="docList"></ul>                                        
                                <select id="inputFileFinStatements" name="inputFileFinStatements">
                                    <option value="">---select---</option>
                                </select>                
                            </div>
                        </div>

                        <div class="control-group">
                            <div class="form-actions">
                                <input type="submit" class="btn btn-primary" title="<fmt:message key="createtender.submit.desc" />" value="<fmt:message key="save" bundle="${cons}" />">
                                <a href="supplier-prepared.jsp" class="btn"><fmt:message key="cancel" bundle="${cons}" /></a>
                            </div>
                        </div>

                    </form>
                </div>
            </div>
        </div>


        <%@include file="WEB-INF/jspf/footer.jspf" %> 
        <script src="js/sessionstorage.1.4.js"></script>
        <script src="js/jquery-ui.js"></script>
        <script src="js/functions.js"></script>
        <script src="js/cpvs.js"></script>
        <script src="js/date.format.js"></script>
        <script src="js/toolsSupplier.js"></script>
        <script src="js/table.js"></script>
        <script src="js/script.js"></script>

        <script>
            $("a").tooltip();
            $("button").tooltip();
            $("label").tooltip();
            $("#inputDeadline").datepicker({dateFormat: 'yy-mm-dd'});
            $("#inputStartDate").datepicker({dateFormat: 'yy-mm-dd'});
            $("#inputEndDate").datepicker({dateFormat: 'yy-mm-dd'});

            var contractURL = sessionStorage.tenderURL;
            var title = sessionStorage.contractTitle;
            var buyerURL = sessionStorage.buyerURL;

            $(window).ready(function() {
                checkUser();
                $('#tenderTitle').append(decodeURIComponent(title));
                $('#buyerURL').attr('value', buyerURL);
                $('#tenderURL').attr('value', tenderURL);

                loadForm();
                loadDocs();

                $("#addDoc").on('click', function() {
                    var nD = newDoc($("#addDocSelect").val(), $("#addDocSelect option:selected").text());
                    $('#docsList').append(nD);
                    nD.fadeIn();
                });
            });


            function loadForm() {

                $.getJSON("PCFilingApp?action=getTenderJson&editTenderURL=" + encodeURIComponent(sessionStorage.editTenderURL), function(data)
                {
                    if (data == null || data.length == 0) {
                        sessionStorage.clear();
                        window.location.href = "./";
                    } else {

                        $("#tenderURL").val(sessionStorage.editTenderURL);
                        $("#inputDescription").html(data.description);
                        $("#inputCurrency").val(data.currency);
                        $("#inputPrice").val(data.price);
                        $("#inputStartDate").val(data.startDate);
                        $("#inputEndDate").val(data.endDate);

                        $.each(data.documents, function() {
                            var field;
                            switch (this.docType) {
                                case "Offer":
                                    field = $("#fileOffer");
                                    break;

                                case "TechSpecs":
                                    field = $("#fileTechSpecs");
                                    break;

                                case "PriceDelivery":
                                    field = $("#filePriceDelivery");
                                    break;

                                case "Requested":
                                    field = $("#fileRequested");
                                    break;

                                case "QualityCertificate":
                                    field = $("#fileCerts");
                                    break;

                                case "CompanyProfile":
                                    field = $("#fileProfile");
                                    break;

                                case "FinancialStatements":
                                    field = $("#fileFinStatements");
                                    break;
                            }

                            field.append('<li id="doc-' + this.token + '"><span class="removeDoc" onclick="unlinkTenderDocument(\'' + this.token + '\')" ><i class="icon-remove-sign"></i></span> <a href="PCFilingApp?action=document&token=' + this.token + '">' + this.fileName + '</a></li>');
                            field.closest('.control-group').show();
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

            function loadDocs() {
                $.getJSON("PCFilingApp?action=getSupplierDocs", function(data) {
                    if (data != null && data.docs) {
                        $.each(data.docs, function() {
                            switch (this.docType) {
                                case "QualityCertificate":
                                    $("#inputFileCerts").append('<option value="' + this.token + '">' + this.fileName + '</option>');
                                    break;

                                case "CompanyProfile":
                                    $("#inputFileProfile").append('<option value="' + this.token + '">' + this.fileName + '</option>');
                                    break;

                                case "FinancialStatements":
                                    $("#inputFileFinStatements").append('<option value="' + this.token + '">' + this.fileName + '</option>');
                                    break;
                            }
                        });
                    }
                });
            }

            function unlinkTenderDocument(token) {
                $.getJSON("PCFilingApp?action=unlinkTenderDocument&tenderURL=" + encodeURIComponent(sessionStorage.editTenderURL) + "&token=" + token, function(data) {
                    if (data != null) {
                        if (data.success) {
                            $("#doc-" + token).remove();
                        }
                    }
                });

            }
        </script>
    </body>
</html>
