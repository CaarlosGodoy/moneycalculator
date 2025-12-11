package software.ulpgc.moneycalculator.application.swing;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import software.ulpgc.moneycalculator.architecture.exceptions.ExchangeRateConnectionException;
import software.ulpgc.moneycalculator.architecture.exceptions.ExchangeRateReadingException;
import software.ulpgc.moneycalculator.architecture.model.Currency;
import software.ulpgc.moneycalculator.architecture.model.ExchangeRate;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WebService {
    private static final String ApiKey = "fca_live_U9Iuy24lwWEJ9xu635NAOTAbu35kcAD456fbJpFl";
    private static final String ApiCurrenciesUrl = "https://api.freecurrencyapi.com/v1/currencies?apikey=API-KEY".replace("API-KEY", ApiKey);
    private static final String ApiHistoricalUrl = "https://api.freecurrencyapi.com/v1/historical?apikey=API-KEY&date=FECHA&base_currency=FROM&currencies=TO".replace("API-KEY", ApiKey);

    public static class CurrencyLoader implements software.ulpgc.moneycalculator.architecture.io.CurrencyLoader {

        @Override
        public List<Currency> loadAll() {
            try {
                return readCurrencies();
            } catch (IOException e) {
                return List.of();
            }
        }

        private List<Currency> readCurrencies() throws IOException {
            try (InputStream is = openInputStream(createConnection())) {
                return readCurrenciesWith(jsonIn(is));
            }
        }

        private List<Currency> readCurrenciesWith(String json) {
            return readCurrenciesWith(jsonObjectIn(json));
        }

        private List<Currency> readCurrenciesWith(JsonObject jsonObject) {
            Map<String, JsonElement> data = jsonObject.get("data").getAsJsonObject().asMap();
            List<Currency> list = new ArrayList<>();
            for (String key : data.keySet()) {
                list.add(readCurrencyWith(key, data.get(key).getAsJsonObject().get("name").getAsString()));
            }
            return list;
        }

        private Currency readCurrencyWith(String key, String val) {
            return new Currency(
                    key, val
            );
        }

        private static String jsonIn(InputStream is) throws IOException {
            return new String(is.readAllBytes());
        }

        private static JsonObject jsonObjectIn(String json) {
            return new Gson().fromJson(json, JsonObject.class);
        }

        private InputStream openInputStream(URLConnection connection) throws IOException {
            return connection.getInputStream();
        }

        private static URLConnection createConnection() throws IOException {
            URL url = new URL((ApiCurrenciesUrl));
            return url.openConnection();
        }
    }

    public static class ExchangeRateLoader implements software.ulpgc.moneycalculator.architecture.io.ExchangeRateLoader {
        @Override
        public ExchangeRate load(Currency from, Currency to, LocalDate date) {
            try {
                String url = ApiHistoricalUrl.replace("FECHA", date.toString()).replace("FROM", from.code()).replace("TO", to.code());
                return new ExchangeRate(
                    date,
                    from,
                    to,
                    readConversionRate(new URL(url), date.toString(), to.code())
                );
            } catch (IOException e) {
                throw new ExchangeRateConnectionException(e);
            }
        }

        private double readConversionRate(URL url, String f, String c) throws IOException {
            return readConversionRate(url.openConnection(), f, c);
        }

        private double readConversionRate(URLConnection connection, String f, String c) throws IOException {
            try (InputStream inputStream = connection.getInputStream()) {
                return readConversionRate(new String(new BufferedInputStream(inputStream).readAllBytes()), f, c);
            }
        }

        private double readConversionRate(String json, String f, String c) {
            return readConversionRate(new Gson().fromJson(json, JsonObject.class), f, c);
        }

        private double readConversionRate(JsonObject jsonObject, String f, String c) {
            try {
                return jsonObject.get("data").getAsJsonObject().get(f).getAsJsonObject().get(c).getAsDouble();
            } catch (IllegalStateException e) {
                throw new ExchangeRateReadingException(e);
            }
        }
    }
}
