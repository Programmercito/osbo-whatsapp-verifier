package bo.osbo.whatsapp.verifier;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
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
        TimerTask timerTask;
        timerTask = new TimerTask() {
            BufferedImage anterior;
            BufferedImage nueva;
            FileReader reader;
            int contador = 0;
            Properties p = new Properties();

            @Override
            public void run() {
                try {
                    reader = new FileReader("conf.properties");
                    p.load(reader);
                    String ruta = (String) p.get("rutatemp");
                    String chat = (String) p.get("chat");

                    int iniciox, inicioy, finx, finy;
                    iniciox = Integer.parseInt((String) p.get("iniciox"));
                    inicioy = Integer.parseInt((String) p.get("inicioy"));
                    finx = Integer.parseInt((String) p.get("finx"));
                    finy = Integer.parseInt((String) p.get("finy"));

                    nueva = Ejecutador.captura(iniciox, inicioy, finx, finy, ruta);
                    boolean compare = Ejecutador.compareImage(nueva, anterior);
                    if (!compare) {
                        System.out.println("ha ocurrido un cambio en :" + (new Date()));
                        anterior = nueva;
                        String bot = (String) p.get("bot");
                        HttpResponse<String> asString = Unirest
                                .post("https://api.telegram.org/bot" + bot + "/sendPhoto").field("chat_id", chat)
                                .field("photo", new File(ruta + "PartialScreenshot.bmp")).asString();
                        System.out.println(asString.getBody());
                    }
                    //se hace click de mouse para ser compatible con version beta de whatsapp
                    int x = iniciox + finx + 1;

                    if (contador < 4) {
                        contador++;
                    } else {
                        MouseUtils.click(x, 0);
                        MouseUtils.move(x, inicioy + finy + 1);
                        MouseUtils.move(x, inicioy + finy + 2);
                        contador = 0;
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

    public static BufferedImage captura(int x1, int y1, int x2, int y2, String ruta) throws IOException, AWTException {
        Robot robot = new Robot();
        String format = "bmp";
        String fileName = ruta + "PartialScreenshot." + format;
        Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle captureRect = new Rectangle(x1, y1, x2, y2);
        BufferedImage screenFullImage = robot.createScreenCapture(captureRect);
        try {
            ImageIO.write(screenFullImage, format, new File(fileName));
        } catch (NullPointerException npe) {
            System.out.println("problemitas de grabacion");
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
