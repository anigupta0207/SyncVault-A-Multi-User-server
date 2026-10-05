package dashboard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Function;

public abstract class DashboardShell extends JPanel {

    protected static final Color NAVY =
            new Color(25, 27, 45);

    protected static final Color NAVY_HI =
            new Color(43, 45, 70);

    protected static final Color PURPLE =
            new Color(101, 87, 230);

    protected static final Color BG =
            new Color(247, 248, 252);

    protected static final Color INK =
            new Color(33, 36, 53);

    protected static final Color MUTED =
            new Color(132, 137, 153);

    protected static final Font BODY =
            new Font(
                    "Segoe UI",
                    Font.PLAIN,
                    13
            );

    private final Function<String, JPanel> pageRenderer;

    private final Runnable logoutAction;

    private final JPanel content;

    private final String role;

    protected DashboardShell(
            String userName,
            String email,
            String role,
            String[] pages,
            String[] icons,
            Function<String, JPanel> pageRenderer,
            Runnable logoutAction
    ) {

        this.pageRenderer = pageRenderer;

        this.logoutAction = logoutAction;

        this.role = role;

        setLayout(
                new BorderLayout()
        );

        setBackground(BG);

        // =====================================================
        // SIDEBAR
        // =====================================================

        JPanel sidebar =
                new JPanel(
                        new BorderLayout()
                );

        sidebar.setBackground(NAVY);

        sidebar.setPreferredSize(
                new Dimension(
                        225,
                        10
                )
        );

        JPanel sideTop =
                new JPanel();

        sideTop.setBackground(NAVY);

        sideTop.setLayout(
                new BoxLayout(
                        sideTop,
                        BoxLayout.Y_AXIS
                )
        );

        // -----------------------------------------------------
        // BRAND
        // -----------------------------------------------------

        JLabel brand =
                new JLabel(
                        "⬡  SyncVault"
                );

        brand.setForeground(
                Color.WHITE
        );

        brand.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        19
                )
        );

        brand.setBorder(
                new EmptyBorder(
                        22,
                        20,
                        20,
                        12
                )
        );

        brand.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        sideTop.add(brand);

        // -----------------------------------------------------
        // WORKSPACE
        // -----------------------------------------------------

        JLabel workspace =
                new JLabel(
                        "  ByteForge Workspace   ⌄"
                );

        workspace.setForeground(
                new Color(
                        221,
                        222,
                        235
                )
        );

        workspace.setOpaque(true);

        workspace.setBackground(
                NAVY_HI
        );

        workspace.setBorder(
                new EmptyBorder(
                        12,
                        8,
                        12,
                        8
                )
        );

        workspace.setMaximumSize(
                new Dimension(
                        205,
                        42
                )
        );

        JPanel workspacePad =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                11,
                                0
                        )
                );

        workspacePad.setBackground(
                NAVY
        );

        workspacePad.add(
                workspace
        );

        sideTop.add(
                workspacePad
        );

        // -----------------------------------------------------
        // NAVIGATION LABEL
        // -----------------------------------------------------

        JLabel navLabel =
                new JLabel(
                        "WORKSPACE"
                );

        navLabel.setForeground(
                new Color(
                        120,
                        125,
                        147
                )
        );

        navLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        9
                )
        );

        navLabel.setBorder(
                new EmptyBorder(
                        24,
                        20,
                        8,
                        8
                )
        );

        navLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        sideTop.add(
                navLabel
        );

        // -----------------------------------------------------
        // NAVIGATION BUTTONS
        // -----------------------------------------------------

        for (int i = 0; i < pages.length; i++) {

            final String page =
                    pages[i];

            final String icon =
                    icons[i];

            JButton button =
                    new JButton(
                            icon
                                    + "   "
                                    + page
                    );

            button.setHorizontalAlignment(
                    SwingConstants.LEFT
            );

            button.setForeground(
                    new Color(
                            177,
                            180,
                            198
                    )
            );

            button.setBackground(
                    NAVY
            );

            button.setFocusPainted(
                    false
            );

            button.setBorder(
                    new EmptyBorder(
                            10,
                            18,
                            10,
                            8
                    )
            );

            button.setFont(
                    BODY
            );

            button.setAlignmentX(
                    Component.LEFT_ALIGNMENT
            );

            button.addActionListener(
                    e -> showPage(page)
            );

            sideTop.add(
                    button
            );
        }

        sidebar.add(
                sideTop,
                BorderLayout.NORTH
        );

        // -----------------------------------------------------
        // SIDEBAR FOOTER
        // -----------------------------------------------------

        JPanel serverPanel =
                new JPanel();

        serverPanel.setBackground(
                NAVY
        );

        serverPanel.setLayout(
                new BoxLayout(
                        serverPanel,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel online =
                new JLabel(
                        "ONLINE  Server connected"
                );

        online.setForeground(
                new Color(
                        110,
                        220,
                        180
                )
        );

        online.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        online.setBorder(
                new EmptyBorder(
                        10,
                        20,
                        2,
                        10
                )
        );

        JLabel connection =
                new JLabel(
                        "TCP · 100.93.142.78:5050"
                );

        connection.setForeground(
                new Color(
                        125,
                        130,
                        150
                )
        );

        connection.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        10
                )
        );

        connection.setBorder(
                new EmptyBorder(
                        0,
                        20,
                        20,
                        10
                )
        );

        serverPanel.add(
                online
        );

        serverPanel.add(
                connection
        );

        sidebar.add(
                serverPanel,
                BorderLayout.SOUTH
        );

        // =====================================================
        // RIGHT SIDE
        // =====================================================

        JPanel right =
                new JPanel(
                        new BorderLayout()
                );

        right.setBackground(
                BG
        );

        // -----------------------------------------------------
        // TOP BAR
        // -----------------------------------------------------

        JPanel topBar =
                new JPanel(
                        new BorderLayout()
                );

        topBar.setBackground(
                Color.WHITE
        );

        topBar.setBorder(
                BorderFactory.createMatteBorder(
                        0,
                        0,
                        1,
                        0,
                        new Color(
                                228,
                                230,
                                238
                        )
                )
        );

        JLabel breadcrumb =
                new JLabel(
                        "ByteForge Workspace   /   "
                                + role
                );

        breadcrumb.setForeground(
                MUTED
        );

        breadcrumb.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        breadcrumb.setBorder(
                new EmptyBorder(
                        16,
                        30,
                        16,
                        10
                )
        );

        topBar.add(
                breadcrumb,
                BorderLayout.WEST
        );

        JPanel topRight =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                9
                        )
                );

        topRight.setOpaque(
                false
        );

        JLabel connected =
                new JLabel(
                        "CONNECTED  SyncVault Server"
                );

        connected.setForeground(
                new Color(
                        85,
                        145,
                        115
                )
        );

        connected.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        JLabel roleBadge =
                new JLabel(
                        role
                );

        roleBadge.setForeground(
                PURPLE
        );

        roleBadge.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        JLabel userLabel =
                new JLabel(
                        userName
                );

        userLabel.setForeground(
                INK
        );

        userLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        JButton logout =
                new JButton(
                        "Sign out"
                );

        logout.setFocusPainted(
                false
        );

        logout.addActionListener(
                e -> logoutAction.run()
        );

        topRight.add(
                connected
        );

        topRight.add(
                roleBadge
        );

        topRight.add(
                userLabel
        );

        topRight.add(
                logout
        );

        topBar.add(
                topRight,
                BorderLayout.EAST
        );

        right.add(
                topBar,
                BorderLayout.NORTH
        );

        // -----------------------------------------------------
        // CONTENT
        // -----------------------------------------------------

        content =
                new JPanel(
                        new BorderLayout()
                );

        content.setBackground(
                BG
        );

        content.setBorder(
                new EmptyBorder(
                        25,
                        30,
                        30,
                        30
                )
        );

        right.add(
                content,
                BorderLayout.CENTER
        );

        // =====================================================
        // ADD EVERYTHING
        // =====================================================

        add(
                sidebar,
                BorderLayout.WEST
        );

        add(
                right,
                BorderLayout.CENTER
        );

        // =====================================================
        // INITIAL PAGE
        // =====================================================

        showPage(
                "Overview"
        );
    }

    // =========================================================
    // PAGE NAVIGATION
    // =========================================================

    private void showPage(
            String page
    ) {

        JPanel pagePanel;

        try {

            pagePanel =
                    pageRenderer.apply(
                            page
                    );

        } catch (Exception ex) {

            pagePanel =
                    new JPanel(
                            new BorderLayout()
                    );

            JLabel error =
                    new JLabel(
                            "<html><b>Unable to load page.</b><br>"
                                    + ex.getMessage()
                                    + "</html>"
                    );

            error.setBorder(
                    new EmptyBorder(
                            30,
                            30,
                            30,
                            30
                    )
            );

            pagePanel.add(
                    error,
                    BorderLayout.NORTH
            );
        }

        content.removeAll();

        content.add(
                pagePanel,
                BorderLayout.CENTER
        );

        content.revalidate();

        content.repaint();
    }
}