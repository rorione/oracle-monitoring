### О приложении
Выводит информацию о последнем блоке, а также изменения курсов валют оракулов.

### Конфигурация
В application.conf производится настройка параметров монитора:
```
app {
  node_url = // websocket адрес ноды
  oracles {
    // пары из идентификатора оракула и адреса контракта-аггрегатора
    eth_usd = "0x37bC7498f4FF12C19678ee8fE19d713b87F6a9e6"
    link_eth = "0xbba12740DE905707251525477bAD74985DeC46D2"
    usdt_eth = "0x7De0d6fce0C128395C488cb4Df667cdbfb35d7DE"
    ...
  }
}
```
Все контракты агрегаторов должны иметь идентичный abi (а именно abi ивента AnswerUpdated).
Идентификатор оракула пишется в лог.

### Сборка
Для сборки необходимо выполнить
* `./gradlew distZip` на Linux или MacOS
* `gradlew distZip` на Windows

### Запуск
Извлечь содержимое архива и вызвать скрипт в папке `bin`.

### Пример вывода в лог
```
2022-11-01 21:55:24.465 [WebSocketConnectReadThread-14] INFO  [BLOCK] - ts: 0x63616b9b, number: 0xf243ef, hash: 0x154ee05c98b87ce4342d978d77ff079a8cf073bb9e6c6f02823da7a5f05e499d
2022-11-01 21:55:37.229 [WebSocketConnectReadThread-14] INFO  [BLOCK] - ts: 0x63616ba7, number: 0xf243f0, hash: 0xc0e0ed36880f4e746b02ea396a1bafecd36977cf3f05221349888d230d0fcf63
2022-11-01 21:55:48.693 [WebSocketConnectReadThread-14] INFO  [BLOCK] - ts: 0x63616bb3, number: 0xf243f1, hash: 0xbc6c343ba6a9351c7caff08c6b3966f6be54806fcd89ad0cfebeb8d7fccb39da
2022-11-01 21:55:48.771 [WebSocketConnectReadThread-14] INFO  [eth_usd] - ts: 1667328947, current: 158151000000, roundId: 36091
2022-11-01 21:56:01.248 [WebSocketConnectReadThread-14] INFO  [BLOCK] - ts: 0x63616bbf, number: 0xf243f2, hash: 0x274ddbbb3faa6a7d8d9f284ee7dfb530790192c84069509db6ccd4b79d043112
2022-11-01 21:56:13.283 [WebSocketConnectReadThread-14] INFO  [BLOCK] - ts: 0x63616bcb, number: 0xf243f3, hash: 0x2ce06182e176089a8680c1dc73667349cb495c862044b3b90d771141e58df73b
2022-11-01 21:56:25.175 [WebSocketConnectReadThread-14] INFO  [BLOCK] - ts: 0x63616bd7, number: 0xf243f4, hash: 0x47eaac3ec5cf3cdcc4c74c5cacbe8aae8a31e3aa01c5c465410b80b4b2d9e5ef
2022-11-01 21:56:37.054 [WebSocketConnectReadThread-14] INFO  [BLOCK] - ts: 0x63616be3, number: 0xf243f5, hash: 0xcb7f5d5703096512c241fc7e02a53b459ea36aaacaf3a5a451e24da81f9956b5
2022-11-01 21:56:48.174 [WebSocketConnectReadThread-14] INFO  [BLOCK] - ts: 0x63616bef, number: 0xf243f6, hash: 0xab31e2cfdc9c579480fb15d859d078f7dee8f30dc0e22a0be666c6bcd5c2e505
2022-11-01 21:57:00.939 [WebSocketConnectReadThread-14] INFO  [BLOCK] - ts: 0x63616bfb, number: 0xf243f7, hash: 0x9296fe40864fe2fff13574f249a163ef9f4187c68bc4e8b2b35fb59b67566c67
```
