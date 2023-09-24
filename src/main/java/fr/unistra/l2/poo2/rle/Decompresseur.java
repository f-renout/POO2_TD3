package fr.unistra.l2.poo2.rle;

import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static fr.unistra.l2.poo2.rle.RLE.CTRL_POIDS_FORT;

@RequiredArgsConstructor
public class Decompresseur {
    private final ColorConverter converter;
    private ArrayList<Byte> extraire(Queue<Byte> aTraiter, int nbChar) {
        var tmp = new ArrayList<Byte>();
        for (int i = 0; i < nbChar; i++) {
            tmp.add(aTraiter.poll());
        }
        return tmp;
    }

    public List<Color> decompresser(Queue<Byte> compression) {
        List<Color> list = new ArrayList<>();
        while (compression.size() != 0) {
            // nos infos de controles sont sur 2 octets = 0000->ffff
            final var extrait = extraire(compression,2);
            //et on les converti en nombre
            short controle = ByteBuffer.wrap(new byte[]{extrait.get(0), extrait.get(1)}).getShort();

            //si le controle a un bit de poids fort à 1
            if ((controle & CTRL_POIDS_FORT) != 0) {
                dechiffreCompression(list, compression, controle);
            } else {
                dechiffreNonCompression(list, compression, controle);
            }
        }
        return list;
    }

    private void dechiffreNonCompression(List<Color> list, Queue<Byte> tmpString, int controle) {
        //bit de poids fort à 0 => controle = le nombre de pixels qui sont non compressées
        //sinon on a X caracteres qui sont present directements
        for (int i = 0; i < controle; i++) {
            //donc on va repeter X fois l'opération suivante:

            //lecture du pixel = 3 octets pour R G B
            final var extraire = extraire(tmpString, 3);
            list.add(converter.toColor(extraire));
        }
    }

    private void dechiffreCompression(List<Color> list, Queue<Byte> tmpString, short controle) {
        //on calcule le nombre d'occurence en remplacant le bit de poids fort pas un 0
        short nb = (short) (controle - CTRL_POIDS_FORT);
        //le pixel qui a été compressée est sur 3 couleur chaque etant sur 1 octer donc 00-ff
        final var extraire = extraire(tmpString, 3);
        Color couleur = converter.toColor(extraire);

        //on converti le pixel en chiffre et on la rajoute le bon nombre de fois (si on a lu 10 blancs, on rajoute 10 fois blancs dans la liste)
        for (int i = 0; i < nb; i++) {
            list.add(couleur);
        }
    }

}
