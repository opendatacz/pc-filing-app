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
				<div class="span12">
          <div class="alert hide" id="userHelper">
            <button class="close"
              onclick="userHelper('off')"
              title="<fmt:message key="disableguide" bundle="${cons}" />" >×</button>
              <fmt:message key="viewtender.help" />
          </div>
          
          <div id="progressbar"></div>
					<div id="view" style="display:none">
						<h3><fmt:message key="viewtender.title" /> '<span style="display: inline;" id="tenderTitle"></span>'</h3>
						<hr>
						<div class="control-group">
							<h4><fmt:message key="createevent.basicinfo" /></h4>
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
							
							<hr>
							
							<div class="control-group">
								<h4><fmt:message key="viewtender.pricingtiming" /></h4>

								<label class="control-label" for="inputPrice"><fmt:message key="offeredprice" bundle="${cons}" /></label>
								<div class="controls">
									<span id="inputPrice"></span> <span id="inputCurrency"></span>
								</div>
							</div>

							<div class="control-group">
								<label class="control-label" for="inputStartDate"><fmt:message key="viewtender.startend" /></label>
								<div class="controls">
									<span id="inputStartDate"></span> - <span id="inputEndDate"></span>
								</div>
							</div>

							<hr>
							
							<h4 id="docsHeader"><fmt:message key="documents" bundle="${cons}" /></h4>

							<div class="control-group" style="display:none">								
								<label class="control-label" for="inputFileOffer"><fmt:message key="viewtender.doc.full" /></label>
								<div class="controls">
									<ul id="fileOffer" class="docList"></ul>

								</div>
							</div>

							<div class="control-group" style="display:none">
								<label class="control-label" for="inputFileTechSpecs"><fmt:message key="viewtender.doc.specs" /></label>
								<div class="controls">
									<ul id="fileTechSpecs" class="docList"></ul>

								</div>
							</div>

							<div class="control-group" style="display:none">
								<label class="control-label" for="inputFilePriceDelivery"><fmt:message key="viewtender.doc.price" /></label>
								<div class="controls">
									<ul id="filePriceDelivery" class="docList"></ul>

								</div>
							</div>

							<div class="control-group" style="display:none">
								<label class="control-label" for="inputFileRequested"><fmt:message key="viewtender.doc.forms" /></label>
								<div class="controls">
									<ul id="fileRequested" class="docList"></ul>

								</div>
							</div>

							<hr>
							
							<h4 id="attaHeader"><fmt:message key="attachments" bundle="${cons}" /></h4>

							<div class="control-group" style="display:none">
								
								<label class="control-label" for="inputFileCerts"><fmt:message key="viewtender.attachments.cert" /></label>
								<div class="controls">
									<ul id="fileCerts" class="docList"></ul>
								</div>
							</div>

							<div class="control-group" style="display:none">
								<label class="control-label" for="inputFileProfile"><fmt:message key="viewtender.attachments.profile" /></label>
								<div class="controls">
									<ul id="fileProfile" class="docList"></ul>
								</div>
							</div>

							<div class="control-group" style="display:none">
								<label class="control-label" for="inputFinStatements"><fmt:message key="viewtender.attachments.financial" /></label>
								<div class="controls">
									<ul id="fileFinStatements" class="docList"></ul>

								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<%@include file="WEB-INF/jspf/stats-buyer.jspf" %>
		</div>
	</div>

	<%@include file="WEB-INF/jspf/footer.jspf" %>

  <script src="js/application.js"></script>
	<script src="js/functions.js"></script>
	<script src="js/sessionstorage.1.4.js"></script>
	<script src="js/toolsBuyer.js"></script>
	<script src="js/script.js"></script>
	<script src="js/table.js"></script>

	<script type="text/javascript">
	  var tenderURL = APP.util.getParameterByName("uri") || sessionStorage.tenderURL; 
	  var title = sessionStorage.contractTitle;
	  var buyerURL = sessionStorage.buyerURL;
	  
	  $(window).ready(function() {		
      $('#tenderTitle').append(title);
      $('#buyerURL').attr('value', buyerURL);
      $('#tenderURL').attr('value', tenderURL);
      loadForm();
	  });
	  
	function loadForm() {
    $.getJSON("PCFilingApp", {
        action: "getTenderJson",
        editTenderURL: tenderURL
      }, function(data) {
					if (data == null || data.length == 0) {
						sessionStorage.clear();
						window.location.href = "./";
					} else {
						$("#supplier").html('<a onclick="showEntity(\''+data.supplier.entity+'\')" href="info-supplier.jsp">'+data.supplier.name+'</a>');
						$("#inputDescription").html(data.description);
						$("#inputCurrency").html(data.currency);
						$("#inputPrice").html(data.price);
						$("#inputStartDate").html(data.startDate);
						$("#inputEndDate").html(data.endDate);
						var docsEnabled = false;
						var attaEnabled = false;
						$.each(data.documents,function(){
							var item;
							switch ( this.docType ) {							
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
							
							item.append('<li id="doc-'+this.token+'"><a href="PCFilingApp?action=document&token='+this.token+'"><i class="icon-download"></i> '+this.fileName+'</a></li>');
							item.closest(".control-group").show();
						});		
					}
					
					$("#progressbar").hide();
					$("#view").fadeIn();
					
				});		
	}
	
	</script>
    </body>
</html>
