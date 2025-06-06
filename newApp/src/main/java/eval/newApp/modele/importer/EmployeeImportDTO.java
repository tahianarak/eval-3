package eval.newApp.modele.importer;


import java.text.SimpleDateFormat;
import java.util.Date;

public class EmployeeImportDTO {

    private String ref;
    private String nom;
    private String prenom;
    private String genre;
    private String dateEmbauche;
    private String dateNaissance;
    private String company;

    int ligne ;


    public int getLigne() {
        return ligne;
    }

    public void setLigne(int ligne) {
        this.ligne = ligne;
    }

    public void parseDate(String dateString)throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        formatter.setLenient(false);

        try {
            Date date = formatter.parse(dateString);
            System.out.println("date string:"+date.toString());

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("la ligne de donnees "+this.ligne +" ,du fichier 1 contient une date inexistante");
        }
    }
    private String mapGenre(String genreFr) {
        return switch (genreFr.toLowerCase()) {
            case "masculin" -> "Male";
            case "feminin" -> "Female";
            default -> "Other";
        };
    }
    // Getters et Setters
    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = mapGenre(genre);
    }

    public String getDateEmbauche() {

        return dateEmbauche;
    }

    public void setDateEmbauche(String dateEmbauche)throws Exception {

        parseDate(dateEmbauche);

        this.dateEmbauche = dateEmbauche;
    }

    public String getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(String dateNaissance)throws Exception {
        parseDate(dateNaissance);
        this.dateNaissance = dateNaissance;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
