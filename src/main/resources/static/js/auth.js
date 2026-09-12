/* =============================================================================
   The single sign-in / sign-up form.

   Two changes from the old register.js beyond styling:

   1. It no longer speaks through alert(). A modal browser dialog for "wrong
      password" is jarring, unstyleable, and on submit it blocks the redirect
      until the reader dismisses it. Feedback is rendered in the form.

   2. The endpoint is an absolute path. It used to POST to 'auth/submit'
      relative to the current URL, which only resolved correctly because the
      page happens to be served at /auth; the same script on /auth/ would have
      posted to /auth/auth/submit.
   ========================================================================= */
(function () {
  "use strict";

  var form = document.getElementById("auth-form");
  if (!form) { return; }

  var username = document.getElementById("username");
  var password = document.getElementById("password");
  var submit = document.getElementById("auth-submit");
  var note = document.getElementById("auth-note");
  var noteText = document.getElementById("auth-note-text");

  function say(message, kind) {
    if (!note || !noteText) { return; }
    noteText.textContent = message;
    note.className = "alert-k form-note is-shown alert-k--" + (kind || "danger");
    note.setAttribute("role", kind === "danger" ? "alert" : "status");
  }

  function quiet() {
    if (!note) { return; }
    note.className = "alert-k form-note";
    note.removeAttribute("role");
  }

  function busy(state) {
    if (!submit) { return; }
    submit.classList.toggle("is-busy", state);
    submit.disabled = state;
    submit.innerHTML = state
      ? '<i class="fas fa-circle-notch fa-spin"></i> Checking'
      : '<i class="fas fa-arrow-right-to-bracket"></i> Continue';
  }

  form.addEventListener("submit", async function (event) {
    event.preventDefault();
    quiet();

    var name = (username.value || "").trim();
    var pass = (password.value || "").trim();

    if (!name || !pass) {
      say("Enter both a username and a password.", "warn");
      (name ? password : username).focus();
      return;
    }

    busy(true);

    try {
      var response = await fetch("/auth/submit", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "same-origin",
        body: JSON.stringify({ username: name, password: pass })
      });

      // A proxy or an error page can answer with HTML, and response.json()
      // then throws a parse error that reads as a network fault. Read the
      // body once as text and decide afterwards.
      var raw = await response.text();
      var payload = {};
      try { payload = raw ? JSON.parse(raw) : {}; } catch (e) { payload = {}; }

      if (response.ok) {
        say(payload.message || "Signed in. Taking you in…", "warn");
        window.location.assign("/");
        return;
      }

      busy(false);
      say(payload.error || ("Sign-in failed (HTTP " + response.status + ")."), "danger");
      password.value = "";
      password.focus();
    } catch (error) {
      busy(false);
      say("Could not reach the server. Check your connection and try again.", "danger");
    }
  });

  // Typing after a rejection should clear the rejection.
  [username, password].forEach(function (field) {
    if (field) { field.addEventListener("input", quiet); }
  });
})();
