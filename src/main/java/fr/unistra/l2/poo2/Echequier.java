package fr.unistra.l2.poo2;

import lombok.extern.java.Log;

public class Echequier {
    private final int[] positions = {0, 0, 0, 0, 0, 0, 0, 0};
    private int nbSolutions = 0;

    private Echequier() {
    }

    public static void main(String... args) {
        Echequier first = new Echequier();
        first.placerReine(0);
        first.afficherNbSolutionTotales();
    }

    private void afficherNbSolutionTotales() {
        System.out.println("Nb solutions totales : " + nbSolutions);
    }

    private void afficher() {
        //on affiche la position des reines sur chaque ligne
        System.out.print("pos : ");
        for (int k = 0; k < 8; k++) {
            System.out.print(1 + positions[k]);
        }
        System.out.println();
        // Affiche sous forme de plateau d'echec avec des R pour les reines
        // et _ pour des cases vides
        for (int colonne = 0; colonne < 8; colonne++) {
            afficheLigne(colonne);
        }
        System.out.println();
    }

    private void afficheLigne(int niemeReine) {
        for (int j = 0; j < 8; j++) {
            System.out.print(positions[niemeReine] == j ? "R " : "_ ");
        }
        System.out.println();
    }

    private boolean conflit(int ligneReine1, int colonneReine1, int ligneReine2, int colonneReine2) {
        //un coup est invalide si même ligne, même colonne ou même diagonale
        return (ligneReine1 == ligneReine2 || colonneReine1 == colonneReine2 || (Math.abs(ligneReine1 - ligneReine2) == Math.abs(colonneReine1 - colonneReine2)));

    }

    // renvoie vrai si un coup est possible avec les reines déjà placées sur les lignes précédentes
    private boolean coupPossible(int ligne, int colonne) {
        if (ligne > 0) {
            for (int ligneExistante = 0; ligneExistante < ligne; ligneExistante++) {
                //un coup est possible si pas de conflit avec les positions des reines déjà placées
                if (conflit(ligneExistante, positions[ligneExistante], ligne, colonne)) {
                    return false;
                }
            }
        }
        return true;
    }

    // Tentative de placer la ieme reine
    private void placerReine(int nieme) {
        for (int colNieme = 0; colNieme < 8; colNieme++) {
            //	Si le coup est possible, on place la reine et on cherche à placer la suivante
            if (coupPossible(nieme, colNieme)) {
                positions[nieme] = colNieme;
                if (nieme == 7) {
                    nbSolutions++;
                    // Si les huit reines sont placees, on affiche la solution
                    afficher();
                } else {
                    placerReine(nieme + 1);
                }
            }
        }
    }
}
