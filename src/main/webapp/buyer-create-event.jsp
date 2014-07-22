<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <%@include file="WEB-INF/jspf/header.jspf" %>
        <%@include file="WEB-INF/jspf/header-buyer.jspf" %>
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.Buyer" />
        <fmt:setBundle basename="cz.opendata.tenderstats.i18n.Constants" var="cons" />
        <link href="./bootstrap/css/won.css" rel="stylesheet" />
    </head>
    <body>
        <%@include file="WEB-INF/jspf/header-private.jspf" %>
        <div class="container-fluid">
            <div class="row-fluid">
                <%@include file="WEB-INF/jspf/menu-buyer.jspf" %>
                <div class="span8">
                    <div class="alert hide" id="userHelper">
                        <button class="close" onclick="userHelper('off')" title="Disable guide" >×</button>
                        <fmt:message key="createevent.help" />
                    </div>

                    <h3><fmt:message key="createevent.title" /></h3>

                    <hr>

                    <!-- <form action="PCFilingApp" method="post" class="form-horizontal" id="contractForm" enctype="multipart/form-data"> -->
                    <form action="PCFilingApp" method="post" class="form-horizontal" id="contractForm" >
                        <input name="action" type="hidden" value="addPrivateContract">
                        <input name="forward" type="hidden" value="buyer-prepared.jsp">

                        <div class="control-group">
                            <h4><fmt:message key="createevent.basicinfo" /></h4>
                            <label class="control-label" for="inputTitle"><fmt:message key="title" bundle="${cons}" /> <font color="red">*</font></label>
                            <div class="controls">
                                <input required type="text" name="title" id="inputTitle" placeholder="<fmt:message key="createevent.eventtitle" />">
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label" for="inputDescription"><fmt:message key="description" bundle="${cons}" /></label>
                            <div class="controls">
                                <textarea name="description" id="inputDescription"></textarea>
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label" for="inputDescription"><fmt:message key="cpvcodes" bundle="${cons}" /> <font color="red">*</font></label>
                            <div class="controls">
                                <input required id="cpv1" type="text" name="cpv1" placeholder="<fmt:message key="maincpvcode" bundle="${cons}" />" autocomplete="off">&nbsp;&nbsp;&nbsp;
                            </div>
                        </div>
                        <div class="control-group">
                            <div class="controls">
                                <input id="cpv2" type="text" name="cpv2" placeholder="<fmt:message key="additionalcpvcode" bundle="${cons}" />" autocomplete="off">&nbsp;&nbsp;&nbsp;
                                <input id="cpv3" type="text" name="cpv3" placeholder="<fmt:message key="additionalcpvcode" bundle="${cons}" />" autocomplete="off">&nbsp;&nbsp;&nbsp;
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label" for="inputProjectID"><fmt:message key="projectid" bundle="${cons}" /></label>
                            <div class="controls">
                                <input type="text" name="projectID" id="inputProjectID" placeholder="<fmt:message key="createevent.idproject.description" />">
                            </div>
                        </div>

                        <div class="control-group">
                            <label class="control-label" for="inputEventReference"><fmt:message key="eventreference" bundle="${cons}" /></label>
                            <div class="controls">
                                <input type="text" name="eventReference" id="inputEventReference" placeholder="<fmt:message key="eventreference" bundle="${cons}" />">
                            </div>
                        </div>

                        <div class="control-group">
                            <label class="control-label" for="procurementMethod"><fmt:message key="procurementmethod" bundle="${cons}" /></label>
                            <div class="controls">
                                <select name="procurementMethod">
                                    <option value="Open"><fmt:message key="open" bundle="${cons}" /></option>
                                    <option value="Restricted"><fmt:message key="restricted" bundle="${cons}" /></option>
                                    <option value="AcceleratedRestricted"><fmt:message key="restrictedaccelerated" bundle="${cons}" /></option>
                                    <option value="Negotiated"><fmt:message key="negotiated" bundle="${cons}" /></option>
                                    <option value="AcceleratedNegotiated"><fmt:message key="negotiatedaccelerated" bundle="${cons}" /></option>
                                    <option value="CompetitiveDialogue"><fmt:message key="competitivedialogue" bundle="${cons}" /></option>                                 
                                </select>
                            </div>
                        </div>

                        <div class="control-group">
                            <div class="controls">
                                <label class="checkbox">
                                    <input type="checkbox" name="tendersSealed" id="inputTendersSealed" placeholder="<fmt:message key="createevent.tenders.sealed" />" value="on"> <fmt:message key="createevent.tenders.aresealed" />
                                </label>                
                            </div>
                        </div>

                        <div class="control-group">
                            <label class="control-label" for="eventType"><fmt:message key="eventtype" bundle="${cons}" /> <font color="red">*</font></label>
                            <div class="controls">
                                <div class="btn-group" data-toggle="buttons-radio" id="eventType">
                                    <button onclick="$('#eventType button').removeClass('btn-primary');
                                            $('#evaluationCriteria').fadeOut('slow');
                                            $('#selectEvent').hide();
                                            $(this).addClass('btn-primary');
                                            $('#inputEventType').val('RFQ');
                                            $('#eventRest').fadeIn('slow');" type="button" data-toggle="button" class="btn" title="<fmt:message key="requestforquotation" bundle="${cons}" />" id="btnRFQ"><fmt:message key="RFQ" bundle="${cons}" /></button>
                                    <button onclick="$('#eventType button').removeClass('btn-primary');
                                            $('#evaluationCriteria').fadeOut('slow');
                                            $('#selectEvent').hide();
                                            $(this).addClass('btn-primary');
                                            $('#inputEventType').val('ITT');
                                            $('#eventRest').fadeIn('slow');" type="button" data-toggle="button" class="btn" title="<fmt:message key="invitationtotender" bundle="${cons}" />" id="btnITT"><fmt:message key="ITT" bundle="${cons}" /></button>
                                    <button onclick="$('#eventType button').removeClass('btn-primary');
                                            $('#evaluationCriteria').fadeIn('slow');
                                            $('#selectEvent').hide();
                                            $(this).addClass('btn-primary');
                                            $('#inputEventType').val('RFP');
                                            $('#eventRest').fadeIn('slow');" type="button" data-toggle="button" class="btn" title="<fmt:message key="requestforproposal" bundle="${cons}" />" id="btnRFP"><fmt:message key="RFP" bundle="${cons}" /></button>
                                    <input id="inputEventType" type="hidden" name="eventType" value="" />
                                </div>
                            </div>
                        </div>



                        <hr>

                        <div class="control-group" id="selectEvent">
                            <label style="padding-left:150px; color:grey;" class="">(<fmt:message key="createevent.selecteventcontinue" />)</label>
                        </div>

                        <div class="in hide" id="eventRest">
                            <div class="control-group">
                                <h4><fmt:message key="createevent.constraints" /></h4>
                                <label class="control-label" for="inputDeadline"><fmt:message key="createevent.tenders.deadline" /> <font color="red">*</font></label>
                                <div class="controls">
                                    <input required type="text" name="deadline" id="inputDeadline">
                                </div>
                            </div>
                            <div class="control-group">
                                <label class="control-label" for="inputExactPrice"><fmt:message key="estimatedprice" bundle="${cons}" /></label>
                                <div class="controls">
                                    <input type="number" step="0.01" name="estimatedPrice" id="inputExactPrice">
                                    <select name="estimatedPriceCurrency">
                                        <option>USD</option>
                                        <option>EUR</option>
                                        <option>CZK</option>
                                    </select>
                                    <label class="checkbox">
                                        <input type="checkbox" checked="checked" name="priceIsConfidential"> <fmt:message key="createevent.priceisconfidential" />
                                    </label>
                                </div>
                            </div>
                            <div class="control-group">
                                <label title="Contract duration" class="control-label" for="inputStartDate"><fmt:message key="createevent.startend" /> <font color="red">*</font></label>
                                <div class="controls">
                                    <input required name="estimatedStartDate" type="text" id="inputStartDate"> - <input required id="inputEndDate" name="estimatedEndDate" type="text">
                                </div>
                            </div>

                            <div class="control-group">
                                <h5><fmt:message key="createevent.locationrealization" /></h5>
                                <label class="control-label" for="inputLocation"><fmt:message key="location" bundle="${cons}" /> <font color="red">*</font></label>
                                <div class="controls">
                                    <input required name="location" type="text" id="inputLocation" placeholder="Ex: Prague or FCA Prague">
                                </div>
                            </div>
                            <div class="control-group">
                                <label class="control-label" for="inputLocationNUTS">NUTS</label>
                                <div class="controls">
                                    <input name="nuts" type="text" id="inputLocationNUTS" autocomplete="off">
                                </div>
                            </div>
                            <div class="control-group">

                            </div>

                            <div class="in hide" id="evaluationCriteria">
                                <div class="control-group">
                                    <h5><fmt:message key="createevent.evaluationcriteria" /></h5>
                                    <label class="control-label" for="inputECPrice"><fmt:message key="price" bundle="${cons}" /></label>&nbsp;&nbsp;&nbsp;
                                    <div class="input-append">
                                        <input name="evalPrice" type="number" max="100" id="inputECPrice" value="" placeholder="Ex: 70">
                                        <span class="add-on"> %</span>
                                    </div>
                                </div>
                                <div class="control-group">
                                    <label class="control-label" for="inputECTech"><fmt:message key="technicalspecification" bundle="${cons}" /></label>&nbsp;&nbsp;&nbsp;
                                    <div class="input-append">
                                        <input name="evalTech" type="number" max="100" id="inputECTech" value="" placeholder="Ex: 20">
                                        <span class="add-on"> %</span>
                                    </div>
                                </div>
                                <div class="control-group">
                                    <label class="control-label" for="inputECDate"><fmt:message key="deliverydate" bundle="${cons}" /></label>&nbsp;&nbsp;&nbsp;
                                    <div class="input-append">
                                        <input name="evalDate" type="number" max="100" id="inputECDate" value="" placeholder="Ex: 10">
                                        <span class="add-on"> %</span>
                                    </div>
                                </div>
                            </div>
                            <!--<div class="control-group">
                              <label class="control-label"><a href="#" class="btn btn-mini">add criterion</a></label>
                            </div>-->
                            <hr>

                            <div id="" class="control-group">
                                <h4><fmt:message key="documents" bundle="${cons}" /></h4>
                                <label class="control-label" for="addDocument"><fmt:message key="createevent.adddocument" /></label>
                                <div class="controls">                                              
                                    <select id="addDocSelect">
                                        <option value="GeneralTerms"><fmt:message key="createevent.doc.general" /></option>
                                        <option value="CallDocument"><fmt:message key="createevent.doc.call" /></option>
                                        <option value="Amendment"><fmt:message key="createevent.doc.revisions" /></option>
                                        <option value="Responses"><fmt:message key="createevent.doc.responses" /></option>
                                        <option value="TechnicalSpecifications"><fmt:message key="createevent.doc.detail" /></option>
                                        <option value="PriceDelivery"><fmt:message key="createevent.doc.price" /></option>
                                        <option value="BidSecurity"><fmt:message key="createevent.doc.bidsecurity" /></option>
                                        <option value="PerformanceSecurity"><fmt:message key="createevent.doc.persecurity" /></option>
                                        <option value="BidSubmissionForm"><fmt:message key="createevent.doc.submission" /></option>                	
                                    </select>                
                                    <button name="addDocument" id="addDoc" class="btn" type="button"><fmt:message key="add" bundle="${cons}" /></button>
                                    <ul id="docsList">                
                                    </ul>
                                </div>
                            </div>

                            <hr>

                            <div class="control-group">
                                <h4><fmt:message key="createevent.contactpoint" /></h4>
                                <label class="control-label" for="inputFn"><fmt:message key="contactperson" bundle="${cons}" /></label>
                                <div class="controls">
                                    <input name="contactPerson" type="text" id="inputFn">
                                </div>
                            </div>
                            <div class="control-group">
                                <label class="control-label" for="inputEmail"><fmt:message key="email" bundle="${cons}" /></label>
                                <div class="controls">
                                    <input name="contactEmail" type="email" id="inputEmail">
                                </div>
                            </div>
                            <div class="control-group">
                                <label class="control-label" for="inputPhone"><fmt:message key="phone" bundle="${cons}" /></label>
                                <div class="controls">
                                    <input name="contactPhone" type="tel" id="inputPhone">
                                </div>
                            </div>
                            <div class="control-group">
                                <label class="control-label" for="inputPoint"><fmt:message key="otherdescription" bundle="${cons}" /></label>
                                <div class="controls">
                                    <textarea name="contactDescription" id="inputPoint"></textarea>
                                </div>
                            </div>
                            <div class="control-group">
                                <div class="form-actions">
                                    <input type="submit" class="btn btn-primary" title="<fmt:message key="createevent.submit.description" />" value="<fmt:message key="save" bundle="${cons}" />">
                                    <a href="buyer-prepared.jsp" class="btn"><fmt:message key="cancel" bundle="${cons}" /></a>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
                <%@include file="WEB-INF/jspf/stats-buyer.jspf" %>
            </div>
        </div>
        <%@include file="WEB-INF/jspf/footer.jspf" %>
        <script src="js/cpv-codes.js"></script>
        <script src="js/locations.js"></script>
        <script src="js/cpvs.js"></script>	
        <script src="js/functions.js"></script>
        <script src="js/sessionstorage.1.4.js"></script>
        <script src="js/script.js"></script>
        <script src="js/toolsBuyer.js"></script>
        <script src="js/date.format.js"></script>
        <script src="js/table.js"></script>

        <script>
                                        $("#enableGuide").tooltip({placement: 'bottom'});
                                        $("a").tooltip();
                                        $("button").tooltip();
                                        $("label").tooltip();
                                        //$('#eventType button').button();  
                                        $("#cpv1").typeahead({source: collection});
                                        $("#cpv2").typeahead({source: collection});
                                        $("#cpv3").typeahead({source: collection});
                                        $("#inputLocationNUTS").typeahead({source: locations});
                                        $("#inputDeadline").datepicker({dateFormat: 'yy-mm-dd'});
                                        $("#inputStartDate").datepicker({dateFormat: 'yy-mm-dd'});
                                        $("#inputEndDate").datepicker({dateFormat: 'yy-mm-dd'});

                                        $(window).ready(function() {


                                            $("#addDoc").on('click', function() {
                                                var nD = newDoc($("#addDocSelect").val(), $("#addDocSelect option:selected").text());
                                                $('#docsList').append(nD);
                                                nD.fadeIn();
                                            });

                                        });
        </script>
    </body>
</html>
