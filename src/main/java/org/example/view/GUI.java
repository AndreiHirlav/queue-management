package org.example.view;
import org.example.ConcreteStrategyQueue;
import org.example.ConcreteStrategyTime;
import org.example.SimulationManager;
import org.example.Strategy;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;
public class GUI extends JFrame{
    private JTextArea inputTime = new JTextArea(1, 10);
    private JTextArea inputClients = new JTextArea(1, 10);
    private JTextArea inputQueues = new JTextArea(1, 10);
    private JTextArea inputMinArrival = new JTextArea(1, 10);
    private JTextArea inputMaxArrival = new JTextArea(1, 10);
    private JTextArea inputMinService = new JTextArea(1, 10);
    private JTextArea inputMaxService = new JTextArea(1, 10);
    private JComboBox<String> strategySelection = new JComboBox<>(new String[]{"Go to shortest queue", "Go to fastest queue"});
    private JTextArea resultsArea = new JTextArea(5, 20);
    private JButton startButton = new JButton("Start simulation");

    public GUI() {
        this.setSize(400, 800);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Queue Management Application");
        this.setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        this.add(configureInputPanel());
        this.add(selectStrategy());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(startButton);
        this.add(buttonPanel);
        this.add(configureResultPanel());
        this.setVisible(true);
    }
    public JPanel configureInputPanel() {
        JPanel inputPanel = new JPanel( new GridLayout(7, 2, 10, 10));
        inputPanel.add(new JLabel("Enter time limit: "));
        inputPanel.add(inputTime);
        inputPanel.add(new JLabel("Enter number of clients: "));
        inputPanel.add(inputClients);
        inputPanel.add(new JLabel("Enter number of queues: "));
        inputPanel.add(inputQueues);
        inputPanel.add(new JLabel("Enter minimum arrival time: "));
        inputPanel.add(inputMinArrival);
        inputPanel.add(new JLabel("Enter maximum arrival time: "));
        inputPanel.add(inputMaxArrival);
        inputPanel.add(new JLabel("Enter minimum service time: "));
        inputPanel.add(inputMinService);
        inputPanel.add(new JLabel("Enter maximum service time: "));
        inputPanel.add(inputMaxService);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSimulation();
            }
        });

        return inputPanel;
    }

    public JPanel selectStrategy() {
        JPanel selectStrategy = new JPanel();
        selectStrategy.add(new JLabel("Select strategy:"));
        selectStrategy.add(strategySelection);

        return selectStrategy;
    }

    public JPanel configureResultPanel() {
        JPanel resultsPanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane= new JScrollPane(resultsArea);
        resultsArea.setEditable(false);
        resultsPanel.setBackground(new Color(155, 201, 246));
        resultsArea.setBackground(new Color(218, 217, 217));
        resultsPanel.add(new JLabel("Event log: "), BorderLayout.NORTH);
        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        return resultsPanel;
    }

    private void startSimulation() {
        int timeLimit = Integer.parseInt((inputTime.getText()));
        int numberOfClients = Integer.parseInt(inputClients.getText());
        int numberOfQueues = Integer.parseInt(inputQueues.getText());
        int minArrivalTime = Integer.parseInt(inputMinArrival.getText());
        int maxArrivalTime = Integer.parseInt(inputMaxArrival.getText());
        int minServiceTime = Integer.parseInt(inputMinService.getText());
        int maxServiceTime = Integer.parseInt(inputMaxService.getText());
        String strat = (String) strategySelection.getSelectedItem();
        Strategy strategy = selectStrategy(strat);
        SimulationManager simulationManager = new SimulationManager(timeLimit, maxServiceTime, minServiceTime, minArrivalTime, maxArrivalTime, numberOfQueues, numberOfClients, strategy, resultsArea);
        Thread thread = new Thread(simulationManager);
        thread.start();
    }

    //in interfata iau strategia sub forma de string, si in functie de val stringului stabilesc strategia
    private Strategy selectStrategy(String strat) {
        if(strat.equals("Go to shortest queue")) {
            return new ConcreteStrategyQueue();
        } else if(strat.equals("Go to fastest queue")) {
            return new ConcreteStrategyTime();
        }

        //in caz de orice returneaza asta
        return new ConcreteStrategyQueue();
    }

}
