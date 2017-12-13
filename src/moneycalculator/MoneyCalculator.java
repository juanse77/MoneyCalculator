package moneycalculator;

import model.Currency;
import model.Money;
import model.ExchangeRate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MoneyCalculator {

    public static void main(String[] args) throws Exception {
        MoneyCalculator moneyCalculator = new MoneyCalculator();
        moneyCalculator.execute();
    }
    private Map<String, Currency> currencies = new HashMap<>();
    private Money money;
    private Currency currencyTo;
    private ExchangeRate exchangeRate;

    public MoneyCalculator() {
        currencies.put("USD", new Currency("USD", "Dolar americano", "$"));
        currencies.put("EUR", new Currency("EUR", "Euro", "€"));
    }

    private void execute() throws Exception {
        input();
        process();
        output();
    }

    private void input() {
        System.out.println("Introduzca cantidad");
        Scanner scanner = new Scanner(System.in);
        double amount = Double.parseDouble(scanner.next());

        System.out.println("Introduzca codigo divisa origen");
        Currency currency = currencies.get(scanner.next().toUpperCase());

        money = new Money(amount, currency);

        System.out.println("Introduzca codigo divisa destino");
        currencyTo = currencies.get(scanner.next().toUpperCase());
    }

    private void process() throws Exception {
        exchangeRate = getExchangeRate(money.getCurrency(), currencyTo);
    }

    private void output() {
        System.out.println(money.getAmount() + money.getCurrency().getSymbol() + " equivalen a " + money.getAmount() * exchangeRate.getRate() + currencyTo.getSymbol());
    }

    private static ExchangeRate getExchangeRate(Currency from, Currency to) throws Exception {
        URL url = new URL("http://api.fixer.io/latest?base=" + from.getCode() + "&symbols=" + to.getCode());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        InputStreamReader input = new InputStreamReader(connection.getInputStream());
        try (BufferedReader reader = new BufferedReader(input)) {
            String line = reader.readLine();
            line = line.substring(line.indexOf(to.getCode()) + 5, line.indexOf("}"));
            return new ExchangeRate(new Date(), from, to, Double.parseDouble(line));
        }
    }
}
