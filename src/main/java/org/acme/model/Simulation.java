package org.acme.model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.lang.Math;
import java.util.Collection;

public class Simulation {

    /** constante gravitationnelle */
    public static double g = 6.6742e-11;
    /** vitesse de la simulation*/
    private static float simuRate = 1f;
    /** liste des astres de la simulation*/
    private ArrayList<Astre> listeAstre;
    /** échelle de distance : 1 unité = 1 milliard de m ou 1 million de km */
    public static double scaleDistance = Math.pow(10, 9);
    /** échelle de la masse : 1 unité = 1*10^22 kg */
    public static double scaleMasse = Math.pow(10,22);
    /** échelle de temps : 1 frame = 43200 secondes = 0.5 jours,
     * au rythme de soixante frame par seconde on obtient 1 seconde réél = 1 mois dans la simulation */
    public static double scaleTemps = 43200 * simuRate; // nb de seconde pendant une frame
    /** son fichier père si elle est généré à partir d'un fichier, null sinon*/
    private File file;

    public Simulation(){
        listeAstre = new ArrayList<>();
        setSimuRate(1);
    }

    /**
     * construit une simulation a partire d'une liste d'astre et d'un fichier
     * @param save fichier d'où la simulation est générée
     * @param listeAstre nouvelle liste d'astres*/
    public Simulation(File save, ArrayList<Astre> listeAstre) throws IOException, ClassNotFoundException {
        this.listeAstre = listeAstre;
        file = save;
        setSimuRate(1);
    }

    public ArrayList<Astre> getListeAstre() {
        return listeAstre;
    }

    public void addListeAstre(Astre a){
        listeAstre.add(a);
    }

    public void setListeAstre(ArrayList<Astre> listeAstre) {
        this.listeAstre = listeAstre;
    }

    public static void setSimuRate(float simuRate) {
        Simulation.simuRate = simuRate;
        scaleTemps = 43200 * simuRate;
    }



    @Override
    public String toString() {
        if (listeAstre.isEmpty()){
            return "la simulation est vide";
        }
        return "Simulation{ " +
                listeAstre +
                '}';
    }

    public File getFile() {
        return file;
    }

    /** construire une liste d'astre a partir d'un fichier
     * @param save fichier a partir duquel on va construire
     * @return la simulation créée
     * */
    public static Simulation setAPartirDunFichier(File save) throws IOException, ClassNotFoundException {
        ArrayList<Astre> listeA = new ArrayList<>();
        CreateurAstre p = new CreateurPlanete();
        for (String ligne : Files.readAllLines(save.toPath())){
            String name = "";
            float taille =0;
            float masse = 0;
            int pX = 0;
            int pY = 0;
            double vitesseX = 0;
            double vitesseY = 0;
            boolean creer = true;
            String [] arguments = ligne.split(" ");
            if (!(arguments[0].equals("//"))){
                name = arguments[0];
                taille = Float.parseFloat(arguments[1]);
                masse = Float.parseFloat(arguments[2]);
                pX = Integer.parseInt(arguments[3]);
                pY = Integer.parseInt(arguments[4]);
                vitesseX = Double.parseDouble(arguments[5]);
                vitesseY = Double.parseDouble(arguments[6]);
            }else{
                creer = false;
            }
            if(creer){
                listeA.add(p.factory(name, taille, masse, pX, pY, vitesseX, vitesseY));
            }
        }
        return  new Simulation(save,listeA);

    }


    /**sauvegarde de la listes des astres dans un fichier
     * @param save le fichier dans lequel on va sauvegarder
     * */
    public static void saveListeAstre(File save, ArrayList<Astre> listeAstre) throws IOException {
        save.createNewFile();
        String commentaire = "// nom taille masse positionX positionY vitesseX vitesseY estFixe \n";
        Files.write(save.toPath(), commentaire.getBytes(), StandardOpenOption.APPEND);
        for(Astre a : listeAstre){
            String arg = a.getArgString();
            Files.write(save.toPath(), arg.getBytes(), StandardOpenOption.APPEND);
        }
    }

    /** pour avoir les autres astres de a d'une listeA
     * @param a l'astre qu'on ne veut pas
     * @param listeA la liste de tout les astres
     * @return la liste des autres astres
     * */
    public static ArrayList<Astre> getOther(Astre a, Collection<Astre> listeA){
        ArrayList<Astre> r = new ArrayList<>(listeA);
        r.remove(a);
        return r;
    }

    public static float getSimuRate() {
        return simuRate;
    }
}
