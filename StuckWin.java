/*
 Membre du groupe : Lucas BESSON et Amine Ladel
 Groupe de classe : C2
 Code JAVA du jeu
*/

import java.util.Scanner;

public class StuckWin {
    static final Scanner input = new Scanner(System.in);
    private static final double BOARD_SIZE = 7;

    enum Result {OK, BAD_COLOR, DEST_NOT_FREE, EMPTY_SRC, TOO_FAR, EXT_BOARD, EXIT}
    enum ModeMvt {REAL, SIMU}
    final char[] joueurs = {'B', 'R'};
    final int SIZE = 8;
    final char VIDE = '.';
    // 'B'=bleu 'R'=rouge '.'=vide '-'=n'existe pas

    char[][] state = {
            {'-', '-', '-', '-', 'R', 'R', 'R', 'R'},
            {'-', '-', '-', '.', 'R', 'R', 'R', 'R'},
            {'-', '-', '.', '.', '.', 'R', 'R', 'R'},
            {'-', 'B', 'B', '.', '.', '.', 'R', 'R'},
            {'-', 'B', 'B', 'B', '.', '.', '.', '-'},
            {'-', 'B', 'B', 'B', 'B', '.', '-', '-'},
            {'-', 'B', 'B', 'B', 'B', '-', '-', '-'},
    };

    // Ci-dessous différents tableaux de test de notre jeu :
    /*
            {'-', '-', '-', '-', 'R', 'B', 'R', 'R'},
            {'-', '-', '-', '.', 'R', '.', 'R', 'R'},
            {'-', '-', '.', '.', '.', 'R', 'R', 'R'},
            {'-', 'B', 'B', '.', '.', '.', 'R', 'R'},
            {'-', 'B', 'B', 'B', '.', '.', '.', '-'},
            {'-', 'B', 'B', 'B', 'B', '.', '-', '-'},
            {'-', 'B', 'B', 'B', 'B', '-', '-', '-'},
     */

    /* test_bleu_vicroire
    char[][] state = {
            {'-', '-', '-', '-', 'R', 'R', 'R', 'R'},
            {'-', '-', '-', '.', 'R', 'R', 'R', 'R'},
            {'-', '-', 'R', 'R', '.', 'R', 'R', 'R'},
            {'-', 'B', 'B', 'R', 'R', '.', 'R', 'R'},
            {'-', 'B', 'B', 'B', 'R', 'R', '.', '-'},
            {'-', 'B', '.', 'B', 'B', 'R', '-', '-'},
            {'-', 'B', 'B', 'B', 'B', '-', '-', '-'},
    };*/

    /* test_rouge_vicroire
    char[][] state = {
            {'-', '-', '-', '-', 'R', 'R', 'R', 'R'},
            {'-', '-', '-', 'B', 'R', 'R', '.', 'R'},
            {'-', '-', '.', 'B', 'B', 'R', 'R', 'R'},
            {'-', 'B', 'B', '.', 'B', 'B', 'R', 'R'},
            {'-', 'B', 'B', 'B', '.', 'B', 'B', '-'},
            {'-', 'B', 'B', 'B', 'B', '.', '-', '-'},
            {'-', 'B', 'B', 'B', 'B', '-', '-', '-'},
    }; */


    // Ce tableau permet d'afficher les lettres dans notre affichage
    // -> Utilisé dans la fonction affiche.
    char[] lettre = {'A','B','C','D','E','F','G'};




