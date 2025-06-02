package eval.newApp.modele;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class FormatUtil {
    public static String formaterMontant(double montant) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.FRANCE);
        symbols.setGroupingSeparator(' ');
        DecimalFormat format = new DecimalFormat("#,##0.00", symbols);
        return format.format(montant);
    }
}
