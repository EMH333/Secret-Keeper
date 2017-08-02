var currentSecret;


$(document).ready(function() {

  nextSecret(); //load a secret to it is all ready to go

  // process the form
  $('form').submit(function(event) {

    $('#submit-secret').prop('disabled', true);
    var counter = 7;
    $('#submit-secret').text(counter);
    var id = setInterval(function() {
      counter--;
      if (counter < 0) {
        $('#submit-secret').prop('disabled', false);
        $('#submit-secret').text("Submit");
        clearInterval(id);
      } else {
        $('#submit-secret').text(counter);
      }
    }, 1000)

    // get the form data
    // there are many ways to get this data using jQuery (you can use the class or id also)
    var formData = 'secret' + '=' + $('input[name=secret]').val();

    // process the form
    $.ajax({
        type: 'POST', // define the type of HTTP verb we want to use (POST for our form)
        url: 'add', // the url where we want to POST
        data: formData, // our data object
        dataType: 'text', // what type of data do we expect back from the server
        encode: true
      })
      // using the done promise callback
      .done(function(data) {

        // log data to the console so we can see
        console.log("Tried to insert secret, it was... " + data);
        //TODO: Never show direct response to user!!!
        alert(data);

        if (currentSecret.id == "null") { //if the user was previously not aloud to view secrets, auto refresh
          nextSecret();
        }

      });

    $('#Secret').val(''); //clear text box

    // stop the form from submitting the normal way and refreshing the page
    event.preventDefault();
  });

});

//gets a secret from the /random url
function nextSecret() {
  $('.hear-buttons').prop('disabled', true); //disable so you can't click multiple times

  $.ajax({
      type: 'GET', // define the type of HTTP verb we want to use (POST for our form)
      url: 'random', // the url where we want to POST
      data: "", // our data object
      dataType: 'json', // what type of data do we expect back from the server
      encode: true,
      error: function(xhr, exception) {
        if (xhr.status === 0)
          console.log('Error : ' + xhr.status + 'You are not connected.');
        else if (xhr.status == "201")
          console.log('Error : ' + xhr.status + '\nServer error.');
        else if (xhr.status == "404")
          console.log('Error : ' + xhr.status + '\nPage note found');
        else if (xhr.status == "500")
          console.log('Internal Server Error [500].');
        else if (exception === 'parsererror')
          console.log('Error : ' + xhr.status +
            '\nImpossible to parse result.');
        else if (exception === 'timeout')
          console.log('Error : ' + xhr.status + '\nRequest timeout.');
        else
          console.log('Error .\n' + xhr.responseText);
      }
    })
    // using the done promise callback
    .done(function(data) {
      var secretObject = JSON.parse(JSON.stringify(data));
      currentSecret = secretObject; //insures we have info we need to vote and do other things

      if ($('#hear-secret').text() == secretObject.secret && secretObject.id != "null") { //check to make sure secret is not a repeat
        nextSecret();
      } else {
        $('#hear-secret').text(secretObject.secret); //displays secret
        $('#hear-votes').text(secretObject.votes); //displays votes

        //disables button for set amount of seconds seconds
        $('.hear-buttons').prop('disabled', true);
        var counter = 2;
        $('#hear-next').text(counter);
        var id = setInterval(function() {
          counter--;
          if (counter < 0) {
            $('.hear-buttons').prop('disabled', false);
            $('#hear-next').text("next");
            clearInterval(id);
          } else {
            $('#hear-next').text(counter);
          }
        }, 1000)
      }
      // here we will handle errors and validation messages
    });

}

function upVote() {
  if ($('#hear-next').prop('disabled') == false) {
    console.log("Up Vote");
    $.ajax({
      type: 'POST', // define the type of HTTP verb we want to use
      url: 'vote', // the url where we want to POST
      data: {
        'id': currentSecret.id,
        'upvote': true
      },
      dataType: 'json', // what type of data do we expect back from the server
      encode: true
    });
    nextSecret();
  }
}

function downVote() {
  if ($('#hear-next').prop('disabled') == false) {
    console.log("Down Vote");
    $.ajax({
      type: 'POST', // define the type of HTTP verb we want to use
      url: 'vote', // the url where we want to POST
      data: {
        'id': currentSecret.id,
        'upvote': false
      },
      dataType: 'json', // what type of data do we expect back from the server
      encode: true
    });
    nextSecret();
  }
}
