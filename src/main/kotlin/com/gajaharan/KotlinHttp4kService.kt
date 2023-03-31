package com.gajaharan

import com.gajaharan.api.ExampleServiceEndpoint
import com.gajaharan.api.ExampleServiceEndpoint.Companion.messageResponseLens
import org.http4k.contract.bind
import org.http4k.contract.contract
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.contract.security.NoSecurity
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.filter.OpenTelemetryMetrics
import org.http4k.filter.OpenTelemetryTracing
import org.http4k.filter.ServerFilters
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Undertow
import org.http4k.server.asServer

val app: HttpHandler = routes(
    "/ping" bind GET to {
        Response(OK).body("pong")
    },
    "/hello" bind GET to {
        Response(OK).with(messageResponseLens of MessageResponse("Hello World!"))
    },
    "/contract/api/v1" bind contract {
        renderer = OpenApi3(ApiInfo("kotlinhttp4kservice API", "v1.0"))
        descriptionPath = "/swagger.json"
        security = NoSecurity
        routes += listOf(
            ExampleServiceEndpoint()()
        )
    },

    "/opentelemetrymetrics" bind GET to {
        Response(OK).body("Example metrics route for kotlinhttp4kservice")
    }
)

fun main() {
    val printingApp: HttpHandler = PrintRequest()
        .then(ServerFilters.OpenTelemetryTracing())
        .then(ServerFilters.OpenTelemetryMetrics.RequestCounter())
        .then(ServerFilters.OpenTelemetryMetrics.RequestTimer()).then(app)

    val server = printingApp.asServer(Undertow(8080)).start()

    println("Server started on " + server.port())
}
