package software.ulpgc.moneycalculator.application.swing;

import software.ulpgc.moneycalculator.architecture.control.ExchangeMoneyCommand;
import software.ulpgc.moneycalculator.architecture.control.SeeEvolutionCommand;

public class Main {
    public static void main(String[] args) {
        Desktop desktop = new Desktop(new WebService.CurrencyLoader().loadAll());
        desktop.addCommand("exchange", new ExchangeMoneyCommand(
                desktop.moneyDialog(),
                desktop.currencyDialog(),
                desktop.dateDialog(),
                new WebService.ExchangeRateLoader(),
                desktop.moneyDisplay()
        ));
        desktop.addCommand("chart", new SeeEvolutionCommand(
                desktop.selectedCurrencyDialog(),
                desktop.compareCurrencyDialog(),
                desktop.fromYearDialog(),
                desktop.toYearDialog(),
                desktop.gapDialog(),
                new WebService.ExchangeRateLoader(),
                desktop.chartDisplayPanel()
        ));
        desktop.setVisible(true);
    }
}
