package scripts.API.Game.Player;

import org.tribot.api.General;
import org.tribot.api2007.Game;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterface;


/**
 * Created by Sphiinx on 1/10/2016.
 */
public class Player07 {

    /**
     * Checks if the player is poisoned.
     * @return True if poisoned; false otherwise.
     * */
    public static boolean isPoisoned() {
        return Game.getSetting(102) > 0;
    }

    /**
     * Checks the players combat level. - NOTE THIS METHOD IS NOT FINISHED.
     * @return The players combat level.
     * */
    public static int getCombatLevel() {
            RSInterface level = Interfaces.get(593, 2);
            if (level != null) {
                String text = level.getText();
                if (text != null) {
                    General.println(text);
                    //TODO Extract number from text.
                }
            }
        return -1;
    }

}

