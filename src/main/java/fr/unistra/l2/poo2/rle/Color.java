package fr.unistra.l2.poo2.rle;

public record Color(byte red, byte green, byte blue) {
    public final static Color WHITE = new Color((byte) -1, (byte) -1, (byte) -1);
    public final static Color BLACK = new Color((byte) 0, (byte) 0, (byte) 0);
}
