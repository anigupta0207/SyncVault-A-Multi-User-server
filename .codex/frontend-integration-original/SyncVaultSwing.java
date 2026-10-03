import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Standalone Swing frontend prototype for SyncVault.
 * Compile: javac SyncVaultSwing.java
 * Run:     java SyncVaultSwing
 */
public class SyncVaultSwing {
    private static final Color NAVY = new Color(25, 27, 45);
    private static final Color NAVY_HI = new Color(43, 45, 70);
    private static final Color PURPLE = new Color(101, 87, 230);
    private static final Color BG = new Color(247, 248, 252);
    private static final Color INK = new Color(33, 36, 53);
    private static final Color MUTED = new Color(132, 137, 153);
    private static final Font BODY = new Font("Segoe UI", Font.PLAIN, 13);
    private static JFrame frame;
    private static String role = "Admin";
    private static String email = "admin@syncvault.com";
    private static JPanel content;
    private static String currentPage = "Overview";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UIManager.put("Button.font", BODY);
            UIManager.put("Label.font", BODY);
            UIManager.put("TextField.font", BODY);
            UIManager.put("PasswordField.font", BODY);
            UIManager.put("ComboBox.font", BODY);
            frame = new JFrame("SyncVault | Multi-User File Workspace");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setMinimumSize(new Dimension(1000, 680));
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);
            showLogin();
            frame.setVisible(true);
        });
    }

    private static void showLogin() {
        JPanel root = new JPanel(new GridLayout(1, 2));
        root.setBackground(Color.WHITE);
        JPanel welcome = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, new Color(30, 32, 57), getWidth(), getHeight(), new Color(57, 48, 117)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        welcome.setOpaque(false);
        JPanel welcomeText = new JPanel();
        welcomeText.setOpaque(false);
        welcomeText.setLayout(new BoxLayout(welcomeText, BoxLayout.Y_AXIS));
        JLabel brand = new JLabel("⬡  SyncVault");
        brand.setForeground(Color.WHITE); brand.setFont(new Font("Segoe UI", Font.BOLD, 22));
        JLabel title = new JLabel("Your files. Your team.\nAll in sync.");
        title.setForeground(Color.WHITE); title.setFont(new Font("Segoe UI", Font.BOLD, 34));
        JLabel description = new JLabel("A secure multi-user workspace for sharing class resources,\nsubmitting work, and keeping every file organized.");
        description.setForeground(new Color(207, 207, 226)); description.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JLabel points = new JLabel("✓  Role-based access     ✓  Version history     ✓  Secure sharing");
        points.setForeground(new Color(211, 205, 255)); points.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT); title.setAlignmentX(Component.LEFT_ALIGNMENT);
        description.setAlignmentX(Component.LEFT_ALIGNMENT); points.setAlignmentX(Component.LEFT_ALIGNMENT);
        welcomeText.add(brand); welcomeText.add(Box.createVerticalStrut(72)); welcomeText.add(title);
        welcomeText.add(Box.createVerticalStrut(17)); welcomeText.add(description);
        welcomeText.add(Box.createVerticalStrut(34)); welcomeText.add(points);
        GridBagConstraints wc = new GridBagConstraints(); wc.gridx = 0; wc.gridy = 0;
        wc.anchor = GridBagConstraints.WEST; wc.insets = new Insets(40, 50, 40, 35);
        welcome.add(welcomeText, wc);

        JPanel formWrap = new JPanel(new GridBagLayout()); formWrap.setBackground(Color.WHITE);
        JPanel form = new JPanel(); form.setBackground(Color.WHITE); form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(360, 445));
        JLabel heading = new JLabel("Welcome back"); heading.setFont(new Font("Segoe UI", Font.BOLD, 27)); heading.setForeground(INK);
        JLabel sub = new JLabel("Sign in to continue to your workspace"); sub.setForeground(MUTED);
        JLabel emailLabel = fieldLabel("Email address");
        JTextField emailField = new JTextField("admin@syncvault.com"); styleField(emailField);
        JLabel passwordLabel = fieldLabel("Password");
        JPasswordField passwordField = new JPasswordField(); styleField(passwordField);
        JLabel roleLabel = fieldLabel("Sign in as");
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Admin", "Teacher", "Student"});
        roleBox.setBackground(Color.WHITE); roleBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 228, 237)), new EmptyBorder(7, 9, 7, 9)));
        JLabel help = new JLabel("Use your SyncVault account credentials"); help.setForeground(MUTED); help.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        JButton signIn = new JButton("Sign in  →"); signIn.setForeground(Color.WHITE); signIn.setBackground(PURPLE);
        signIn.setFocusPainted(false); signIn.setBorder(new EmptyBorder(12, 16, 12, 16)); signIn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        signIn.setAlignmentX(Component.LEFT_ALIGNMENT); signIn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        JLabel error = new JLabel(" "); error.setForeground(new Color(195, 64, 76)); error.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        form.add(heading); form.add(Box.createVerticalStrut(7)); form.add(sub); form.add(Box.createVerticalStrut(28));
        form.add(emailLabel); form.add(Box.createVerticalStrut(7)); form.add(emailField); form.add(Box.createVerticalStrut(16));
        form.add(passwordLabel); form.add(Box.createVerticalStrut(7)); form.add(passwordField); form.add(Box.createVerticalStrut(16));
        form.add(roleLabel); form.add(Box.createVerticalStrut(7)); form.add(roleBox); form.add(Box.createVerticalStrut(12));
        form.add(help); form.add(Box.createVerticalStrut(19)); form.add(signIn); form.add(Box.createVerticalStrut(5)); form.add(error);
        GridBagConstraints fc = new GridBagConstraints(); fc.gridx = 0; fc.gridy = 0; fc.fill = GridBagConstraints.HORIZONTAL;
        formWrap.add(form, fc);
        signIn.addActionListener(e -> {
            if (emailField.getText().trim().isEmpty() || passwordField.getPassword().length == 0) {
                error.setText("Enter your email address and password to continue."); return;
            }
            email = emailField.getText().trim(); role = (String) roleBox.getSelectedItem(); currentPage = "Overview"; showDashboard();
        });
        passwordField.addActionListener(e -> signIn.doClick());
        root.add(welcome); root.add(formWrap); frame.setContentPane(root); frame.revalidate(); frame.repaint();
    }

    private static JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text); label.setFont(new Font("Segoe UI", Font.BOLD, 11)); label.setForeground(new Color(70, 74, 92));
        label.setAlignmentX(Component.LEFT_ALIGNMENT); return label;
    }

    private static void styleField(JTextField field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42)); field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 228, 237)), new EmptyBorder(9, 10, 9, 10)));
        field.setAlignmentX(Component.LEFT_ALIGNMENT); field.setBackground(Color.WHITE);
    }

    private static void showDashboard() {
        JPanel app = new JPanel(new BorderLayout()); app.setBackground(BG);
        JPanel sidebar = new JPanel(new BorderLayout()); sidebar.setBackground(NAVY); sidebar.setPreferredSize(new Dimension(225, 10));
        JPanel sideTop = new JPanel(); sideTop.setBackground(NAVY); sideTop.setLayout(new BoxLayout(sideTop, BoxLayout.Y_AXIS));
        JLabel brand = new JLabel("⬡  SyncVault"); brand.setForeground(Color.WHITE); brand.setFont(new Font("Segoe UI", Font.BOLD, 19));
        brand.setBorder(new EmptyBorder(22, 20, 20, 12)); brand.setAlignmentX(Component.LEFT_ALIGNMENT); sideTop.add(brand);
        JLabel workspace = new JLabel("  ByteForge Workspace   ⌄"); workspace.setForeground(new Color(221, 222, 235));
        workspace.setOpaque(true); workspace.setBackground(NAVY_HI); workspace.setBorder(new EmptyBorder(12, 8, 12, 8));
        workspace.setAlignmentX(Component.LEFT_ALIGNMENT); workspace.setMaximumSize(new Dimension(205, 42));
        JPanel workspacePad = new JPanel(new FlowLayout(FlowLayout.LEFT, 11, 0)); workspacePad.setBackground(NAVY); workspacePad.add(workspace); sideTop.add(workspacePad);
        JLabel navlabel = new JLabel("WORKSPACE"); navlabel.setForeground(new Color(120, 125, 147)); navlabel.setFont(new Font("Segoe UI", Font.BOLD, 9));
        navlabel.setBorder(new EmptyBorder(24, 20, 8, 8)); navlabel.setAlignmentX(Component.LEFT_ALIGNMENT); sideTop.add(navlabel);
        String[] pages = {"Overview", "Files", "Requests", "Submissions", "File versions", "Team & roles", "Audit log", "Settings"};
        String[] icons = {"▦", "▤", "⇄", "⇧", "◷", "♧", "☷", "⚙"};
        for (int i = 0; i < pages.length; i++) {
            final String p = pages[i]; JButton b = new JButton(icons[i] + "   " + p);
            b.setHorizontalAlignment(SwingConstants.LEFT); b.setForeground(p.equals(currentPage) ? Color.WHITE : new Color(177, 180, 198));
            b.setBackground(p.equals(currentPage) ? NAVY_HI : NAVY); b.setFocusPainted(false); b.setBorder(new EmptyBorder(10, 18, 10, 8));
            b.setFont(new Font("Segoe UI", Font.PLAIN, 12)); b.addActionListener(e -> { currentPage = p; showDashboard(); }); sideTop.add(b);
        }
        sidebar.add(sideTop, BorderLayout.NORTH);
        JPanel server = new JPanel(); server.setBackground(NAVY); server.setLayout(new BoxLayout(server, BoxLayout.Y_AXIS));
        JLabel online = new JLabel("ONLINE  Server connected"); online.setForeground(new Color(137, 213, 183)); online.setBorder(new EmptyBorder(14, 18, 5, 8));
        JLabel address = new JLabel("    TCP · 100.93.142.78:5050"); address.setForeground(new Color(125, 130, 151)); address.setFont(new Font("Segoe UI", Font.PLAIN, 9)); address.setBorder(new EmptyBorder(0, 8, 14, 8));
        server.add(online); server.add(address); sidebar.add(server, BorderLayout.SOUTH);

        JPanel right = new JPanel(new BorderLayout()); right.setBackground(BG);
        JPanel topbar = new JPanel(new BorderLayout()); topbar.setBackground(Color.WHITE); topbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(234, 236, 243)));
        JLabel crumb = new JLabel("  ByteForge Workspace   /   " + currentPage); crumb.setForeground(MUTED); crumb.setBorder(new EmptyBorder(18, 18, 18, 10));
        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12)); topRight.setBackground(Color.WHITE);
        JLabel serverStatus = new JLabel("ONLINE  Admin server"); serverStatus.setForeground(new Color(90, 130, 111));
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Admin", "Teacher", "Student"}); roleBox.setSelectedItem(role);
        roleBox.addActionListener(e -> { role = (String) roleBox.getSelectedItem(); currentPage = "Overview"; showDashboard(); });
        JLabel user = new JLabel(initials(email) + "  " + displayName(email) + "   "); user.setForeground(INK); user.setFont(new Font("Segoe UI", Font.BOLD, 11));
        JButton logout = new JButton("Sign out"); logout.addActionListener(e -> showLogin());
        topRight.add(serverStatus); topRight.add(roleBox); topRight.add(user); topRight.add(logout); topbar.add(crumb, BorderLayout.WEST); topbar.add(topRight, BorderLayout.EAST);
        content = new JPanel(new BorderLayout()); content.setBackground(BG); content.setBorder(new EmptyBorder(25, 30, 30, 30));
        buildPage(); right.add(topbar, BorderLayout.NORTH); right.add(content, BorderLayout.CENTER);
        app.add(sidebar, BorderLayout.WEST); app.add(right, BorderLayout.CENTER); frame.setContentPane(app); frame.revalidate(); frame.repaint();
    }

    private static String initials(String value) { return value.isEmpty() ? "U" : ("" + Character.toUpperCase(value.charAt(0)) + Character.toUpperCase(value.charAt(Math.max(0, value.indexOf('@') - 1)))); }
    private static String displayName(String value) { if (value.contains("@")) return value.substring(0, value.indexOf('@')); return value; }

    private static void buildPage() {
        content.removeAll(); content.setLayout(new BorderLayout(0, 18));
        JPanel header = new JPanel(new BorderLayout()); header.setOpaque(false);
        JLabel title = new JLabel(currentPage.equals("Overview") ? "Workspace overview" : currentPage);
        title.setFont(new Font("Segoe UI", Font.BOLD, 23)); title.setForeground(INK);
        JLabel subtitle = new JLabel(subtitle()); subtitle.setForeground(MUTED); subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        JPanel titles = new JPanel(); titles.setOpaque(false); titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS)); titles.add(title); titles.add(Box.createVerticalStrut(4)); titles.add(subtitle);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); actions.setOpaque(false);
        if (role.equals("Student")) actions.add(actionButton("Submit assignment", true));
        else { actions.add(actionButton("＋ New folder", false)); actions.add(actionButton("⇧ Upload file", true)); }
        header.add(titles, BorderLayout.WEST); header.add(actions, BorderLayout.EAST);
        content.add(header, BorderLayout.NORTH);
        JPanel body = new JPanel(new BorderLayout(0, 16)); body.setOpaque(false);
        if (currentPage.equals("Overview")) body.add(dashboardContent(), BorderLayout.CENTER);
        else body.add(pageContent(), BorderLayout.CENTER);
        content.add(body, BorderLayout.CENTER); content.revalidate(); content.repaint();
    }

    private static String subtitle() {
        if (!currentPage.equals("Overview")) return "SyncVault workspace · " + role + " access";
        if (role.equals("Teacher")) return "Manage class resources and review student submissions.";
        if (role.equals("Student")) return "Find class resources, submit your work, and track requests.";
        return "Manage files, access requests, and activity across your server.";
    }

    private static JButton actionButton(String text, boolean primary) {
        JButton button = new JButton(text); button.setFocusPainted(false); button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setForeground(primary ? Color.WHITE : new Color(75, 79, 96)); button.setBackground(primary ? PURPLE : Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(primary ? PURPLE : new Color(228, 230, 238)), new EmptyBorder(8, 12, 8, 12)));
        button.addActionListener(e -> JOptionPane.showMessageDialog(frame, "This prototype is ready to connect to the SyncVault server.", text, JOptionPane.INFORMATION_MESSAGE));
        return button;
    }

    private static JPanel dashboardContent() {
        JPanel panel = new JPanel(new BorderLayout(14, 15)); panel.setOpaque(false);
        JPanel stats = new JPanel(new GridLayout(1, 4, 12, 0)); stats.setOpaque(false);
        String[][] data = role.equals("Student") ? new String[][]{{"My files","18","3 new this week"},{"Shared with me","12","From 4 teachers"},{"Submissions","6","2 awaiting review"},{"Requests","1","Access pending"}}
                : role.equals("Teacher") ? new String[][]{{"My files","86","12 shared with classes"},{"Students","124","Across 4 classes"},{"Submissions","18","7 need review"},{"Requests","3","Awaiting response"}}
                : new String[][]{{"Total files","1,284","Across all users"},{"Active users","42","6 joined this week"},{"Pending requests","4","2 high priority"},{"Server uptime","99.8%","All services online"}};
        for (String[] d : data) stats.add(statCard(d[0], d[1], d[2]));
        panel.add(stats, BorderLayout.NORTH);
        JPanel lower = new JPanel(new GridLayout(1, 2, 14, 0)); lower.setOpaque(false);
        lower.add(fileCard()); lower.add(role.equals("Admin") ? requestCard() : activityCard()); panel.add(lower, BorderLayout.CENTER); return panel;
    }

    private static JPanel statCard(String label, String value, String hint) {
        JPanel p = whiteCard(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS)); p.setBorder(new EmptyBorder(14, 15, 13, 14));
        JLabel l = new JLabel(label); l.setForeground(MUTED); l.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        JLabel v = new JLabel(value); v.setForeground(INK); v.setFont(new Font("Segoe UI", Font.BOLD, 23)); v.setBorder(new EmptyBorder(9, 0, 2, 0));
        JLabel h = new JLabel(hint); h.setForeground(new Color(142, 147, 161)); h.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        p.add(l); p.add(v); p.add(h); return p;
    }

    private static JPanel fileCard() {
        JPanel p = whiteCard(); p.setLayout(new BorderLayout()); p.add(cardHeader("Recent files", "Latest files in your workspace"), BorderLayout.NORTH);
        String[] cols = {"FILE", "OWNER", "UPDATED", "ACCESS"};
        Object[][] rows = {{"Operating Systems - Unit 3.pdf", "Dr. Maya Kim", "Today, 10:42", "Shared"}, {"DBMS lab assignment 05.docx", "Dr. Maya Kim", "Today, 9:18", "Shared"}, {"client-server-notes", "Animesh Gupta", "Yesterday", "Private"}, {"syncvault_schema.sql", "Animesh Gupta", "Sep 22", "Shared"}, {"Assignment 2 - Gupta.pdf", "Animesh Gupta", "Sep 21", "Submitted"}};
        JTable table = table(cols, rows); p.add(new JScrollPane(table), BorderLayout.CENTER); return p;
    }

    private static JPanel requestCard() {
        JPanel p = whiteCard(); p.setLayout(new BorderLayout()); p.add(cardHeader("Request queue", "Pending server operations · FCFS / priority"), BorderLayout.NORTH);
        String[] cols = {"ID", "OPERATION", "REQUESTER", "PRIORITY", "STATUS"};
        Object[][] rows = {{"RQ-1042", "File access", "Arshita Gupta", "High", "Pending"}, {"RQ-1043", "Upload file", "Animesh Gupta", "Normal", "Processing"}, {"RQ-1044", "Share file", "Krishni Rastogi", "Normal", "Pending"}, {"RQ-1045", "Download", "Prashasti Rai", "Low", "Pending"}};
        p.add(new JScrollPane(table(cols, rows)), BorderLayout.CENTER); return p;
    }

    private static JPanel activityCard() {
        JPanel p = whiteCard(); p.setLayout(new BorderLayout()); p.add(cardHeader(role.equals("Student") ? "My submissions" : "Class activity", "Recent workspace updates"), BorderLayout.NORTH);
        String[] cols = {"ACTIVITY", "RESOURCE", "TIME"};
        Object[][] rows = {{"Shared by Dr. Maya Kim", "Operating Systems - Unit 3.pdf", "12 min ago"}, {"Uploaded by Alex Lee", "DBMS lab assignment 05", "1 hour ago"}, {"Submitted by Arshita Gupta", "DBMS Assignment 04", "3 hours ago"}, {"Access approved", "RQ-1042", "Yesterday"}};
        p.add(new JScrollPane(table(cols, rows)), BorderLayout.CENTER); return p;
    }

    private static JPanel pageContent() {
        if (currentPage.equals("Files") || currentPage.equals("Submissions") || currentPage.equals("File versions")) return fileCard();
        if (currentPage.equals("Requests")) return requestCard();
        if (currentPage.equals("Team & roles")) {
            JPanel p = whiteCard(); p.setLayout(new BorderLayout()); p.add(cardHeader("Team & role management", "Accounts and access levels"), BorderLayout.NORTH);
            p.add(new JScrollPane(table(new String[]{"MEMBER", "EMAIL", "ROLE", "STATUS"}, new Object[][]{{"Animesh Gupta", "admin@syncvault.com", "Admin", "Active"}, {"Dr. Maya Kim", "maya@syncvault.com", "Teacher", "Active"}, {"Arshita Gupta", "arshita@syncvault.com", "Student", "Active"}, {"Krishni Rastogi", "krishni@syncvault.com", "Student", "Active"}})), BorderLayout.CENTER); return p;
        }
        if (currentPage.equals("Settings")) {
            JPanel p = whiteCard(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS)); p.setBorder(new EmptyBorder(20, 22, 20, 22));
            for (String line : new String[]{"Server connection     TCP · 100.93.142.78:5050", "Request scheduling    Priority + FCFS", "Concurrent workers    8 threads", "Database              MySQL · JDBC connected", "Audit records         Enabled"}) {
                JLabel label = new JLabel(line); label.setForeground(INK); label.setBorder(new EmptyBorder(13, 4, 13, 4)); p.add(label);
            } return p;
        }
        JPanel p = whiteCard(); p.setLayout(new BorderLayout()); p.add(cardHeader(currentPage, "Important file and user events"), BorderLayout.NORTH);
        p.add(new JScrollPane(table(new String[]{"TIME", "USER", "EVENT", "RESOURCE", "RESULT"}, new Object[][]{{"12 min ago", "Dr. Maya Kim", "Shared", "OS Unit 3.pdf", "Success"}, {"1 hour ago", "Animesh Gupta", "Uploaded", "schema.sql", "Success"}, {"3 hours ago", "Arshita Gupta", "Submitted", "DBMS Assignment 04", "Success"}})), BorderLayout.CENTER); return p;
    }

    private static JPanel whiteCard() { JPanel p = new JPanel(new BorderLayout()); p.setBackground(Color.WHITE); p.setBorder(BorderFactory.createLineBorder(new Color(233, 235, 242))); return p; }
    private static JPanel cardHeader(String title, String subtitle) {
        JPanel h = new JPanel(); h.setBackground(Color.WHITE); h.setLayout(new BoxLayout(h, BoxLayout.Y_AXIS)); h.setBorder(new EmptyBorder(14, 15, 12, 12));
        JLabel t = new JLabel(title); t.setForeground(INK); t.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel s = new JLabel(subtitle); s.setForeground(MUTED); s.setFont(new Font("Segoe UI", Font.PLAIN, 9)); s.setBorder(new EmptyBorder(4, 0, 0, 0));
        h.add(t); h.add(s); return h;
    }
    private static JTable table(String[] columns, Object[][] rows) {
        DefaultTableModel model = new DefaultTableModel(rows, columns) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        JTable t = new JTable(model); t.setFont(new Font("Segoe UI", Font.PLAIN, 10)); t.setRowHeight(38); t.setForeground(new Color(79, 84, 101));
        t.setGridColor(new Color(239, 240, 245)); t.setShowVerticalLines(false); t.setSelectionBackground(new Color(241, 240, 255));
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 8)); t.getTableHeader().setForeground(MUTED); t.getTableHeader().setBackground(new Color(252, 252, 254));
        t.getTableHeader().setPreferredSize(new Dimension(10, 32)); t.setFillsViewportHeight(true); return t;
    }
}
