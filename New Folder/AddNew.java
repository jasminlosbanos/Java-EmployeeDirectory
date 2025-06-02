package jas;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class AddNew extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField NameTbx;
	private JTextField ContactTbx;
	private EmployeeDirectory parentDirectory;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AddNew frame = new AddNew(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public AddNew(EmployeeDirectory edParent) {
		this.parentDirectory = edParent;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 308);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("ADD NEW EMPLOYEE");
		lblNewLabel.setFont(new Font("Century Gothic", Font.BOLD, 16));
		lblNewLabel.setBounds(10, 11, 414, 32);
		contentPane.add(lblNewLabel);
		
		JLabel lblFullName = new JLabel("FULL NAME");
		lblFullName.setFont(new Font("Century Gothic", Font.BOLD, 12));
		lblFullName.setBounds(10, 54, 414, 25);
		contentPane.add(lblFullName);
		
		NameTbx = new JTextField();
		NameTbx.setFont(new Font("Century", Font.PLAIN, 12));
		NameTbx.setBounds(10, 80, 414, 20);
		contentPane.add(NameTbx);
		NameTbx.setColumns(10);
		
		JLabel lblDepartment = new JLabel("DEPARTMENT");
		lblDepartment.setFont(new Font("Century Gothic", Font.BOLD, 12));
		lblDepartment.setBounds(10, 111, 414, 25);
		contentPane.add(lblDepartment);
		
		String[] departments = {"Finance", "Marketing", "IT", "Sales"};
		JComboBox DeptCbx = new JComboBox(departments);
		DeptCbx.setBounds(10, 136, 414, 22);
		DeptCbx.setSelectedIndex(-1);
		contentPane.add(DeptCbx);
		
		JLabel lblContactNo = new JLabel("CONTACT NO.");
		lblContactNo.setFont(new Font("Century Gothic", Font.BOLD, 12));
		lblContactNo.setBounds(10, 175, 414, 25);
		contentPane.add(lblContactNo);
		
		ContactTbx = new JTextField();
		ContactTbx.setFont(new Font("Century", Font.PLAIN, 12));
		ContactTbx.setColumns(10);
		ContactTbx.setBounds(10, 201, 414, 20);
		contentPane.add(ContactTbx);
		
		JButton AddBtn = new JButton("ADD");
		AddBtn.setFont(new Font("Tahoma", Font.PLAIN, 12));
		AddBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = NameTbx.getText();
				String dept = (String)DeptCbx.getSelectedItem();
				String contact = ContactTbx.getText();
				
				if (name.equals("") || dept == null || contact.equals("")) {
					JOptionPane.showMessageDialog(contentPane, "Fill up fields...");
				} else {
					Employee employee = new Employee(name, dept, contact);
					AddEmployee(employee);
					parentDirectory.LoadEmployees();
					dispose();
				}
			}
		});
		AddBtn.setBounds(14, 232, 195, 23);
		contentPane.add(AddBtn);
		
		JButton CancelBtn = new JButton("CANCEL");
		CancelBtn.setFont(new Font("Tahoma", Font.PLAIN, 12));
		CancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		CancelBtn.setBounds(223, 232, 195, 23);
		contentPane.add(CancelBtn);
	}
	
	public void AddEmployee(Employee em) {
		boolean found = false;
		String isExisting = "SELECT * FROM employees WHERE name = ?";
		String add = "INSERT INTO employees (name, department, contactNo) VALUES (?,?,?)";
		
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(isExisting);
			pstmt.setString(1, em.getName());
			ResultSet res = pstmt.executeQuery();
			
			while (res.next()) {
				found = true;
				int response = JOptionPane.showConfirmDialog(contentPane, "Employee " + em.getName() + " already exist. Continue adding employee?", "Confirm Add", JOptionPane.YES_NO_OPTION);
				
				if(response == JOptionPane.YES_OPTION) {
					PreparedStatement addExistingStmt = conn.prepareStatement(add);
					addExistingStmt.setString(1, em.getName().toLowerCase());
					addExistingStmt.setString(2, em.getDept());
					addExistingStmt.setString(3, em.getContact());
					addExistingStmt.executeUpdate();
					
					JOptionPane.showMessageDialog(contentPane, "Employee successfully added...");
				} else {
					return;
				}
			}
			
			if (!found) {
				PreparedStatement addNewStmt = conn.prepareStatement(add);
				addNewStmt.setString(1, em.getName().toLowerCase());
				addNewStmt.setString(2, em.getDept());
				addNewStmt.setString(3, em.getContact());
				addNewStmt.executeUpdate();
				
				JOptionPane.showMessageDialog(contentPane, "Employee successfully added...");
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(contentPane, "Failed: " + ex.getMessage());
		}
	}
}

class Employee {
	String name;
	String dept;
	String contact;
	
	public Employee(String name, String dept, String contact) {
		this.name = name;
		this.dept = dept;
		this.contact = contact;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDept() {
		return dept;
	}
	
	public String getContact() {
		return contact;
	}
}
