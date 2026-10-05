package Teacher;
import client.FileClientService;
import client.RequestClientService;
import request.Request;

import javax.swing.*;
        import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
        import java.util.ArrayList;
import java.util.List;

public class TeacherSubmissionPanel {

    private JPanel panel;

    private DefaultTableModel model;

    private JTable table;

    private List<Request> submissionRequests =
            new ArrayList<>();

    public TeacherSubmissionPanel() {
        createPanel();
        loadSubmissions();
    }

    // =========================================================
    // CREATE PANEL
    // =========================================================

    private void createPanel() {

        panel =
                new JPanel(
                        new BorderLayout(
                                0,
                                20
                        )
                );

        panel.setBackground(
                new Color(
                        248,
                        249,
                        251
                )
        );

        panel.setBorder(
                new EmptyBorder(
                        30,
                        35,
                        30,
                        35
                )
        );

        // =====================================================
        // HEADER
        // =====================================================

        JPanel header =
                new JPanel(
                        new BorderLayout()
                );

        header.setOpaque(false);

        JPanel titlePanel =
                new JPanel();

        titlePanel.setOpaque(false);

        titlePanel.setLayout(
                new BoxLayout(
                        titlePanel,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel title =
                new JLabel(
                        "Student Submissions"
                );

        title.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        26
                )
        );

        JLabel subtitle =
                new JLabel(
                        "Review assignments submitted by students."
                );

        subtitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        subtitle.setForeground(
                new Color(
                        120,
                        123,
                        135
                )
        );

        titlePanel.add(title);

        titlePanel.add(
                Box.createVerticalStrut(5)
        );

        titlePanel.add(subtitle);

        header.add(
                titlePanel,
                BorderLayout.WEST
        );

        // =====================================================
        // REFRESH BUTTON
        // =====================================================

        JButton refreshButton =
                new JButton(
                        "↻ Refresh"
                );

        refreshButton.setFocusPainted(false);

        refreshButton.setPreferredSize(
                new Dimension(
                        110,
                        38
                )
        );

        refreshButton.addActionListener(
                e -> loadSubmissions()
        );

        header.add(
                refreshButton,
                BorderLayout.EAST
        );

        panel.add(
                header,
                BorderLayout.NORTH
        );

        // =====================================================
        // TABLE CARD
        // =====================================================

        JPanel card =
                new JPanel(
                        new BorderLayout()
                );

        card.setBackground(
                Color.WHITE
        );

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        225,
                                        227,
                                        233
                                )
                        ),
                        new EmptyBorder(
                                18,
                                18,
                                18,
                                18
                        )
                )
        );

        String[] columns = {
                "REQUEST",
                "STUDENT",
                "FILE",
                "PRIORITY",
                "STATUS",
                "ACTION"
        };

        model =
                new DefaultTableModel(
                        columns,
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        table =
                new JTable(model);

        table.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        table.setRowHeight(42);

        table.setFillsViewportHeight(true);

        table.getTableHeader().setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        11
                )
        );

        table.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(90);

        table.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(100);

        table.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(220);

        table.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(90);

        table.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(100);

        table.getColumnModel()
                .getColumn(5)
                .setPreferredWidth(100);

        JScrollPane scrollPane =
                new JScrollPane(table);

        card.add(
                scrollPane,
                BorderLayout.CENTER
        );

        // =====================================================
        // ACTION BUTTONS
        // =====================================================

        JPanel actions =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                10
                        )
                );

        actions.setOpaque(false);

        JButton approveButton =
                new JButton(
                        "✓ Approve"
                );

        JButton rejectButton =
                new JButton(
                        "✕ Reject"
                );

        approveButton.setFocusPainted(false);

        rejectButton.setFocusPainted(false);

        approveButton.setPreferredSize(
                new Dimension(
                        110,
                        38
                )
        );

        rejectButton.setPreferredSize(
                new Dimension(
                        110,
                        38
                )
        );

        approveButton.addActionListener(
                e -> approveSelected()
        );

        rejectButton.addActionListener(
                e -> rejectSelected()
        );

        actions.add(
                approveButton
        );

        actions.add(
                rejectButton
        );

        card.add(
                actions,
                BorderLayout.SOUTH
        );

        panel.add(
                card,
                BorderLayout.CENTER
        );
    }

    // =========================================================
    // LOAD SUBMISSIONS
    // =========================================================

    private void loadSubmissions() {

        model.setRowCount(0);

        submissionRequests.clear();

        try {

            RequestClientService requestService =
                    new RequestClientService();

            List<Request> requests =
                    requestService.getPendingRequests();

            for (Request request : requests) {

                if (
                        request.getRequestType() == null
                                ||
                                !request.getRequestType()
                                        .equalsIgnoreCase(
                                                "SUBMISSION"
                                        )
                ) {
                    continue;
                }

                submissionRequests.add(request);

                model.addRow(
                        new Object[]{
                                "RQ-" +
                                        request.getRequestId(),

                                "User #" +
                                        request.getUserId(),

                                getFileName(
                                        request.getFileId()
                                ),

                                priorityText(
                                        request.getPriority()
                                ),

                                capitalize(
                                        request.getStatus()
                                ),

                                "Select row"
                        }
                );
            }

            if (submissionRequests.isEmpty()) {

                model.addRow(
                        new Object[]{
                                "-",
                                "-",
                                "No pending submissions",
                                "-",
                                "-",
                                "-"
                        }
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            model.addRow(
                    new Object[]{
                            "-",
                            "-",
                            "Unable to load submissions",
                            "-",
                            "ERROR",
                            "-"
                    }
            );
        }
    }

    // =========================================================
    // GET FILE NAME
    // =========================================================

    private String getFileName(
            Integer fileId
    ) {

        if (fileId == null) {
            return "No file";
        }

        try {

            FileClientService fileService =
                    new FileClientService();

            var files =
                    fileService.getAllFiles();

            for (var file : files) {

                if (
                        file.getFileId()
                                == fileId
                ) {

                    return file.getFileName();
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return "File #" + fileId;
    }

    // =========================================================
    // APPROVE
    // =========================================================

    private void approveSelected() {

        int row =
                table.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    panel,
                    "Please select a submission first.",
                    "No Submission Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (
                row >= submissionRequests.size()
        ) {
            return;
        }

        Request request =
                submissionRequests.get(row);

        int confirmation =
                JOptionPane.showConfirmDialog(
                        panel,
                        "Approve this submission?\n\n"
                                + "Request: RQ-"
                                + request.getRequestId(),
                        "Confirm Approval",
                        JOptionPane.YES_NO_OPTION
                );

        if (
                confirmation
                        != JOptionPane.YES_OPTION
        ) {
            return;
        }

        try {

            FileClientService fileService =
                    new FileClientService();

            boolean success =
                    fileService.approveSubmission(
                            request.getRequestId()
                    );

            if (success) {

                JOptionPane.showMessageDialog(
                        panel,
                        "Submission approved successfully.",
                        "Approved",
                        JOptionPane.INFORMATION_MESSAGE
                );

                loadSubmissions();

            } else {

                JOptionPane.showMessageDialog(
                        panel,
                        "The submission could not be approved.",
                        "Approval Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    panel,
                    "Error while approving submission:\n\n"
                            + e.getMessage(),
                    "Server Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // REJECT
    // =========================================================

    private void rejectSelected() {

        int row =
                table.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    panel,
                    "Please select a submission first.",
                    "No Submission Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (
                row >= submissionRequests.size()
        ) {
            return;
        }

        Request request =
                submissionRequests.get(row);

        int confirmation =
                JOptionPane.showConfirmDialog(
                        panel,
                        "Reject this submission?\n\n"
                                + "Request: RQ-"
                                + request.getRequestId(),
                        "Confirm Rejection",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (
                confirmation
                        != JOptionPane.YES_OPTION
        ) {
            return;
        }

        try {

            FileClientService fileService =
                    new FileClientService();

            boolean success =
                    fileService.rejectSubmission(
                            request.getRequestId()
                    );

            if (success) {

                JOptionPane.showMessageDialog(
                        panel,
                        "Submission rejected successfully.",
                        "Rejected",
                        JOptionPane.INFORMATION_MESSAGE
                );

                loadSubmissions();

            } else {

                JOptionPane.showMessageDialog(
                        panel,
                        "The submission could not be rejected.",
                        "Rejection Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    panel,
                    "Error while rejecting submission:\n\n"
                            + e.getMessage(),
                    "Server Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private String priorityText(
            int priority
    ) {

        if (priority >= 5) {
            return "High";
        }

        if (priority >= 3) {
            return "Normal";
        }

        return "Low";
    }

    private String capitalize(
            String value
    ) {

        if (
                value == null
                        ||
                        value.isEmpty()
        ) {
            return value;
        }

        return value.substring(
                0,
                1
        ).toUpperCase()
                +
                value.substring(
                        1
                ).toLowerCase();
    }

    // =========================================================
    // GET PANEL
    // =========================================================

    public JPanel getPanel() {

        return panel;
    }
}