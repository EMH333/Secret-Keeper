/**
 * Handles the sign in button press.
 */
function signIn() {
  var email = document.getElementById('email').value;
  var password = document.getElementById('password').value;
  if (email.length < 4) {
    alert('Please enter an email address.');
    return;
  }
  if (password.length < 4) {
    alert('Please enter a password.');
    return;
  }
  // Sign in with email and pass.
  // [START authwithemail]
  firebase.auth().signInWithEmailAndPassword(email, password).catch(function(error) {
    // Handle Errors here.
    var errorCode = error.code;
    var errorMessage = error.message;
    // [START_EXCLUDE]
    if (errorCode === 'auth/wrong-password') {
      alert('Wrong password.');
    } else {
      alert(errorMessage);
    }
    console.log(error);
    document.getElementById('sign-in').disabled = false;
  });
  document.getElementById('sign-in').disabled = true;
}

function signInButton() {
  if (firebase.auth().currentUser) {
    firebase.auth().signOut();
    popUp("You are signed out");
  } else {
    var modal = $("#sign-in-popup");
    modal.modal('show');
  }
}
/**
 * Handles the sign up button press.
 */
function handleSignUp() {
  var email = document.getElementById('email').value;
  var password = document.getElementById('password').value;
  if (email.length < 4) { //TODO 16-Oct-2017 insure email and password are actually secure and valid
    alert('Please enter an email address.');
    return;
  }
  if (password.length < 4) {
    alert('Please enter a password.');
    return;
  }
  // Sign in with email and pass.
  var wasError = false;
  firebase.auth().createUserWithEmailAndPassword(email, password).catch(function(error) {
    // Handle Errors here.
    var errorCode = error.code;
    var errorMessage = error.message;
    // [START_EXCLUDE]
    if (errorCode == 'auth/weak-password') {
      alert('The password is too weak.');
    } else {
      alert(errorMessage);
    }
    console.log(error);
    wasError = true;
  });

  //if the current user is valid and has not verifyed email, then send verification email.
  if (firebase.auth().currentUser != null && wasError == false && firebase.auth().currentUser.emailVerified == false) {
    sendEmailVerification();
  }

}
/**
 * Sends an email verification to the user.
 */
function sendEmailVerification() {
  // [START sendemailverification]
  firebase.auth().currentUser.sendEmailVerification().then(function() {
    // Email Verification sent!
    // [START_EXCLUDE]
    alert('Email Verification Sent!');
    // [END_EXCLUDE]
  });
  // [END sendemailverification]
}

function sendPasswordReset() {
  var email = document.getElementById('email').value;
  // [START sendpasswordemail]
  firebase.auth().sendPasswordResetEmail(email).then(function() {
    // Password Reset Email Sent!
    // [START_EXCLUDE]
    alert('Password Reset Email Sent!');
    // [END_EXCLUDE]
  }).catch(function(error) {
    // Handle Errors here.
    var errorCode = error.code;
    var errorMessage = error.message;
    // [START_EXCLUDE]
    if (errorCode == 'auth/invalid-email') {
      alert(errorMessage);
    } else if (errorCode == 'auth/user-not-found') {
      alert(errorMessage);
    }
    console.log(error);
    // [END_EXCLUDE]
  });
  // [END sendpasswordemail];
}
/**
 * initApp handles setting up UI event listeners and registering Firebase auth listeners:
 *  - firebase.auth().onAuthStateChanged: This listener is called when the user is signed in or
 *    out, and that is where we update the UI.
 */
function initApp() {
  // Listening for auth state changes.
  firebase.auth().onAuthStateChanged(function(user) {
    if (user) {
      // User is signed in.
      var displayName = user.displayName;
      var email = user.email;
      var emailVerified = user.emailVerified;
      var photoURL = user.photoURL;
      var isAnonymous = user.isAnonymous;
      var uid = user.uid;
      var providerData = user.providerData;
      document.getElementById('sign-in-button').textContent = 'Sign out';
      document.getElementById('sign-in').textContent = 'Sign out';

      //hide the sign in popup
      var modal = $("#sign-in-popup");
      modal.modal('hide');
    } else {
      // User is signed out.
      // [START_EXCLUDE]
      document.getElementById('sign-in-button').textContent = 'Sign in';
      document.getElementById('sign-in').textContent = 'Sign in';
      // [END_EXCLUDE]
    }
    // [START_EXCLUDE silent]
    document.getElementById('sign-in').disabled = false;
    // [END_EXCLUDE]
  });
  // [END authstatelistener]
  document.getElementById('sign-in').addEventListener('click', signIn, false);
  document.getElementById('sign-up').addEventListener('click', handleSignUp, false);
  document.getElementById('password-reset').addEventListener('click', sendPasswordReset, false);
  document.getElementById('sign-in-button').addEventListener('click', signInButton, false)
}
window.onload = function() {
  initApp();
};
