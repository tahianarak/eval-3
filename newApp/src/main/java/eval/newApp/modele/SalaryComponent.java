package eval.newApp.modele;


import org.apache.catalina.LifecycleState;

import java.util.List;

public class SalaryComponent
{
    String type;
    String name;
    String abbr;

    String salary_component;

    String formula;



    public String toJsonObject() {
        return "{" +
                "\"doctype\":\"" + (type.equals("Earning") ? "Salary Structure Salary Component" : "Salary Structure Deduction") + "\"," +
                "\"salary_component\":\"" + name + "\"," +
                "\"amount_based_on_formula\":1," +
                "\"formula\":\"" + formula + "\"," +
                "\"abbr\":\"" + abbr + "\"" +
                "}";
    }

    public static String getJsonDeduction(List<SalaryComponent> salaryComponents) {
        StringBuilder ans = new StringBuilder("[");
        for (SalaryComponent salaryComponent : salaryComponents) {
            if ("Deduction".equals(salaryComponent.getType()) && !salaryComponent.formula.equals("")) {
                ans.append(salaryComponent.toJsonObject()).append(",");
            }
        }
        // Retirer la dernière virgule si nécessaire
        if (ans.charAt(ans.length() - 1) == ',') {
            ans.deleteCharAt(ans.length() - 1);
        }
        ans.append("]");
        return ans.toString();
    }

    public static String getJsonEarning(List<SalaryComponent> salaryComponents) {
        StringBuilder ans = new StringBuilder("[");
        for (SalaryComponent salaryComponent : salaryComponents) {
            if ("Earning".equals(salaryComponent.getType()) && !salaryComponent.formula.equals("")) {
                ans.append(salaryComponent.toJsonObject()).append(",");
            }
        }
        // Retirer la dernière virgule si nécessaire
        if (ans.charAt(ans.length() - 1) == ',') {
            ans.deleteCharAt(ans.length() - 1);
        }
        ans.append("]");
        return ans.toString();
    }
    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public SalaryComponent(){}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        this.abbr = abbr;
    }

    public String getSalary_component() {
        return salary_component;
    }

    public void setSalary_component(String salary_component) {
        this.salary_component = salary_component;
    }
}

