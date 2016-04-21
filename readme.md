# dirac-sample [![GitHub license](https://img.shields.io/github/license/binaryage/dirac-sample.svg)](license.txt)

This project is an example of integration of [**Dirac DevTools**](https://github.com/binaryage/dirac) into a
Leiningen-based ClojureScript project.

![](https://dl.dropboxusercontent.com/u/559047/dirac-repl-01.png)

## Local setup

    git clone https://github.com/binaryage/dirac-sample.git
    cd dirac-sample

## Demo time!

### Installation

Launch latest [Chrome Canary](https://www.google.com/chrome/browser/canary.html) from command-line.
I recommend to run it with a dedicated user profile, because you will install there a helper [Chrome Extension](https://chrome.google.com/webstore/detail/dirac-devtools/kbkdngfljkchidcjpnfcgcokkbhlkogi).
Also you have to run it with [remote debugging](https://developer.chrome.com/devtools/docs/debugger-protocol) enabled on port 9222 (better make an alias of this command):

    /Applications/Google\ Chrome\ Canary.app/Contents/MacOS/Google\ Chrome\ Canary \
      --remote-debugging-port=9222 \
      --no-first-run \
      --user-data-dir=.test-dirac-chrome-profile

Now you can install the [Dirac DevTools Chrome extension](https://chrome.google.com/webstore/detail/dirac-devtools/kbkdngfljkchidcjpnfcgcokkbhlkogi).

After installation, should see a new extension icon next to your address bar <img src="https://dl.dropboxusercontent.com/u/559047/dirac-extension-icon.png" height="32">.

Now you can launch the demo project from terminal:

    lein demo

At this point you should have a demo website running at [http://localhost:9977](http://localhost:9977).

Please navigate there, do not open internal DevTools and click Dirac icon while on the `http://localhost:9977/demo.html` page.
It should open you a new window with Dirac DevTools app.
It will look almost the same as internal DevTools, but you can tell the difference at first glance: active tab highlight
will be green instead of blue (see the screenshots above).

Ok, now you can switch to Javascript Console in Dirac DevTools. Focus prompt field and press `PageUp` or `PageDown`.
This will switch prompt from Javascript to ClojureScript REPL.

You should see a red message on a green background: `Dirac Agent is not listening at ws://localhost:8231 (need help?).`

That's correct. Dirac REPL uses nREPL protocol so we have to provide it with some nREPL server.
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
    Dirac Agent v0.2.0
    Connected to nREPL server at nrepl://localhost:8230.
    Tunnel is accepting connections at ws://localhost:8231.

Last three lines ensure you that Dirac Agent was launched alongside your nREPL server. It connected to it and is accepting
DevTools connections on the websocket port 8231.

After your Dirac Agent is up your Dirac DevTools should eventually reconnect. If you don't want to wait for the countdown,
you may close Dirac window and open it again.

Connected? The red message should go away and you should see `cljs.user` indicating your
current namespace. REPL is ready for your input at this point. You can try:

    (+ 1 2)
    js/window
    (doc filter)
    (filter odd? (range 42))

If you see something very similar to the first screenshot at the top, you have Dirac running properly.

**Congratulations!**

### Hello, World!

Let's try to call `hello!` function from our namespace `dirac-sample.demo`.

    (dirac-sample.demo/hello! "World")

It worked `"Hello, World!"` was logged into the console (white background means that the logging output originated in Javascript land).
But we got a warning: `Use of undeclared Var dirac-sample.demo/hello! at line 1 <dirac repl>`.
This is expected because REPL environment has no idea that such function exists. It just blindly transpiled our command into Javascript.

As you probably know you have to first require (or eval) namespace in the REPL context to make it aware of namespace content.

    (require 'dirac-sample.demo)

Now you can call `(dirac-sample.demo/hello! "World!")` and it works without warnings.

But still you have to type fully qualified name because currently you are in `cljs.user` namespace. To switch you can use `in-ns` special function.

Let's try it:

    (in-ns)

You get an error `java.lang.IllegalArgumentException: Argument to in-ns must be a symbol.`. This is a Java exception from nREPL side.
 Execute `(doc in-ns)` to see the documentation for this special REPL function. It expects namespace name as the first argument.

    (in-ns 'dirac-sample.demo)
    (hello! "Dirac")

Should log `Hello, Dirac!` into the console without warnings.

### Breakpoints

You can also test evaluation of ClojureScript in the context of selected stack frame when paused on a breakpoint:

1. click the "demo a breakpoint" button on the page
2. debugger should pause on the `(js-debugger)` line in the breakpoint-demo function

Custom formatters should be presented as inlined values in the source code.
Also property names in the scope panel are sorted and displayed in a more friendly way.

Now hit ESC to bring up console drawer. Make sure you are switched to Dirac REPL and then enter:

    js/items

You should see actual value `(0 1 2)` of the `items` variable from local scope (formatted by custom formatters from cljs-devtools).
Same way as you would expect when evaluating a Javascript command in breakpoint context. Actually you can try it.
Hit "Page Up" to switch to Javascript console prompt (white background) and enter:

    items

This should return the same output.

And now return back to Dirac REPL by pressing "Page Up" again and enter:

    (str (map inc js/items))

You should get back a string `"(1 2 3)"`.

This is a proof that Dirac REPL can execute arbitrary ClojureScript code in the context of selected stack frame.