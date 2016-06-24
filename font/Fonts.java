package scripts.TribotAPI.font;

import java.awt.*;

/**
 * Created by Sphiinx on 6/22/2016.
 */
public enum Fonts {

    PAINT_TITLE_FONT(new Font("Verdana", 0, 20)),
    PAINT_VERSION_FONT(new Font("Verdana", Font.ITALIC, 15)),
    PAINT_INFO_FONT(new Font("Verdana", 0, 13));

    private final Font FONT;

    Fonts(Font font) {
        this.FONT = font;
    }

    public Font getFont() {
        return FONT;
    }

}