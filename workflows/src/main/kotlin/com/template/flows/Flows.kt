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
        println("[INITIATOR] START")
        val b = toParty("O=PartyB,L=New York,C=US")
        val c = toParty("O=PartyC,L=New York,C=US")
        val d = toParty("O=PartyD,L=New York,C=US")
        val e = toParty("O=PartyE,L=New York,C=US")

        println("[INITIATE FLOW] START")
        val sessionB = initiateFlow(b)
        val sessionC = initiateFlow(c)
        val sessionD = initiateFlow(d)
        val sessionE = initiateFlow(e)
        println("[INITIATE FLOW] END")

        println("[SEND] START")
        sendAll(data, setOf(sessionB, sessionC, sessionD, sessionE))
        println("[SEND] END")

        println("[TRANSACTION BUILDER] START")
        val notary = serviceHub.networkMapCache.notaryIdentities[0]
        val output = TemplateState(data, listOf(ourIdentity, b, c))
        val cmd = Command(TemplateContract.Commands.Action(), listOf(ourIdentity.owningKey, b.owningKey, c.owningKey))
        val txBuilder = TransactionBuilder(notary)
                .addCommand(cmd)
                .addOutputState(output)
        println("[TRANSACTION BUILDER] END")

        println("[TRANSACTION VERIFY] START")
        txBuilder.verify(serviceHub)
        println("[TRANSACTION VERIFY] END")

        println("[SIGN INITIAL TRANSACTION] START")
        val signedTx = serviceHub.signInitialTransaction(txBuilder)
        println("[SIGN INITIAL TRANSACTION] END")

        println("[COLLECT SIGN] START")
        val fullySignedTx = subFlow(CollectSignaturesFlow(signedTx, setOf(sessionB, sessionC)))
        println("[COLLECT SIGN] END")

        println("[FINALITY FLOW] START")
        subFlow(FinalityFlow(fullySignedTx, setOf(sessionB, sessionC)))
        println("[FINALITY FLOW] END")

        println("[INITIATOR] END")
        println()
        val end = System.currentTimeMillis()
        println("TIME : ${end - start} ms")
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
        println("[RESPONDER] START")

        println("[RECEIVE ALL] START")
        val list = receiveAll(String::class.java, listOf(counterPartySession))
        list[0].unwrap { data -> println(data) }
        println("[RECEIVE ALL] END")

        println("----------------------------")

        println("[SIGN TRANSACTION] START")
        val signTransactionFlow = object : SignTransactionFlow(counterPartySession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {}
        }
        val txId = subFlow(signTransactionFlow).id
        subFlow(ReceiveFinalityFlow(counterPartySession, expectedTxId = txId))
        println("[SIGN TRANSACTION] $txId")
        println("[SIGN TRANSACTION] END")
        
        println("[RESPONDER] END")
        println()
    }
}
