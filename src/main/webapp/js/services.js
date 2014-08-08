var services = {
  payolaView: (function () {
    var defaultViewPlugin = "cz_payola_web_client_views_graph_visual_techniques_tree_TreeTechnique";
    $("#matchResultsTable").delegate(".payolaView", "click", function (e) {
      var $target = $(e.target),
        businessEntityUri = $target.closest("tr").find(".uri").attr("href"),
        endpoint = $target.data("endpoint"),
        payolaUrl = endpoint 
          + "#browseUri="
          + encodeURIComponent(businessEntityUri)
          + "&viewPlugin="
          + defaultViewPlugin;
      window.open(payolaUrl);
    });
  })(),
  predictBidders: function (contractUri, callback) {
    $.getJSON("NumberOfBidders",
      {uri: contractUri},
      callback);
  },
  predictContractPrice: function (cpv, duration, callback) {
    $.getJSON("PriceEstimation",
      {cpv: cpv,
       dur: duration},
      callback);
  }
};
