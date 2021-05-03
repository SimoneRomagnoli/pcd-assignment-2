package part2.view;

import io.vertx.core.Future;
import part2.api.model.*;
import part2.controller.InputListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.stream.IntStream;

/**
 * GUI component of the view.
 *
 */
public class ViewFrame extends JFrame implements ActionListener {

	private static final int WIDTH = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth()/1.1);
	private static final int HEIGHT = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()/1.25);

	private static final int COLS = 10;

	private static final Object[] SOLUTIONS_TABLE_COLUMNS = { "Departure", "Arrival", "Scales", "Trains" };

	private static final Object[] DETAILS_TABLE_COLUMNS = { "Train" , "From", "To", "Departure", "Arrival", "Duration" };

	private static final Object[] MONITOR_TABLE_COLUMNS = { "Train", "From", "To", "Date", "Departure", "Arrival", "Delay" };

	//TRAVEL SEARCH
	private JLabel travelLabel;
	private JLabel travelFromLabel;
	private JTextField travelFrom;
	private JLabel travelToLabel;
	private JTextField travelTo;
	private JLabel travelInDateLabel;
	private JTextField travelInDate;
	private JLabel travelFromTimeLabel;
	private JTextField travelFromTime;
	private JButton travelSearchButton;

	//GET TRAIN INFO
	private JLabel trainLabel;
	private JLabel trainCodeLabel;
	private JTextField trainCode;
	private JLabel trainOriginLabel;
	private JTextField trainOrigin;
	private JButton trainInfoButton;

	//GET STATION INFO
	private JLabel stationLabel;
	private JLabel stationCodeLabel;
	private JTextField stationCode;
	private ButtonGroup stationArrivalsOrDepartures;
	private JRadioButton stationArrivals;
	private JRadioButton stationDepartures;
	private JButton stationInfoButton;

	//TRAVEL SOLUTIONS
	private JLabel travelTableLabel;
	private JScrollPane travelTableContainer;
	private JTable travelTable;
	private List<Travel> currentTravels;

	//SOLUTION DETAILS
	private JButton detailsButton;
	private JLabel detailsTravelLabel;
	private JScrollPane detailsTableContainer;
	private JTable detailsTable;

	//MONITORING
	private JButton travelTableMonitorButton;
	private JLabel monitorTravelLabel;
	private JScrollPane monitorTableContainer;
	private JTable monitorTable;
	private JButton monitorStopButton;
	
	private InputListener listener;

	public ViewFrame(){
		super("View");
		setSize(600,400);

		this.createTravelInput();
		this.createTrainInfoInput();
		this.createStationInfoInput();
		this.createTravelOutput();
		this.createMonitoringOutput();
		this.createDetailOutput();

		this.setSize(WIDTH, HEIGHT);
		setResizable(false);
		this.setLayout(new BorderLayout());
		this.setVisible(true);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public void setInputListener(InputListener l){
		this.listener = l;
	}

	public void actionPerformed(ActionEvent ev){
		Object src = ev.getSource();
		if(this.travelSearchButton.equals(src)) {
			final String origin = this.travelFrom.getText().toUpperCase();
			final String destination = this.travelTo.getText().toUpperCase();
			final String date = this.travelInDate.getText();
			final int time = Integer.parseInt(this.travelFromTime.getText());
			Future<List<Travel>> travelSolutions = this.travelSearch(origin, destination, date, time);
			travelSolutions.onSuccess(travels -> {
				SwingUtilities.invokeLater( () -> {
					this.currentTravels = new ArrayList<>(travels);
					DefaultTableModel model = (DefaultTableModel) this.travelTable.getModel();
					IntStream.generate(() -> 0).limit(model.getRowCount()).forEach(model::removeRow);
					for(Travel travel:travels) {
						model.addRow(new String[] {
								travel.getDepartureTime()+"    "+travel.getDepartureDate(),
								travel.getArrivalTime()+"    "+travel.getArrivalDate(),
								String.valueOf(travel.getScales()),
								travel.getTrainList().toString().replace("[", "").replace("]", "")
						});
					}

					this.detailsButton.setEnabled(true);
					this.travelTableMonitorButton.setEnabled(true);
				});
			});
		}
		if(this.trainInfoButton.equals(src)) {
			final String trainCode = this.trainCode.getText();
			final String stationCode = this.trainOrigin.getText();
			this.trainInfo(trainCode, stationCode);
		}
		if(this.stationInfoButton.equals(src)) {
			final String stationCode = this.stationCode.getText();
			final StationStatus.ArrivalsOrDepartures arrivalsOrDepartures = this.stationArrivals.isSelected() ? StationStatus.ArrivalsOrDepartures.ARRIVALS : StationStatus.ArrivalsOrDepartures.DEPARTURES;
			this.stationInfo(stationCode, arrivalsOrDepartures);
		}
		if(this.travelTableMonitorButton.equals(src) && this.travelTableMonitorButton.isEnabled()) {
			final Travel monitoredTravel = this.currentTravels.get(this.travelTable.getSelectedRow());
			this.monitorTravelLabel.setText("Monitoring solution "+monitoredTravel.getSolutionId());
			System.out.println(monitoredTravel.getSolutionId());
			//for(monitoredTravel.)
		}
		if(this.detailsButton.equals(src)) {
			List<TravelDetails> details = this.currentTravels.get(this.travelTable.getSelectedRow()).getDetails().get();
			DefaultTableModel model = (DefaultTableModel) this.detailsTable.getModel();
			IntStream.generate(() -> 0).limit(model.getRowCount()).forEach(model::removeRow);
			for(TravelDetails detail: details) {
				model.addRow(new String[] {
						detail.getIdentifier(),
						detail.getDepartureStation(),
						detail.getArrivalStation(),
						detail.getDepartureTime(),
						detail.getArrivalTime(),
						detail.getDuration()
				});
			}
		}
	}

	private Future<List<Travel>> travelSearch(String origin, String destination, String date, int time) {
		return this.listener.searchTravel(origin, destination, date, time);
	}

	private Future<Train> trainInfo(String trainCode, String stationCode) {
		return this.listener.trainInfo(trainCode, stationCode);
	}

	private Future<Station> stationInfo(String stationCode, StationStatus.ArrivalsOrDepartures arrivalsOrDepartures) {
		return this.listener.stationInfo(stationCode, arrivalsOrDepartures);
	}

	private void createTravelInput() {
		//TITLE
		this.travelLabel = new JLabel("Search your travel: ");
		this.travelLabel.setBounds((int)(WIDTH*0.025), (int)(HEIGHT*0.05), (int)(WIDTH*0.3), (int)(HEIGHT*0.05));
		this.add(this.travelLabel);

		//FROM
		this.travelFromLabel = new JLabel("From: ");
		this.travelFromLabel.setBounds((int)(WIDTH*0.05), (int)(HEIGHT*0.1), (int)(WIDTH*0.05), (int)(HEIGHT*0.05));
		this.travelFrom = new JTextField(COLS);
		this.travelFrom.setBounds((int)(WIDTH*0.075), (int)(HEIGHT*0.1), (int)(WIDTH*0.1), (int)(HEIGHT*0.05));
		this.add(this.travelFromLabel);
		this.add(this.travelFrom);

		//TO
		this.travelToLabel = new JLabel("To: ");
		this.travelToLabel.setBounds((int)(WIDTH*0.2), (int)(HEIGHT*0.1), (int)(WIDTH*0.1), (int)(HEIGHT*0.05));
		this.travelTo = new JTextField(COLS);
		this.travelTo.setBounds((int)(WIDTH*0.24), (int)(HEIGHT*0.1), (int)(WIDTH*0.1), (int)(HEIGHT*0.05));
		this.add(this.travelToLabel);
		this.add(this.travelTo);

		//DATE
		this.travelInDateLabel = new JLabel("Date: ");
		this.travelInDateLabel.setBounds((int)(WIDTH*0.05), (int)(HEIGHT*0.15), (int)(WIDTH*0.05), (int)(HEIGHT*0.05));
		this.travelInDate = new JTextField(COLS);
		this.travelInDate.setBounds((int)(WIDTH*0.075), (int)(HEIGHT*0.15), (int)(WIDTH*0.1), (int)(HEIGHT*0.05));
		this.travelInDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime()));
		this.add(this.travelInDateLabel);
		this.add(this.travelInDate);

		//TIME
		this.travelFromTimeLabel = new JLabel("Time: ");
		this.travelFromTimeLabel.setBounds((int)(WIDTH*0.2), (int)(HEIGHT*0.15), (int)(WIDTH*0.1), (int)(HEIGHT*0.05));
		this.travelFromTime = new JTextField(COLS);
		this.travelFromTime.setBounds((int)(WIDTH*0.24), (int)(HEIGHT*0.15), (int)(WIDTH*0.1), (int)(HEIGHT*0.05));
		this.travelFromTime.setText(new SimpleDateFormat("HH").format(Calendar.getInstance().getTime()));
		this.add(this.travelFromTimeLabel);
		this.add(this.travelFromTime);

		//SEARCH
		this.travelSearchButton = new JButton("Search");
		this.travelSearchButton.setBounds((int)(WIDTH*0.05), (int)(HEIGHT*0.225), (int)(WIDTH*0.1), (int)(HEIGHT*0.05));
		this.travelSearchButton.addActionListener(this);
		this.add(this.travelSearchButton);
	}

	private void createTrainInfoInput() {
		//TITLE
		this.trainLabel = new JLabel("Get real time train info: ");
		this.trainLabel.setBounds((int)(WIDTH*0.025), (int)(HEIGHT*0.35), (int)(WIDTH*0.3), (int)(HEIGHT*0.05));
		this.add(this.trainLabel);

		//CODE
		this.trainCodeLabel = new JLabel("Train: ");
		this.trainCodeLabel.setBounds((int)(WIDTH*0.05), (int)(HEIGHT*0.4), (int)(WIDTH*0.1), (int)(HEIGHT*0.05));
		this.trainCode = new JTextField(COLS);
		this.trainCode.setBounds((int)(WIDTH*0.075), (int)(HEIGHT*0.4), (int)(WIDTH*0.1), (int)(HEIGHT*0.05));
		this.add(this.trainCodeLabel);
		this.add(this.trainCode);

		//ORIGIN STATION
		this.trainOriginLabel = new JLabel("Origin: ");
		this.trainOriginLabel.setBounds((int)(WIDTH*0.2), (int)(HEIGHT*0.4), (int)(WIDTH*0.2), (int)(HEIGHT*0.05));
		this.trainOrigin = new JTextField(COLS);
		this.trainOrigin.setBounds((int)(WIDTH*0.24), (int)(HEIGHT*0.4), (int)(WIDTH*0.1), (int)(HEIGHT*0.05));
		this.add(this.trainOriginLabel);
		this.add(this.trainOrigin);

		//GET INFO
		this.trainInfoButton = new JButton("Get Info");
		this.trainInfoButton.setBounds((int)(WIDTH*0.05), (int)(HEIGHT*0.475), (int)(WIDTH*0.1), (int)(HEIGHT*0.05));
		this.trainInfoButton.addActionListener(this);
		this.add(this.trainInfoButton);
	}

	private void createStationInfoInput() {
		//TITLE
		this.stationLabel = new JLabel("Get real time station info: ");
		this.stationLabel.setBounds((int)(WIDTH*0.025), (int)(HEIGHT*0.65), (int)(WIDTH*0.3), (int)(HEIGHT*0.05));
		this.add(this.stationLabel);

		//CODE
		this.stationCodeLabel = new JLabel("Station: ");
		this.stationCodeLabel.setBounds((int)(WIDTH*0.05), (int)(HEIGHT*0.7), (int)(WIDTH*0.1), (int)(HEIGHT*0.05));
		this.stationCode = new JTextField(COLS);
		this.stationCode.setBounds((int)(WIDTH*0.1), (int)(HEIGHT*0.7), (int)(WIDTH*0.1), (int)(HEIGHT*0.05));
		this.add(this.stationCodeLabel);
		this.add(this.stationCode);

		//ARRIVALS OR DEPARTURES
		this.stationArrivalsOrDepartures = new ButtonGroup();
		this.stationArrivals = new JRadioButton("Arrivi");
		this.stationArrivals.setSelected(true);
		this.stationArrivals.setActionCommand("Arrivi");
		this.stationArrivals.setBounds((int)(WIDTH*0.225), (int)(HEIGHT*0.7), (int)(WIDTH*0.05), (int)(HEIGHT*0.05));
		this.stationDepartures = new JRadioButton("Partenze");
		this.stationDepartures.setSelected(false);
		this.stationDepartures.setBounds((int)(WIDTH*0.275), (int)(HEIGHT*0.7), (int)(WIDTH*0.1), (int)(HEIGHT*0.05));
		this.stationArrivalsOrDepartures.add(this.stationArrivals);
		this.stationArrivalsOrDepartures.add(this.stationDepartures);
		this.stationArrivals.addActionListener(this);
		this.stationDepartures.addActionListener(this);
		this.add(this.stationArrivals);
		this.add(this.stationDepartures);

		//GET INFO
		this.stationInfoButton = new JButton("Get Info");
		this.stationInfoButton.setBounds((int)(WIDTH*0.05), (int)(HEIGHT*0.775), (int)(WIDTH*0.1), (int)(HEIGHT*0.05));
		this.stationInfoButton.addActionListener(this);
		this.add(this.stationInfoButton);
	}

	private void createTravelOutput() {
		//TITLE
		this.travelTableLabel = new JLabel("Travel solutions: ");
		this.travelTableLabel.setBounds((int)(WIDTH*0.375), (int)(HEIGHT*0.05), (int)(WIDTH*0.4), (int)(HEIGHT*0.05));
		this.add(this.travelTableLabel);

		//TABLE
		this.travelTable = new JTable(new DefaultTableModel(SOLUTIONS_TABLE_COLUMNS, 0));
		this.travelTableContainer = new JScrollPane(this.travelTable);
		final int panelWidth = (int)(WIDTH*0.6);
		this.travelTableContainer.setBounds((int)(WIDTH*0.375), (int)(HEIGHT*0.1), panelWidth, (int)(HEIGHT*0.15));
		this.travelTable.setFillsViewportHeight(true);
		this.travelTable.getColumnModel().getColumn(0).setPreferredWidth((int)(0.2*panelWidth));
		this.travelTable.getColumnModel().getColumn(1).setPreferredWidth((int)(0.2*panelWidth));
		this.travelTable.getColumnModel().getColumn(2).setPreferredWidth((int)(0.05*panelWidth));
		this.travelTable.getColumnModel().getColumn(3).setPreferredWidth((int)(0.5*panelWidth));
		this.travelTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		this.add(this.travelTableContainer);
	}

	private void createMonitoringOutput() {
		//MONITOR BUTTON
		this.travelTableMonitorButton = new JButton("Monitor");
		this.travelTableMonitorButton.setBounds((int)(WIDTH*0.5), (int)(HEIGHT*0.3), (int)(WIDTH*0.1), (int)(HEIGHT*0.05));
		this.travelTableMonitorButton.addActionListener(this);
		this.add(this.travelTableMonitorButton);
		this.travelTableMonitorButton.setEnabled(false);

		//TITLE
		this.monitorTravelLabel = new JLabel("Monitoring off");
		this.monitorTravelLabel.setBounds((int)(WIDTH*0.375), (int)(HEIGHT*0.6), (int)(WIDTH*0.4), (int)(HEIGHT*0.05));
		this.add(this.monitorTravelLabel);

		//TABLE
		this.monitorTable = new JTable(new DefaultTableModel(MONITOR_TABLE_COLUMNS, 0));
		this.monitorTableContainer = new JScrollPane(this.monitorTable);
		final int panelWidth = (int)(WIDTH*0.6);
		this.monitorTableContainer.setBounds((int)(WIDTH*0.375), (int)(HEIGHT*0.65), panelWidth, (int)(HEIGHT*0.15));
		this.monitorTable.setFillsViewportHeight(true);
		this.monitorTable.getColumnModel().getColumn(0).setPreferredWidth((int)(0.2*panelWidth));
		this.monitorTable.getColumnModel().getColumn(1).setPreferredWidth((int)(0.2*panelWidth));
		this.monitorTable.getColumnModel().getColumn(2).setPreferredWidth((int)(0.2*panelWidth));
		this.monitorTable.getColumnModel().getColumn(3).setPreferredWidth((int)(0.2*panelWidth));
		this.monitorTable.getColumnModel().getColumn(4).setPreferredWidth((int)(0.2*panelWidth));
		this.monitorTable.getColumnModel().getColumn(5).setPreferredWidth((int)(0.2*panelWidth));
		this.monitorTable.getColumnModel().getColumn(6).setPreferredWidth((int)(0.1*panelWidth));
		this.monitorTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		this.add(this.monitorTableContainer);

		//STOP
		this.monitorStopButton = new JButton("Stop");
		this.monitorStopButton.setBounds((int)(WIDTH*0.375), (int)(HEIGHT*0.85), (int)(WIDTH*0.1), (int)(HEIGHT*0.05));
		this.monitorStopButton.addActionListener(this);
		this.add(this.monitorStopButton);
		this.monitorStopButton.setEnabled(false);
	}

	private void createDetailOutput() {
		//DETAILS BUTTON
		this.detailsButton = new JButton("Open detail");
		this.detailsButton.setBounds((int)(WIDTH*0.375), (int)(HEIGHT*0.3), (int)(WIDTH*0.1), (int)(HEIGHT*0.05));
		this.detailsButton.addActionListener(this);
		this.add(this.detailsButton);
		this.detailsButton.setEnabled(false);

		//TITLE
		this.detailsTravelLabel = new JLabel("Details:");
		this.detailsTravelLabel.setBounds((int)(WIDTH*0.375), (int)(HEIGHT*0.35), (int)(WIDTH*0.4), (int)(HEIGHT*0.05));
		this.add(this.detailsTravelLabel);

		//TABLE
		this.detailsTable = new JTable(new DefaultTableModel(DETAILS_TABLE_COLUMNS, 0));
		this.detailsTableContainer = new JScrollPane(this.detailsTable);
		final int panelWidth = (int)(WIDTH*0.6);
		this.detailsTableContainer.setBounds((int)(WIDTH*0.375), (int)(HEIGHT*0.4), panelWidth, (int)(HEIGHT*0.15));
		this.detailsTable.setFillsViewportHeight(true);
		this.detailsTable.getColumnModel().getColumn(0).setPreferredWidth((int)(0.2*panelWidth));
		this.detailsTable.getColumnModel().getColumn(1).setPreferredWidth((int)(0.2*panelWidth));
		this.detailsTable.getColumnModel().getColumn(2).setPreferredWidth((int)(0.2*panelWidth));
		this.detailsTable.getColumnModel().getColumn(3).setPreferredWidth((int)(0.2*panelWidth));
		this.detailsTable.getColumnModel().getColumn(4).setPreferredWidth((int)(0.2*panelWidth));
		this.detailsTable.getColumnModel().getColumn(5).setPreferredWidth((int)(0.2*panelWidth));
		this.detailsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		this.add(this.detailsTableContainer);
	}
}
	
