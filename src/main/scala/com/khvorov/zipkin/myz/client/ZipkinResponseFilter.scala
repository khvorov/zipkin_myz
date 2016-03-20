package com.khvorov.zipkin.myz.client

import javax.ws.rs.client.{ClientResponseContext, ClientRequestContext, ClientResponseFilter}
import javax.ws.rs.ext.Provider

import com.twitter.finagle.http.HttpTracing
import com.twitter.finagle.tracing._

/**
  * Created by sammy on 3/19/16.
  */
@Provider
class ZipkinResponseFilter(val name: String, val tracer: Tracer) extends ClientResponseFilter {
  override def filter(requestContext: ClientRequestContext, responseContext: ClientResponseContext): Unit = {
    var spanId = SpanId.fromString(requestContext.getHeaders().getFirst(HttpTracing.Header.SpanId).toString)

    spanId foreach { sid =>
      val getFirstHeader = (hdr: String) => requestContext.getHeaders().getFirst(hdr)

      val traceId = SpanId.fromString(getFirstHeader(HttpTracing.Header.TraceId).toString)

      val parentSpanId = getFirstHeader(HttpTracing.Header.ParentSpanId) match {
        case s: String => SpanId.fromString(s.toString)
        case _ => None
      }

      val sampled = getFirstHeader(HttpTracing.Header.Sampled) match {
        case s: String => s.toString.toBoolean
        case _ => true
      }

      val flags = Flags(getFirstHeader(HttpTracing.Header.Flags).toString.toLong)

      Trace.letId(TraceId(traceId, parentSpanId, sid, Option(sampled), flags)) {}
    }
  }
}
