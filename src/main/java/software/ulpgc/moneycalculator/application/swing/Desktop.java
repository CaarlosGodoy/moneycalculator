package software.ulpgc.moneycalculator.application.swing;

import software.ulpgc.moneycalculator.architecture.control.Command;
import software.ulpgc.moneycalculator.architecture.exceptions.ExchangeRateReadingException;
import software.ulpgc.moneycalculator.architecture.model.Currency;
import software.ulpgc.moneycalculator.architecture.model.Money;
import software.ulpgc.moneycalculator.architecture.ui.*;

import javax.swing.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.awt.*;
import java.util.Map;

public class Desktop extends JFrame {
    private final Map<String, Command> commands;
    private final List<Currency> currencies;

    private final JPanel exchangePanel;
    private final JPanel evolutionPanel;
    private final JPanel mainPanel;
    private final CardLayout cardLayout;
    private JPanel chartPanel;

    private JLabel title;
    private JTextField inputAmount;
    private JComboBox<Currency> inputCurrency;
    private JTextField outputAmount;
    private JComboBox<Currency> outputCurrency;
    private JComboBox<Integer> inputDay;
    private JComboBox<Integer> inputMonth;
    private JComboBox<Integer> inputYear;

    private JComboBox<Currency> selectedCurrency;
    private JComboBox<Currency> compareCurrency;
    private JComboBox<Integer> toYear;
    private JComboBox<Integer> fromYear;
    private JComboBox<Integer> gap;

    public Desktop(List<Currency> currencies) throws HeadlessException {
        this.commands = new HashMap<>();
        this.currencies = currencies;
        this.setTitle("Money Calculator");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 600);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setLayout(new BorderLayout());

        this.getContentPane().add(sidePanel(), BorderLayout.WEST);

        mainPanel = new JPanel(new CardLayout());
        this.exchangePanel = exchangePanel();
        this.evolutionPanel = evolutionPanel();
        mainPanel.add(exchangePanel, "exchange");
        mainPanel.add(evolutionPanel, "evolution");

