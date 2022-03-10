package bo.osbo.whatsapp.verifier;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

/**
 *
 * @author Programmercito <devtecpro.org>
 */
public class MouseUtils {

    public static void click(int x, int y) throws AWTException {
        Robot bot = new Robot();
        bot.mouseMove(x, y);
        bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

    }

    public static void move(int x, int y) throws AWTException {
        Robot bot = new Robot();
        bot.mouseMove(x, y);
    }
}