        /**
         * Déplace un pion ou simule son déplacement
         * @param couleur couleur du pion à déplacer
         * @param lcSource case source Lc
         * @param lcDest case destination Lc
         * @param mode ModeMVT.REAL/SIMU selon qu'on réalise effectivement le déplacement ou qu'on le simule seulement.
         * @return enum {OK, BAD_COLOR, DEST_NOT_FREE, EMPTY_SRC, TOO_FAR, EXT_BOARD, EXIT} selon le déplacement
         */
    Result deplace(char couleur, String lcSource, String lcDest,  ModeMvt mode) {
        // Cela permet de récupérer les valeurs de lcSourc et lcDest et
        // de les décomposer afin d'obtenir séparément le numéro de ligne
        // ainsi que le numéro de colonne, tous cela en entier (int)
        int lignesource = 0;
        int lignedest = 0;
        int colonnesource = Character.getNumericValue(lcSource.charAt(1));
        int colonnedest = Character.getNumericValue(lcDest.charAt(1));
        for (int i = 0; i < lettre.length; i++) {
            if (lcSource.charAt(0) == lettre[i]) {
                lignesource = i;
            }
            if (lcDest.charAt(0) == lettre[i]) {
                lignedest = i;
            }

        }
        // Cela permet de vérifier que l'utilisateur sort pas du tableau :
        // Si il rentre une valeur supérieure à la taille maxiumum
        // des lignes et un valeur inférieur à -1 cela renvoie EXT_BOARD
        // Nous faisions les mêmes vérifictions pour les colonnes.
        // La condition EXT_BOARD avec le caratère '-' que l'on vérifie plus bas
        // ne suffit pas, l'utilisateur peut aller plus loins que cela, sortir
        // donc du tableau et cela ferait cracher le programme.
        // Grâce à cette verification il n'y aura pas ce souci.
        if (lignedest >= state.length || lignedest <= -1 ||
                colonnedest >= state[0].length || colonnedest < 0){
            return Result.EXT_BOARD;
        }

        //Vérifier que l'utilisateur ne fait pas jouer une case vide à savoir un '.'
        if (state[lignesource][colonnesource] == VIDE) {
            return Result.EMPTY_SRC;
        }
        // Vérifier que c'est bien la bonne couleur qui joue
        if (state[lignesource][colonnesource] != couleur) {
            return Result.BAD_COLOR;
        }

        // Vérifier que l'on ne joue pas sur une case qui est déjà prise par
        // une autre couleur
        if (state[lignedest][colonnedest] == 'R' ||
                state[lignedest][colonnedest] == 'B') {
                return Result.DEST_NOT_FREE;
        }

        // Vérifier que l'on ne sort pas du tableau en allant sur une case
        // représentée par '-'
        if (state[lignedest][colonnedest] == '-' ||
                colonnedest>state[0].length) {
            return Result.EXT_BOARD;
        }

        // On met dans un tableau de string le retour de la fonction possibleDests
        String[] possibledest = possibleDests(couleur, lignesource, colonnesource);

        // Vérifier que l'utilisateur ne va pas trop loin : on vérifie donc
        // que la destination choisie elle est bien dans les destinations possibles.
        // le cas échéant, on renvoie TOO_FAR.
        if (!(possibledest[0]).equals(lcDest) &&
                !(possibledest[1]).equals(lcDest) &&
                !(possibledest[2]).equals(lcDest)) {
            return Result.TOO_FAR;
        }

        // On parcourt le tableau de string créé plus haut, si la valeur
        // de destination est dedans, on fait le mouvement
        for (int i = 0; i < 3; i++) {
            if (possibledest[i].equals(lcDest)) {
                if (mode == ModeMvt.REAL) {
                    state[lignesource][colonnesource] = VIDE;
                    state[lignedest][colonnedest] = couleur;
                    return Result.OK;
                } else {
                    return Result.OK;
                }
            }
        }
        return Result.EXIT;
    }


