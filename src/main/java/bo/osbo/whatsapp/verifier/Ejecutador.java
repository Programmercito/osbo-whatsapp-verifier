/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.osbo.whatsapp.verifier;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

/**
 *
 * @author jheredia
 */
public class Ejecutador {

    public static void main(String args[]) {
        TimerTask timerTask = new TimerTask() {
            BufferedImage anterior;
            BufferedImage nueva;

            @Override
            public void run() {
                try {
                    nueva = Ejecutador.captura(3100, 90, 100, 40);
                    boolean compare = Ejecutador.compareImage(nueva, anterior);
                    if (!compare) {
                        System.out.println("ha ocurrido un cambio en :" + (new Date()));
                        anterior = nueva;
                        HttpResponse<String> asString = Unirest.post("https://api.telegram.org/botxxxxxxxxx/sendPhoto").field("chat_id", "509861682").field("photo", new File ("i://wha//PartialScreenshot.bmp")).asString();
                        System.out.println(asString.getBody());
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Ejecutador.class.getName()).log(Level.SEVERE, null, ex);
                } catch (AWTException ex) {
                    Logger.getLogger(Ejecutador.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    public static BufferedImage captura(int x1, int y1, int x2, int y2) throws IOException, AWTException {
        Robot robot = new Robot();
        String format = "bmp";
        String fileName = "i://wha//PartialScreenshot." + format;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle captureRect = new Rectangle(x1, y1, x2, y2);
        BufferedImage screenFullImage = robot.createScreenCapture(captureRect);
        try {
            ImageIO.write(screenFullImage, format, new File(fileName));
        } catch (NullPointerException npe) {
            System.out.println("problemitas");
        }
        return screenFullImage;
    }

    public static boolean compareImage(BufferedImage fileA, BufferedImage fileB) {
        try {
            // take buffer data from botm image files //

            DataBuffer dbA = fileA.getData().getDataBuffer();
            int sizeA = dbA.getSize();

            DataBuffer dbB = fileB.getData().getDataBuffer();
            int sizeB = dbB.getSize();
            // compare data-buffer objects //
            if (sizeA == sizeB) {
                for (int i = 0; i < sizeA; i++) {
                    if (dbA.getElem(i) != dbB.getElem(i)) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println("Failed to compare image files ...");
            return false;
        }
    }
}
