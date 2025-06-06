package eval.newApp.modele.statistique;


import java.util.*;

public class SalaryStatData {
    private List<String> months;
    private Map<String, List<Double>> earningData;
    private Map<String, List<Double>> deductionData;
    private List<Double> grossTotals;
    private List<Double> deductionTotals;
    private List<Double> netTotals;

    public SalaryStatData() {
        this.months = new ArrayList<>();
        this.earningData = new LinkedHashMap<>();
        this.deductionData = new LinkedHashMap<>();
        this.grossTotals = new ArrayList<>();
        this.deductionTotals = new ArrayList<>();
        this.netTotals = new ArrayList<>();
    }

    // Getters and setters
    public List<String> getMonths() { return months; }
    public void setMonths(List<String> months) { this.months = months; }

    public Map<String, List<Double>> getEarningData() { return earningData; }
    public void setEarningData(Map<String, List<Double>> earningData) { this.earningData = earningData; }

    public Map<String, List<Double>> getDeductionData() { return deductionData; }
    public void setDeductionData(Map<String, List<Double>> deductionData) { this.deductionData = deductionData; }

    public List<Double> getGrossTotals() { return grossTotals; }
    public void setGrossTotals(List<Double> grossTotals) { this.grossTotals = grossTotals; }

    public List<Double> getDeductionTotals() { return deductionTotals; }
    public void setDeductionTotals(List<Double> deductionTotals) { this.deductionTotals = deductionTotals; }

    public List<Double> getNetTotals() { return netTotals; }
    public void setNetTotals(List<Double> netTotals) { this.netTotals = netTotals; }
}
