
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

        JButton publishButton = new JButton("Publish Resource");
        publishButton.addActionListener(e -> publishResource());

        if (!"Teacher".equalsIgnoreCase(role)) {
            publishButton.setVisible(false);
        }

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel headerButtons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );
        headerButtons.setOpaque(false);
        headerButtons.add(publishButton);
        headerButtons.add(refreshButton);

        header.add(title, BorderLayout.WEST);
        header.add(headerButtons, BorderLayout.EAST);

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
        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.addActionListener(e -> deleteSelected());

        if (!"Teacher".equalsIgnoreCase(role)) {
            deleteButton.setVisible(false);
        }

        JPanel footer = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );
        footer.setOpaque(false);

        footer.add(downloadButton);
        footer.add(deleteButton);

        panel.add(footer, BorderLayout.SOUTH);

        loadResources();
    }

    private void loadResources() {
        model.setRowCount(0);

        try {
            FileClientService service = new FileClientService();

            resources = service.getPublishedResources(sessionToken);

            for (SharedResource resource : resources) {
                model.addRow(new Object[]{
                        resource.getFileId(),
                        resource.getResourceType(),
                        resource.getTitle(),
                        resource.getFileName(),
                        resource.getDueDate().isBlank()
                                ? "-"
                                : resource.getDueDate()
                });
            }

            if (resources.isEmpty()) {
                model.addRow(new Object[]{
                        "-", "-", "No published resources yet", "-", "-"
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    panel,
                    "Could not load resources:\n" + e.getMessage(),
                    "Resource Loading Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void downloadSelected() {
        int row = table.getSelectedRow();

        if (row == -1 || row >= resources.size()) {
            JOptionPane.showMessageDialog(
                    panel,
                    "Please select a resource first."
            );
            return;
        }

        SharedResource resource = resources.get(row);

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose Download Location");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (chooser.showSaveDialog(panel) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            boolean success = new FileClientService().downloadFile(
                    resource.getFileId(),
                    chooser.getSelectedFile().getAbsolutePath(),
                    sessionToken
            );

            JOptionPane.showMessageDialog(
                    panel,
                    success ? "Resource downloaded successfully."
                            : "Download failed. Check the server response."
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    panel,
                    "Download error:\n" + e.getMessage(),
                    "Download Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    private void publishResource() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Note or Assignment");

        if (chooser.showOpenDialog(panel) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        JTextField titleField = new JTextField();
        JComboBox<String> typeBox =
                new JComboBox<>(new String[]{"NOTE", "ASSIGNMENT"});
        JTextArea descriptionArea = new JTextArea(4, 20);
        JTextField deadlineField = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));
        form.add(new JLabel("Title:"));
        form.add(titleField);
        form.add(new JLabel("Type:"));
        form.add(typeBox);
        form.add(new JLabel("Description (optional):"));
        form.add(new JScrollPane(descriptionArea));
        form.add(new JLabel("Deadline (optional: yyyy-MM-dd HH:mm:ss):"));
        form.add(deadlineField);

        int choice = JOptionPane.showConfirmDialog(
                panel, form, "Publish Resource",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        String title = titleField.getText().trim();
        String type = (String) typeBox.getSelectedItem();
        String description = descriptionArea.getText().trim();
        String deadline = deadlineField.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Please enter a title.");
            return;
        }

        try {
            if (!deadline.isEmpty()) {
                deadline = java.sql.Timestamp.valueOf(deadline).toString();
            }

            FileClientService service = new FileClientService();

            int fileId = service.uploadResourceFile(
                    chooser.getSelectedFile().getAbsolutePath(),
                    sessionToken
            );

            boolean published = service.publishResourceToServer(
                    fileId,
                    type,
                    title,
                    description,
                    deadline,
                    sessionToken
            );

            if (published) {
                JOptionPane.showMessageDialog(
                        panel, "Resource published successfully."
                );
                loadResources();
            } else {
                JOptionPane.showMessageDialog(
                        panel,
                        "Upload succeeded, but publishing failed. File ID: " + fileId,
                        "Publishing Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    panel,
                    "Invalid deadline. Use yyyy-MM-dd HH:mm:ss.",
                    "Invalid Deadline",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    panel,
                    "Could not publish resource:\n" + e.getMessage(),
                    "Publishing Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public JPanel getPanel() {
        return panel;
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();

        if (row < 0 || row >= resources.size()) {
            JOptionPane.showMessageDialog(
                    panel,
                    "Please select a published resource first.",
                    "No Resource Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        SharedResource resource = resources.get(row);

        int confirmation = JOptionPane.showConfirmDialog(
                panel,
                "Are you sure you want to delete this resource?\n\n"
                        + resource.getTitle()
                        + "\nFile: " + resource.getFileName(),
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            FileClientService service = new FileClientService();

            boolean success = service.deleteFile(
                    resource.getFileId(),
                    sessionToken
            );

            if (success) {
                JOptionPane.showMessageDialog(
                        panel,
                        "Resource deleted successfully.",
                        "Deletion Successful",
                        JOptionPane.INFORMATION_MESSAGE
                );

                loadResources();
            } else {
                JOptionPane.showMessageDialog(
                        panel,
                        "Deletion failed. You may not own this file, "
                                + "or it may already be inactive.",
                        "Deletion Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    panel,
                    "Could not delete resource:\n" + e.getMessage(),
                    "Deletion Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

}
