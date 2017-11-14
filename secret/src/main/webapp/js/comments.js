$(document).ready(function() {
  // process the form
  $('#comment-form').submit(function(event) {
    // get the form data
    // there are many ways to get this data using jQuery (you can use the class or id also)
    var formData = 'comment' + '=' + $('input[name=comment]').val() + '&secretID' + '=' + getCurrentSecret().id;

    // process the form
    $.ajax({
        type: 'POST', // define the type of HTTP verb we want to use (POST for our form)
        url: 'addcomment', // the url where we want to POST
        data: {
          'comment': $('input[name=comment]').val(),
          'secretID': getCurrentSecret().id,
          'idToken': token
        }, // our data object
        dataType: 'text', // what type of data do we expect back from the server
        encode: true,
      })
      // using the done promise callback
      .done(function(data) {

        // log data to the console so we can see
        console.log("Tried to add comment, it was... " + data);
        //TODO: Never show direct response to user!!!
        popUp(data);
        nextSecret();
      });

    $('#comment').val(''); //clear text box

    // stop the form from submitting the normal way and refreshing the page
    event.preventDefault();
  });
});

function displayComments(comments) {
  if (comments == null) {
    console.log("No comments!");
    var commentsList = $('#comment-list');
    return;
  } else {
    console.log(comments.length + " comments!");
  }
  $('#comment-list').empty(); //remove all previous comments from list

  //loop through comments and display them
  for (var i = 0; i < comments.length; i++) {
    var comment = comments[i];
    comment = JSON.parse(comment);
    loadComment(comment);
  }

  sortComments(); //sort the comments when we are done loading new ones TODO need to implement so that we get the top comments first automaticly
}

function loadComment(data) {
  var commentHtml = createComment(data);
  var commentEl = $(commentHtml);
  commentEl.hide();
  var commentsList = $('#comment-list');
  commentsList.addClass('has-comments');
  commentsList.prepend(commentEl);
  if (data.isCreatorComment) { //only show star if the comment if from the creator
    $('#' + data.id).find('p').find('span').css('visibility', 'visible');
  }
  commentEl.slideDown();
}

function createComment(data) {
  var html = '' +
    '<li class="list-group-item" value="' + data.votes + '"><article id="' + data.id + '">' +
    '<p class="entry-content comment-info">' + '<span class="glyphicon glyphicon-star" style="visibility: hidden;" aria-hidden="true"></span>' + data.comment + '</p>' +
    '</article></li>';
  return html;
}

function sortComments() {
  $("#comment-list").html(

    $("#comment-list").children("li").sort(function(a, b) {

      return $(a).val() - $(b).val();

    }) // End Sort

  ); // End HTML
}
