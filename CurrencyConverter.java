import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class CurrencyConverter extends JFrame {
    private JComboBox<String> fromCurrency;
    private JComboBox<String> toCurrency;
    private JTextField amountField;
    private JButton convertButton;
    private JLabel resultLabel;
    private Map<String, Double> rates = new HashMap<>();
    private String apiKey = "ff00e740260ba270ff5d3bb3";  
    private String baseUrl = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/USD";

    public CurrencyConverter() {
        super("Currency Converter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);  

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel fromPanel = new JPanel();
        fromCurrency = new JComboBox<>();
        fromPanel.add(new JLabel("From:"));
        fromPanel.add(fromCurrency);

        JPanel toPanel = new JPanel();
        toCurrency = new JComboBox<>();
        toPanel.add(new JLabel("To:"));
        toPanel.add(toCurrency);

        JPanel amountPanel = new JPanel();
        amountField = new JTextField(10);
        amountPanel.add(new JLabel("Amount:"));
        amountPanel.add(amountField);

        JPanel buttonPanel = new JPanel();
        convertButton = new JButton("Convert");
        buttonPanel.add(convertButton);

        resultLabel = new JLabel("");
        panel.add(fromPanel);
        panel.add(toPanel);
        panel.add(amountPanel);
        panel.add(buttonPanel);
        panel.add(resultLabel);

        add(panel);

        manuallyAddCurrencies();
        fetchExchangeRates();

        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double amount = Double.parseDouble(amountField.getText());
                    String from = fromCurrency.getSelectedItem().toString();
                    String to = toCurrency.getSelectedItem().toString();
                    double rateFrom = rates.getOrDefault(from, 1.0);
                    double rateTo = rates.getOrDefault(to, 1.0);
                    double result = amount * rateTo / rateFrom;
                    resultLabel.setText(amount + " " + from + " = " + result + " " + to);
                } catch (NumberFormatException ex) {
                    resultLabel.setText("Invalid input. Please enter a valid number.");
                }
            }
        });
    }

    private void manuallyAddCurrencies() {
        String[] currencies = {"USD", "EUR", "INR", "GBP", "AUD", "CAD", "SGD", "CHF", "JPY", "CNY"};
        for (String currency : currencies) {
            fromCurrency.addItem(currency);
            toCurrency.addItem(currency);
        }
    }

    private void fetchExchangeRates() {
        try {
            URL url = new URL(baseUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject ratesObject = jsonResponse.getJSONObject("conversion_rates");

            rates.clear();
            rates.put("USD", ratesObject.getDouble("USD"));
            rates.put("EUR", ratesObject.getDouble("EUR"));
            rates.put("INR", ratesObject.getDouble("INR"));
            rates.put("GBP", ratesObject.getDouble("GBP"));
            rates.put("AUD", ratesObject.getDouble("AUD"));
            rates.put("CAD", ratesObject.getDouble("CAD"));
            rates.put("SGD", ratesObject.getDouble("SGD"));
            rates.put("CHF", ratesObject.getDouble("CHF"));
            rates.put("JPY", ratesObject.getDouble("JPY"));
            rates.put("CNY", ratesObject.getDouble("CNY"));

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(CurrencyConverter.this, "Failed to fetch exchange rates. Please check your internet connection and API key.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CurrencyConverter().setVisible(true);
        });
    }
}