        this.getContentPane().add(mainPanel, BorderLayout.CENTER);
        cardLayout = (CardLayout) mainPanel.getLayout();
        cardLayout.show(mainPanel, "exchange");
    }

    private JLabel label(String text, int size, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, size));
        label.setBounds(x, y, width, height);
        return label;
    }
    private JLabel label(String text, int size, int x, int y, int width, int height, Color c) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, size));
        label.setForeground(c);
        label.setBounds(x, y, width, height);
        return label;
    }

    // SIDE PANEL
    private JPanel sidePanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(250, 600));
        panel.setBackground(new Color(76, 0, 76));
        panel.add(addTitle());
        panel.add(subMenuBottons());
        return panel;
    }
    private JPanel addTitle() {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(new Color(76, 0, 76));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(80, 0, 20, 0));
        titlePanel.add(title = new JLabel("MONEY CALCULATOR"));
        title.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        title.setForeground(Color.WHITE);
        return titlePanel;
    }
    private JPanel subMenuBottons() {
        JPanel subMenuPanel = new JPanel(new GridLayout(2, 1, 30, 10));
        subMenuPanel.setBackground(new Color(76, 0, 76));
        subMenuPanel.setBounds(15, 100, 200, 40);
        subMenuPanel.add(exchangeRateBtn());
        subMenuPanel.add(seeEvolutionBtn());
        return subMenuPanel;
    }
    private JButton exchangeRateBtn() {
        JButton btn = sideBtn("EXCHANGE RATE");
        btn.addActionListener(e -> {
            cardLayout.show(mainPanel, "exchange");
            revalidate();
            repaint();
        });
        return btn;
    }
    private JButton seeEvolutionBtn() {
        JButton btn = sideBtn("SEE EVOLUTION");
        btn.addActionListener(e -> {
            cardLayout.show(mainPanel, "evolution");
            revalidate();
            repaint();
        });
        return btn;
    }
    private JButton sideBtn(String s) {
        JButton btn = new JButton(s);
        btn.setBackground(new Color(120, 60, 120));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBorder(null);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(100, 40, 100));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(120, 60, 120));
            }
        });

        return btn;
    }

    // EXCHANGE PANEL
    private JPanel exchangePanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(null);

        panel.add(label("Money Calculator", 20, 75, 30, 300, 35));
        panel.add(label("Currency Exchange", 14, 75, 75, 300, 25));
        panel.add(label("Amount", 12, 75, 135, 80, 20, new Color(100, 149, 237)));
        panel.add(inputAmount = amountInput());
        panel.add(label("From Currency", 12, 205, 135, 80, 20, new Color(100, 149, 237)));
        panel.add(inputCurrency = currencySelector(205));
        panel.add(label("To Currency", 12, 335, 135, 100, 20, new Color(100, 149, 237)));
        panel.add(outputCurrency = currencySelector(335));
        panel.add(label("Day", 12, 465, 135, 50, 20, new Color(100, 149, 237)));
        panel.add(inputDay = daySelector(465));
        panel.add(label("Month", 12, 525, 135, 50, 20, new Color(100, 149, 237)));
        panel.add(inputMonth = monthSelector(525));
        panel.add(label("Year", 12, 585, 135, 70, 20, new Color(100, 149, 237)));
        panel.add(inputYear = yearSelector(585));

        panel.add(calculateButton());

        JPanel resultPanel = new JPanel();
        resultPanel.setBackground(new Color(230, 240, 255));
        resultPanel.setBounds(75, 290, 460, 50);
        resultPanel.setLayout(new GridLayout(1, 2, 15, 0));

        JLabel resultLabel = new JLabel("  Converted Amount");
        resultLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resultPanel.add(resultLabel);

        resultPanel.add(outputAmount = amountOutput());

        panel.add(resultPanel);

        return panel;
    }

    private JButton calculateButton() {
        JButton btn = new JButton("Exchange");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setBackground(new Color(76, 0, 76));
        btn.setForeground(Color.WHITE);
        btn.setBounds(75, 200, 580, 40);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> commands.get("exchange").execute());

        return actionBtn(btn);
    }

    // EVOLUTION PANEL
    private JPanel evolutionPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(null);

        panel.add(label("Money Calculator", 20, 75, 30, 300, 35));
        panel.add(label("Currency Evolution", 14, 75, 75, 300, 25));
        panel.add(label("Currency", 12, 75, 135, 80, 20, new Color(100, 149, 237)));
        panel.add(selectedCurrency = currencySelector(75));
        panel.add(label("Compare To", 12, 205, 135, 80, 20, new Color(100, 149, 237)));
        panel.add(compareCurrency = currencySelector(205));
        panel.add(label("From", 12, 335, 135, 70, 20, new Color(100, 149, 237)));
        panel.add(fromYear = yearSelector(335));
        panel.add(label("To", 12, 415, 135, 70, 20, new Color(100, 149, 237)));
        panel.add(toYear = yearSelector(415));
        panel.add(label("Gap", 12, 495, 135, 70, 20, new Color(100, 149, 237)));
        panel.add(gap = gapSelector(495));
        panel.add(chartButton());

        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(Color.LIGHT_GRAY);
        chartPanel.setBounds(75, 200, 580, 350);
        panel.add(chartPanel);

        return panel;
    }

    private JButton chartButton() {
        JButton btn = new JButton("Generate");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(new Color(76, 0, 76));
        btn.setForeground(Color.WHITE);
        btn.setBounds(555, 155, 100, 30);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> commands.get("chart").execute());

        return actionBtn(btn);
    }

    private JButton actionBtn(JButton btn) {
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(56, 0, 56));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(76, 0, 76));
            }
        });
        return btn;
    }

    private JComboBox<Integer> gapSelector(int x) {
        JComboBox<Integer> dayJComboBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        dayJComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dayJComboBox.setBounds(x, 155, 50, 30);
        return dayJComboBox;
    }

    public CurrencyDialog selectedCurrencyDialog() {
        return this::selectedCurrency;
    }

    private Currency selectedCurrency() {
        return (Currency) selectedCurrency.getSelectedItem();
    }

    public CurrencyDialog compareCurrencyDialog() {
        return this::compareCurrency;
    }

    private Currency compareCurrency() {
        return (Currency) compareCurrency.getSelectedItem();
    }

    public IntegerDialog fromYearDialog() {
        return this::fromYear;
    }

    private int fromYear() {
        return (int) fromYear.getSelectedItem();
    }

    public IntegerDialog toYearDialog() {
        return this::toYear;
    }

    private int toYear() {
        return (int) toYear.getSelectedItem();
    }

    public IntegerDialog gapDialog() {
        return this::gap;
    }

    private int gap() {
        return (int) gap.getSelectedItem();
    }

    public JPanel chartDisplayPanel() {
        return this.chartPanel;
    }

    private JTextField amountInput() {
        JTextField textField = new JTextField("100");
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textField.setBounds(75, 155, 120, 30);
        textField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        return textField;
    }
    private JTextField amountOutput() {
        JTextField textField = new JTextField("0.0");
        textField.setFont(new Font("Segoe UI", Font.BOLD, 14));
        textField.setEditable(false);
        textField.setBorder(null);
        textField.setBackground(new Color(230, 240, 255));
        return textField;
    }

    private JComboBox<Currency> currencySelector(int x) {
        JComboBox<Currency> currencyJComboBox = new JComboBox<>(toArray(currencies));
        currencyJComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        currencyJComboBox.setBounds(x, 155, 120, 30);
        return currencyJComboBox;
    }
    private Currency[] toArray(List<Currency> currencies) {
        return currencies.toArray(new Currency[0]);
    }

    private JComboBox<Integer> daySelector(int x) {
        JComboBox<Integer> dayJComboBox = new JComboBox<>(createArray(1, 32));
        dayJComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dayJComboBox.setBounds(x, 155, 50, 30);
        return dayJComboBox;
    }
    private JComboBox<Integer> monthSelector(int x) {
        JComboBox<Integer> monthJComboBox = new JComboBox<>(createArray(1, 13));
        monthJComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        monthJComboBox.setBounds(x, 155, 50, 30);
        return monthJComboBox;
    }
    private JComboBox<Integer> yearSelector(int x) {
        JComboBox<Integer> yearJComboBox = new JComboBox<>(createArray(2000, 2026));
        yearJComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        yearJComboBox.setBounds(x, 155, 70, 30);
        return yearJComboBox;
    }
    private Integer[] createArray(int from, int to) {
        Integer[] vals = new Integer[to - from];
        int i = 0;
        while (from < to) {
            vals[i] = from;
            i++;
            from++;
        }
        return vals;
    }

    public void addCommand(String name, Command command) {
        this.commands.put(name, command);
    }

    public MoneyDialog moneyDialog() {
        return () -> new Money(inputAmount(), inputCurrency());
    }

    public CurrencyDialog currencyDialog() {
        return this::outputCurrency;
    }

    public DateDialog dateDialog() {
        return this::inputDate;
    }

    public MoneyDisplay moneyDisplay() {
        return money -> outputAmount.setText(money.amount() + "");
    }

    private double inputAmount() {
        return toDouble(inputAmount.getText());
    }

    private double toDouble(String text) {
        return Double.parseDouble(text);
    }

    private Currency inputCurrency() {
        return (Currency) inputCurrency.getSelectedItem();
    }

    private Currency outputCurrency() {
        return (Currency) outputCurrency.getSelectedItem();
    }

    private LocalDate inputDate() {
        try {
            return LocalDate.of(
                    (int) inputYear.getSelectedItem(),
                    (int) inputMonth.getSelectedItem(),
                    (int) inputDay.getSelectedItem()
            );
        } catch (DateTimeException e) {
            throw new ExchangeRateReadingException(e);
        }
    }
}