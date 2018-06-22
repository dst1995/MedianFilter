package com.company.csp.client;

import com.company.ImageByte;
import com.company.csp.params.ConnectionConstant;
import com.company.Stopwatch;
import com.company.csp.params.MedianReturn;
import com.company.csp.params.MedianSend;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQObjectMessage;
import javax.imageio.ImageIO;
import javax.jms.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FilterClient {

    public static Stopwatch stopwatch = new Stopwatch();
    public static ImageByte converter = new ImageByte();

    public static void main(String[] args) throws Throwable{
        System.setProperty("org.apache.activemq.SERIALIZABLE_PACKAGES","*");

        String photo = (args[0] != null) ? args[0] : "yen110.jpg";
        int threads = (args[1] != null) ? Integer.parseInt(args[1]) : 10;

        File f = new File("./photos/noisy/" + photo);        //Input Photo File
        BufferedImage mainImageOriginal = ImageIO.read(f);

        stopwatch.start();
        BufferedImage mainImageAltered = alterImage(mainImageOriginal);

        BufferedImage[] subImages = new BufferedImage[threads];
        BufferedImage[] filteredSubImages = cutImages(mainImageAltered, threads);


        try {

            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ConnectionConstant.MQ_URL);
            connectionFactory.setTrustAllPackages(true);

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();
            //Session variables

            System.out.println();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination in = session.createQueue(ConnectionConstant.MEDIAN_IN);
            Destination out = session.createQueue(ConnectionConstant.MEDIAN_OUT);


            MessageConsumer consumer = session.createConsumer(out);
            MessageProducer producer = session.createProducer(in);

            System.out.println("sending subImages");
            for(int i = 0; i < threads; i++) {

                // Creating an ObjectMessage
                byte[] subImageBytes = converter.imageToByte(subImages[i]);
                MedianSend msgSend = new MedianSend(i ,subImageBytes);
                ObjectMessage objMessage = session.createObjectMessage(msgSend);

                producer.send(objMessage);
            }

            System.out.println("waiting for response");
            // Wait for the returning message
            int k = 0;
            while( k++ < threads) {

                Message message = consumer.receive();
                if (message instanceof ActiveMQObjectMessage) {
                    ObjectMessage objMessage = (ObjectMessage) message;
                    MedianReturn receivedMsg = (MedianReturn) objMessage.getObject();

                    BufferedImage receivedSubImage = converter.byteToImage(receivedMsg.getSubImage());
                    filteredSubImages[receivedMsg.getId()] = receivedSubImage;

                    System.out.println("received: " + receivedMsg.toString());

                } else {
                    throw new IllegalArgumentException("Unexpected message " + message);
                }

            }

            System.out.println("putting all subImages together");
            int heightCurr = 0;
            BufferedImage concatImage = new BufferedImage(mainImageOriginal.getWidth(), mainImageOriginal.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = concatImage.createGraphics();
            for (int j = 0; j < filteredSubImages.length; j++) {
                BufferedImage si = filteredSubImages[j];
                g2d.drawImage(filteredSubImages[j], 0, heightCurr, null);
                heightCurr += filteredSubImages[j].getHeight();
            }

            System.out.println("finished filter for photo " + photo + "\n" +
                    "It took " + stopwatch.getDuration() + " microSeconds to finish with " + threads + " threads");

            // Clean up
            g2d.dispose();
            session.close();
            connection.close();
        }
        catch (JMSException e) {
            throw new RuntimeException(e);
        }

    }

    /** example
     * Transforming the JSON body to object
     * param text JSON
     * @return GregorySeriesReturn object
     * @throws IOException
     */

    public static BufferedImage alterImage(BufferedImage original) throws IOException{

        GraphicsConfiguration config =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage altered = config.createCompatibleImage(
                original.getWidth(),
                original.getHeight() + 2);

        Graphics2D g2 = altered.createGraphics();
        //add white line top
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, altered.getWidth(), 1);
        //put in original image
        g2.drawImage(original, 0, 1, null);
        //add white line bottom
        g2.fillRect(0, altered.getHeight()-1, altered.getWidth(), 1);
        g2.dispose();

        return altered;
    }


    public static BufferedImage[] cutImages(BufferedImage img, int threads) {
        int subHeight = img.getHeight() / threads;

        BufferedImage[] filteredSubImages = new BufferedImage[threads];
        for(int i = 0; i < threads; i++) {
            int startHeight = subHeight * i + 1;
            BufferedImage subImg = img.getSubimage(0,startHeight - 1, img.getWidth(),subHeight + 2);
            filteredSubImages[i] = subImg;
        }
        return filteredSubImages;
    }

}
