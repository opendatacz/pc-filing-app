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
                    <div class="alert hide" id="userHelper">
                      <button class="close"
                        onclick="userHelper('off')"
                        title="<fmt:message key="disableguide" bundle="${cons}" />" >×</button>
                        <fmt:message key="viewcontract.help" />
                    </div>

                    <div id="progressbar"></div>
                    <div id="view" style="display: none">
                        <h3>
                            <fmt:message key="viewevent.title" /> '<span style="display: inline;" id="eventTitle"></span>'
                        </h3>
                        <hr>
                        <div class="control-group">
                            <h4><fmt:message key="createevent.basicinfo" /></h4>
                            <label class="control-label" for="inputTitle">
                              <strong><fmt:message key="title" bundle="${cons}" /></strong>
                            </label>
                            <div class="controls">
                                <span id="inputTitle"></span>
                            </div>
                        </div>
                        <div class="control-group">							
                            <label class="control-label" for="contractor">
                              <strong><fmt:message key="contractingauthority" bundle="${cons}" /></strong>
                            </label>
                            <div class="controls">
                                <span id="contractor"></span>
                            </div>
                        </div>
                        <div class="control-group hide">							
                          <label class="control-label" for="tendersOpened">
                            <strong><fmt:message key="tendersopened" bundle="${cons}" /></strong>
                          </label>
                            <div class="controls">
                                <span id="tendersOpened"></span>
                            </div>
                        </div>      
                        <div class="control-group">
                          <label class="control-label" for="inputDescription">
                            <strong><fmt:message key="description" bundle="${cons}" /></strong>
                          </label>
                            <div class="controls">
                                <div id="inputDescription"></div>
                            </div>
                        </div>
                        <div class="control-group">
                          <label class="control-label" for="inputDescription">
                            <strong><fmt:message key="cpvcodes" bundle="${cons}" /></strong>
                          </label>
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
                          <label class="control-label" for="inputProjectID">
                            <strong><fmt:message key="projectid" bundle="${cons}" /></strong>
                          </label>
                            <div class="controls">
                                <span id="inputProjectID"></span>
                            </div>
                        </div>

                        <div class="control-group">
                          <label class="control-label" for="inputEventReference">
                            <strong><fmt:message key="eventreference" bundle="${cons}" /></strong>
                          </label>
                            <div class="controls">
                                <span id="inputEventReference"></span>
                            </div>
                        </div>

                        <div class="control-group">
                          <label class="control-label" for="procurementMethod">
                            <strong><fmt:message key="procurementmethod" bundle="${cons}" /></strong>
                          </label>
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
                          <label class="control-label" for="eventType">
                            <strong><fmt:message key="eventtype" bundle="${cons}" /></strong>
                          </label>
                            <div class="controls">
                                <span class="hide" id="<fmt:message key="RFQ" bundle="${cons}" />"><fmt:message key="requestforquotation" bundle="${cons}" /></span>
                                <span class="hide" id="<fmt:message key="ITT" bundle="${cons}" />"><fmt:message key="invitationtotender" bundle="${cons}" /></span>
                                <span class="hide" id="<fmt:message key="RFP" bundle="${cons}" />"><fmt:message key="requestforproposal" bundle="${cons}" /></span>								
                            </div>
                        </div>
                        <hr>
                        <div class="control-group">
                            <h4><fmt:message key="createevent.constraints" /></h4>
                            <label class="control-label" for="inputDeadline">
                              <strong><fmt:message key="createevent.tenders.deadline" /></strong>
                            </label>
                            <div class="controls">
                                <span id="inputDeadline"></span>
                            </div>
                        </div>
                        <div class="control-group">
                          <label class="control-label" for="inputExactPrice">
                            <strong><fmt:message key="estimatedprice" bundle="${cons}" /></strong>
                          </label>
                            <div class="controls">
                                <span id="inputExactPrice"></span> <span id="estimatedPriceCurrency"></span> <span class="hide" id="priceConfidential" style="font-style: italic;">(<fmt:message key="createevent.priceisconfidential" />)</span>
                            </div>
                        </div>
                        <div class="control-group">
                          <label class="control-label" for="inputStartDate">
                            <strong><fmt:message key="createevent.startend" /></strong>
                          </label>
                            <div class="controls">
                                <span id="inputStartDate"></span> - <span id="inputEndDate"></span>
                            </div>
                        </div>

                        <div class="control-group">
                            <h5><fmt:message key="createevent.locationrealization" /></h5>
                            <label class="control-label" for="inputLocation">
                              <strong><fmt:message key="location" bundle="${cons}" /></strong>
                            </label>
                            <div class="controls">
                                <span id="inputLocation"></span>
                            </div>
                        </div>
                        <div class="control-group">
                          <label class="control-label" for="inputLocationNUTS">
                            <strong>NUTS</strong>
                          </label>
                            <div class="controls">
                                <span id="inputLocationNUTS"></span>
                            </div>
                        </div>
                        <div class="control-group"></div>

                        <div class="in hide" id="evaluationCriteria">
                            <div class="control-group">
                                <h5><fmt:message key="createevent.evaluationcriteria" /></h5>
                                <label class="control-label" for="inputECPrice">
                                  <strong><fmt:message key="price" bundle="${cons}" /></strong>
                                </label>
                                <div class="controls">
                                  <span id="controls"></span><span> %</span>
                                </div>
                            </div>
                            <div class="control-group">
                              <label class="control-label" for="inputECTech">
                                <strong><fmt:message key="technicalspecification" bundle="${cons}" /></strong>
                              </label>
                                <div class="controls">
                                    <span id="inputECTech"></span><span> %</span>
                                </div>
                            </div>
                            <div class="control-group">
                              <label class="control-label" for="inputECDate">
                                <strong><fmt:message key="deliverydate" bundle="${cons}" /></strong>
                              </label>
                                <div class="controls">
                                    <span id="inputECDate"></span><span> %</span>
                                </div>
                            </div>
                        </div>

                        <hr>

                        <h4 id="documents"><fmt:message key="documents" bundle="${cons}" /></h4>

                        <div class="control-group hide">							
                          <label class="control-label" for="inputFileGenTerms">
                            <strong><fmt:message key="createevent.doc.general" /></strong>
                          </label>
                            <div class="controls">
                                <ul id="fileGenTerms" class="docList"></ul>
                            </div>
                        </div>

                        <div class="control-group hide">
                          <label class="control-label" for="inputFileCallDoc">
                            <strong><fmt:message key="createevent.doc.call" /></strong>
                          </label>
                            <div class="controls">
                                <ul id="fileCallDoc" class="docList"></ul>
                            </div>
                        </div>

                        <div class="control-group hide">
                          <label class="control-label" for="inputFileAmendment">
                            <strong><fmt:message key="createevent.doc.revisions" /></strong>
                          </label>
                            <div class="controls">
                                <ul id="fileAmendment" class="docList"></ul>
                            </div>
                        </div>

                        <div class="control-group hide">
                          <label class="control-label" for="inputFileResponses">
                            <strong><fmt:message key="createevent.doc.responses" /></strong>
                          </label>
                            <div class="controls">
                                <ul id="fileResponses" class="docList"></ul>
                            </div>
                        </div>

                        <div class="control-group hide">
                          <label class="control-label" for="inputFileTechSpec">
                            <strong><fmt:message key="createevent.doc.detail" /></strong>
                          </label>
                            <div class="controls">
                                <ul id="fileTechSpec" class="docList"></ul>
                            </div>
                        </div>

                        <div class="control-group hide">
                          <label class="control-label" for="inputFilePriceDelivery">
                            <strong><fmt:message key="createevent.doc.price" /></strong>
                          </label>
                            <div class="controls">
                                <ul id="filePriceDelivery" class="docList"></ul>
                            </div>
                        </div>

                        <div class="control-group hide">
                          <label class="control-label" for="inputFileBidSec">
                            <strong><fmt:message key="createevent.doc.bidsecurity" /></strong>
                          </label>
                            <div class="controls">
                                <ul id="fileBidSec" class="docList"></ul>
                            </div>
                        </div>

                        <div class="control-group hide">
                          <label class="control-label" for="inputFilePerfSec">
                            <strong><fmt:message key="createevent.doc.persecurity" /></strong>
                          </label>
                            <div class="controls">
                                <ul id="filePerfSec" class="docList"></ul>
                            </div>
                        </div>

                        <div class="control-group hide">
                          <label class="control-label" for="inputFileBidSubForm">
                            <strong><fmt:message key="createevent.doc.submission" /></strong>
                          </label>
                            <div class="controls">
                                <ul id="fileBidSubForm" class="docList"></ul>
                            </div>
                        </div>

                        <hr>						
                        <h4><fmt:message key="createevent.contactpoint" /></h4>

                        <div class="control-group">

                          <label class="control-label" for="inputFn">
                            <strong><fmt:message key="contactperson" bundle="${cons}" /></strong>
                          </label>
                            <div class="controls">
                                <span id="inputFn"></span>
                            </div>
                        </div>
                        <div class="control-group">
                          <label class="control-label" for="inputEmail">
                            <strong><fmt:message key="email" bundle="${cons}" /></strong>
                          </label>
                            <div class="controls">
                                <span id="inputEmail"></span>
                            </div>
                        </div>
                        <div class="control-group">
                          <label class="control-label" for="inputPhone">
                            <strong><fmt:message key="phone" bundle="${cons}" /></strong>
                          </label>
                            <div class="controls">
                                <span id="inputPhone"></span>
                            </div>
                        </div>
                        <div class="control-group">
                          <label class="control-label" for="inputPoint">
                            <strong><fmt:message key="otherdescription" bundle="${cons}" /></strong>
                          </label>
                            <div class="controls">
                                <div id="inputPoint"></div>
                            </div>
                        </div>
                    </div>

                </div>
                <%@include file="WEB-INF/jspf/stats-buyer.jspf" %>
            </div>
        </div>

        <%@include file="WEB-INF/jspf/footer.jspf" %>

        <script src="js/cpv-codes.js"></script>
        <script src="js/locations.js"></script>
        <script src="js/functions.js"></script>
        <script src="js/sessionstorage.1.4.js"></script>
        <script src="js/toolsBuyer.js"></script>
        <script src="js/script.js"></script>	
        <script src="js/table.js"></script>

        <script type="text/javascript">


            var buyerURL = sessionStorage.buyerURL;

            $(window).ready(function() {
                fillEvent();
            });

              function fillEvent() {
                var opts = {
                  action: "getContractJson",
                  copyContractURL: sessionStorage.contractURL,
                  public: sessionStorage.public 
                };
                $.getJSON("PCFilingApp", opts, function (data)
                {
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
                        if (typeof data.contractingAuthority !== "undefined") {
                          contr.append(data.contractingAuthority.name);
                        }
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

                        //$("#btn"+data.eventType).click();

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
                            appendObj.closest(".control-group").removeClass('hide');
                            appendObj.append('<li id="doc-' + this.token + '"><a href="PCFilingApp?action=document&token=' + this.token + '"><i class="icon-download"></i> <a href="PCFilingApp?action=document&token=' + this.token + '">' + this.fileName + '</a></li>');

                        });
                    }

                    $("#progressbar").hide();
                    $("#view").fadeIn();
                });
            }



        </script>
    </body>
</html>
