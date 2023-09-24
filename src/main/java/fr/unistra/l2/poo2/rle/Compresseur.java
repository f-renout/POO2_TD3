package fr.unistra.l2.poo2.rle;

import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;
import java.util.*;

import static fr.unistra.l2.poo2.rle.RLE.CTRL_POIDS_FORT;

@RequiredArgsConstructor
public class Compresseur {

    private final ColorConverter converter;

    private static byte[] shortToArray(short nbRepetitions, boolean compresse) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        short toput = compresse ? (short) (nbRepetitions | CTRL_POIDS_FORT) : nbRepetitions;
        buffer.putShort(toput);
        return buffer.array();
    }

    public Queue<Byte> compresser(List<Color> image) {
        //la queue qui contiendra le resultat de la compression
        Queue<Byte> result = new LinkedList<>();
        //le buffer qui ca tenir la liste des couleurs en cours d'analyse
        Queue<Color> buffer = new LinkedList<>();
        //quelle est la derniere couleur lue
        Color couleurRepetee = null;
        //compteur sur combien de fois la derniere couleur est repetée
        short nbRepetitions = 0;

        //pour chaque pixel de notre image
        for (Color pixel : image) {
            // Si début d'une série
            if (nbRepetitions == 0) {
                couleurRepetee = pixel;
                nbRepetitions++;
            } else if (Objects.equals(couleurRepetee, pixel)) {
                // Si on est dans la répétition
                nbRepetitions++;
                //on "démarre" la compression
                if (nbRepetitions == 3) {
                    //nb represente le nombre de pixels qui auraient pu etre bufferises avant qu'on compresse
                    var nb = buffer.size() - 2;
                    //le buffer ne contient pas encore le pixel courant.
                    // Si on a plus de 2 elements dans le buffer c'est qu'on a stocké en début de buffer
                    // des elements qui ne sont pas de la couleur actuelle
                    if (nb != 0) {
                        //par exemple si on a en buffer BNBNBB et qu'on recoit B
                        // -> on doit "sauver" [B,N,B,N] qui sont des pixels non compressé
                        //avant de continuer à "stocker" la suite de notre buffer
                        //les octets de controles = le nb de pixels non compressés qui vont etre inserés
                        stockePixelsNonCompresses((short) nb, result, buffer);
                    }
                    //sinon c'est qu'on est en train de stocker des pixels à compresser donc pas de traitement spécifique,
                    // on rajoutera en fin de boucle le pixel courant
                }
            } else if (nbRepetitions > 2) { //on a changé de couleur et on etait dans une repetition induisant une compression et on change par ex on a lu 4 B et on lit N
                stockePixelCompresses(nbRepetitions, couleurRepetee, result);
                //on clear notre buffer
                buffer.clear();
                //et on traite le pixel courant => la couleur repetée est la nouvelle couleur lue et le nb de repet est 1
                couleurRepetee = pixel;
                nbRepetitions = 1;
            } else {
                //sinon on est dans un cas ou la repet c'est 1 ou 2
                //on met juste la couleur repetée
                couleurRepetee = pixel;
            }
            //on lit le pixel et on le rajoute au buffer
            buffer.add(pixel);
        }
        // On gère la fin (ce qui reste dans notre buffer une fois qu'on a fini de lire notre image)
        traiterBufferEnFinDeLecture(result, buffer, couleurRepetee, nbRepetitions);
        return result;
    }

    private void stockePixelCompresses(short nbRepetitions, Color couleurRepetee, Queue<Byte> result) {
        //on définit nos octets de controle
        //et le pixel qui est repetée
        List<Byte> couleurString = converter.toHexa(couleurRepetee);
        //on rajoute à notre chaine les octets de controle et le pixel repeté
        final var array = shortToArray(nbRepetitions, true);
        result.add(array[0]);
        result.add(array[1]);
        result.addAll(couleurString);
    }

    private void stockePixelsNonCompresses(short nb, Queue<Byte> result, Queue<Color> buffer) {
        final var array = shortToArray(nb, false);
        result.add(array[0]);
        result.add(array[1]);
        final var uncompressedList = buffer.stream().limit(nb).map(converter::toHexa).flatMap(Collection::stream).toList();
        result.addAll(uncompressedList);
    }

    private void traiterBufferEnFinDeLecture(Queue<Byte> result, Queue<Color> buffer, Color couleurRepetee, short nbRepetitions) {
        //soit on a moins de 3 repet et on va stocket ce qu'on a dans le buffer sans compression
        if (nbRepetitions < 3) {
            var nb = buffer.size();
            if (nb != 0) {
                stockePixelsNonCompresses((short) nb, result, buffer);
            }
            //soit on a 3 repets ou plus et la on a un pixel "compressé" dans notre buffer donc on le stocke
        } else {
            stockePixelCompresses(nbRepetitions, couleurRepetee, result);
        }
    }

}
