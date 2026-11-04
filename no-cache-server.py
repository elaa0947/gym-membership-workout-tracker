#!/usr/bin/env python3
import http.server
import socketserver
import sys
import os

PORT = int(sys.argv[1]) if len(sys.argv) > 1 else 5500
DIRECTORY = os.path.dirname(os.path.abspath(__file__))

class NoCacheHTTPRequestHandler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=DIRECTORY, **kwargs)

    def end_headers(self):
        self.send_header('Cache-Control', 'no-store, no-cache, must-revalidate, max-age=0')
        self.send_header('Pragma', 'no-cache')
        self.send_header('Expires', '0')
        self.send_header('Access-Control-Allow-Origin', '*')
        super().end_headers()

    def do_OPTIONS(self):
        self.send_response(200)
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS, PUT, DELETE')
        self.send_header('Access-Control-Allow-Headers', 'X-Requested-With, Content-Type, Authorization, X-Member-Id')
        self.end_headers()

class ThreadingTCPServer(socketserver.ThreadingMixIn, socketserver.TCPServer):
    allow_reuse_address = True

if __name__ == '__main__':
    with ThreadingTCPServer(("", PORT), NoCacheHTTPRequestHandler) as httpd:
        print(f"Serving {DIRECTORY} on port {PORT} with zero-cache headers...")
        httpd.serve_forever()
