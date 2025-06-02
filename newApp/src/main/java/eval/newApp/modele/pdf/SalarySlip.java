package eval.newApp.modele.pdf;



import java.util.List;

public class SalarySlip {
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String employeeName;
    private String employeeId;
    private String payPeriod;
    private String department;

    private List<Earning> earnings;
    private List<Deduction> deductions;

    private double grossPay;
    private double totalDeductions;
    private double netPay;

    // Getters & Setters

    public String getEmployeeName() {
        return employeeName;
    }
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeId() {
        return employeeId;
    }
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getPayPeriod() {
        return payPeriod;
    }
    public void setPayPeriod(String payPeriod) {
        this.payPeriod = payPeriod;
    }

    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }

    public List<Earning> getEarnings() {
        return earnings;
    }
    public void setEarnings(List<Earning> earnings) {
        this.earnings = earnings;
    }

    public List<Deduction> getDeductions() {
        return deductions;
    }
    public void setDeductions(List<Deduction> deductions) {
        this.deductions = deductions;
    }

    public double getGrossPay() {
        return grossPay;
    }
    public void setGrossPay(double grossPay) {
        this.grossPay = grossPay;
    }

    public double getTotalDeductions() {
        double ans=0;
        for(Deduction deduction:deductions)
        {
            ans=ans+deduction.getAmount();
        }
        return ans;
    }
    public void setTotalDeductions(double totalDeductions) {
        this.totalDeductions = totalDeductions;
    }

    public double getNetPay() {
        return netPay;
    }
    public void setNetPay(double netPay) {
        this.netPay = netPay;
    }
}
