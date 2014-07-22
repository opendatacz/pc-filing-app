<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <%@include file="WEB-INF/jspf/header.jspf" %>
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.Supplier" />
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.Constants" var="cons" />
        <link href="bootstrap/css/won.css" rel="stylesheet" />
    </head>
    <body>
        <%@include file="WEB-INF/jspf/header-private.jspf" %>
        <div class="container-fluid">
            <div class="row-fluid">
                <%@include file="WEB-INF/jspf/menu-supplier.jspf" %>

                <div class="span10">

                    <div id="progressbar"></div>

                    <div class="hide view">

                        <h3><fmt:message key="viewevent.title" /> '<span style="display: inline;" id="eventTitle"></span>'</h3>

                        <hr>

                        <div class="control-group">
                            <h4><fmt:message key="viewevent.basicinfo" /></h4>
                            <label class="control-label" for="inputTitle"><fmt:message key="title" bundle="${cons}" /></label>
                            <div class="controls">
                                <span id="inputTitle"></span>
                            </div>
                        </div>
                        <div class="control-group">							
                            <label class="control-label" for="contractor"><fmt:message key="contractingauthority" bundle="${cons}" /></label>
                            <div class="controls">
                                <span id="contractor"></span>
                            </div>
                        </div>
                        <div class="control-group hide">							
                            <label class="control-label" for="tendersOpened"><fmt:message key="tendersopened" bundle="${cons}" /></label>
                            <div class="controls">
                                <span id="tendersOpened"></span>
                            </div>
                        </div>      
                        <div class="control-group">
                            <label class="control-label" for="inputDescription"><fmt:message key="description" bundle="${cons}" /></label>
                            <div class="controls">
                                <div id="inputDescription"></div>
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label" for="cpv1"><fmt:message key="cpvcodes" bundle="${cons}" /></label>
                            <div class="controls">
                                <span id="cpv1"></span>
                            </div>
                        </div>
                        <div class="control-group">
                            <div class="controls">
                                <span id="cpv2"></span>
                            </div>
                        </div>
                        <div class="control-group">
                            <div class="controls">
                                <span id="cpv3"></span>
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label" for="inputProjectID"><fmt:message key="projectid" bundle="${cons}" /></label>
                            <div class="controls">
                                <span id="inputProjectID"></span>
                            </div>
                        </div>

                        <div class="control-group">
                            <label class="control-label" for="inputEventReference"><fmt:message key="eventreference" bundle="${cons}" /></label>
                            <div class="controls">
                                <span id="inputEventReference"></span>
                            </div>
                        </div>

                        <div class="control-group">
                            <label class="control-label" for="procurementMethod"><fmt:message key="procurementmethod" bundle="${cons}" /></label>
                            <div class="controls">
                                <span class="hide" id="pMOpen"><fmt:message key="open" bundle="${cons}" /></span>
                                <span class="hide" id="pMRestricted"><fmt:message key="restricted" bundle="${cons}" /></span>
                                <span class="hide" id="pMAcceleratedRestricted"><fmt:message key="restrictedaccelerated" bundle="${cons}" /></span>
                                <span class="hide" id="pMNegotiated"><fmt:message key="negotiated" bundle="${cons}" /></span>
                                <span class="hide" id="pMAcceleratedNegotiated"><fmt:message key="negotiatedaccelerated" bundle="${cons}" /></span>
                                <span class="hide" id="pMCompetitiveDialogue"><fmt:message key="competitivedialogue" bundle="${cons}" /></span>								
                            </div>
                        </div>



                        <div class="control-group">
                            <label class="control-label" for="eventType"><fmt:message key="eventtype" bundle="${cons}" /></label>
                            <div class="controls">
                                <span class="hide" id="RFQ"><fmt:message key="requestforquotation" bundle="${cons}" /></span>
                                <span class="hide" id="ITT"><fmt:message key="invitationtotender" bundle="${cons}" /></span>
                                <span class="hide" id="RFP"><fmt:message key="requestforproposal" bundle="${cons}" /></span>								
                            </div>
                        </div>

                        <hr>


                        <div class="control-group">
                            <h4><fmt:message key="requestforproposal" bundle="${cons}" /><fmt:message key="viewevent.constraints" /></h4>
                            <label class="control-label" for="inputDeadline"><fmt:message key="viewevent.deadlinetenders" /></label>
                            <div class="controls">
                                <span id="inputDeadline"></span>
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label" for="inputExactPrice"><fmt:message key="estimatedprice" bundle="${cons}" /></label>
                            <div class="controls">
                                <span id="inputExactPrice"></span> <span id="estimatedPriceCurrency"></span> <span class="hide" id="priceConfidential" style="font-style: italic;">(<fmt:message key="viewevent.priceisconfidential" />)</span>
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label" for="inputStartDate"><fmt:message key="viewevent.startend" /></label>
                            <div class="controls">
                                <span id="inputStartDate"></span> - <span id="inputEndDate"></span>
                            </div>
                        </div>

                        <div class="control-group">
                            <h5><fmt:message key="viewevent.location" /></h5>
                            <label class="control-label" for="inputLocation"><fmt:message key="location" bundle="${cons}" /></label>
                            <div class="controls">
                                <span id="inputLocation"></span>
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label" for="inputLocationNUTS">NUTS</label>
                            <div class="controls">
                                <span id="inputLocationNUTS"></span>
                            </div>
                        </div>
                        <div class="control-group"></div>

                        <div class="in hide" id="evaluationCriteria">
                            <div class="control-group">
                                <h5><fmt:message key="viewevent.evaluation" /></h5>
                                <label class="control-label" for="inputECPrice"><fmt:message key="price" bundle="${cons}" /></label>
                                <div class="controls">
                                    <span id="controls"></span><span> %</span>
                                </div>
                            </div>
                            <div class="control-group">
                                <label class="control-label" for="inputECTech"><fmt:message key="technicalspecification" bundle="${cons}" /></label>
                                <div class="controls">
                                    <span id="inputECTech"></span><span> %</span>
                                </div>
                            </div>
                            <div class="control-group">
                                <label class="control-label" for="inputECDate"><fmt:message key="deliverydate" bundle="${cons}" /></label>
                                <div class="controls">
                                    <span id="inputECDate"></span><span> %</span>
                                </div>
                            </div>
                        </div>

                        <hr>

                        <h4 id="documents"><fmt:message key="documents" bundle="${cons}" /></h4>

                        <div class="control-group hide">							
                            <label class="control-label" for="inputFileGenTerms"><fmt:message key="viewevent.doc.terms" /></label>
                            <div class="controls">
                                <ul id="fileGenTerms" class="docList"></ul>
                            </div>
                        </div>

                        <div class="control-group hide">
                            <label class="control-label" for="inputFileCallDoc"><fmt:message key="viewevent.doc.callfortender" /></label>
                            <div class="controls">
                                <ul id="fileCallDoc" class="docList"></ul>
                            </div>
                        </div>

                        <div class="control-group hide">
                            <label class="control-label" for="inputFileAmendment"><fmt:message key="viewevent.doc.revisions" /></label>
                            <div class="controls">
                                <ul id="fileAmendment" class="docList"></ul>
                            </div>
                        </div>

                        <div class="control-group hide">
                            <label class="control-label" for="inputFileResponses"><fmt:message key="viewevent.doc.responses" /></label>
                            <div class="controls">
                                <ul id="fileResponses" class="docList"></ul>
                            </div>
                        </div>

                        <div class="control-group hide">
                            <label class="control-label" for="inputFileTechSpec"><fmt:message key="viewevent.doc.spec" /></label>
                            <div class="controls">
                                <ul id="fileTechSpec" class="docList"></ul>
                            </div>
                        </div>

                        <div class="control-group hide">
                            <label class="control-label" for="inputFilePriceDelivery"><fmt:message key="viewevent.doc.price" /></label>
                            <div class="controls">
                                <ul id="filePriceDelivery" class="docList"></ul>
                            </div>
                        </div>

                        <div class="control-group hide">
                            <label class="control-label" for="inputFileBidSec"><fmt:message key="viewevent.doc.bidsecurity" /></label>
                            <div class="controls">
                                <ul id="fileBidSec" class="docList"></ul>
                            </div>
                        </div>

                        <div class="control-group hide">
                            <label class="control-label" for="inputFilePerfSec"><fmt:message key="viewevent.doc.persec" /></label>
                            <div class="controls">
                                <ul id="filePerfSec" class="docList"></ul>
                            </div>
                        </div>

                        <div class="control-group hide">
                            <label class="control-label" for="inputFileBidSubForm"><fmt:message key="viewevent.doc.bidsubmission" /></label>
                            <div class="controls">
                                <ul id="fileBidSubForm" class="docList"></ul>
                            </div>
                        </div>

                        <hr>						
                        <h4><fmt:message key="viewevent.doc.contactpoint" /></h4>

                        <div class="control-group">

                            <label class="control-label" for="inputFn"><fmt:message key="contactperson" bundle="${cons}" /></label>
                            <div class="controls">
                                <span id="inputFn"></span>
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label" for="inputEmail"><fmt:message key="email" bundle="${cons}" /></label>
                            <div class="controls">
                                <span id="inputEmail"></span>
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label" for="inputPhone"><fmt:message key="phone" bundle="${cons}" /></label>
                            <div class="controls">
                                <span id="inputPhone"></span>
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label" for="inputPoint"><fmt:message key="otherdescription" bundle="${cons}" /></label>
                            <div class="controls">
                                <div id="inputPoint"></div>
                            </div>
                        </div>
                    </div>


                </div>
            </div>
        </div>

        <%@include file="WEB-INF/jspf/footer.jspf" %> 
        <script src="js/cpv-codes.js"></script>
        <script src="js/locations.js"></script>
        <script src="js/jquery-ui.js"></script>
        <script src="js/main.js"></script>

        <script>
            $("a").tooltip();
            $("button").tooltip();
            $("label").tooltip();
            //$('#eventType button').button();  
            $("#cpv1").typeahead({
                source: collection
            });
            $("#cpv2").typeahead({
                source: collection
            });
            $("#cpv3").typeahead({
                source: collection
            });
            $("#inputLocationNUTS").typeahead({
                source: locations
            });
            $("#inputDeadline").datepicker({
                dateFormat: 'yy-mm-dd'
            });
            $("#inputStartDate").datepicker({
                dateFormat: 'yy-mm-dd'
            });
            $("#inputEndDate").datepicker({
                dateFormat: 'yy-mm-dd'
            });

            $(window).ready(function() {
                fillEvent();
            });

            function clearEC() {
                $("#inputECPrice").val("");
                $("#inputECTech").val("");
                $("#inputECDate").val("");
            }

            function fillEvent() {
                var address;
                if (sessionStorage.buyerURL != null
                        && sessionStorage.buyerURL != "undefined") {
                    address = "PCFilingApp?action=getContractJsonSupplier&buyerURL="
                            + encodeURIComponent(sessionStorage.buyerURL)
                            + "&contractURL="
                            + encodeURIComponent(sessionStorage.contractURL)
                } else {
                    address = "PCFilingApp?action=getContractJson&copyContractURL="
                            + encodeURIComponent(sessionStorage.contractURL)
                }
                $.getJSON(address, function(data) {
                    if (data == null || data.length == 0) {
                        sessionStorage.clear();
                        window.location.href = "./";
                    } else {

                        var field;
                        $("#contractName").html(data.title);
                        $("#inputTitle").html(data.title);
                        $('#eventTitle').append(data.title);
                        var contr = $('<a>').attr('href', 'entity-buyer.jsp');
                        contr.on('click', function() {
                            showEntity(data.contractingAuthority.entity);
                        });
                        contr.append(data.contractingAuthority.name);
                        $("#contractor").append(contr);

                        field = $("#inputDescription")
                        if (data.description) {
                            field.html(data.description);
                        } else {
                            field.closest(".control-group").addClass('hide');
                        }

                        $.each(collection, function(key, value) {
                            if (value.indexOf(data.mainCPV) > -1)
                                $("#cpv1").html(value);
                            if (data.additionalCPV)
                                $.each(data.additionalCPV, function(k, v) {
                                    if (value.indexOf(v) > -1) {
                                        $("#cpv" + (Number(k) + 2)).html(value);
                                    }
                                });
                        });

                        if (data.tendersOpening) {
                            $("#tendersOpened").html((new Date(data.tendersOpening)).toUTCString());
                            $("#tendersOpened").closest('.control-group').show();
                        }

                        if (data.procedureType) {
                            $("#pM" + data.procedureType).removeClass("hide");
                        }
                        if (data.eventType) {
                            $("#" + data.eventType).removeClass("hide");
                        }

                        field = $("#inputDeadline");
                        if (data.deadline) {
                            field.html(data.deadline);
                        } else {
                            field.closest(".control-group").addClass('hide');
                        }

                        field = $("#inputExactPrice");
                        if (data.price) {
                            field.html(data.price);
                        } else {
                            field.closest(".control-group").addClass('hide');
                        }

                        $("#estimatedPriceCurrency").html(data.currency);
                        if (data.confidential == true) {
                            $("#priceConfidential").removeClass("hide");
                        }

                        $("#inputStartDate").html(data.startDate);
                        $("#inputEndDate").html(data.estimatedEndDate);

                        $("#inputLocation").html(data.locationLabel);

                        var NUTSe = false;
                        $.each(locations, function(key, value) {
                            if (value.indexOf(data.locationNUTS + "#") > -1) {
                                $("#inputLocationNUTS").html(value);
                                NUTSe = true;
                                return false;
                            }
                        });
                        if (!NUTSe) {
                            $("#inputLocationNUTS").closest(".control-group").addClass('hide');
                        }

                        field = $("#inputEventReference");
                        if (data.eventReference) {
                            field.html(data.eventReference);
                        } else {
                            field.closest(".control-group").addClass('hide');
                        }
                        field = $("#inputProjectID");
                        if (data.projectID) {
                            field.html(data.projectID);
                        } else {
                            field.closest(".control-group").addClass('hide');
                        }

                        field = $("#inputFn");
                        if (data.vcFN) {
                            field.html(data.vcFN);
                        } else {
                            field.closest(".control-group").addClass('hide');
                        }
                        field = $("#inputEmail");
                        if (data.vcEmail) {
                            field.html(data.vcEmail);
                        } else {
                            field.closest(".control-group").addClass('hide');
                        }
                        field = $("#inputPhone");
                        if (data.vcPhone) {
                            field.html(data.vcPhone);
                        } else {
                            field.closest(".control-group").addClass('hide');
                        }
                        field = $("#inputPoint");
                        if (data.vcNote) {
                            field.html(data.vcNote);
                        } else {
                            field.closest(".control-group").addClass('hide');
                        }

                        if (data.criteria.LowestPrice)
                            $("#inputECPrice").html(data.criteria.LowestPrice);
                        if (data.criteria.TechnicalQuality)
                            $("#inputECTech").html(data.criteria.TechnicalQuality);
                        if (data.criteria.BestDate)
                            $("#inputECDate").html(data.criteria.BestDate);

                        $.each(data.documents, function() {
                            var appendObj;

                            switch (this.docType) {

                                case "GeneralTerms":
                                    appendObj = $("#fileGenTerms");
                                    break;

                                case "CallDocument":
                                    appendObj = $("#fileCallDoc");
                                    break;

                                case "Amendment":
                                    appendObj = $("#fileAmendment");
                                    break;

                                case "Responses":
                                    appendObj = $("#fileResponses");
                                    break;

                                case "TechnicalSpecifications":
                                    appendObj = $("#fileTechSpec");
                                    break;

                                case "PriceDelivery":
                                    appendObj = $("#filePriceDelivery");
                                    break;

                                case "BidSecurity":
                                    appendObj = $("#fileBidSec");
                                    break;

                                case "PerformanceSecurity":
                                    appendObj = $("#filePerfSec");
                                    break;

                                case "BidSubmissionForm":
                                    appendObj = $("#fileBidSubForm");
                                    break;
                            }

                            appendObj.append('<li id="doc-' + this.token + '"><a href="PCFilingApp?action=document&token=' + this.token + '"><i class="icon-download"></i> <a href="PCFilingApp?action=document&token=' + this.token + '">' + this.fileName + '</a></li>');
                        });

                        $("#progressbar").hide();
                        $(".view").fadeIn();
                    }
                });
            }
        </script>
    </body>
</html>
