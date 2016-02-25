# Auction Management Using JMS Technology

Per il corretto funzionamento del progetto effettuare le seguenti operazioni:

1. creare una risorsa jms di tipo TopicConnectionFactory con nome jndi "jms/TopicConnectionFactory";

2. creare un topic con nome jndi "jms/Topic";

3. creare un topic con nome jndi "jms/Topic2";

4. inserire i quattro progetti Client, Manager, ReplicaManager e TransactionManager_Bully in altrettante Enterprise Application (al fine di poter importare la libreria Middelware_library);

5. per configurare i singoli elementi inserire gli argomenti dalle proprietà della singola Enterprise Application (o passarli dalla linea di comando).


Created by Messina Marco + Sinatra Luigi + Ventura Daniela.