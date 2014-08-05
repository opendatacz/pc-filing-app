var services = {
  payolaView : (function () {
    $("#matchResultsTable").delegate(".payolaView", "click", function (e) {
      var $target = $(e.target),
        businessEntityUri = $target.closest("tr").find(".uri").attr("href"),
        endpoint = $target.data("endpoint"),
        payolaUrl = endpoint 
          + "#browseUri="
          + businessEntityUri
          + "&viewPlugin=cz_payola_web_client_views_graph_sigma_GraphSigmaPluginView";
      window.open(payolaUrl);
    });
  })(),
  predictBidders : function (contractUri) {
    $.getJSON("NumberOfBidders",
      {uri: contractUri},
      function (data) {
        alert("Predicting: " + data["prediction"]);
      });
  }
};
