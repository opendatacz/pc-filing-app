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

                    <h3 style="margin-bottom: 20px;"><fmt:message key="account.title" /></h3>

                    <div style="text-align: center;" id="progressbar">
                        <br> <img src="images/progressbar.gif" />
                    </div>

                    <div class="view hide"> 
                        <h4><fmt:message key="account.interest" />: </h4>
                        <br>
                        <form action="SystemManager" method="post" class="form-horizontal" >
                            <input name="action" type="hidden" value="updateCPVs">
                            <input name="forward" id="fwd" type="hidden" value="supplier-calls.jsp">
                            <input name="forward-if-fail" type="hidden" value="supplier-calls.jsp?t=error&m=<fmt:message key="account.error" />">
                            <div class="control-group">
                                <label class="control-label" for="inputDescription"><fmt:message key="cpvcodes" bundle="${cons}" /> <font color="red">*</font></label>
                                <div class="controls">
                                    <input required id="cpv1" type="text" name="cpv1" placeholder="<fmt:message key="account.cpv.first" />" autocomplete="off">&nbsp;&nbsp;&nbsp;
                                    <input id="cpv2" type="text" name="cpv2" placeholder="<fmt:message key="account.cpv.second" />" autocomplete="off">&nbsp;&nbsp;&nbsp;
                                    <input id="cpv3" type="text" name="cpv3" placeholder="<fmt:message key="account.cpv.third" />" autocomplete="off">&nbsp;&nbsp;&nbsp;
                                </div>
                            </div>
                            <div class="control-group">
                                <div class="form-actions">
                                    <input type="submit" class="btn btn-primary" value="<fmt:message key="save" bundle="${cons}" />">                
                                </div>
                            </div>            
                        </form>


                        <h4 style="margin-bottom: 20px;"><fmt:message key="documents" bundle="${cons}" /></h4>

                        <!-- <form action="PCFilingApp" method="post" class="form-horizontal" id="contractForm" enctype="multipart/form-data"> -->
                        <form action="PCFilingApp" method="post" class="form-horizontal" id="contractForm" >
                            <input type="hidden" name="action" value="supplierDocsUpload"/>
                            <input name="forward" type="hidden" value="supplier-account.jsp">          

                            <div class="control-group">
                                <label class="control-label" for="inputFileCerts"><fmt:message key="account.doc.certificates" /></label>
                                <div class="controls">                             
                                    <ul id="fileCerts" class="docList"></ul>
                                </div>
                            </div>

                            <div class="control-group">			
                                <label class="control-label" for="inputFileProfile"><fmt:message key="account.doc.profile" /></label>
                                <div class="controls">
                                    <ul id="fileProfile" class="docList"></ul>          
                                </div>
                            </div>

                            <div class="control-group">			
                                <label class="control-label" for="inputFileFinStatements"><fmt:message key="account.doc.financial" /></label>
                                <div class="controls">
                                    <ul id="fileFinStatements" class="docList"></ul>
                                </div>
                            </div>

                            <div class="control-group">			
                                <label class="control-label" for="addDocument"><fmt:message key="account.doc.add" /></label>
                                <div class="controls">                                              
                                    <select id="addDocSelect">
                                        <option value="QualityCertificate"><fmt:message key="account.doc.certificates" /></option>
                                        <option value="CompanyProfile"><fmt:message key="account.doc.profile" /></option>
                                        <option value="FinancialStatements"><fmt:message key="account.doc.financial" /></option>
                                    </select>
                                    <button name="addDocument" id="addDoc" class="btn" type="button"><fmt:message key="add" bundle="${cons}" /></button>
                                    <ul id="docsList">                
                                    </ul>
                                </div>
                            </div>

                            <div class="control-group">
                                <div class="form-actions">
                                    <input type="submit" class="btn btn-primary" value="<fmt:message key="save" bundle="${cons}" />">                
                                </div>
                            </div>            
                        </form>

                    </div>
                </div>
            </div>
        </div>

        <%@include file="WEB-INF/jspf/footer.jspf" %>
        <script src="js/sessionstorage.1.4.js"></script>
        <script src="js/functions.js"></script>
        <script src="js/toolsSupplier.js"></script>
        <script src="js/main.js"></script>
        <script type="text/javascript" src="js/cpv-codes.js"></script>
        <script type="text/javascript">

            function searchStringInArray(str, strArray) {
                for (var j = 0; j < strArray.length; j++) {
                    if (strArray[j].match(str))
                        return j;
                }
                return -1;
            }

            $(window).ready(function() {

                $("#cpv1").typeahead({source: collection});
                $("#cpv2").typeahead({source: collection});
                $("#cpv3").typeahead({source: collection});

                $.getJSON("SystemManager?action=getUserPreferences", function(data) {
                    if (data != null) {
                        if (data.cpv1) {
                            var index = searchStringInArray(data.cpv1, collection);
                            if (index > -1)
                                $("#cpv1").val(collection[index]);
                        }
                        if (data.cpv2) {
                            var index = searchStringInArray(data.cpv2, collection);
                            if (index > -1)
                                $("#cpv2").val(collection[index]);
                        }
                        if (data.cpv3) {
                            var index = searchStringInArray(data.cpv3, collection);
                            if (index > -1)
                                $("#cpv3").val(collection[index]);
                        }
                    }
                });

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
                                    $("#fileCerts").append('<li id="doc-' + this.token + '"> <a href="PCFilingApp?action=document&token=' + this.token + '"><i class="icon-download"></i> ' + this.fileName + '</a></li>');
                                    break;

                                case "CompanyProfile":
                                    $("#fileProfile").append('<li id="doc-' + this.token + '"> <a href="PCFilingApp?action=document&token=' + this.token + '"><i class="icon-download"></i> ' + this.fileName + '</a></li>');
                                    break;

                                case "FinancialStatements":
                                    $("#fileFinStatements").append('<li id="doc-' + this.token + '"> <a href="PCFilingApp?action=document&token=' + this.token + '"><i class="icon-download"></i> ' + this.fileName + '</a></li>');
                                    break;
                            }
                        });

                        $("#progressbar").hide();
                        $(".view").fadeIn();
                    }
                });
            }

        </script>

        <script>
            $("a").tooltip();
        </script>
    </body>
</html>