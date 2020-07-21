# corda_sendall_sample
Corda 4.5 の新機能であるFlowLogic.sendAllのサンプルです

* Flows.kt
  * sendAll を使用するサンプルです
* Flows2.kt
  * send を使用するサンプルです。比較用にご使用ください

※デフォルトでは、ノードA~E＋Notaryの計6台を起動するため、マシンのスペックによっては、上手く起動しないことがあります。  
　その際は、build.gradle、各Flowを修正してください

## How to use?
使い方です。  
PartyAから、他のノードに対して送る。というベタ書きなので、PartyAで実行してください。

* ビルド
  * `gradlew.bat deployNodes`
* ノード起動
  * `.\build\nodes\runnodes`
* SendAll（PartyAで実行）
  * `start Initiator data : "sendAll"`
* Send（PartyAで実行）
  * `start Initiator2 data : "send"`

## 実行結果
SendAll、Sendをそれぞれ10回実施した結果です。  
ノードは上記の通り、ードA~E＋Notaryの計6台。  
CLIでコマンドをポチポチと叩きました。  

