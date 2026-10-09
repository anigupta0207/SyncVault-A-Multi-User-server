
 package Teacher;


import client.FileClientService;
import file.SharedResource;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SharedResourcesPanel {

    private final JPanel panel;
    private final String sessionToken;
    private final String role;

    private final DefaultTableModel model;
    private final JTable table;

    private List<SharedResource> resources;

    public SharedResourcesPanel(
            String sessionToken,
            String role
    ) {
        this.sessionToken = sessionToken;
        this.role = role;

        panel = new JPanel(new BorderLayout(0, 15));
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        panel.setBackground(new Color(248, 249, 251));

        JLabel title = new JLabel("Notes & Assignments");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadResources());

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.WEST);
        header.add(refreshButton, BorderLayout.EAST);

        panel.add(header, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{
                        "ID", "TYPE", "TITLE",
                        "FILE", "DEADLINE"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(35);

        panel.add(
                new JScrollPane(table),
                BorderLayout.CENTER
        );

        JButton downloadButton = new JButton("Download Selected");
        downloadButton.addActionListener(e -> downloadSelected());

        JPanel footer = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );
        footer.setOpaque(false);
        footer.add(downloadButton);

        panel.add(footer, BorderLayout.SOUTH);

        loadResources();
    }

    private void loadResources() {
        model.setRowCount(0);

        // Client-server loading will be connected here next.
        model.addRow(new Object[]{
                "-", "-", "Connecting resource service next",
                "-", "-"
        });
    }

    private void downloadSelected() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                    panel,
                    "Please select a resource first."
            );
            return;
        }

        JOptionPane.showMessageDialog(
                panel,
                "The resource download connection will be added next."
        );
    }

    public JPanel getPanel() {
        return panel;
    }
}