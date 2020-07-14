package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.*
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.unwrap

// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class Initiator2(private val data: String) : FlowLogic<Unit>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call() {
        val start = System.currentTimeMillis()
        println()
        println("[INITIATOR2 START]")
        val b = toParty("O=PartyB,L=New York,C=US")
        val c = toParty("O=PartyC,L=New York,C=US")
        val d = toParty("O=PartyD,L=New York,C=US")
        val e = toParty("O=PartyE,L=New York,C=US")

        println("[INITIATE FLOW]")
        val sessionB = initiateFlow(b)
        val sessionC = initiateFlow(c)
        val sessionD = initiateFlow(d)
        val sessionE = initiateFlow(e)

        println("[SEND START]")
        sessionB.send(data)
        sessionC.send(data)
        sessionD.send(data)
        sessionE.send(data)
        println("[SEND END]")

        println("[INITIATOR2 END]")
        println()
        val end = System.currentTimeMillis()
        println("TIME2 : ${end-start} ms")
    }

    private fun toParty(name: String): Party {
        println(name)
        val x500Name = CordaX500Name.parse(name)
        return serviceHub.networkMapCache.getPeerByLegalName(x500Name) as Party
    }
}

@InitiatedBy(Initiator2::class)
class Responder2(val counterPartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        println()
        println("[RESPONDER2 START]")

        val receive = counterPartySession.receive<String>()
        receive.unwrap { data -> println(data) }

        println("[RESPONDER2 END]")
        println()
    }
}
