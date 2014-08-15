var APP = {
  dom: {
    normalizeInputValidity: function (invalidMsg) {
      $("input").on("invalid", function (e) {
        e.target.setCustomValidity("");
        if (!e.target.validity.valid) {
          e.target.setCustomValidity(invalidMsg);
        }
      }).on("input", function (e) {
        e.target.setCustomValidity("");
      });
    }
  },
  util: {
    dateFormat: function (date) {
      // If `date` is valid, then format it using long form,
      // else return the date unformatted.
      var formattedDate;
      try {
        formattedDate = dateFormat(date, dateFormat.masks.longDate);
      } catch (e) {
        formattedDate = date;
      }
      return formattedDate;
    },
    getParameterByName: function (name) {
      name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
      var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
          results = regex.exec(location.search);
      return results == null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
    }
  }
};
