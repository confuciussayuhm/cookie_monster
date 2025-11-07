package burp.cookiemonster;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * User interface panel for managing blocked cookies and domain filtering.
 */
public class CookieMonsterUI extends JPanel {
    private final CookieBlocklistManager blocklistManager;
    private final DefaultListModel<String> cookieListModel;
    private final DefaultListModel<String> domainListModel;
    private final JList<String> cookieList;
    private final JList<String> domainList;
    private final JTextField cookieNameField;
    private final JTextField domainNameField;
    private final ButtonGroup filterModeGroup;
    private final JRadioButton allDomainsRadio;
    private final JRadioButton inScopeRadio;
    private final JRadioButton customDomainsRadio;
    private JPanel customDomainsPanel;

    public CookieMonsterUI(CookieBlocklistManager blocklistManager) {
        this.blocklistManager = blocklistManager;
        this.cookieListModel = new DefaultListModel<>();
        this.domainListModel = new DefaultListModel<>();
        this.cookieList = new JList<>(cookieListModel);
        this.domainList = new JList<>(domainListModel);
        this.cookieNameField = new JTextField(20);
        this.domainNameField = new JTextField(20);

        // Initialize radio buttons
        this.filterModeGroup = new ButtonGroup();
        this.allDomainsRadio = new JRadioButton(DomainFilterMode.ALL_DOMAINS.getDisplayName(), true);
        this.inScopeRadio = new JRadioButton(DomainFilterMode.IN_SCOPE_ONLY.getDisplayName());
        this.customDomainsRadio = new JRadioButton(DomainFilterMode.CUSTOM_DOMAINS.getDisplayName());

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create main split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        // Left panel: Cookie management
        JPanel cookiePanel = createCookiePanel();
        splitPane.setLeftComponent(cookiePanel);

        // Right panel: Domain filtering
        JPanel domainPanel = createDomainFilterPanel();
        splitPane.setRightComponent(domainPanel);

        add(splitPane, BorderLayout.CENTER);

        // Load existing data
        refreshCookieList();
        refreshDomainList();
        updateDomainFilterUI();
    }

