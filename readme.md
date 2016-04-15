# dirac-sample [![GitHub license](https://img.shields.io/github/license/binaryage/dirac-sample.svg)](license.txt)

This project is an example of integration of [**Dirac DevTools**](https://github.com/binaryage/dirac) into a
Leiningen-based ClojureScript project.

![](https://dl.dropboxusercontent.com/u/559047/dirac-repl-01.png)

## Local setup

    git clone https://github.com/binaryage/dirac-sample.git
    cd dirac-sample

## The demo time!

Launch latest [Chrome Canary](https://www.google.com/chrome/browser/canary.html) from command-line.
I will want you to install [Dirac Chrome Extension](https://chrome.google.com/webstore/detail/dirac-devtools/kbkdngfljkchidcjpnfcgcokkbhlkogi) there,
so I recommend to run it with a dedicated user profile.

    mkdir .test-dirac-chrome-profile

Also you have to run it with [remote debugging](https://developer.chrome.com/devtools/docs/debugger-protocol)
enabled on port 9222 (better make an alias of this command):

    /Applications/Google\ Chrome\ Canary.app/Contents/MacOS/Google\ Chrome\ Canary \
      --remote-debugging-port=9222 \
      --no-first-run \
      --user-data-dir=.test-dirac-chrome-profile

Now you can install the [Dirac DevTools Chrome extension](https://chrome.google.com/webstore/detail/dirac-devtools/kbkdngfljkchidcjpnfcgcokkbhlkogi).
After installation, should see a new extension icon next to your address bar ![Dirac extension icon](https://dl.dropboxusercontent.com/u/559047/dirac-extension-icon.png).

    lein demo

At this point you should have a demo website running at [http://localhost:9977](http://localhost:9977).

Please navigate there, do not open internal DevTools and click Dirac icon while on the `localhost:9977` page.
It should open you a new window with Dirac DevTools app.
It will look almost the same as internal DevTools, but you can tell the difference at first glance: active tab highlight
will be green instead of blue (see the screenshots above).

Ok, now you can switch to Javascript Console in Dirac DevTools. Focus prompt field and press `PageUp` or `PageDown`.
This will switch prompt from Javascript to ClojureScript REPL. (Note you might need to refresh the page once to see
custom formatters - DevTools do not render them first time for some reason).

You should see a red message on a green background: `Dirac Agent is disconnected. Check your nREPL tunnel at ws://localhost:8231.`

That's correct. Dirac REPL uses nREPL protocol, so we have to provide it with some nREPL server.
Luckily enough leiningen offers nREPL server by simply running (in a new terminal session):

    lein repl

The terminal should print something similar to this:

    Compiling ClojureScript...
    nREPL server started on port 8230 on host 127.0.0.1 - nrepl://127.0.0.1:8230
    REPL-y 0.3.7, nREPL 0.2.12
    Clojure 1.8.0
    Java HotSpot(TM) 64-Bit Server VM 1.8.0_60-b27
        Docs: (doc function-name-here)
              (find-doc "part-of-name-here")
      Source: (source function-name-here)
     Javadoc: (javadoc java-object-or-class-here)
        Exit: Control+D or (exit) or (quit)
     Results: Stored in vars *1, *2, *3, an exception in *e

    user=>
    Dirac Agent v0.1.4
    Connected to nREPL server at nrepl://localhost:8230.
    Tunnel is accepting connections at ws://localhost:8231.

Last three lines ensure you that Dirac Agent was launched alongside your nREPL server. It connected to it and is accepting
DevTools connections on the websocket port 8231.

After your Dirac Agent is up your Dirac DevTools should eventually reconnect. Or close Dirac window and open it again if
you don't want to wait for reconnection countdown.

Connected? The red message should go away and you should see `cljs.user` on green-ish background indicating your
current namespace. REPL is ready for your input at this point. You can try:

    (+ 1 2)
    js/window
    (doc filter)
    (filter odd? (range 42))
    (in-ns)
    (in-ns 'my.ns)

If you see something very similar to the first screenshot at the top, you have Dirac running properly.

**Congratulations!**

---

TODO: outdated - review this!

You can also test evaluation of ClojureScript in the context of selected stack frame when paused on a breakpoint:

1. set breakpoint to line 40 of `more.cljs` (on Mac, you can use use CMD+P to quickly open a file at source panel)
2. click the "breakpoint test" button on the page
3. debugger should pause on the line (similar to the second screenshot at the top)

You could notice that custom formatters are presented everywhere including inlined values in the source code.
Also property names in the scope panel are sorted and displayed in a more friendly way.

Now hit ESC to bring up console drawer. Make sure you are switched to Dirac REPL and then enter:

    js/rng

You should see actual value of `rng` variable from local scope (formatted by custom formatters from cljs-devtools).
Same way as you would expect when evaluating a Javascript command. Actually you can try it.
Hit "Page Up" to switch to Javascript console prompt and enter:

    rng

This should return the same value.

And now return back to Dirac REPL by pressing "Page Up" again and enter:

    (take 3 js/rng)

This is a proof that Dirac REPL can execute arbitrary ClojureScript code in the context of selected stack frame.