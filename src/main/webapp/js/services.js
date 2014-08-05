var services = {
  predictBidders : function (contractUri) {
    $.getJSON("NumberOfBidders",
      {uri: contractUri},
      function (data) {
        alert("Predicting: " + data["prediction"]);
      });
  }
};
