package software.ulpgc.moneycalculator.architecture.control;

import software.ulpgc.moneycalculator.architecture.exceptions.ExchangeRateConnectionException;
import software.ulpgc.moneycalculator.architecture.exceptions.ExchangeRateReadingException;
import software.ulpgc.moneycalculator.architecture.io.ExchangeRateLoader;
import software.ulpgc.moneycalculator.architecture.model.Currency;
import software.ulpgc.moneycalculator.architecture.model.ExchangeRate;
import software.ulpgc.moneycalculator.architecture.model.Money;
import software.ulpgc.moneycalculator.architecture.ui.CurrencyDialog;
import software.ulpgc.moneycalculator.architecture.ui.DateDialog;
import software.ulpgc.moneycalculator.architecture.ui.MoneyDialog;
import software.ulpgc.moneycalculator.architecture.ui.MoneyDisplay;

import javax.swing.*;
import java.time.LocalDate;

public class ExchangeMoneyCommand implements Command {
    private final MoneyDialog moneyDialog;
    private final CurrencyDialog currencyDialog;
    private final DateDialog dateDialog;
    private final ExchangeRateLoader exchangeRateLoader;
    private final MoneyDisplay moneyDisplay;

    public ExchangeMoneyCommand(MoneyDialog moneyDialog, CurrencyDialog currencyDialog, DateDialog dateDialog, ExchangeRateLoader exchangeRateLoader, MoneyDisplay moneyDisplay) {
        this.moneyDialog = moneyDialog;
        this.currencyDialog = currencyDialog;
        this.dateDialog = dateDialog;
        this.exchangeRateLoader = exchangeRateLoader;
        this.moneyDisplay = moneyDisplay;
    }

    @Override
    public void execute() {
        Money money = moneyDialog.get();
        Currency currency = currencyDialog.get();
        try {
            LocalDate date = dateDialog.get();
            ExchangeRate exchangeRate = exchangeRateLoader.load(money.currency(), currency, date);
            Money result = new Money(money.amount() * exchangeRate.rate(), currency);
            moneyDisplay.show(result);
        } catch (ExchangeRateConnectionException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "ERROR: Could not connect to the exchange rate service",
                        "Connection Error",
                    JOptionPane.ERROR_MESSAGE
            );
            moneyDisplay.show(new Money(0, currency));
        } catch (ExchangeRateReadingException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "ERROR: Could not find the requested data",
                        "Data Error",
                    JOptionPane.ERROR_MESSAGE
            );
            moneyDisplay.show(new Money(0, currency));
        }
    }
}
