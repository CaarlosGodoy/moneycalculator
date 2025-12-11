package software.ulpgc.moneycalculator.architecture.control;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;
import software.ulpgc.moneycalculator.architecture.exceptions.ExchangeRateConnectionException;
import software.ulpgc.moneycalculator.architecture.exceptions.ExchangeRateReadingException;
import software.ulpgc.moneycalculator.architecture.io.ExchangeRateLoader;
import software.ulpgc.moneycalculator.architecture.model.Currency;
import software.ulpgc.moneycalculator.architecture.model.ExchangeRate;
import software.ulpgc.moneycalculator.architecture.ui.CurrencyDialog;
import software.ulpgc.moneycalculator.architecture.ui.IntegerDialog;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class SeeEvolutionCommand implements Command {
    private final CurrencyDialog currencyDialog;
    private final CurrencyDialog compareCurrencyDialog;
    private final IntegerDialog fromDialog;
    private final IntegerDialog toDialog;
    private final IntegerDialog gapDialog;
    private final ExchangeRateLoader exchangeRateLoader;
    private final JPanel chartDisplayPanel;

    public SeeEvolutionCommand(CurrencyDialog currencyDialog, CurrencyDialog compareCurrencyDialog, IntegerDialog fromDialog, IntegerDialog toDialog, IntegerDialog gapDialog, ExchangeRateLoader exchangeRateLoader, JPanel chartDisplayPanel) {
        this.currencyDialog = currencyDialog;
        this.compareCurrencyDialog = compareCurrencyDialog;
        this.fromDialog = fromDialog;
        this.toDialog = toDialog;
        this.gapDialog = gapDialog;
        this.exchangeRateLoader = exchangeRateLoader;
        this.chartDisplayPanel = chartDisplayPanel;
    }

    @Override
    public void execute() {
        Currency currency = currencyDialog.get();
        Currency compareTo = compareCurrencyDialog.get();
        int from = fromDialog.get();
        int to = toDialog.get();
        int gap = gapDialog.get();

        TimeSeries serieDivisa = new TimeSeries(currency.code() + "/" + compareTo.code());
        for (int year = from; year < to; year+=gap) {
            try {
                ExchangeRate rate = exchangeRateLoader.load(currency, compareTo, LocalDate.of(year, 1, 1));
                serieDivisa.add(new Year(year), rate.rate());
            } catch (ExchangeRateConnectionException e) {
                JOptionPane.showMessageDialog(
                        null,
                        "ERROR: Could not connect to the exchange rate service",
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE
                );
                displayPanel();
                return;
            } catch (ExchangeRateReadingException e) {
                JOptionPane.showMessageDialog(
                        null,
                        "ERROR: Could not find the requested data",
                        "Data Error",
                        JOptionPane.ERROR_MESSAGE
                );
                displayPanel();
                return;
            }
        }
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(serieDivisa);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Evolution " + currency.code() + " vs " + compareTo.code() + " (" + from + "-" + to + ")",
                "Year",
                "Exchange Rate (" + compareTo.code() + ")",
                dataset,
                true, true, false
        );

        displayPanel(chart);
    }

    private void displayPanel(JFreeChart chart) {
        SwingUtilities.invokeLater(() -> {
            ChartPanel chartPanelComponent = new ChartPanel(chart);
            chartDisplayPanel.removeAll();
            chartDisplayPanel.add(chartPanelComponent, BorderLayout.CENTER);
            chartDisplayPanel.revalidate();
            chartDisplayPanel.repaint();
        });
    }
    private void displayPanel() {
        SwingUtilities.invokeLater(() -> {
            ChartPanel chartPanelComponent = new ChartPanel(null);
            chartDisplayPanel.removeAll();
            chartDisplayPanel.revalidate();
            chartDisplayPanel.repaint();
        });
    }
}
