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
                        <fmt:message key="copyevent.help" />
                    </div>

                    <h3><fmt:message key="copyevent.title" /> "<span id="contractName"></span>"</h3>
                    <hr>
                    <form action="PCFilingApp" method="post" class="form-horizontal" id="contractForm">
                        <input name="action" type="hidden" value="addPrivateContract">
                        <input name="forward" type="hidden" value="buyer-prepared.jsp">

                        <div class="control-group required">
                            <h4><fmt:message key="createevent.basicinfo" /></h4>
                            <label class="control-label" for="inputTitle">
                              <fmt:message key="title" bundle="${cons}" />
                            </label>
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
                        <div class="control-group required">
                          <label class="control-label" for="inputDescription">
                            <fmt:message key="cpvcodes" bundle="${cons}" />
                          </label>
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
                                <select id="procurementMethod" name="procurementMethod">
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
                        <div class="control-group required">
                          <label class="control-label" for="eventType">
                            <fmt:message key="eventtype" bundle="${cons}" />
                          </label>
                            <div class="controls">
                                <div class="btn-group" data-toggle="buttons-radio" id="eventType">
                                    <button onclick="$('#eventType button').removeClass('btn-primary');
                                            $('#evaluationCriteria').fadeOut('slow');
                                            $('#selectEvent').hide();
                                            $(this).addClass('btn-primary');
                                            clearEC();
                                            $('#inputEventType').val('RFQ');
                                            $('#eventRest').fadeIn('slow');" type="button" data-toggle="button" class="btn" title="<fmt:message key="requestforquotation" bundle="${cons}" />" id="btnRFQ"><fmt:message key="RFQ" bundle="${cons}" /></button>
                                    <button onclick="$('#eventType button').removeClass('btn-primary');
                                            $('#evaluationCriteria').fadeOut('slow');
                                            $('#selectEvent').hide();
                                            $(this).addClass('btn-primary');
                                            clearEC();
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
                            <div class="control-group required">
                                <h4><fmt:message key="createevent.constraints" /></h4>
                                <label class="control-label" for="inputDeadline">
                                  <fmt:message key="createevent.tenders.deadline" />
                                </label>
                                <div class="controls">
                                    <input required type="text" name="deadline" id="inputDeadline">
                                </div>
                            </div>
                            <div class="control-group">
                                <label class="control-label" for="inputExactPrice"><fmt:message key="estimatedprice" bundle="${cons}" /></label>
                                <div class="controls">
                                    <input type="number" step="0.01" name="estimatedPrice" id="inputExactPrice">
                                    <select id="estimatedPriceCurrency" name="estimatedPriceCurrency">
                                        <option>USD</option>
                                        <option>EUR</option>
                                        <option>CZK</option>
                                    </select>
                                    <label class="checkbox">
                                        <input type="checkbox" id="priceConfidential" name="priceIsConfidential"> <fmt:message key="createevent.priceisconfidential" />
                                    </label>
                                </div>
                            </div>
                            <div class="control-group required">
                              <label class="control-label" for="inputStartDate">
                                <fmt:message key="createevent.startend" />
                                <a class="help-msg"
                                  href="javascript:void(0);"
                                  data-content="<fmt:message key="createevent.startend.help" />"
                                  data-placement="bottom"
                                  data-toggle="popover">
                                  <i class="icon-question-sign"></i>
                                </a>
                              </label>
                                <div class="controls">
                                    <input required name="estimatedStartDate" type="text" id="inputStartDate"> - <input required id="inputEndDate" name="estimatedEndDate" type="text">
                                </div>
                            </div>

                            <div class="control-group required">
                                <h5><fmt:message key="createevent.locationrealization" /></h5>
                                <label class="control-label" for="inputLocation">
                                  <fmt:message key="location" bundle="${cons}" />
                                </label>
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
                                    <a href="my-events.jsp" class="btn"><fmt:message key="cancel" bundle="${cons}" /></a>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
                <%@include file="WEB-INF/jspf/stats-buyer.jspf" %>
            </div>
        </div>
        <%@include file="WEB-INF/jspf/footer.jspf" %>
        <script src="js/cpv-codes-${pageContext.request.locale}.js"></script>
        <script src="js/locations.js"></script>
        <script src="js/functions.js"></script>
        <script src="js/sessionstorage.1.4.js"></script>
        <script src="js/toolsBuyer.js"></script>
        <script src="js/script.js"></script>	
        <script src="js/table.js"></script>    
        <script src="js/application.js"></script>

        <script>
          var cpvAutocompleteOpts = {
            source: cpvCollection,
            sorter: APP.autocomplete.cpvSorter
          };

                                        $("a").tooltip();
                                        $("button").tooltip();
                                        $("label").tooltip();
                                        //$('#eventType button').button();  
                                        $("#cpv1").typeahead(cpvAutocompleteOpts);
                                        $("#cpv2").typeahead(cpvAutocompleteOpts);
                                        $("#cpv3").typeahead(cpvAutocompleteOpts);
                                        $("#inputLocationNUTS").typeahead({source: locations});
                                        $("#inputDeadline").datepicker({dateFormat: 'yy-mm-dd'});
                                        $("#inputStartDate").datepicker({dateFormat: 'yy-mm-dd'});
                                        $("#inputEndDate").datepicker({dateFormat: 'yy-mm-dd'});

                                        $(window).ready(function() {
                                          APP.dom.normalizeInputValidity("<fmt:message key="pleasefill" bundle="${cons}" />");
                                          $(".help-msg").popover();
                                          fillEvent();
                                        });

                                        function clearEC() {
                                            $("#inputECPrice").val("");
                                            $("#inputECTech").val("");
                                            $("#inputECDate").val("");
                                        }

                                        function fillEvent() {
                                            $.getJSON("PCFilingApp?action=getContractJson&copyContractURL=" + encodeURIComponent(sessionStorage.copyContractURL), function(data)
                                            {
                                                if (data == null || data.length == 0) {
                                                    sessionStorage.clear();
                                                    window.location.href = "./";
                                                } else {
                                                    $("#contractName").html(data.title);
                                                    $("#inputTitle").val(data.title);
                                                    $("#inputDescription").html(data.description);
                                                    $("#inputTendersSealed").attr("checked", data.tendersSealed);
                                                    $.each(cpvCollection, function(key, value) {
                                                        if (value.indexOf(data.mainCPV) > -1)
                                                            $("#cpv1").val(value);
                                                        if (data.additionalCPV)
                                                            $.each(data.additionalCPV, function(k, v) {
                                                                if (value.indexOf(v) > -1) {
                                                                    $("#cpv" + (Number(k) + 2)).val(value);
                                                                }
                                                            });
                                                    });
                                                    $("#procurementMethod").val(data.procedureType);
                                                    $("#inputDeadline").val(data.deadline);
                                                    $("#inputExactPrice").val(data.price);
                                                    $("#estimatedPriceCurrency").val(data.currency);
                                                    $("#priceConfidential").attr("checked", data.confidential);
                                                    $("#inputStartDate").val(data.startDate);
                                                    $("#inputEndDate").val(data.estimatedEndDate);
                                                    $("#inputLocation").val(data.locationLabel);
                                                    $.each(locations, function(key, value) {
                                                        if (value.indexOf(data.locationNUTS + "#") > -1) {
                                                            $("#inputLocationNUTS").val(value);
                                                            return false;
                                                        }
                                                    });
                                                    $("#inputEventReference").val(data.eventReference);
                                                    $("#inputProjectID").val(data.projectID);

                                                    $("#inputFn").val(data.vcFN);
                                                    $("#inputEmail").val(data.vcEmail);
                                                    $("#inputPhone").val(data.vcPhone);
                                                    $("#inputPoint").val(data.vcNote);

                                                    if (data.criteria.LowestPrice)
                                                        $("#inputECPrice").val(data.criteria.LowestPrice);
                                                    if (data.criteria.TechnicalQuality)
                                                        $("#inputECTech").val(data.criteria.TechnicalQuality);
                                                    if (data.criteria.BestDate)
                                                        $("#inputECDate").val(data.criteria.BestDate);

                                                    $("#btn" + data.eventType).click();

                                                }
                                            });
                                        }
        </script>
    </body>
</html>
