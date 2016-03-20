package com.khvorov.zipkin.myz

import java.util.concurrent.Callable

import com.twitter.finagle.stats.DefaultStatsReceiver
import com.twitter.finagle.tracing.Trace
import com.twitter.finagle.zipkin.thrift.ZipkinTracer

/**
  * Created by sammy on 3/20/16.
  */
object Zipkin {
  lazy val tracer = ZipkinTracer.mk(host = "localhost", port = 9410, DefaultStatsReceiver, 1)

  def invoke[R](service: String, method: String, callable: Callable[R]): R = Trace {

  }
}
