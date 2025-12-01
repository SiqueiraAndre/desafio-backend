import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateTest {
    public static void main(String[] args) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        System.out.println("Testando parsing de datas:");

        try {
            String dataStr1 = "01/01/2024";
            LocalDate data1 = LocalDate.parse(dataStr1, dateFormatter);
            System.out.println("✓ Parse de '" + dataStr1 + "' = " + data1);

            String dataStr2 = "31/12/2024";
            LocalDate data2 = LocalDate.parse(dataStr2, dateFormatter);
            System.out.println("✓ Parse de '" + dataStr2 + "' = " + data2);

        } catch (Exception e) {
            System.err.println("✗ Erro no parsing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
