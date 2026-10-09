package Teacher;
import client.FileClientService;
import client.RequestClientService;
import request.Request;
import file.FileInfo;

import java.io.File;
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
    private final String sessionToken;
    public TeacherSubmissionPanel(String sessionToken) {
        if (sessionToken == null || sessionToken.isBlank()) {
            throw new IllegalArgumentException(
                    "A valid teacher session token is required."
            );
        }

        this.sessionToken = sessionToken;
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

        JButton downloadButton =
                new JButton(
                        "↓ Download"
                );

        JButton approveButton =
                new JButton(
                        "✓ Approve"
                );

        JButton rejectButton =
                new JButton(
                        "✕ Reject"
                );

        downloadButton.setFocusPainted(false);

        approveButton.setFocusPainted(false);

        rejectButton.setFocusPainted(false);

        downloadButton.setPreferredSize(
                new Dimension(
                        120,
                        38
                )
        );

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

        downloadButton.addActionListener(
                e -> downloadSelected()
        );

        approveButton.addActionListener(
                e -> approveSelected()
        );

        rejectButton.addActionListener(
                e -> rejectSelected()
        );

        actions.add(
                downloadButton
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
    // DOWNLOAD SELECTED SUBMISSION
    // =========================================================

    private void downloadSelected() {

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

        if (row >= submissionRequests.size()) {
            return;
        }

        Request request =
                submissionRequests.get(row);

        Integer fileId =
                request.getFileId();

        if (fileId == null) {

            JOptionPane.showMessageDialog(
                    panel,
                    "This submission does not have an attached file.",
                    "Download Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        try {

            // =================================================
            // FIND FILE INFORMATION
            // =================================================

            FileClientService fileService =
                    new FileClientService();

            List<FileInfo> files =
                    fileService.getAllFiles();

            FileInfo selectedFile = null;

            for (FileInfo file : files) {

                if (
                        file.getFileId()
                                == fileId
                ) {

                    selectedFile = file;

                    break;
                }
            }

            if (selectedFile == null) {

                JOptionPane.showMessageDialog(
                        panel,
                        "The submitted file could not be found.",
                        "Download Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            if (
                    !"active".equalsIgnoreCase(
                            selectedFile.getStatus()
                    )
            ) {

                JOptionPane.showMessageDialog(
                        panel,
                        "This file is no longer active.",
                        "Download Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            // =================================================
            // CHOOSE DESTINATION FOLDER
            // =================================================

            JFileChooser chooser =
                    new JFileChooser();

            chooser.setDialogTitle(
                    "Choose Download Location"
            );

            chooser.setFileSelectionMode(
                    JFileChooser.DIRECTORIES_ONLY
            );

            int result =
                    chooser.showSaveDialog(panel);

            if (
                    result
                            != JFileChooser.APPROVE_OPTION
            ) {
                return;
            }

            File destination =
                    chooser.getSelectedFile();

            // =================================================
            // DOWNLOAD
            // =================================================

            boolean success =
                    fileService.downloadFile(
                            fileId,
                            destination.getAbsolutePath(),
                            sessionToken
                    );

            if (success) {

                JOptionPane.showMessageDialog(
                        panel,
                        "File downloaded successfully.\n\n"
                                + "File: "
                                + selectedFile.getFileName()
                                + "\n"
                                + "Location: "
                                + destination.getAbsolutePath(),
                        "Download Successful",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } else {

                JOptionPane.showMessageDialog(
                        panel,
                        "The file could not be downloaded.",
                        "Download Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    panel,
                    "Error while downloading the submission:\n\n"
                            + e.getMessage(),
                    "Download Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
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