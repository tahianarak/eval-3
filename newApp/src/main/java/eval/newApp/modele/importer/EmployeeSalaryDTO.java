package eval.newApp.modele.importer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class EmployeeSalaryDTO {

    int ligne;
    private String mois;
    private int refEmploye;
    private double salaireBase;
    private String salaire;

    public int getLigne() {
        return ligne;
    }

    public EmployeeSalaryDTO(){}

    public void setLigne(int ligne) {
        this.ligne = ligne;
    }

    public void parseDate(String dateString)throws Exception {
        if (!Pattern.matches("\\d{2}/\\d{2}/\\d{4}", dateString)) {
            throw new Exception("la ligne de données " + this.ligne + " ,du fichier 3 contient un format de date invalide");
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        formatter.setLenient(false);

        try {
            Date date = formatter.parse(dateString);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("la ligne de donnees "+ligne +" ,du fichier 3 contient une date inexistante");
        }
    }

    // Constructeur
    public EmployeeSalaryDTO(String mois, int refEmploye, double salaireBase, String salaire) {
        this.mois = mois;
        this.refEmploye = refEmploye;
        this.salaireBase = salaireBase;
        this.salaire = salaire;
    }

    // Getters et Setters
    public String getMois() {
        return mois;
    }

    public void setMois(String mois)throws Exception {
        parseDate(mois);
        this.mois = mois;
    }

    public int getRefEmploye() {
        return refEmploye;
    }

    public void setRefEmploye(int refEmploye) {
        this.refEmploye = refEmploye;
    }

    public double getSalaireBase() {
        return salaireBase;
    }

    public void setSalaireBase(double salaireBase) {
        this.salaireBase = salaireBase;
    }

    public String getSalaire() {
        return salaire;
    }

    public void setSalaire(String salaire) {
        this.salaire = salaire;
    }

    @Override
    public String toString() {
        return "EmployeeSalaryDTO{" +
                "mois=" + mois +
                ", refEmploye=" + refEmploye +
                ", salaireBase=" + salaireBase +
                ", salaire='" + salaire + '\'' +
                '}';
    }
}