    /**
     * Create the cookie management panel.
     */
    private JPanel createCookiePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new TitledBorder("Blocked Cookies"));

        // Header with description
        JPanel headerPanel = new JPanel(new BorderLayout(5, 5));
        JLabel descLabel = new JLabel("Cookies in this list will be automatically removed from requests");
        descLabel.setFont(new Font(descLabel.getFont().getName(), Font.PLAIN, 12));
        descLabel.setForeground(Color.GRAY);
        headerPanel.add(descLabel, BorderLayout.NORTH);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Cookie list with scroll pane
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        JLabel listLabel = new JLabel("Blocked Cookie Names:");
        centerPanel.add(listLabel, BorderLayout.NORTH);

        cookieList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(cookieList);
        scrollPane.setPreferredSize(new Dimension(300, 250));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);

        // Input panel for adding cookies
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        JLabel inputLabel = new JLabel("Cookie Name:");
        addPanel.add(inputLabel);

        cookieNameField.addActionListener(e -> addCookie());
        addPanel.add(cookieNameField);

        JButton addButton = new JButton("Add Cookie");
        addButton.addActionListener(e -> addCookie());
        addPanel.add(addButton);

        inputPanel.add(addPanel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JButton removeButton = new JButton("Remove Selected");
        removeButton.addActionListener(e -> removeSelectedCookies());
        buttonPanel.add(removeButton);

        JButton clearButton = new JButton("Clear All");
        clearButton.addActionListener(e -> clearAllCookies());
        buttonPanel.add(clearButton);

        inputPanel.add(buttonPanel, BorderLayout.CENTER);

        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("Total blocked cookies: 0");
        statusPanel.add(statusLabel);
        inputPanel.add(statusPanel, BorderLayout.SOUTH);

        panel.add(inputPanel, BorderLayout.SOUTH);

        // Update status label when list changes
        cookieListModel.addListDataListener(new javax.swing.event.ListDataListener() {
            public void intervalAdded(javax.swing.event.ListDataEvent e) {
                updateStatus();
            }

            public void intervalRemoved(javax.swing.event.ListDataEvent e) {
                updateStatus();
            }

            public void contentsChanged(javax.swing.event.ListDataEvent e) {
                updateStatus();
            }

            private void updateStatus() {
                statusLabel.setText("Total blocked cookies: " + cookieListModel.size());
            }
        });

        return panel;
    }

    /**
     * Create the domain filtering panel.
     */
    private JPanel createDomainFilterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new TitledBorder("Domain Filtering"));

        // Filter mode selection
        JPanel modePanel = new JPanel();
        modePanel.setLayout(new BoxLayout(modePanel, BoxLayout.Y_AXIS));
        modePanel.setBorder(new EmptyBorder(5, 5, 10, 5));

        JLabel modeLabel = new JLabel("Apply cookie filtering to:");
        modeLabel.setFont(new Font(modeLabel.getFont().getName(), Font.BOLD, 12));
        modeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        modePanel.add(modeLabel);
        modePanel.add(Box.createVerticalStrut(5));

        // Configure radio buttons
        allDomainsRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
        inScopeRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
        customDomainsRadio.setAlignmentX(Component.LEFT_ALIGNMENT);

        filterModeGroup.add(allDomainsRadio);
        filterModeGroup.add(inScopeRadio);
        filterModeGroup.add(customDomainsRadio);

        allDomainsRadio.addActionListener(e -> onFilterModeChanged(DomainFilterMode.ALL_DOMAINS));
        inScopeRadio.addActionListener(e -> onFilterModeChanged(DomainFilterMode.IN_SCOPE_ONLY));
        customDomainsRadio.addActionListener(e -> onFilterModeChanged(DomainFilterMode.CUSTOM_DOMAINS));

        modePanel.add(allDomainsRadio);
        modePanel.add(inScopeRadio);
        modePanel.add(customDomainsRadio);

        panel.add(modePanel, BorderLayout.NORTH);

        // Custom domains panel
        customDomainsPanel = createCustomDomainsPanel();
        panel.add(customDomainsPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Create the custom domains management panel.
     */
    private JPanel createCustomDomainsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 5, 5, 5),
                BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        ));

        // Description
        JPanel descPanel = new JPanel();
        descPanel.setLayout(new BoxLayout(descPanel, BoxLayout.Y_AXIS));
        descPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel titleLabel = new JLabel("Custom Domain List");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 12));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        descPanel.add(titleLabel);

        JLabel descLabel = new JLabel("Cookies will only be removed from these domains (supports subdomains)");
        descLabel.setFont(new Font(descLabel.getFont().getName(), Font.PLAIN, 11));
        descLabel.setForeground(Color.GRAY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        descPanel.add(descLabel);

        panel.add(descPanel, BorderLayout.NORTH);

        // Domain list with scroll pane
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel listLabel = new JLabel("Domains (e.g., example.com):");
        centerPanel.add(listLabel, BorderLayout.NORTH);

        domainList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(domainList);
        scrollPane.setPreferredSize(new Dimension(300, 150));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);

        // Input panel for adding domains
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JLabel inputLabel = new JLabel("Domain:");
        addPanel.add(inputLabel);

        domainNameField.addActionListener(e -> addDomain());
        addPanel.add(domainNameField);

        JButton addButton = new JButton("Add Domain");
        addButton.addActionListener(e -> addDomain());
        addPanel.add(addButton);

        inputPanel.add(addPanel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JButton removeButton = new JButton("Remove Selected");
        removeButton.addActionListener(e -> removeSelectedDomains());
        buttonPanel.add(removeButton);

        JButton clearButton = new JButton("Clear All");
        clearButton.addActionListener(e -> clearAllDomains());
        buttonPanel.add(clearButton);

        inputPanel.add(buttonPanel, BorderLayout.CENTER);

        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("Total custom domains: 0");
        statusPanel.add(statusLabel);
        inputPanel.add(statusPanel, BorderLayout.SOUTH);

        panel.add(inputPanel, BorderLayout.SOUTH);

        // Update status label when list changes
        domainListModel.addListDataListener(new javax.swing.event.ListDataListener() {
            public void intervalAdded(javax.swing.event.ListDataEvent e) {
                updateStatus();
            }

            public void intervalRemoved(javax.swing.event.ListDataEvent e) {
                updateStatus();
            }

            public void contentsChanged(javax.swing.event.ListDataEvent e) {
                updateStatus();
            }

            private void updateStatus() {
                statusLabel.setText("Total custom domains: " + domainListModel.size());
            }
        });

        return panel;
    }

    /**
     * Handle filter mode changes.
     */
    private void onFilterModeChanged(DomainFilterMode mode) {
        blocklistManager.setFilterMode(mode);
        updateDomainFilterUI();
    }

    /**
     * Update the UI based on the selected filter mode.
     */
    private void updateDomainFilterUI() {
        DomainFilterMode mode = blocklistManager.getFilterMode();
        boolean enableCustomDomains = (mode == DomainFilterMode.CUSTOM_DOMAINS);
        customDomainsPanel.setEnabled(enableCustomDomains);
        setComponentsEnabled(customDomainsPanel, enableCustomDomains);
    }

    /**
     * Recursively enable/disable components in a container.
     */
    private void setComponentsEnabled(Container container, boolean enabled) {
        for (Component component : container.getComponents()) {
            component.setEnabled(enabled);
            if (component instanceof Container) {
                setComponentsEnabled((Container) component, enabled);
            }
        }
    }

    // Cookie management methods

    private void addCookie() {
        String cookieName = cookieNameField.getText().trim();

        if (cookieName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a cookie name",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (blocklistManager.addCookie(cookieName)) {
            cookieListModel.addElement(cookieName);
            cookieNameField.setText("");
            cookieNameField.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Cookie '" + cookieName + "' is already in the blocklist",
                    "Duplicate Cookie",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void removeSelectedCookies() {
        java.util.List<String> selectedCookies = cookieList.getSelectedValuesList();

        if (selectedCookies.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select one or more cookies to remove",
                    "No Selection",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (String cookieName : selectedCookies) {
            blocklistManager.removeCookie(cookieName);
            cookieListModel.removeElement(cookieName);
        }
    }

    private void clearAllCookies() {
        if (cookieListModel.isEmpty()) {
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to clear all blocked cookies?",
                "Confirm Clear",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            blocklistManager.clear();
            cookieListModel.clear();
        }
    }

    private void refreshCookieList() {
        cookieListModel.clear();
        for (String cookieName : blocklistManager.getBlockedCookies()) {
            cookieListModel.addElement(cookieName);
        }
    }

    // Domain management methods

    private void addDomain() {
        String domain = domainNameField.getText().trim();

        if (domain.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a domain name",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate domain format
        if (!isValidDomain(domain)) {
            JOptionPane.showMessageDialog(this,
                    "Invalid domain format. Enter a domain like 'example.com' (without protocol)",
                    "Invalid Domain",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (blocklistManager.addCustomDomain(domain)) {
            domainListModel.addElement(domain);
            domainNameField.setText("");
            domainNameField.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Domain '" + domain + "' is already in the custom list",
                    "Duplicate Domain",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void removeSelectedDomains() {
        java.util.List<String> selectedDomains = domainList.getSelectedValuesList();

        if (selectedDomains.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select one or more domains to remove",
                    "No Selection",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (String domain : selectedDomains) {
            blocklistManager.removeCustomDomain(domain);
            domainListModel.removeElement(domain);
        }
    }

    private void clearAllDomains() {
        if (domainListModel.isEmpty()) {
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to clear all custom domains?",
                "Confirm Clear",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            blocklistManager.clearCustomDomains();
            domainListModel.clear();
        }
    }

    private void refreshDomainList() {
        domainListModel.clear();
        for (String domain : blocklistManager.getCustomDomains()) {
            domainListModel.addElement(domain);
        }
    }

    /**
     * Basic domain validation.
     */
    private boolean isValidDomain(String domain) {
        // Remove protocol if present
        domain = domain.replaceFirst("^https?://", "");

        // Remove path if present
        domain = domain.split("/")[0];

        // Check basic domain pattern
        return domain.matches("^([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}$") ||
               domain.matches("^[a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.[a-zA-Z]{2,}$");
    }
}
