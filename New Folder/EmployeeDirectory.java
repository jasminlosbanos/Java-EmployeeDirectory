package jas;

import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.PublicKey;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class EmployeeDirectory extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable TblEmployees;
	private DefaultTableModel model;
	private JLabel lblNewLabel;
	private JTextField SearchTbx;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				EmployeeDirectory frame = new EmployeeDirectory();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public EmployeeDirectory() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 709, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 91, 673, 309);
		contentPane.add(scrollPane);

		String[] columnNames = {"ID", "NAME", "DEPARTMENT", "CONTACT"};
		model = new DefaultTableModel(columnNames, 0);
		TblEmployees = new JTable(model);
		TblEmployees.setFont(new Font("Century", Font.PLAIN, 12));
		scrollPane.setViewportView(TblEmployees);

		TblEmployees.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int selectedRow = TblEmployees.getSelectedRow();

				if (selectedRow >= 0) {
					String idStr = TblEmployees.getValueAt(selectedRow, 0).toString();
					String name = TblEmployees.getValueAt(selectedRow, 1).toString();
					String dept = TblEmployees.getValueAt(selectedRow, 2).toString();
					String contact = TblEmployees.getValueAt(selectedRow, 3).toString();

					int option = JOptionPane.showOptionDialog(
						contentPane,
						"What do you want to do with employee \"" + name + "\"?",
						"Employee Options",
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						new String[]{"Delete", "Update", "Cancel"},
						"Cancel"
					);

					if (option == 0) {
						DeleteEmployee(Integer.parseInt(idStr));
					} else if (option == 1) {
						new UpdateEmployee(EmployeeDirectory.this, name, dept, contact, Integer.parseInt(idStr)).setVisible(true);
					}
				}
			}
		});

		JButton AddBtn = new JButton("ADD NEW");
		AddBtn.setFont(new Font("Tahoma", Font.PLAIN, 12));
		AddBtn.setBackground(new Color(255, 255, 255));
		AddBtn.setForeground(SystemColor.desktop);
		AddBtn.setBounds(561, 62, 122, 23);
		contentPane.add(AddBtn);
		
		lblNewLabel = new JLabel("SEARCH");
		lblNewLabel.setFont(new Font("Century Gothic", Font.BOLD, 12));
		lblNewLabel.setBounds(10, 66, 71, 14);
		contentPane.add(lblNewLabel);
		
		SearchTbx = new JTextField();
		SearchTbx.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String searchValue = SearchTbx.getText();
				
				if (!searchValue.equals("")) {
					SearchEmployee(searchValue);
				} else {
					LoadEmployees();
				}
			}
		});
		SearchTbx.setBounds(69, 63, 482, 20);
		contentPane.add(SearchTbx);
		SearchTbx.setColumns(10);
		
		lblNewLabel_1 = new JLabel("EMPLOYEE DIRECTORY");
		lblNewLabel_1.setFont(new Font("Century", Font.BOLD, 16));
		lblNewLabel_1.setBounds(10, 11, 673, 14);
		contentPane.add(lblNewLabel_1);
		
		lblNewLabel_2 = new JLabel("PROGRAMMED BY: JASMIN KATE LOSBAÑOS");
		lblNewLabel_2.setFont(new Font("Century", Font.ITALIC, 11));
		lblNewLabel_2.setBounds(10, 36, 673, 14);
		contentPane.add(lblNewLabel_2);

		AddBtn.addActionListener(e -> new AddNew(EmployeeDirectory.this).setVisible(true));

		LoadEmployees();
	}

	public void LoadEmployees() {
		model.setRowCount(0);
		boolean found = false;
		String query = "SELECT * FROM employees";

		try (Connection connection = DBConnection.getConnection();
			 Statement stmt = connection.createStatement();
			 ResultSet res = stmt.executeQuery(query)) {

			while (res.next()) {
				found = true;
				model.addRow(new Object[]{
					res.getString("id"),
					res.getString("name").toUpperCase(),
					res.getString("department"),
					res.getString("contactNo")
				});
			}

			if (!found) {
				model.addRow(new Object[]{"No employees added...", "", "", ""});
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(TblEmployees, "Failed to load employees: " + e.getMessage());
		}
	}

	public void DeleteEmployee(int id) {
		String deleteSQL = "DELETE FROM employees WHERE id=?";

		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement deleteStmt = conn.prepareStatement(deleteSQL)) {

			deleteStmt.setInt(1, id);
			int affected = deleteStmt.executeUpdate();

			if (affected > 0) {
				JOptionPane.showMessageDialog(contentPane, "Employee deleted!");
				LoadEmployees();
			} else {
				JOptionPane.showMessageDialog(contentPane, "Employee not found.");
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(contentPane, "Failed to delete employee: " + e.getMessage());
		}
	}
	
	public void SearchEmployee(String searchValue) {
		model.setRowCount(0);
		boolean found = false;
		String search = "%" + searchValue + "%";
		String searchQuery = "SELECT * FROM employees WHERE name LIKE ?";
		
		try {
			Connection conn = DBConnection.getConnection();
			PreparedStatement sPstmt = conn.prepareStatement(searchQuery);
			sPstmt.setString(1, search);
			ResultSet res = sPstmt.executeQuery();
			
			while (res.next()) {
				found = true;
				model.addRow(new Object[] {
						res.getString("id"),
						res.getString("name").toUpperCase(),
						res.getString("department"),
						res.getString("contactNo")
				});
			}
			
			if (!found) {
				model.addRow(new Object[] {"No employee found.."});
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(contentPane, "Failed to search employee: " + e.getMessage());
		}
	}
}
