package com.khvorov.zipkin.myz.client

import javax.ws.rs.client.{ClientRequestContext, ClientRequestFilter}
import javax.ws.rs.ext.Provider
import com.twitter.finagle.http.HttpTracing
import com.twitter.finagle.tracing.{Annotation, Trace, Tracer}

@Provider
class ZipkinRequestFilter(val name: String, val tracer: Tracer) extends ClientRequestFilter {
  override def filter(requestContext: ClientRequestContext): Unit = {
    Trace.letTracerAndNextId(tracer) {
      requestContext.getHeaders().add(HttpTracing.Header.TraceId, Trace.id.traceId.toString)
      requestContext.getHeaders().add(HttpTracing.Header.SpanId, Trace.id.spanId.toString)

      Trace.id._parentId foreach { id =>
        requestContext.getHeaders().add(HttpTracing.Header.ParentSpanId, id.toString)
      }

      Trace.id.sampled foreach { sampled =>
        requestContext.getHeaders().add(HttpTracing.Header.Sampled, sampled.toString)
      }

      requestContext.getHeaders().add(HttpTracing.Header.Flags, Trace.id.flags.toLong.toString)

      if (Trace.isActivelyTracing) {
        Trace.recordRpcname(name, requestContext.getMethod)
        Trace.recordBinary("http.uri", requestContext.getUri().toString)
        Trace.record(Annotation.ClientSend())
      }
    }
  }
}