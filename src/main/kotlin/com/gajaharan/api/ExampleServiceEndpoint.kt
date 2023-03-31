package com.gajaharan.api

import com.gajaharan.*
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.*
import org.http4k.core.Body
import org.http4k.core.Method.POST
import org.http4k.core.Status.Companion.OK
import org.http4k.format.Jackson.auto


class ExampleServiceEndpoint {
    private val contract = "/echo" meta {
        summary = "echoes the name and message sent to the endpoint"
        receiving(messageRequestLens to MessageRequest("Gaj", "hello world"))
        returning(OK, messageResponseLens to MessageResponse("hello world Gaj"))
    } bindContract POST

    private fun handler(): HttpHandler = { request: Request ->
        messageRequestLens(request).let {
            Response(OK).with(messageResponseLens of MessageResponse("${it.message} ${it.name}"))
        }
    }

    operator fun invoke(): ContractRoute = contract to handler()

    companion object {
        val messageRequestLens = Body.auto<MessageRequest>().toLens()
        val messageResponseLens = Body.auto<MessageResponse>().toLens()
    }
}
