var APP = {
  autocomplete: {
    cpvSorter: function (items) {
      var countZeros = function (cpv) {
        var zeros = cpv.match(/^\d+/)[0].match(/0/g);
        return zeros ? zeros.length : 0;
      };
      var cpvCompare = function (cpv1, cpv2) {
        return countZeros(cpv2) - countZeros(cpv1); 
      };

      var beginsWith = [],
        caseInsensitive = [],
        queryLowerCase = this.query.toLowerCase(),
        item;

      while (item = items.shift()) {
        var trimmed = item.toLowerCase().replace(/^[\d\-#]+/, "");
        if (trimmed.indexOf(queryLowerCase) === 0) {
          beginsWith.push(item);
        } else {
          caseInsensitive.push(item);
        }
      }

      beginsWith.sort(cpvCompare);
      caseInsensitive.sort(cpvCompare);
      return beginsWith.concat(caseInsensitive);
    }
  },
  dom: {
    dismissablePopover: function (e) {
      // Stolen from <http://stackoverflow.com/a/14857326/385505>
      $("[data-toggle='popover']").each(function () {
        //the 'is' for buttons that trigger popups
        //the 'has' for icons within a button that triggers a popup
        if (!$(this).is(e.target)
            &&
            $(this).has(e.target).length === 0
            &&
            $(".popover").has(e.target).length === 0) {
              $(this).popover("hide");
        }
    });
    },
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
