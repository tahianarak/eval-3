package eval.newApp.modele;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FormatUtil {
    public static String formaterMontant(double montant) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.FRANCE);
        symbols.setGroupingSeparator(' ');
        DecimalFormat format = new DecimalFormat("#,##0.00", symbols);
        return format.format(montant);
    }

    public static List<LocalDate> getIntermediateMonths(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> result = new ArrayList<>();
        LocalDate current = startDate.plusMonths(1).withDayOfMonth(1);
        while (current.isBefore(endDate)) {
            result.add(current);
            current = current.plusMonths(1);
        }
        return result;
    }
}
