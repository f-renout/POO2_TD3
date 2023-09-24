package fr.unistra.l2.poo2.rle;

import java.io.PrintStream;
import java.util.HexFormat;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import static fr.unistra.l2.poo2.rle.Color.BLACK;
import static fr.unistra.l2.poo2.rle.Color.WHITE;

public class RLE {
    public static final short CTRL_POIDS_FORT = Short.MIN_VALUE;

    ColorConverter converter = new ColorConverter();
    private final Compresseur compresseur = new Compresseur(converter);
    private final Decompresseur decompresseur = new Decompresseur(converter);

    public static void main(String[] args) {
        List<Color> tab = List.of(WHITE, WHITE, WHITE, BLACK, BLACK, BLACK, BLACK, WHITE, BLACK, WHITE, BLACK, WHITE, WHITE, WHITE, WHITE, WHITE);
        RLE rle = new RLE();

        final var out = System.out;
        rle.afficheTableau(tab, out);
        rle.afficheTableauHexa(tab, out);

        out.println("compression");
        Queue<Byte> compr = rle.compresseur.compresser(tab);
        out.println(afficherCompression(compr));

        out.println("Decompression");
        List<Color> image = rle.decompresseur.decompresser(compr);
        rle.afficheTableau(image, out);
        rle.afficheTableauHexa(image, out);

    }

    private static String afficherCompression(Queue<Byte> compr) {
        byte[] tmp = new byte[compr.size()];
        int i = 0;
        for (Byte aByte : compr) {
            tmp[i++] = aByte;
        }
        return HexFormat.ofDelimiter("|").formatHex(tmp);
    }

    public void afficheTableau(List<Color> image, PrintStream out) {
        out.println("Image");
        String collect = image.stream().map(this::getCouleurStr).collect(Collectors.joining());
        out.println(collect);
    }

    private String getCouleurStr(Color pixel) {
        if (pixel.equals(BLACK)) {
            return "N";
        } else if (pixel.equals(WHITE)) {
            return "B";
        }
        throw new IllegalArgumentException();
    }

    public void afficheTableauHexa(List<Color> image, PrintStream out) {
        out.println("Image en hexa");
        final var collect = image.stream().map(converter::toHexaString).collect(Collectors.joining("|"));
        out.printf(collect);
        out.println();
    }

}
