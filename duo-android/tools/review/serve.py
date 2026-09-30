#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Start the pronunciation review page on this computer.

    python serve.py

then open the printed address. On Windows you can also just double-click
`serve.bat`.

Why a server at all? The page plays the app's own .ogg files, which live
outside this folder, and browsers refuse to load media from a `file://` page
for security reasons. Serving one directory over localhost fixes that.

Nothing is sent anywhere. The server binds to 127.0.0.1, which only this
computer can reach, makes no outbound connection, and serves bytes that are
already on disk.
"""
from __future__ import annotations

import http.server
import os
import socketserver
import sys
import threading
import webbrowser

HERE = os.path.dirname(os.path.abspath(__file__))
APP = os.path.abspath(os.path.join(HERE, "..", ".."))   # duo-android/
PORT = 8765
PAGE = "/tools/review/index.html"



class Handler(http.server.SimpleHTTPRequestHandler):
    """Static files from duo-android/, plus an index that points at the page."""

    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=APP, **kwargs)

    def log_message(self, fmt, *args):
        # One tidy line per request. The only line worth silencing is a 404 for
        # a reference recording, because the page asks about all of them and
        # most clips will not have one for a long time.
        if "/references/" in self.path and '"' in fmt and " 404 " in fmt:
            return
        sys.stderr.write("  %s\n" % (fmt % args))


    def send_head(self):
        if self.path in ("/", "/index.html", "/review", "/review/"):
            self.send_response(302)
            self.send_header("Location", PAGE)
            self.send_header("Content-Length", "0")
            self.end_headers()
            return None
        return super().send_head()


class Server(socketserver.ThreadingTCPServer):
    # Deliberately NOT reusing the address. On Windows SO_REUSEADDR lets a
    # second copy bind a port that is already serving, which would leave the
    # reviewer talking to whichever process won, at random. Refusing to bind
    # is what makes the port search below move on to the next port.
    allow_reuse_address = False
    daemon_threads = True


def main():
    if not os.path.isfile(os.path.join(HERE, "data", "clips.json")):
        raise SystemExit(
            "data/clips.json is missing.  Build it first:\n"
            "    python build_index.py")

    for port in range(PORT, PORT + 20):
        try:
            httpd = Server(("127.0.0.1", port), Handler)
            break
        except OSError:
            continue
    else:
        raise SystemExit("no free port between %d and %d" % (PORT, PORT + 19))

    url = "http://127.0.0.1:%d%s" % (port, PAGE)
    print("Pronunciation review")
    print("  " + url)
    print()
    print("  Ctrl+C to stop.")
    print()
    threading.Timer(0.7, lambda: webbrowser.open(url)).start()
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        print("\nstopped.")
    finally:
        httpd.server_close()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
