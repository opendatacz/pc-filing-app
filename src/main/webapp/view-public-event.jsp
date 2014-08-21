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

                    <div style="text-align: center;" id="progressbar">
                        <br> <img src="images/progressbar.gif" />
                    </div>

                    <div id="view" class="hide">
                        <h3><fmt:message key="viewevent.title" /> '<span id="contractName"></span>'</h3>

                        <hr>

                        <div class="control-group">
                            <h4><fmt:message key="viewevent.basicinfo" bundle="${supp}" /></h4>
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

                            <label class="control-label" for="actualEnd"><fmt:message key="viewevent.actualend" /></label>
                            <div class="controls">
                                <span id="actualEnd"></span>
                            </div>
                        </div>

                        <div class="control-group">			
                            <label class="control-label" for="actualPrice"><fmt:message key="actualprice" bundle="${cons}" /></label>
                            <div class="controls">
                                <span id="actualPrice"></span>
                            </div>
                        </div>

                        <div class="control-group">
                            <label class="control-label" for="inputDescription"><fmt:message key="description" bundle="${cons}" /></label>
                            <div class="controls">
                                <div id="inputDescription"></div>
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label" for="inputDescription"><fmt:message key="cpvcodes" bundle="${cons}" /></label>
                            <div class="controls">
                                <span id="cpv1"></span>
                            </div>
                        </div>
                        <div class="control-group">
                            <div class="controls">
                                <span id="cpv2" ></span>				
                            </div>
                        </div>
                        <div class="control-group">
                            <div class="controls">				
                                <span id="cpv3" ></span>
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


                        <hr>

                        <div class="control-group">
                            <h4><fmt:message key="viewevent.constraints" bundle="${supp}" /></h4>
                            <label class="control-label" for="inputDeadline"><fmt:message key="viewevent.deadlinetenders" bundle="${supp}" /></label>
                            <div class="controls">
                                <span id="inputDeadline"></span>
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label" for="inputExactPrice"><fmt:message key="estimatedprice" bundle="${cons}" /></label>
                            <div class="controls">
                                <span id="inputExactPrice"></span> <span id="estimatedPriceCurrency"></span> <span class="hide" id="priceConfidential" style="font-style: italic;">(<fmt:message key="viewevent.priceisconfidential" bundle="${supp}" />)</span>
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label" for="inputStartDate"><fmt:message key="viewevent.startend" bundle="${supp}" /></label>
                            <div class="controls">
                                <span id="inputStartDate"></span> - <span id="inputEndDate"></span>
                            </div>
                        </div>

                        <div class="control-group">
                            <h5><fmt:message key="viewevent.location" bundle="${supp}" /></h5>
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
                        <div class="control-group">

                        </div>

                        <div class="in hide" id="evaluationCriteria">
                            <div class="control-group">
                                <h5><fmt:message key="viewevent.evaluation" bundle="${supp}" /></h5>
                                <label class="control-label" for="inputECPrice"><fmt:message key="price" bundle="${cons}" /></label>
                                <div class="controls">
                                    <span id="controls" ></span><span> %</span>
                                </div>
                            </div>
                            <div class="control-group">
                                <label class="control-label" for="inputECTech"><fmt:message key="technicalspecification" bundle="${cons}" /></label>
                                <div class="controls">
                                    <span id="inputECTech" ></span><span> %</span>
                                </div>
                            </div>
                            <div class="control-group">
                                <label class="control-label" for="inputECDate"><fmt:message key="deliverydate" bundle="${cons}" /></label>
                                <div class="controls">
                                    <span id="inputECDate"></span><span> %</span>
                                </div>
                            </div>

                        </div>			
                    </div>
                </div>
            </div>
        </div>


        <%@include file="WEB-INF/jspf/footer.jspf" %> 
        <script src="js/cpv-codes-<c:out value="${pageContext.request.locale.language}" />.js"></script>
        <script src="js/locations.js"></script>    
        <script src="js/jquery-ui.js"></script>
        <script src="js/main.js"></script>    

        <script>
                                $("a").tooltip();
                                $("button").tooltip();
                                $("label").tooltip();

                                $(window).ready(function() {
                                    fillEvent();
                                });

                                function fillEvent() {
                                    var address;

                                    address = "PCFilingApp?action=getPublicContractJson&contractURL=" + encodeURIComponent(sessionStorage.contractURL)

                                    $.getJSON(address, function(d)
                                    {
                                        $("#progressbar").hide();

                                        if (d == null || d.data.length == 0) {
                                            sessionStorage.clear();
                                            window.location.href = "./";
                                        } else {
                                            var data = d.data;

                                            var contr = $('<a>').attr('href', 'info-buyer.jsp');
                                            contr.on('click', function() {
                                                showEntity(data.contractingAuthority.entity);
                                            });
                                            contr.append(data.contractingAuthority.name);
                                            $("#contractor").append(contr);

                                            if (data.tendersOpening) {
                                                $("#tendersOpened").html((new Date(data.tendersOpening)).toUTCString());
                                                $("#tendersOpened").closest('.control-group').show();
                                            }

                                            field = $("#inputDescription")
                                            if (data.description) {
                                                field.html(data.description);
                                            } else {
                                                field.closest(".control-group").addClass('hide');
                                            }

                                            $("#contractName").html(data.title);
                                            $("#inputTitle").html(data.title);
                                            $("#inputDescription").html(data.description);
                                            $.each(cpvCollection, function(key, value) {
                                                if (value.indexOf(data.mainCPV) > -1)
                                                    $("#cpv1").html(value);
                                                if (data.additionalCPV)
                                                    $.each(data.additionalCPV, function(k, v) {
                                                        if (value.indexOf(v) > -1) {
                                                            $("#cpv" + (Number(k) + 2)).html(value);
                                                        }
                                                    });
                                            });
                                            if (data.procedureType) {
                                                $("#pM" + data.procedureType).removeClass("hide");
                                            }
                                            $("#inputDeadline").html(data.deadline);

                                            field = $("#actualEnd")
                                            if (data.aend) {
                                                field.html(data.aend);
                                            } else {
                                                field.closest(".control-group").addClass('hide');
                                            }

                                            field = $("#actualPrice")
                                            if (data.aprice) {
                                                field.html(data.aprice + " " + data.acurrency);
                                            } else {
                                                field.closest(".control-group").addClass('hide');
                                            }


                                            field = $("#inputExactPrice");
                                            if (data.price) {
                                                field.html(data.price);
                                                $("#estimatedPriceCurrency").html(data.currency);
                                            } else {
                                                $("#priceConfidential").removeClass("hide");
                                            }

                                            $("#priceConfidential").attr("disabled", true);
                                            $("#inputStartDate").html(data.startDate);
                                            $("#inputEndDate").html(data.estimatedEndDate);

                                            $("#inputLocation").html(data.locationLabel);
                                            var NUTSe = false;
                                            $.each(locations, function(key, value) {
                                                if (value.indexOf(data.locationNUTS) > -1) {
                                                    $("#inputLocationNUTS").html(value);
                                                    NUTSe = true;
                                                    return false;
                                                }
                                            });
                                            if (!NUTSe) {
                                                $("#inputLocationNUTS").closest(".control-group").addClass('hide');
                                            }

                                            $("#inputEventReference").html(data.eventReference);
                                            $("#inputProjectID").html(data.projectID);

                                            $("#inputFn").html(data.vcFN);
                                            $("#inputEmail").html(data.vcEmail);
                                            $("#inputPhone").html(data.vcPhone);
                                            $("#inputPoint").html(data.vcNote);

                                            if (data.criteria.LowestPrice)
                                                $("#inputECPrice").html(data.criteria.LowestPrice);
                                            if (data.criteria.TechnicalQuality)
                                                $("#inputECTech").html(data.criteria.TechnicalQuality);
                                            if (data.criteria.BestDate)
                                                $("#inputECDate").html(data.criteria.BestDate);

                                            $("#view").fadeIn();
                                        }
                                    });
                                }
        </script>
    </body>
</html>
