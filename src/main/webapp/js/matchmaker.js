var MATCHMAKER = {
  matchesPerPage: 10,
  decodeEntities: (function() {
    // Stolen from <http://stackoverflow.com/a/9609450/385505>
    //
    // this prevents any overhead from creating the object each time
    var element = document.createElement("div");

    function decodeHTMLEntities (str) {
      if (str && typeof str === "string") {
        // strip script/html tags
        str = str.replace(/<script[^>]*>([\S\s]*?)<\/script>/gmi, "");
        str = str.replace(/<\/?\w(?:[^"'>]|"[^"]*"|'[^']*')*>/gmi, "");
        element.innerHTML = str;
        str = element.textContent;
        element.textContent = "";
      }
      return str;
    }
    return decodeHTMLEntities;
  })(),
  displayMatches: function (config, matches) {
    var matchResultsBody = config.dom.$matchResultsTable.children("tbody"),
      template = $(config.dom.templateId).html();
    matchResultsBody.delegate(".contract-link", "click", function (e) {
      sessionStorage.contractURL = $(e.target).data("contract-uri");
      sessionStorage.public = true;
    });

    Mustache.parse(template);

    config.dom.$progressbar.hide();
    //$("#additionalMetrics").show()
    config.dom.$matchResultsTable.fadeIn("slow");

    var matchesCount = matches.length;
    if (matchesCount !== 0 && !MATCHMAKER.isObjectEmpty(matches[0])) {
      config.dom.$pagination.twbsPagination({
        href: "#page={{number}}",
        totalPages: Math.ceil(matchesCount / MATCHMAKER.matchesPerPage),
        first: config.labels.first, 
        prev: config.labels.prev, 
        last: config.labels.last, 
        visiblePages: 3,
        onPageClick: function (event, page) {
          matchResultsBody.html(Mustache.render(template, {
            matches: jQuery.map(matches, function (match, i) {
              if (MATCHMAKER.inPage(i, page)) {
                match.rank = i + 1;
                match.label = MATCHMAKER.decodeEntities(match.label);
                if (match.publicationDate) {
                  match.publicationDate = APP.util.dateFormat(match.publicationDate);
                }
                if (match.tenderDeadline) {
                  match.tenderDeadline = APP.util.dateFormat(match.tenderDeadline);
                }
                return match;
              }
            })
          }));
          config.dom.$matchResultsTable.trigger("change");
        }
      });
    } else {
      var parent = config.dom.$matchResultsTable.parent();
      config.dom.$matchResultsTable.remove();
      config.dom.$pagination.parent().remove();
      parent.append("<p>"
        + config.labels.notfound
        + "</p>");
    }
  },
  getMatches: function (config) {
    // Attach event listeners to matchmaker's results
    if (config.labels.truncate) {
      config.dom.$matchResultsTable.on("change", function (e) {
        $(".truncate").jTruncate(jQuery.extend({length: 140}, config.labels.truncate));
      });
    }
    config.dom.$matchResultsTable.delegate(".notificationButton", "click", function (e) {
      var $target = $(e.target),
        email = prompt($target.data("email-prompt"), $target.data("email"));
      if (email) {
        $.getJSON("InvitationComponent", {
            action: "send",
            contract: sessionStorage.contractTitle,
            contractURL: sessionStorage.contractURL,
            email: email,
            name: sessionStorage.username
          },
          function (data) {
            if (data.sent) {
              alert(data.message);
            }
          });
      }
    });

    $.getJSON("Matchmaker",
      {private: true, 
       source: config.source,
       target: config.target,
       uri: config.resourceUri},
      function (matches) {
        return MATCHMAKER.displayMatches(config, matches);
      });
  },
  inPage: function (index, page) {
    return (index >= (page * MATCHMAKER.matchesPerPage) - MATCHMAKER.matchesPerPage)
           &&
           (index < (page * MATCHMAKER.matchesPerPage));
  },
  isObjectEmpty: function (object) {
    // Stolen from <http://stackoverflow.com/a/23785342/385505>
    if ("object" !== typeof object) {
        throw new Error("Object must be specified.");
    }

    if ("undefined" !== Object.keys) {
        // Using ECMAScript 5 feature.
        return (0 === Object.keys(object).length);
    } else {
        // Using legacy compatibility mode.
        for (var key in object) {
            if (object.hasOwnProperty(key)) {
                return false;
            }
        }
        return true;
    }
  }
};
