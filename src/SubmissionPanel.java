import client.FileClientService;
import client.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

public class SubmissionPanel {

    private JPanel panel;

    private JLabel selectedFileLabel;
    private JLabel selectedFileSizeLabel;

    private File selectedFile;

    private JComboBox<String> priorityBox;

    public SubmissionPanel() {
        createPanel();
    }

    // =========================================================
    // CREATE PANEL
    // =========================================================

    private void createPanel() {

        panel = new JPanel(new BorderLayout(0, 20));

        panel.setBackground(
                new Color(248, 249, 251)
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

        JPanel header = new JPanel(
                new BorderLayout()
        );

        header.setOpaque(false);

        JPanel titlePanel = new JPanel();

        titlePanel.setLayout(
                new BoxLayout(
                        titlePanel,
                        BoxLayout.Y_AXIS
                )
        );

        titlePanel.setOpaque(false);

        JLabel title = new JLabel(
                "Submit Assignment"
        );

        title.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        26
                )
        );

        JLabel subtitle = new JLabel(
                "Choose a file from your computer and submit it for teacher approval."
        );

        subtitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        subtitle.setForeground(
                new Color(120, 123, 135)
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

        panel.add(
                header,
                BorderLayout.NORTH
        );

        // =====================================================
        // MAIN CARD
        // =====================================================

        JPanel card = new JPanel(
                new GridBagLayout()
        );

        card.setBackground(Color.WHITE);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(225, 227, 233)
                        ),
                        new EmptyBorder(
                                30,
                                35,
                                30,
                                35
                        )
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.weightx = 1.0;

        gbc.insets =
                new Insets(
                        8,
                        5,
                        8,
                        5
                );

        // =====================================================
        // FILE LABEL
        // =====================================================

        JLabel fileTitle = new JLabel(
                "Assignment File"
        );

        fileTitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        15
                )
        );

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        card.add(
                fileTitle,
                gbc
        );

        // =====================================================
        // SELECTED FILE DISPLAY
        // =====================================================

        JPanel fileBox = new JPanel(
                new BorderLayout(15, 0)
        );

        fileBox.setBackground(
                new Color(248, 249, 252)
        );

        fileBox.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 223, 230)
                        ),
                        new EmptyBorder(
                                15,
                                15,
                                15,
                                15
                        )
                )
        );

        selectedFileLabel = new JLabel(
                "No file selected"
        );

        selectedFileLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        selectedFileSizeLabel = new JLabel(
                "Choose a file from your computer."
        );

        selectedFileSizeLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        selectedFileSizeLabel.setForeground(
                new Color(130, 133, 145)
        );

        JPanel fileInfo = new JPanel();

        fileInfo.setLayout(
                new BoxLayout(
                        fileInfo,
                        BoxLayout.Y_AXIS
                )
        );

        fileInfo.setOpaque(false);

        fileInfo.add(
                selectedFileLabel
        );

        fileInfo.add(
                Box.createVerticalStrut(4)
        );

        fileInfo.add(
                selectedFileSizeLabel
        );

        JButton chooseButton =
                new JButton(
                        "Choose File"
                );

        chooseButton.setPreferredSize(
                new Dimension(
                        130,
                        38
                )
        );

        chooseButton.addActionListener(
                e -> chooseFile()
        );

        fileBox.add(
                fileInfo,
                BorderLayout.CENTER
        );

        fileBox.add(
                chooseButton,
                BorderLayout.EAST
        );

        gbc.gridy = 1;

        gbc.gridwidth = 2;

        card.add(
                fileBox,
                gbc
        );

        // =====================================================
        // PRIORITY
        // =====================================================

        JLabel priorityLabel =
                new JLabel(
                        "Submission Priority"
                );

        priorityLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        15
                )
        );

        gbc.gridy = 2;

        gbc.gridwidth = 1;

        card.add(
                priorityLabel,
                gbc
        );

        priorityBox =
                new JComboBox<>(
                        new String[]{
                                "Low",
                                "Normal",
                                "High"
                        }
                );

        priorityBox.setPreferredSize(
                new Dimension(
                        160,
                        38
                )
        );

        gbc.gridx = 1;

        card.add(
                priorityBox,
                gbc
        );

        // =====================================================
        // INFORMATION
        // =====================================================

        JLabel information =
                new JLabel(
                        "<html>" +
                                "<b>What happens after submission?</b><br>" +
                                "Your file will first be uploaded to SyncVault. " +
                                "A submission request will then be created for " +
                                "teacher approval." +
                                "</html>"
                );

        information.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        information.setForeground(
                new Color(100, 103, 115)
        );

        gbc.gridx = 0;

        gbc.gridy = 3;

        gbc.gridwidth = 2;

        gbc.insets =
                new Insets(
                        20,
                        5,
                        15,
                        5
                );

        card.add(
                information,
                gbc
        );

        // =====================================================
        // SUBMIT BUTTON
        // =====================================================

        JButton submitButton =
                new JButton(
                        "Upload & Submit Assignment"
                );

        submitButton.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        submitButton.setPreferredSize(
                new Dimension(
                        240,
                        45
                )
        );

        submitButton.addActionListener(
                e -> uploadAndSubmit()
        );

        gbc.gridy = 4;

        gbc.anchor = GridBagConstraints.EAST;

        card.add(
                submitButton,
                gbc
        );

        panel.add(
                card,
                BorderLayout.CENTER
        );
    }

    // =========================================================
    // CHOOSE FILE
    // =========================================================

    private void chooseFile() {

        JFileChooser fileChooser =
                new JFileChooser();

        fileChooser.setDialogTitle(
                "Select Assignment File"
        );

        int result =
                fileChooser.showOpenDialog(
                        panel
                );

        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        selectedFile =
                fileChooser.getSelectedFile();

        selectedFileLabel.setText(
                selectedFile.getName()
        );

        selectedFileSizeLabel.setText(
                formatSize(
                        selectedFile.length()
                )
        );
    }

    // =========================================================
    // UPLOAD + SUBMIT
    // =========================================================

    private void uploadAndSubmit() {

        if (selectedFile == null) {

            JOptionPane.showMessageDialog(
                    panel,
                    "Please choose an assignment file first.",
                    "No File Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (!selectedFile.exists()) {

            JOptionPane.showMessageDialog(
                    panel,
                    "The selected file no longer exists.",
                    "File Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        if (Session.getUser() == null) {

            JOptionPane.showMessageDialog(
                    panel,
                    "No authenticated student session found.",
                    "Authentication Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        int studentId =
                Session.getUser().getUserId();

        int priority =
                getPriorityValue();

        String filePath =
                selectedFile.getAbsolutePath();

        String fileName =
                selectedFile.getName();

        try {

            FileClientService fileService =
                    new FileClientService();

            // =================================================
            // STEP 1 — UPLOAD FILE
            // =================================================

            boolean uploaded =
                    fileService.uploadFile(
                            filePath,
                            studentId
                    );

            if (!uploaded) {

                JOptionPane.showMessageDialog(
                        panel,
                        "The file could not be uploaded to SyncVault.",
                        "Upload Failed",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            // =================================================
            // STEP 2 — FIND THE NEW FILE
            // =================================================

            int uploadedFileId =
                    findUploadedFileId(
                            fileService,
                            fileName,
                            studentId
                    );

            if (uploadedFileId == -1) {

                JOptionPane.showMessageDialog(
                        panel,
                        "The file was uploaded, but SyncVault could not "
                                + "identify the uploaded file.",
                        "Submission Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            // =================================================
            // STEP 3 — CREATE SUBMISSION REQUEST
            // =================================================

            boolean submitted =
                    fileService.submitFile(
                            studentId,
                            uploadedFileId,
                            priority
                    );

            if (!submitted) {

                JOptionPane.showMessageDialog(
                        panel,
                        "File uploaded successfully, but the "
                                + "submission request could not be created.",
                        "Submission Failed",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            // =================================================
            // SUCCESS
            // =================================================

            JOptionPane.showMessageDialog(
                    panel,
                    "Assignment submitted successfully!\n\n"
                            + "File: " + fileName + "\n"
                            + "Priority: "
                            + priorityText(priority)
                            + "\n\n"
                            + "Waiting for teacher approval.",
                    "Submission Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // Reset selection

            selectedFile = null;

            selectedFileLabel.setText(
                    "No file selected"
            );

            selectedFileSizeLabel.setText(
                    "Choose a file from your computer."
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    panel,
                    "An error occurred while submitting the assignment:\n\n"
                            + e.getMessage(),
                    "Submission Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // FIND UPLOADED FILE
    // =========================================================

    private int findUploadedFileId(
            FileClientService fileService,
            String fileName,
            int studentId
    ) {

        try {

            var files =
                    fileService.getAllFiles();

            int latestFileId = -1;

            for (var file : files) {

                if (
                        file.getOwnerId() == studentId
                                &&
                                file.getFileName().equals(fileName)
                                &&
                                "active".equalsIgnoreCase(
                                        file.getStatus()
                                )
                ) {

                    latestFileId =
                            Math.max(
                                    latestFileId,
                                    file.getFileId()
                            );
                }
            }

            return latestFileId;

        } catch (Exception e) {

            return -1;
        }
    }

    // =========================================================
    // PRIORITY
    // =========================================================

    private int getPriorityValue() {

        String selected =
                (String) priorityBox.getSelectedItem();

        if ("High".equals(selected)) {
            return 5;
        }

        if ("Normal".equals(selected)) {
            return 3;
        }

        return 1;
    }

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

    // =========================================================
    // FILE SIZE
    // =========================================================

    private String formatSize(long size) {

        if (size < 1024) {
            return size + " B";
        }

        if (size < 1024 * 1024) {

            return String.format(
                    "%.1f KB",
                    size / 1024.0
            );
        }

        if (size < 1024 * 1024 * 1024) {

            return String.format(
                    "%.1f MB",
                    size / (1024.0 * 1024.0)
            );
        }

        return String.format(
                "%.1f GB",
                size / (1024.0 * 1024.0 * 1024.0)
        );
    }

    // =========================================================
    // GET PANEL
    // =========================================================

    public JPanel getPanel() {

        return panel;
    }
}