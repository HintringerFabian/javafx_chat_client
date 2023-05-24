# What to do to achive a communication between multiple clients?

- create two interfaces for the client and the server
- there will be a database for the messages on the server
    - should the client still have a database?
    - answer: no it will only hold the messages in memory as described in the TODO
- the server will have a list of clients
- when a message is sent from the client, it will be sent to the server, the server will save it in the database and send it to the other client
    - the client will receive the message and show it
- the client will have a thread that will listen for messages from the server
- the server will hold a list of clients which need to be notified when a message is received
