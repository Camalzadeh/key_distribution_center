/* =============================================================================
   Shared behaviour: theme switch, navigation rail, copy buttons.

   Replaces index.js, which assumed both #sidebarToggle and #themeToggle
   existed on every page and threw on the ones where they did not — the auth
   page had to carry its own duplicate copy of the theme logic for exactly
   that reason. Everything here is guarded, so one file serves every page.
   ========================================================================= */
(function () {
  "use strict";

  var root = document.documentElement;
  var STORE_THEME = "kdc.theme";
  var STORE_RAIL = "kdc.rail";

  /* --- storage ------------------------------------------------------------
     Private windows and blocked site data make localStorage throw on access
     rather than return null, and a theme toggle is not worth a broken page.  */
  function read(key) {
    try { return window.localStorage.getItem(key); } catch (e) { return null; }
  }
  function write(key, value) {
    try { window.localStorage.setItem(key, value); } catch (e) { /* ignore */ }
  }

  /* --- theme --------------------------------------------------------------
     The attribute itself is set by a small inline script in the document head
     so the first paint is already correct; this only handles changes.        */
  function currentTheme() {
    return root.getAttribute("data-bs-theme") === "dark" ? "dark" : "light";
  }

  function applyTheme(theme) {
    root.setAttribute("data-bs-theme", theme);
    var dark = theme === "dark";
    var buttons = document.querySelectorAll("[data-theme-toggle]");
    for (var i = 0; i < buttons.length; i++) {
      var btn = buttons[i];
      var icon = btn.querySelector("i");
      if (icon) { icon.className = dark ? "fas fa-sun" : "fas fa-moon"; }
      btn.setAttribute("aria-label", dark ? "Switch to light theme" : "Switch to dark theme");
      btn.setAttribute("title", dark ? "Light theme" : "Dark theme");
    }
  }

  applyTheme(currentTheme());

  document.addEventListener("click", function (event) {
    var btn = event.target.closest("[data-theme-toggle]");
    if (!btn) { return; }
    var next = currentTheme() === "dark" ? "light" : "dark";
    applyTheme(next);
    write(STORE_THEME, next);
  });

  /* --- navigation rail ---------------------------------------------------- */
  var rail = document.getElementById("rail");
  var scrim = document.getElementById("scrim");

  function isDrawerWidth() {
    return window.matchMedia("(max-width: 991.98px)").matches;
  }

  function closeDrawer() {
    if (!rail) { return; }
    rail.classList.remove("is-open");
    if (scrim) { scrim.classList.remove("is-shown"); }
  }

  if (rail) {
    // The collapsed state persists across pages; without that, collapsing the
    // rail would silently undo itself on the next navigation.
    if (read(STORE_RAIL) === "collapsed") { rail.classList.add("is-collapsed"); }

    // One button, two jobs, decided by width: collapse the rail on a wide
    // screen, open it as a drawer on a narrow one.
    //
    // There used to be two buttons, and the collapse one lived inside the rail
    // — where the collapsed state hid it. The rail could be collapsed once and
    // never reopened, because the only control for it was behind the thing it
    // had just closed.
    var navToggle = document.getElementById("navToggle");

    function announce() {
      if (!navToggle) { return; }
      var shown = isDrawerWidth()
        ? rail.classList.contains("is-open")
        : !rail.classList.contains("is-collapsed");
      navToggle.setAttribute("aria-expanded", shown ? "true" : "false");
    }

    if (navToggle) {
      navToggle.addEventListener("click", function () {
        if (isDrawerWidth()) {
          var open = rail.classList.toggle("is-open");
          if (scrim) { scrim.classList.toggle("is-shown", open); }
        } else {
          var collapsed = rail.classList.toggle("is-collapsed");
          write(STORE_RAIL, collapsed ? "collapsed" : "open");
        }
        announce();
      });
      announce();
    }

    if (scrim) { scrim.addEventListener("click", function () { closeDrawer(); announce(); }); }

    document.addEventListener("keydown", function (event) {
      if (event.key === "Escape") { closeDrawer(); announce(); }
    });

    // Dragging the window from phone to desktop width leaves the drawer
    // class set, which on desktop reads as a rail stuck in place.
    window.addEventListener("resize", function () {
      if (!isDrawerWidth()) { closeDrawer(); }
      announce();
    });
  }

  /* --- copy buttons -------------------------------------------------------
     The old inline handler walked the DOM by hand
     (this.parentElement.previousElementSibling.value) and broke as soon as a
     wrapper was added around the field. The target is named explicitly.      */
  function flash(button, message) {
    var original = button.getAttribute("data-label") || button.textContent.trim();
    button.classList.add("is-done");
    button.innerHTML = '<i class="fas fa-check"></i> ' + message;
    window.setTimeout(function () {
      button.classList.remove("is-done");
      button.innerHTML = '<i class="fas fa-copy"></i> ' + original;
    }, 1600);
  }

  document.addEventListener("click", function (event) {
    var button = event.target.closest("[data-copy]");
    if (!button) { return; }

    var selector = button.getAttribute("data-copy");
    var source = selector ? document.querySelector(selector) : null;
    if (!source) { return; }

    var text = "value" in source ? source.value : source.textContent;
    text = (text || "").trim();
    if (!text) { return; }

    // navigator.clipboard needs a secure context; over plain HTTP it is
    // undefined, and a copy button that does nothing is worse than one that
    // selects the text for the reader to copy themselves.
    if (navigator.clipboard && window.isSecureContext) {
      navigator.clipboard.writeText(text).then(
        function () { flash(button, "Copied"); },
        function () { select(source, button); }
      );
    } else {
      select(source, button);
    }
  });

  function select(source, button) {
    try {
      if (typeof source.select === "function") {
        source.focus();
        source.select();
      } else {
        var range = document.createRange();
        range.selectNodeContents(source);
        var selection = window.getSelection();
        selection.removeAllRanges();
        selection.addRange(range);
      }
      flash(button, "Selected");
    } catch (e) { /* nothing sensible left to do */ }
  }

  /* --- cipher forms -------------------------------------------------------
     Two things follow from the chosen algorithm, and both used to be handled
     by a separate inline script on each of the three cipher pages:

     1. Block mode. Only Caesar and Vigenere run through a block mode here;
        Playfair and Rail Fence rewrite the whole text, so the select is
        disabled for them. The three inline copies had already drifted apart
        in which algorithms they considered block-capable.

     2. Key shape. Key.giveKey picks a key type FROM the algorithm:
        Caesar and Rail Fence demand an integer, Playfair demands letters
        only, Vigenere takes anything. Give a Caesar cipher the placeholder's
        own suggestion of "MYSECRETKEY" and it throws — and the old pages
        rendered nothing at all in that case, so the form simply came back
        blank. Saying it up front is cheaper than explaining the exception.  */
  var forms = document.querySelectorAll("[data-cipher-form]");
  for (var f = 0; f < forms.length; f++) {
    (function (form) {
      var algo = form.querySelector("[data-cipher-algorithm]");
      if (!algo) { return; }

      var mode = form.querySelector("[data-cipher-mode]");
      var modeHint = form.querySelector("[data-cipher-mode-hint]");
      var keyField = form.querySelector("[data-cipher-key]");
      var keyHint = form.querySelector("[data-cipher-key-hint]");

      var blockCapable = mode
        ? (mode.getAttribute("data-block-capable") || "").split(/[,\s]+/).filter(Boolean)
        : [];

      function sync() {
        var option = algo.options[algo.selectedIndex];

        if (mode) {
          var supported = blockCapable.indexOf(algo.value) !== -1;
          mode.disabled = !supported;
          if (!supported) { mode.value = mode.getAttribute("data-none-value") || "NONE"; }
          if (modeHint) { modeHint.hidden = supported; }
        }

        if (option && keyHint) {
          keyHint.textContent = option.getAttribute("data-key-hint") || "";
        }
        if (option && keyField) {
          keyField.setAttribute("placeholder", option.getAttribute("data-key-example") || "");
          // A numeric keypad on a phone for the two ciphers that need a
          // number, and a plain one otherwise.
          keyField.setAttribute("inputmode", option.getAttribute("data-key-numeric") ? "numeric" : "text");
        }
      }

      algo.addEventListener("change", sync);
      sync();
    })(forms[f]);
  }
})();
