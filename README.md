# twitter-buy-hooks

A consumer for Twitter Buy button webhooks.

#### Requirements
* [Java JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* Scala 2.11.6 (`brew install scala`)
* sbt 0.13.8 (`brew install sbt`)
* MySQL (`brew install mysql`), schema is located in `schema.sql`

#### Getting Started
```sh
# Invoke sbt from the project directory
$ sbt

# Build codebase (prepend with ~ to watch source tree and rebuild automatically when files change)
> compile

# Run hooks server
> re-start

# Stop hooks server
> re-stop