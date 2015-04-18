# lawl-challenge-analysis
data importer and analysis engine in scala leveraging akka.io

## Components
This project comprises of three primary components that form a pipeline.

- Importer
- Analysis Engine
- Journaler

### Importer
Responsible for file watching and pushing work into the pipeline. You can also apply back pressure to this component
in the event later staging of processing is not able to keep up with data rate.

### Analysis Engine
This is the largest subsystem of the application. It has two parts. Both of which are held together and ran in parallel
form using akka.io.
#### Analyzers
These are pure functions that accept json input and outputs the result of the analysis.
#### Accumulators
Accepts the output of analyzers and accumulate them. It also defines a function to serialize the data back into json.
A generic accumulator is defined that accepts a
[Monoid[T]](http://docs.typelevel.org/api/scalaz/nightly/index.html#scalaz.Monoid) and
[Format[T]](https://www.playframework.com/documentation/2.2.x/api/scala/index.html#play.api.libs.json.Format)
to accumulate and serialize respectively.

### Journaler
The results of the analysis engine gets serialized to disk along with moving the files it processed to an archive. If
the application restarts for any reason it will be capable of restoring where it last left off from the journaler.

##Performance
Early versions yielded a processing rate of about 4 files per second. After several revisions this climbed up over 30
per second. On a macbook pro running i7, solid state drives and 16GB of RAM it processed 95515 files in 35 minutes
on average. Additional gains could be made by:

- Using jackson streaming API to write analysis results to disk
- Running akka in cluster mode backed by distributed file system such as HDFS.
- Hooked up to a live data stream and with akka.io scala in an elastic manner to handle any
load.
- [LMAX](https://lmax-exchange.github.io/disruptor/) was another avenue I thought about however the patterns of 
processing lend itself much better in a distributed environment.