package GUI;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import automationFramework.IExtractor;
import automationFramework.SeleniumExtractor;

import java.awt.Font;
import java.awt.Color;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainForm {

	private JFrame _frame;
	private JTextField txtTesterID;
	private JTextField txtTesterPassword;
	private JLabel lblLoginArea;
	private JLabel lblExamDetails;
	private JLabel lblExamDate;
	private JTextField textExamDate;
	private JLabel lblExamNumber;
	private JTextField textExamNumber;
	private JButton btnSunbit;

	public String _testerId;
	public String _testerPass;
	public String _examDate;
	public String _examNumber;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainForm window = new MainForm();
					window._frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public String getTesterId() {
		return this._testerId;
	}

	/**
	 * Create the application.
	 */
	public MainForm() {
		_examDate = "";
		_examNumber = "";
		_testerId = "";
		_testerPass = "";
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		_frame = new JFrame();
		_frame.setResizable(false);
		_frame
		.setTitle("Automation Metazmen Extractor");
		_frame.setBounds(100, 100, 450, 300);
		_frame
		.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		_frame.getContentPane().setLayout(null);

		JLabel lblTesterId = new JLabel("Tester ID");
		lblTesterId.setBounds(10, 80, 76, 14);
		_frame.getContentPane().add(lblTesterId);

		JLabel lblTesterPass = new JLabel("Tester Password");
		lblTesterPass.setBounds(10, 105, 104, 14);
		_frame.getContentPane().add(lblTesterPass);

		txtTesterID = new JTextField();
		txtTesterID.setToolTipText("Type your ID number");
		txtTesterID.setBounds(114, 77, 86, 20);
		_frame.getContentPane().add(txtTesterID);
		txtTesterID.setColumns(10);

		lblLoginArea = new JLabel("System Login");
		lblLoginArea.setForeground(new Color(0, 0, 139));
		lblLoginArea.setFont(lblLoginArea.getFont().deriveFont(
				lblLoginArea.getFont().getStyle() | Font.BOLD));
		lblLoginArea.setBounds(10, 55, 95, 14);
		_frame.getContentPane().add(lblLoginArea);

		lblExamDetails = new JLabel("Exam Details");
		lblExamDetails.setForeground(new Color(0, 0, 139));
		lblExamDetails.setFont(lblExamDetails.getFont().deriveFont(
				lblExamDetails.getFont().getStyle() | Font.BOLD));
		lblExamDetails.setBounds(10, 145, 76, 14);
		_frame.getContentPane().add(lblExamDetails);

		lblExamDate = new JLabel("Exam Date");
		lblExamDate.setBounds(10, 170, 95, 14);
		_frame.getContentPane().add(lblExamDate);

		textExamDate = new JTextField();
		textExamDate.setToolTipText("dd/mm/yyyy");
		textExamDate.setBounds(114, 167, 86, 20);
		_frame.getContentPane().add(textExamDate);
		textExamDate.setColumns(10);

		lblExamNumber = new JLabel("Exam Number");
		lblExamNumber.setBounds(10, 195, 95, 14);
		_frame.getContentPane().add(lblExamNumber);

		textExamNumber = new JTextField();
		textExamNumber.setToolTipText("Type the exam number");
		textExamNumber.setBounds(114, 192, 86, 20);
		_frame.getContentPane().add(textExamNumber);
		textExamNumber.setColumns(10);

		btnSunbit = new JButton("Submit");
		btnSunbit.setToolTipText("Submit Data");
		btnSunbit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				_testerId = txtTesterID.getText();
				_testerPass = txtTesterPassword.getText();
				_examDate = textExamDate.getText();
				_examNumber = textExamNumber.getText();
				// Close form
				_frame.dispose();
				// Get exam files
				IExtractor obj = new SeleniumExtractor(_testerId,
													   _testerPass,
													   _examDate,
													   _examNumber);
				obj.Extract();
			}
		});
		btnSunbit.setBounds(10, 227, 89, 23);
		_frame.getContentPane().add(btnSunbit);

		txtTesterPassword = new JTextField();
		txtTesterPassword.setToolTipText("Type your ID number");
		txtTesterPassword.setColumns(10);
		txtTesterPassword.setBounds(114, 105, 86, 20);
		_frame.getContentPane().add(txtTesterPassword);
	}
}
