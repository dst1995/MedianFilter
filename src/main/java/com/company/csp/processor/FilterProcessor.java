package com.company.csp.processor;

import com.company.ImageByte;
import com.company.csp.params.ConnectionConstant;
import com.company.csp.params.MedianReturn;
import com.company.csp.params.MedianSend;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQObjectMessage;

import javax.jms.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class FilterProcessor {

    public static ImageByte converter = new ImageByte();

    public static void main(String[] args) {

//        System.setProperty("org.apache.activemq.SERIALIZABLE_PACKAGES","*");

        try {

            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ConnectionConstant.MQ_URL);
            connectionFactory.setTrustAllPackages(true);

            // Create a ConnectionConstant
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination in = session.createQueue(ConnectionConstant.MEDIAN_IN);
            Destination out = session.createQueue(ConnectionConstant.MEDIAN_OUT);

            // Create a MessageConsumer from the Session to the Topic or Queue
            MessageConsumer consumer = session.createConsumer(in);
            MessageProducer producer = session.createProducer(out);

            ObjectMapper mapper = new ObjectMapper();

            while(true) {
                // Wait for a message
                Message message = consumer.receive();
                System.out.println("received subImage to process");
                if (message instanceof ActiveMQObjectMessage) {
                    ObjectMessage objMessage = (ObjectMessage) message;
                    MedianSend receivedMsg = (MedianSend) objMessage.getObject();

                    ThreadManager tm = new ThreadManager(session, producer);
                    BufferedImage receivedSubImage = converter.byteToImage(receivedMsg.getSubImage());
                    tm.createThread(receivedMsg.getId(), receivedSubImage);

                } else {
                    throw new IllegalArgumentException("Unexpected message " + message);
                }
            }

        } catch (JMSException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    static class ThreadManager implements FilterListener {
        private Session session;
        private MessageProducer producer;
        private Integer id;
        private FilterThread thread;

        public ThreadManager(Session session, MessageProducer prod) {
            this.session = session;
            this.producer = prod;
        }

        public void createThread(int id, BufferedImage img) {
            this.id = id;
            this.thread = new FilterThread(img);
            thread.addListener(this);
            thread.start();
        }

        @Override
        public void filterFinished()  {
            try{
                byte[] subImageBytes = converter.imageToByte(this.thread.getFilteredSubImage());
                MedianReturn msgReturn = new MedianReturn(this.id, subImageBytes);

                ObjectMessage objMessage = session.createObjectMessage(msgReturn);
                this.producer.send(objMessage);
                System.out.println("message sent: " + msgReturn.toString());
            } catch(JMSException | IOException e) {
                System.out.println(e);
            }
        }

    }

}