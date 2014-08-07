var APP = {
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
    }
  }
};
