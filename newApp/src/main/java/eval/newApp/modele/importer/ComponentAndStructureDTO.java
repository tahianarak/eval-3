package eval.newApp.modele.importer;



public class ComponentAndStructureDTO {

    private String salaryStructure;
    private String name;
    private String abbr;
    private String type;      // "earning" ou "deduction"
    private String valeur;    // ex: "base", "SB * 0.3", "(SB + IND) * 0.2"
    private String company;

    // --- Constructeurs ---
    public ComponentAndStructureDTO() {
    }

    public ComponentAndStructureDTO(String salaryStructure, String name, String abbr, String type, String valeur, String company) {
        this.salaryStructure = salaryStructure;
        this.name = name;
        this.abbr = abbr;
        this.type = type;
        this.valeur = valeur;
        this.company = company;
    }

    // --- Getters & Setters ---
    public String getSalaryStructure() {
        return salaryStructure;
    }

    public void setSalaryStructure(String salaryStructure) {
        this.salaryStructure = salaryStructure;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        System.out.println("abbreviation:"+abbr);
        this.abbr = abbr;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type.toLowerCase();
    }

    public String getValeur() {
        return valeur;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    // --- toString ---
    @Override
    public String toString() {
        return "ComponentAndStructureDTO{" +
                "salaryStructure='" + salaryStructure + '\'' +
                ", name='" + name + '\'' +
                ", abbr='" + abbr + '\'' +
                ", type='" + type + '\'' +
                ", valeur='" + valeur + '\'' +
                ", company='" + company + '\'' +
                '}';
    }
}

