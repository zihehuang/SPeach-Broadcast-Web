Broadcast Webserver for SPeach
==============================
This is the webserver that broadcasts a transcript and receives additions and edits in the SPeach system.

Prerequisites
-------------
* [Play Framework](http://www.playframework.com/documentation/2.2.1/Installing) (which requires [JDK6+](http://www.oracle.com/technetwork/java/javase/downloads/index.html))
* [Bower](http://bower.io) (which requires [node.js](http://nodejs.org/))

To get things running
---------------------
1. Get web dependencies by going to `/public/` and executing `bower install`.
2. Execute `play run` in the top SPeach-Broadcast-Web directory.
3. Open up browser to `localhost:9000`.
4. If you are configuring your phone for additions, make sure that the ip in the android code is set to the ip of your computer, at port 9000.