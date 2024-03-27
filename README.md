#### Prerequisites:
- Java 17
- Gradle 8.4 (wrapper)

#### Tested setup:
- Server OS: `Windows 10`
- Client OS: `Windows 10`
- Telnet client: `Windows Telnet`, `Putty`*

*In case of Putty negotiation mode should be set to "Passive" in `Connection` -> `Telnet` -> `Telnet negotiation mode`
(Not found how to handle "Active" mode gracefully)

#### Few implementation notes:
- Task says only one thread can access file system, BUT users must receive results in parallel, 
which is technically not possible since only one thread gathers the results. =\
- Path in results are relative to the root path
- Task does not clearly specifies how application "accepts" parameters to the server, so `System.in` is used


## Task description:

The tasks must be completed in Java (not Scala). Maven/Gradle to choose from. Competently,
use the standard library rather than inventing a wheel (for example, for
concurrency).
1. Traversal of a tree to a given depth without using recursion.
   Write a console application that takes three parameters:
- path to the starting directory (rootPath)
- search depth - non-negative integer (depth)
- mask - string (mask)
  The application must find all elements of the file system tree located on
  depth depth from the root of the rootPath tree, which contain the string mask in their name.
  Requirements:
- The application must be implemented WITHOUT using recursion.
2. Modify the application from task 1 as follows:
- one thread performs the search
- another thread prints the results to the console as they appear.
3. Modify the application from task 1, 2 into a simple multi-user one
   telnet server:
   the application accepts two parameters:
- serverPort - the port that it will “listen to”
- path to the starting directory (rootPath)
  Search criteria (depth and mask) are set via the telnet client console
  (use standard programs for this: telnet, putty, ...)
  Requirements:
- all access to the file system must be made from a single thread
  Those. there is a thread on the server, from which and only from it access is made
  file system.
- "telnet server" must be multi-user + interactive
  if 4 clients access the server at the same time and each sets a “search query”, then
  results should come to clients in parallel, not sequentially,
  those. the user should not wait for the results to complete for everyone
  previous users.