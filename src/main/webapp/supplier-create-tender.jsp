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

                    <div class="alert hide" id="userHelper">
                        <button class="close" onclick="userHelper('off')" title="Disable guide" >×</button>
                        <fmt:message key="createtender.help" />
                    </div>

                    <h3><fmt:message key="createtender.title" /> '<span style="display:inline;" id="contractTitle"></span>'</h3>

                    <hr>

                    <!-- <form action="PCFilingApp" method="post" class="form-horizontal" id="contractForm" enctype="multipart/form-data"> -->
                    <form action="PCFilingApp" method="post" enctype="multipart/form-data" class="form-horizontal" id="contractForm">
                        <input name="action" type="hidden" value="addPrivateTender">
                        <input name="forward" type="hidden" value="supplier-prepared.jsp">
                        <input name="buyerURL" id="buyerURL" type="hidden" value="">
                        <input name="contractURL" id="contractURL" type="hidden" value="">

                        <div class="control-group">
                            <h4><fmt:message key="createtender.basicinfo" /></h4>              
                            <div class="control-group">
                                <label class="control-label" for="inputDescription"><fmt:message key="description" bundle="${cons}" /></label>
                                <div class="controls">
                                    <textarea name="description" id="inputDescription"></textarea>
                                </div>
                            </div>
                        </div>
                        <div class="control-group">
                            <h4><fmt:message key="createtender.pricingtiming" /></h4>

                            <label class="control-label" for="inputPrice"><fmt:message key="offeredprice" bundle="${cons}" /> <font color="red">*</font></label>
                            <div class="controls">              
                                <input type="number" step="0.01" name="price" id="inputPrice">
                                <select name="currency">
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

                        <div id="" class="control-group">
                            <h4><fmt:message key="documents" bundle="${cons}" /></h4>
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
                                <select id="inputFileCerts" name="inputFileCerts">
                                    <option value="">---select---</option>
                                </select>                
                            </div>
                        </div>

                        <div class="control-group">			
                            <label class="control-label" for="inputFileProfile"><fmt:message key="createtender.attachments.profile" /></label>
                            <div class="controls">                                              
                                <select id="inputFileProfile" name="inputFileProfile">
                                    <option value="">---select---</option>
                                </select>                
                            </div>
                        </div>

                        <div class="control-group">			
                            <label class="control-label" for="inputFinStatements"><fmt:message key="createtender.attachments.financial" /></label>
                            <div class="controls">                                              
                                <select id="inputFinStatements" name="inputFileFinStatements">
                                    <option value="">---select---</option>
                                </select>                
                            </div>
                        </div>

                        <div class="control-group">
                            <div class="form-actions">
                                <input type="submit" class="btn btn-primary" title="<fmt:message key="createtender.submit.desc" />" value="<fmt:message key="save" bundle="${cons}" />">
                                <a href="supplier-invitations.jsp" class="btn"><fmt:message key="cancel" bundle="${cons}" /></a>
                            </div>
                        </div>

                    </form>
                </div>
            </div>
        </div>

        <%@include file="WEB-INF/jspf/footer.jspf" %> 
        <script src="js/functions.js"></script>    
        <script src="js/sessionstorage.1.4.js"></script>
        <script src="js/cpv-codes.js"></script>  
        <script src="js/jquery-ui.js"></script>
        <script src="js/main.js"></script>
        <script src="js/toolsSupplier.js"></script>        

        <script>
                                $("a").tooltip();
                                $("button").tooltip();
                                $("label").tooltip();
                                $("#inputDeadline").datepicker({dateFormat: 'yy-mm-dd'});
                                $("#inputStartDate").datepicker({dateFormat: 'yy-mm-dd'});
                                $("#inputEndDate").datepicker({dateFormat: 'yy-mm-dd'});

                                var contractURL = sessionStorage.contractURL;
                                var title = sessionStorage.contractTitle;
                                var buyerURL = sessionStorage.buyerURL;

                                $(window).ready(function() {
                                    $('#contractTitle').append(title);
                                    $('#buyerURL').attr('value', buyerURL);
                                    $('#contractURL').attr('value', contractURL);
                                    loadDocs();

                                    $("#addDoc").on('click', function() {
                                        var nD = newDoc($("#addDocSelect").val(), $("#addDocSelect option:selected").text());
                                        $('#docsList').append(nD);
                                        nD.fadeIn();
                                    });
                                });

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
                                                        $("#inputFinStatements").append('<option value="' + this.token + '">' + this.fileName + '</option>');
                                                        break;
                                                }
                                            });
                                        }
                                    });
                                }

        </script>
    </body>
</html>
