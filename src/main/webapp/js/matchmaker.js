var MATCHMAKER = {
  matchesPerPage: 10,
  inPage: function (index, page) {
            return (index >= (page * MATCHMAKER.matchesPerPage) - MATCHMAKER.matchesPerPage)
                    &&
                   (index < (page * MATCHMAKER.matchesPerPage));
          }
};
