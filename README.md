# corda_sendall_sample
Corda 4.5 の新機能であるFlowLogic.sendAllのサンプルです

* Flows.kt
  * sendAll を使用するサンプルです
* Flows2.kt
  * send を使用するサンプルです。比較用にご使用ください

※デフォルトでは、ノードA~E＋Notaryの計6台を起動するため、マシンのスペックによっては、上手く起動しないことがあります。  
　その際は、build.gradle、各Flowを修正してください
