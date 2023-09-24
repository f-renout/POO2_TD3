package fr.unistra.l2.poo2.rle;

import java.util.HexFormat;
import java.util.List;

public class ColorConverter {

    public List<Byte> toHexa(Color pixel) {
        return List.of(pixel.red(), pixel.green(), pixel.blue());
    }

    public String toHexaString(Color pixel) {
        byte[] tmp = new byte[]{pixel.red(), pixel.green(), pixel.blue()};
        return HexFormat.of().formatHex(tmp);
    }

    public Color toColor(List<Byte> toProcess) {
        var red = toProcess.get(0);
        var green = toProcess.get(1);
        var blue = toProcess.get(2);
        return new Color(red, green, blue);
    }
}
