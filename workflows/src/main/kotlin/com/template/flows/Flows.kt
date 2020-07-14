package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.template.contracts.TemplateContract
import com.template.states.TemplateState
import net.corda.core.contracts.Command
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.unwrap

// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class Initiator(private val data: String) : FlowLogic<Unit>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call() {
        val start = System.currentTimeMillis()
        println()
        println("[INITIATOR START]")
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
        sendAll(data, setOf(sessionB, sessionC, sessionD, sessionE))
        println("[SEND END]")

        println("[INITIATOR END]")
        println()
        val end = System.currentTimeMillis()
        println("TIME : ${end-start} ms")
    }

    private fun toParty(name: String): Party {
        println(name)
        val x500Name = CordaX500Name.parse(name)
        return serviceHub.networkMapCache.getPeerByLegalName(x500Name) as Party
    }
}

@InitiatedBy(Initiator::class)
class Responder(val counterPartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        println()
        println("[RESPONDER START]")

        val list = receiveAll(String::class.java, listOf(counterPartySession))
        list[0].unwrap { data -> println(data) }

        println("[RESPONDER END]")
        println()
    }
}
