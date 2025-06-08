package eval.newApp.modele.utils;



import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class NumberFormatterUtil {

    public static String formatAmount(double amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');

        DecimalFormat formatter = new DecimalFormat("#,##0.00", symbols);
        return formatter.format(amount) + " Ar";
    }

}