    /**
     * Construit les trois chaînes représentant les positions accessibles
     * à partir de la position de départ [idLettre][idCol].
     * @param couleur couleur du pion à jouer
     * @param idLettre id de la ligne du pion à jouer
     * @param idCol id de la colonne du pion à jouer
     * @return tableau des trois positions jouables par le pion (redondance possible sur les bords)
     */
    String[] possibleDests(char couleur, int idLettre, int idCol){
        // Création d'une tableau de String de 3 élements
        String[] resultat = new String[3];
        // Vérifation pour les bleus des destionations possible
        if (couleur =='B') {
            // On vérifie si on n'a pas rentré une lettre inférieur à -1,
            // et si on n'a pas rentré une ligne qui sort du tableau.
            // On fait de même pour les colonnes sauf que l'on vérifie
            // si elles sont bien supérieur à 0
            if (idLettre-1 < state.length && idLettre - 1 > -1 &&
                    idCol < state[0].length && idCol > 0) {
                // Si la destination est possible,
                // on l’ajoute au tableau crée plus haut :
                if (state[idLettre - 1][idCol] == '.')
                    resultat[0] = "" + (lettre[idLettre - 1]) + (idCol);
                // Si il y a la destination est pas possible,
                // on revoit la position du jeton actuelle
                else {
                    resultat[0] = "" + (lettre[idLettre]) + (idCol);
                }
            }

            if (idLettre-1 < state.length && idLettre - 1 > -1 &&
                    idCol+1 < state[0].length && idCol+1 > 0) {
                if (state[idLettre-1][idCol+1] == '.')
                    resultat[1] = "" + (lettre[idLettre-1]) + (idCol+1);
                else {
                    resultat[1] = "" + (lettre[idLettre]) + (idCol);
                }
            }

            if (idLettre < state.length && idLettre > -1 &&
                    idCol+1 < state[0].length && idCol +1 > 0) {
                if (state[idLettre][idCol+1] == '.')
                    resultat[2] = "" + (lettre[idLettre]) + (idCol+1);
                else {
                    resultat[2] = "" + (lettre[idLettre]) + (idCol);
                }
            }

        }
        // Vérifation pour les rouges des destionations possibles
        else if (couleur =='R'){
            if (idLettre+1 < state.length && idLettre + 1 > -1 &&
                    idCol < state[0].length && idCol > 0) {
                if (state[idLettre+1][idCol] == '.')
                    resultat[0] = "" + (lettre[idLettre+1]) + (idCol);
                else {
                    resultat[0] = "" + (lettre[idLettre]) + (idCol);
                }
            }

            if (idLettre+1 < state.length && idLettre +1 > -1 &&
                    idCol-1 < state[0].length && idCol-1 > 0) {
                if (state[idLettre+1][idCol-1] == '.')
                    resultat[1] = "" + (lettre[idLettre+1]) + (idCol-1);
                else {
                    resultat[1] = "" + (lettre[idLettre]) + (idCol);
                }
            }

            if (idLettre < state.length && idLettre > -1 &&
                    idCol-1 < state[0].length && idCol -1 > 0) {
                if (state[idLettre][idCol-1] == '.')
                    resultat[2] = "" +(lettre[idLettre]) + (idCol-1);
                else {
                    resultat[2] = "" + (lettre[idLettre]) + (idCol);
                }
            }

        }

        // Cela permet de convertir les valeurs null en renvoyant
        // la position du jeton actuelle.
        for (int i = 0 ; i < resultat.length ; i++) {
            if (resultat[i] == null) {
                resultat[i] = "" + (lettre[idLettre]) + (idCol);
            }
        }
        return resultat;
    }

    /**
     * Affiche le plateau de jeu dans la configuration portée par
     * l'attribut d'état "state"
     */

    public void affiche() {
        System.out.println();
        // On parcourt le tableau en diagonale pour le lire
        for (int i = state.length-1; i >= 0; i--) {
            System.out.print("  ");
            for (int j = 0; j < 7; j++) {
                if (j >= i) {
                    // On met les R en rouge et affiche les lettres (A,B,C..) avec
                    // le tableau créé tout au début du programme. Enfin on fait
                    // les espacse nésessaires pour l'affichage
                    if(state[j-i][j+1] == 'R')
                        System.out.print(ConsoleColors.RED_BACKGROUND +
                                lettre[j-i] + (j+1) + ConsoleColors.RESET + "  " );
                    // On fait la même chose que le précédent sauf pour les '.'
                    else if (state[j-i][j+1] == '.')
                        System.out.print(ConsoleColors.WHITE_BACKGROUND +
                                lettre[j-i] + (j+1) + ConsoleColors.RESET + "  " );
                    else if (state[j-i][j+1] == '-')
                        System.out.print( "    " );
                    else if (state[j-i][j+1] == 'B')
                        System.out.print(ConsoleColors.BLUE_BACKGROUND +
                                lettre[j-i] + (j+1) + ConsoleColors.RESET + "  " );
                } else {
                    System.out.print("  ");
                }
            }
            System.out.println();
        }

        for (int i = 0; i <= state.length; i++) {
            // On lit le tableau encore en diagonale
            // On a choisi de prendre une variable temporaire
            // Celle-ci fera les espaces nécessaires et affichera
            // la diagonale complète une fois qu'elle sera complètement lue
            // PS : Sans la variable, les espaces ne se faisait pas correctement
            // et donc du mauvais côté
            String temp = "";
            for (int j = 0; j < 7; j++) {
                if (i + j < state.length) {
                    if(state[j+i][j] == 'R')
                        temp += (ConsoleColors.RED_BACKGROUND +
                                lettre[j+i] + j + ConsoleColors.RESET + "  ");
                    else if (state[j+i][j] == '.')
                        temp += (ConsoleColors.WHITE_BACKGROUND +
                                lettre[j+i] + j + ConsoleColors.RESET + "  ");
                    else if (state[j+i][j] == '-')
                        temp += ("    ");
                    else if (state[j+i][j] == 'B')
                        temp += (ConsoleColors.BLUE_BACKGROUND +
                                lettre[j+i] + j + ConsoleColors.RESET + "  ");
                } else {
                    System.out.print("  ");
                }
            }
            System.out.print(temp);
            System.out.println();
        }

    }



