package com.gajaharan

import com.gajaharan.api.ExampleServiceEndpoint.Companion.messageRequestLens
import com.gajaharan.api.ExampleServiceEndpoint.Companion.messageResponseLens
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class KotlinHttp4kServiceTest {

    @Test
    fun `Ping test`() {
        assertEquals(app(Request(Method.GET, "/ping")), Response(OK).body("pong"))
    }

    @Test
    fun `Hello test`() {
        val resp: Response = app(Request(Method.GET, "/hello"))
        assertEquals("Hello World!", messageResponseLens.extract(resp).message)
    }

    @Test
    fun `echo test`() {
        val resp = app(
            Request(Method.POST, "/contract/api/v1/echo")
                .with(messageRequestLens of MessageRequest("Gaj", "Hello"))
        )
        assertEquals("Hello Gaj", messageResponseLens.extract(resp).message)
    }
}
