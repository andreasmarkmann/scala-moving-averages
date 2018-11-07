# Running Averages in Scala

Run with `sbt test`.

Class that computes and returns running averages for the last 10, 30, and 60 seconds every time a number is sent
to it. Millisecond granularity permits storing only 60000 values even if millions of events are sent per minute.

Example of test-driven design.
