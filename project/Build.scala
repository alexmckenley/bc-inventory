import sbt._ 
import Keys._
import spray.revolver.RevolverPlugin.Revolver

object TwitterBuyHooksBuild extends Build { 
	val akkaV 	= "2.4-M1"
	val sprayV 	= "1.3.3"
	val akkaStreamV = "1.0-RC2"

	val extraResolvers = Seq( 
		"Spray Repo" at "http://repo.spray.io",
		"Spray Nightlies repo" at "http://nightlies.spray.io",
		"Typesafe Snapshots" at "http://repo.akka.io/snapshots/",
		"Sonotype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
		"softprops-maven" at "http://dl.bintray.com/content/softprops/maven"
	)

	val dependencies = Seq(
		"com.github.nscala-time" 	%% 	 "nscala-time" 		% "1.8.0",
		"com.typesafe.akka"   		%%   "akka-actor"    	% akkaV,
		"com.typesafe.akka"   		%%   "akka-testkit"  	% akkaV,
		"com.typesafe.akka"   		%%   "akka-slf4j"    	% akkaV,
		"org.slf4j"      			% 	 "jcl-over-slf4j"  	% "1.7.5",
		"ch.qos.logback" 			% 	 "logback-classic"  % "1.0.13",
		"io.spray"            		%%   "spray-can"     	% sprayV,
		"io.spray"            		%%   "spray-routing" 	% sprayV,
		"io.spray"            		%%   "spray-testkit" 	% sprayV,
		"io.spray"            		%%   "spray-json"    	% "1.3.1",
		"io.spray"            		%%   "spray-client"    	% sprayV,
		"org.scalatest" 			%%	 "scalatest" 			% "2.2.4" % "test"
	)

	lazy val commonSettings = Seq(
		organization := "com.bigcommerce", 
		version := "0.1.0", 
		scalaVersion := "2.11.6",
		libraryDependencies ++= dependencies,
		resolvers ++= extraResolvers
	)

	lazy val root = Project(id = "twitter-buy-hooks", base = file("."))
		.settings(commonSettings: _*)
		.settings(Revolver.settings: _*)
}