var MATCHMAKER = {
  matchesPerPage: 10,
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
                  match.publicationDate = MATCHMAKER.dateFormat(match.publicationDate);
                }
                if (match.tenderDeadline) {
                  match.tenderDeadline = MATCHMAKER.dateFormat(match.tenderDeadline);
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
        contract = sessionStorage.contractURL,
        email = prompt($target.data("email-prompt"), $target.data("email")),
        contractTitle = sessionStorage.contractTitle,
        subjectLine = $target.data("subject"),
        bodyTemplate = $target.data("template"),
        link = Mustache.render("mailto:{{email}}?subject={{subject}}&body={{body}}",
          {email: email,
           subject: encodeURIComponent(Mustache.render(
                         "{{subject}} \"{{contractTitle}}\" | PC Filing App",
                         {subject: subjectLine,
                          contractTitle: contractTitle})),
           body: encodeURIComponent(bodyTemplate + " " + contract)
          });
      if (email) {
        $.getJSON("InvitationComponent",
          {action: "send",
           contract: sessionStorage.contractTitle,
           contractURL: contract,
           email: email,
           name: sessionStorage.username},
           function (data) {
             window.location.href = link;
           });
      }
    });

    $.getJSON("Matchmaker",
      {private: true, // MATCHMAKER.getParameterByName("private") === "true" ? true : false,
       source: config.source,
       target: config.target,
       uri: config.resourceUri},
      function (matches) {
        return MATCHMAKER.displayMatches(config, matches);
      });
  },
  getParameterByName: function (name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results == null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
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