    /**
     * Joue un tour
     * @param couleur couleur du pion à jouer
     * @return tableau contenant la position de départ et la destination du pion à jouer.
     */
    String[] jouerIA(char couleur) {
        // votre code ici. Supprimer la ligne ci-dessous.
        throw new java.lang.UnsupportedOperationException("à compléter");
    }

    /**
     * gère le jeu en fonction du joueur/couleur
     * @param couleur
     * @return tableau de deux chaînes {source,destination} du pion à jouer
     */
    String[] jouer(char couleur){
        String src = "";
        String dst = "";
        String[] mvtIa;
        switch(couleur) {
            case 'B':
                System.out.println("Mouvement " + couleur);
                //System.err.println("Mouvement " + couleur);
                src = input.next();
                dst = input.next();
                System.out.println(src + "->" + dst);
                //System.err.println(src + "->" + dst);
                break;
            case 'R':
                // Cela permet de faire jouer le bleu et le rouge par un utilisateur
                System.out.println("Mouvement " + couleur);
                //System.err.println("Mouvement " + couleur);
                src = input.next();
                dst = input.next();
                System.out.println(src + "->" + dst);
                //System.err.println(src + "->" + dst);
                break;
                // Nous ferons la version avec l’IA dans la prochaine SAE
                /*
                System.out.println("Mouvement " + couleur);
                mvtIa = jouerIA(couleur);
                src = mvtIa[0];
                dst = mvtIa[1];
                System.out.println(src + "->" + dst);
                break;*/
        }
        return new String[]{src, dst};
    }

    /**
     * retourne 'R' ou 'B' si vainqueur, 'N' si partie pas finie
     * @param couleur
     * @return
     */
    char finPartie(char couleur){
        // on parcours l'ensemble du tableau state
        for (int i = 0; i<state.length ; i++){
            for (int j = 0; j<state[i].length ; j++){
                if (state[i][j] == couleur){
                    // On met dans un tableau de string comme plus haut
                    // les valeurs retourner de possibleDests
                    String[] temp = possibleDests(couleur, i, j);
                    // on parcours ses valeurs
                    for (int k = 0; k < temp.length; k++) {
                        // On essaye de faire avec le mode simulation
                        // un déplacement. Si celui-ci est possible,
                        // c'est que la partie n'est pas fini
                        if (deplace(couleur,
                                ("" + lettre[i]+ j),
                                temp[k],
                                ModeMvt.SIMU) == Result.OK){
                            // On retourne que la partie est toujours en cours
                            return 'N';
                        }
                    }
                }
            }
        }
        // Si on arrive ici, il y a un gagnant alors on retourne la couleur
        // de celui-ci.
        return couleur;
    }




    public static void main(String[] args) {
        StuckWin jeu = new StuckWin();
        String src = "";
        String dest;
        String[] reponse;
        Result status;
        char partie = 'N';
        char curCouleur = jeu.joueurs[0];
        char nextCouleur = jeu.joueurs[1];
        char tmp;
        int cpt = 0;

        // version console
        do {
            // séquence pour Bleu ou rouge
            jeu.affiche();
            do {
                status = Result.EXIT;
                reponse = jeu.jouer(curCouleur);
                src = reponse[0];
                dest = reponse[1];
                if("q".equals(src))
                    return;
                status = jeu.deplace(curCouleur, src, dest, ModeMvt.REAL);
                partie = jeu.finPartie(nextCouleur);
                System.out.println("status : "+status + " partie : " + partie);
                //System.err.println("status : "+status + " partie : " + partie);
            } while(status != Result.OK && partie=='N');
            tmp = curCouleur;
            curCouleur = nextCouleur;
            nextCouleur = tmp;
            cpt ++;
        } while(partie =='N'); // TODO affiche vainqueur
        System.out.printf("Victoire : " + partie + " (" + (cpt/2) + " coups)");
        //System.err.printf("Victoire : " + partie + " (" + (cpt/2) + " coups)");
    }
}