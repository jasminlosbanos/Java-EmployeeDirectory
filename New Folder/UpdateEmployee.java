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
import java.awt.event.ActionEvent;
import java.awt.Font;

public class UpdateEmployee extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField NameTbx;
	private JTextField ContactTbx;
	private EmployeeDirectory parentDirectory;

	/**
	 * Create the frame.
	 */
	public UpdateEmployee(EmployeeDirectory edParent, String name, String dept, String contact, int id) {
		this.parentDirectory = edParent;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 295);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("UPDATE EMPLOYEE");
		lblNewLabel.setFont(new Font("Century Gothic", Font.BOLD, 16));
		lblNewLabel.setBounds(10, 11, 414, 22);
		contentPane.add(lblNewLabel);
		
		JLabel lblName = new JLabel("NAME:");
		lblName.setFont(new Font("Century Gothic", Font.BOLD, 12));
		lblName.setBounds(10, 44, 414, 14);
		contentPane.add(lblName);
		
		JLabel lblDepartment = new JLabel("DEPARTMENT:");
		lblDepartment.setFont(new Font("Century Gothic", Font.BOLD, 12));
		lblDepartment.setBounds(10, 100, 414, 14);
		contentPane.add(lblDepartment);
		
		JLabel lblContactNo = new JLabel("CONTACT NO.:");
		lblContactNo.setFont(new Font("Century Gothic", Font.BOLD, 12));
		lblContactNo.setBounds(10, 158, 414, 14);
		contentPane.add(lblContactNo);
		
		NameTbx = new JTextField(name.toUpperCase());
		NameTbx.setFont(new Font("Century", Font.PLAIN, 12));
		NameTbx.setBounds(10, 69, 414, 20);
		contentPane.add(NameTbx);
		NameTbx.setColumns(10);
		
		ContactTbx = new JTextField(contact);
		ContactTbx.setFont(new Font("Century", Font.PLAIN, 12));
		ContactTbx.setColumns(10);
		ContactTbx.setBounds(10, 183, 414, 20);
		contentPane.add(ContactTbx);
		
		String[] departments = {"Finance", "Marketing", "IT", "Sales"};
		JComboBox DeptCbx = new JComboBox(departments);
		DeptCbx.setFont(new Font("Century", Font.PLAIN, 12));
		DeptCbx.setSelectedItem(dept);
		DeptCbx.setBounds(10, 125, 414, 22);
		contentPane.add(DeptCbx);
		
		JButton SaveBtn = new JButton("SAVE");
		SaveBtn.setFont(new Font("Tahoma", Font.PLAIN, 12));
		SaveBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String newName = NameTbx.getText();
				String newDept = (String)DeptCbx.getSelectedItem();
				String newContact = ContactTbx.getText();
				
				if(name.equals("") || dept == null || contact.equals("")) {
					JOptionPane.showMessageDialog(contentPane, "Fill up all fields...");
				} else {
					UpdateEmployee(newName, newDept, newContact, id);
				}
			}
		});
		SaveBtn.setBounds(16, 222, 193, 23);
		contentPane.add(SaveBtn);
		
		JButton CancelBtn = new JButton("CANCEL");
		CancelBtn.setFont(new Font("Tahoma", Font.PLAIN, 12));
		CancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		CancelBtn.setBounds(225, 222, 193, 23);
		contentPane.add(CancelBtn);
	}
	
	public void UpdateEmployee(String name, String dept, String contact, int id) {
		String update = "UPDATE employees SET name=?, department=?, contactNo=? WHERE id=?";
		
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement updatePstmt = conn.prepareStatement(update);
			updatePstmt.setString(1, name.toLowerCase());
			updatePstmt.setString(2, dept);
			updatePstmt.setString(3, contact);
			updatePstmt.setInt(4, id);
			int affected = updatePstmt.executeUpdate();
			
			if (affected > 0) {
				JOptionPane.showMessageDialog(contentPane, "Successfully updated employee's information!");
				parentDirectory.LoadEmployees();
				dispose();
			} else {
				JOptionPane.showMessageDialog(contentPane, "Failed to update... Try again...");
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(contentPane, "Failed to update employee: " + e.getMessage());
		}
	}
}
