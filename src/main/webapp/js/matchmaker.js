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
              
    Mustache.parse(template);

    config.dom.$progressbar.hide();
    //$("#additionalMetrics").show()
    config.dom.$matchResultsTable.fadeIn("slow");

    var matchesCount = matches.length;
    if (matchesCount !== 0) {
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
        email = $target.data("email"),
        contractTitle = sessionStorage.contractTitle,
        subjectLine = $target.data("subject"),
        bodyTemplate = $target.data("template"),
        link = Mustache.render("mailto:{{email}}?subject={{subject}}&body={{body}}",
          {email: email,
           subject: encodeURIComponent(Mustache.render(
                         "{{subject}} \"{{contractTitle}}\" | PC Filing App",
                         {subject: subjectLine,
                          contractTitle: contractTitle})),
           body: encodeURIComponent(bodyTemplate + " " + sessionStorage.contractURL)
          });
      
      window.location.href = link;
    });

    $.getJSON("Matchmaker",
      {private: config.private,
       source: config.source,
       target: config.target,
       uri: config.contractUri},
      function (matches) {
        console.log(matches);
        return MATCHMAKER.displayMatches(config, matches);
      });
  },
  inPage: function (index, page) {
    return (index >= (page * MATCHMAKER.matchesPerPage) - MATCHMAKER.matchesPerPage)
           &&
           (index < (page * MATCHMAKER.matchesPerPage));
  }
};
