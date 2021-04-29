package part2.view;

import part2.controller.InputListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * GUI component of the view.
 *
 */
public class ViewFrame extends JFrame implements ActionListener {

	private static final int WIDTH = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2.5);
	private static final int HEIGHT = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()/1.25);

	private static final int COLS = 10;

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
	
	private ArrayList<InputListener> listeners;

	public ViewFrame(){
		super("View");
		setSize(600,400);
		listeners = new ArrayList<>();

		this.createTravelInput();

		this.setSize(WIDTH, HEIGHT);
		setResizable(false);
		this.setLayout(new BorderLayout());
		this.setVisible(true);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public void addListener(InputListener l){
		listeners.add(l);
	}

	public void actionPerformed(ActionEvent ev){
		Object src = ev.getSource();
		if(this.travelSearchButton.equals(src)) {
			final String origin = this.travelFrom.getText().toUpperCase();
			final String destination = this.travelTo.getText().toUpperCase();
			final String date = this.travelInDate.getText();
			final int time = Integer.parseInt(this.travelFromTime.getText());
			this.notifyTravelSearch(origin, destination, date, time);
		} else {}

	}

	private void notifyTravelSearch(String origin, String destination, String date, int time) {
		for(InputListener listener: this.listeners) {
			listener.searchTravel(origin, destination, date, time);
		}
	}
	
	public void update() {
		SwingUtilities.invokeLater(() -> {

		});
	}
	
	public void done() {
		SwingUtilities.invokeLater(() -> {

		});
	}

	private void createTravelInput() {
		//TITLE
		this.travelLabel = new JLabel("Search your travel: ");
		this.travelLabel.setBounds((int)(WIDTH*0.05), (int)(HEIGHT*0.05), WIDTH, (int)(HEIGHT*0.05));
		this.add(this.travelLabel);

		//FROM
		this.travelFromLabel = new JLabel("From: ");
		this.travelFromLabel.setBounds((int)(WIDTH*0.1), (int)(HEIGHT*0.1), (int)(WIDTH*0.1), (int)(HEIGHT*0.05));
		this.travelFrom = new JTextField(COLS);
		this.travelFrom.setBounds((int)(WIDTH*0.15), (int)(HEIGHT*0.1), (int)(WIDTH*0.3), (int)(HEIGHT*0.05));
		this.add(this.travelFromLabel);
		this.add(this.travelFrom);

		//TO
		this.travelToLabel = new JLabel("To: ");
		this.travelToLabel.setBounds((int)(WIDTH*0.5), (int)(HEIGHT*0.1), (int)(WIDTH*0.2), (int)(HEIGHT*0.05));
		this.travelTo = new JTextField(COLS);
		this.travelTo.setBounds((int)(WIDTH*0.6), (int)(HEIGHT*0.1), (int)(WIDTH*0.3), (int)(HEIGHT*0.05));
		this.add(this.travelToLabel);
		this.add(this.travelTo);

		//DATE
		this.travelInDateLabel = new JLabel("Date: ");
		this.travelInDateLabel.setBounds((int)(WIDTH*0.1), (int)(HEIGHT*0.15), (int)(WIDTH*0.1), (int)(HEIGHT*0.05));
		this.travelInDate = new JTextField(COLS);
		this.travelInDate.setBounds((int)(WIDTH*0.15), (int)(HEIGHT*0.15), (int)(WIDTH*0.3), (int)(HEIGHT*0.05));
		this.travelInDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime()));
		this.add(this.travelInDateLabel);
		this.add(this.travelInDate);

		//TIME
		this.travelFromTimeLabel = new JLabel("From time: ");
		this.travelFromTimeLabel.setBounds((int)(WIDTH*0.5), (int)(HEIGHT*0.15), (int)(WIDTH*0.2), (int)(HEIGHT*0.05));
		this.travelFromTime = new JTextField(COLS);
		this.travelFromTime.setBounds((int)(WIDTH*0.6), (int)(HEIGHT*0.15), (int)(WIDTH*0.3), (int)(HEIGHT*0.05));
		this.travelFromTime.setText(new SimpleDateFormat("HH").format(Calendar.getInstance().getTime()));
		this.add(this.travelFromTimeLabel);
		this.add(this.travelFromTime);

		//SEARCH
		this.travelSearchButton = new JButton("Search");
		this.travelSearchButton.setBounds((int)(WIDTH*0.15), (int)(HEIGHT*0.2), (int)(WIDTH*0.2), (int)(HEIGHT*0.05));
		this.travelSearchButton.addActionListener(this);
		this.add(this.travelSearchButton);
	}

}
	
