var MATCHMAKER = {
  matchesPerPage: 10,
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
                return match;
              }
            })
          }));
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
    $.getJSON("Matchmaker",
      {source: config.source,
       target: config.target,
       uri: config.contractUri},
      function (matches) {
        return MATCHMAKER.displayMatches(config, matches);
      });
  },
  inPage: function (index, page) {
    return (index >= (page * MATCHMAKER.matchesPerPage) - MATCHMAKER.matchesPerPage)
           &&
           (index < (page * MATCHMAKER.matchesPerPage));
  }
};
