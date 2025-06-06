package eval.newApp.service;

import eval.newApp.modele.pdf.*;
import eval.newApp.modele.statistique.SalaryStatData;
import eval.newApp.modele.statistique.StatSalarySlip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class SalarySlipStatisticsService {

    private static final SimpleDateFormat monthFormatter = new SimpleDateFormat("yyyy-MM");

    @Autowired
    SalarySlipService salarySlipService;


    public SalaryStatData buildStatData(List<StatSalarySlip> slips) {
        SalaryStatData statData = new SalaryStatData();

        Set<String> allEarningTypes = new LinkedHashSet<>();
        Set<String> allDeductionTypes = new LinkedHashSet<>();

        for (StatSalarySlip stat : slips) {
            for (Earning e : stat.getEarnings()) {
                allEarningTypes.add(e.getDescription());
            }
            for (Deduction d : stat.getDeductions()) {
                allDeductionTypes.add(d.getDescription());
            }
        }

        // Earnings
        for (String type : allEarningTypes) {
            List<Double> values = new ArrayList<>();
            for (StatSalarySlip stat : slips) {
                double amount = stat.getEarnings().stream()
                        .filter(e -> e.getDescription().equals(type))
                        .mapToDouble(Earning::getAmount)
                        .findFirst()
                        .orElse(0.0);
                values.add(amount);
            }
            statData.getEarningData().put(type, values);
        }

        // Deductions
        for (String type : allDeductionTypes) {
            List<Double> values = new ArrayList<>();
            for (StatSalarySlip stat : slips) {
                double amount = stat.getDeductions().stream()
                        .filter(d -> d.getDescription().equals(type))
                        .mapToDouble(Deduction::getAmount)
                        .findFirst()
                        .orElse(0.0);
                values.add(amount);
            }
            statData.getDeductionData().put(type, values);
        }

        // Months & Totals
        for (StatSalarySlip stat : slips) {
            statData.getMonths().add(stat.getMonth());
            statData.getGrossTotals().add(stat.getGrossTotal());
            statData.getDeductionTotals().add(stat.getDeductionTotal());
            statData.getNetTotals().add(stat.getNetTotal());
        }

        return statData;
    }
    public List<StatSalarySlip> getStaticsDataFiltre(String sid, int annee) throws Exception {
        // Récupère toutes les fiches de paie (sans filtre sur le mois)
        List<SalarySlip> allSalarySlips = salarySlipService.getSalarySlipsByMonth(sid, null);

        // Filtrer les fiches par année selon startDate
        List<SalarySlip> filteredSalarySlips = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        for (SalarySlip slip : allSalarySlips) {
            Date startDate = slip.getStartDate();
            if (startDate != null) {
                cal.setTime(startDate);
                int slipYear = cal.get(Calendar.YEAR);
                if (slipYear == annee) {
                    filteredSalarySlips.add(slip);
                }
            }
        }

        // Grouper les fiches filtrées par mois et retourner la liste de StatSalarySlip
        return groupSalarySlipsByMonth(filteredSalarySlips);
    }

    public List<StatSalarySlip> getStaticsData(String sid)throws Exception
    {
        List<SalarySlip> salarySlips=salarySlipService.getSalarySlipsByMonth(sid,null);
        return groupSalarySlipsByMonth(salarySlips);
    }

    public List<StatSalarySlip> groupSalarySlipsByMonth(List<SalarySlip> slips) {
        Map<String, StatSalarySlip> groupedStats = new HashMap<>();

        for (SalarySlip slip : slips) {
            String month = monthFormatter.format(slip.getStartDate());

            StatSalarySlip stat;

            // Vérifie si le mois existe déjà
            if (groupedStats.containsKey(month)) {
                stat = groupedStats.get(month);
            } else {
                stat = new StatSalarySlip();
                stat.setMonth(month);
                stat.setEarnings(new ArrayList<>());
                stat.setDeductions(new ArrayList<>());
                stat.setGrossTotal(0);
                stat.setNetTotal(0);
                stat.setDeductionTotal(0);
                groupedStats.put(month, stat);
            }

            // === Gérer les earnings ===
            List<Earning> existingEarnings = stat.getEarnings();
            for (Earning e : slip.getEarnings()) {
                boolean found = false;
                for (Earning ex : existingEarnings) {
                    if (ex.getDescription().equals(e.getDescription())) {
                        ex.setAmount(ex.getAmount() + e.getAmount());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    Earning newEarning = new Earning();
                    newEarning.setDescription(e.getDescription());
                    newEarning.setAmount(e.getAmount());
                    existingEarnings.add(newEarning);
                }
            }

            // === Gérer les deductions ===
            List<Deduction> existingDeductions = stat.getDeductions();
            for (Deduction d : slip.getDeductions()) {
                boolean found = false;
                for (Deduction ex : existingDeductions) {
                    if (ex.getDescription().equals(d.getDescription())) {
                        ex.setAmount(ex.getAmount() + d.getAmount());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    Deduction newDeduction = new Deduction();
                    newDeduction.setDescription(d.getDescription());
                    newDeduction.setAmount(d.getAmount());
                    existingDeductions.add(newDeduction);
                }
            }

            // === Ajouter les totaux ===
            stat.setGrossTotal(stat.getGrossTotal() + slip.getGrossPay());
            stat.setNetTotal(stat.getNetTotal() + slip.getNetPay());
            stat.setDeductionTotal(stat.getDeductionTotal() + slip.getTotalDeductions());
        }

        List<StatSalarySlip> result = new ArrayList<>(groupedStats.values());
        result.sort(Comparator.comparing(StatSalarySlip::getMonth));

        return result;
    }
}

