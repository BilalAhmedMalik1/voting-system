import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;

public class OnlineVotingSystem {

    // ---------------- DATA ----------------
    static String adminId = "admin";
    static String adminPass = "1234";

    static class Voter {
        String name, father, dob, cnic;
        boolean voted = false;

        Voter(String n, String f, String d, String c) {
            name = n;
            father = f;
            dob = d;
            cnic = c;
        }
    }

    static ArrayList<Voter> voters = new ArrayList<>();

    static final String DATA_FILE = "voters.txt";

    static int pti = 0, pmln = 0, mqm = 0, jms = 0;

    // table model references so UI can update immediately
    static DefaultTableModel voterTableModel = null;
    static JTable voterTable = null;
    // keep single instances of major frames
    static JFrame adminFrame = null;
    static JFrame voterFrame = null;
    static JFrame adminLoginFrame = null;
    static JFrame calcFrame = null;

    // ---------------- UI THEME & HELPERS ----------------
    static final Color BG = new Color(245, 248, 255);
    static final Color PRIMARY = new Color(0, 102, 204);
    static final Color SUCCESS = new Color(0, 153, 76);
    static final Color DANGER = new Color(153, 0, 0);

    static void setupFrame(JFrame f, int w, int h) {
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setSize(w, h);
        f.setMinimumSize(new Dimension(Math.min(w, 400), Math.min(h, 300)));
        f.getContentPane().setBackground(BG);
        f.setLocationRelativeTo(null); // center on screen
    }

    static void styleButton(final JButton b, Color bg) {
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            Color original = b.getBackground();
            public void mouseEntered(MouseEvent e) { b.setBackground(original.darker()); }
            public void mouseExited(MouseEvent e) { b.setBackground(original); }
        });
    }

    // small helper to create a styled button with tooltip and mnemonic
    static JButton createButton(String text, Color color, String tooltip, int mnemonic) {
        JButton b = new JButton(text);
        styleButton(b, color);
        if (tooltip != null) b.setToolTipText(tooltip);
        if (mnemonic > 0) b.setMnemonic(mnemonic);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        return b;
    }

    // attach a window listener and clear a frame reference when closed
    static void attachCloseListener(JFrame f, Runnable clearRef) {
        f.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent e) { clearRef.run(); }
            public void windowClosing(java.awt.event.WindowEvent e) { clearRef.run(); }
        });
    }

    // ---------------- MAIN ----------------
    public static void main(String[] args) {
        loadVotersFromFile();
        // If no data file / empty, populate some sample data and save
        if (voters.isEmpty()) {
            voters.add(new Voter("ali", "ahmed", "01-01-2000", "42101"));
            voters.add(new Voter("sara", "khan", "02-02-2000", "42102"));
            voters.add(new Voter("usman", "raza", "03-03-2000", "42103"));
            voters.add(new Voter("huzaifa", "raza", "03-03-2000", "412042529"));
            voters.add(new Voter("rehan", "sabir", "03-03-2000", "42103"));
            saveVotersToFile();
        }
        showSplash();
        showMainMenu();
    }

    static void showSplash() {
        final JDialog d = new JDialog((Frame) null, true);
        d.setUndecorated(true);
        JLabel lbl = new JLabel("voting system for united states of johar", SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 28));
        lbl.setForeground(Color.WHITE);
        lbl.setOpaque(true);
        lbl.setBackground(PRIMARY);
        lbl.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        d.getContentPane().add(lbl);
        d.pack();
        d.setSize(700, 140);
        d.setLocationRelativeTo(null);
        // attempt to set initial opacity; if unsupported, we'll just wait and close
        try { d.setOpacity(1f); } catch (Throwable t) {}

        // wait 1.5s, then run a fade-out over ~30 steps (50ms each)
        javax.swing.Timer wait = new javax.swing.Timer(1500, null);
        wait.setRepeats(false);
        wait.addActionListener(ev -> {
            final int steps = 30;
            final int delay = 50;
            final int[] count = {0};
            javax.swing.Timer fade = new javax.swing.Timer(delay, null);
            fade.addActionListener(fe -> {
                try {
                    float op = 1f - ((float) count[0] / steps);
                    d.setOpacity(Math.max(0f, op));
                } catch (Throwable ignored) {}
                count[0]++;
                if (count[0] > steps) {
                    ((javax.swing.Timer) fe.getSource()).stop();
                    d.dispose();
                }
            });
            fade.start();
        });
        wait.start();
        d.setVisible(true); // modal: will block until disposed
    }

    static void loadVotersFromFile() {
        voters.clear();
        File f = new File(DATA_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                // format: name|father|dob|cnic|voted
                String[] parts = line.split("\\|", -1);
                if (parts.length < 5) continue;
                Voter v = new Voter(parts[0], parts[1], parts[2], parts[3]);
                v.voted = "1".equals(parts[4]);
                voters.add(v);
            }
        } catch (IOException ex) {
            // ignore load errors for now
        }
    }

    static void saveVotersToFile() {
        File f = new File(DATA_FILE);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
            for (Voter v : voters) {
                String line = String.join("|",
                    v.name == null ? "" : v.name,
                    v.father == null ? "" : v.father,
                    v.dob == null ? "" : v.dob,
                    v.cnic == null ? "" : v.cnic,
                    v.voted ? "1" : "0"
                );
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException ex) {
            // ignore save errors for now
        }
    }

    // ---------------- MAIN MENU ----------------
    static void showMainMenu() {
        JFrame f = new JFrame("Online Voting System");
        setupFrame(f, 600, 420);
        f.setLayout(new BorderLayout());
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel title = new JLabel("ONLINE VOTING SYSTEM", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setOpaque(true);
        title.setBackground(PRIMARY);
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JButton adminBtn = createButton("ADMIN PANEL", PRIMARY, "Open the admin panel (Alt+A)", KeyEvent.VK_A);
        JButton voterBtn = createButton("VOTER PANEL", SUCCESS, "Open the voter form (Alt+V)", KeyEvent.VK_V);
        JButton calcBtn = createButton("CALCULATION", DANGER, "View results (Alt+C)", KeyEvent.VK_C);

        adminBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        voterBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        calcBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(Box.createVerticalStrut(30));
        center.add(adminBtn);
        center.add(Box.createVerticalStrut(16));
        center.add(voterBtn);
        center.add(Box.createVerticalStrut(16));
        center.add(calcBtn);
        center.add(Box.createVerticalGlue());

        f.add(title, BorderLayout.NORTH);
        f.add(center, BorderLayout.CENTER);

        adminBtn.addActionListener(e -> adminLogin());
        voterBtn.addActionListener(e -> voterForm());
        calcBtn.addActionListener(e -> calculationLogin());

        f.setVisible(true);
    }

    // ---------------- ADMIN LOGIN ----------------
    static void adminLogin() {
        if (adminLoginFrame != null && adminLoginFrame.isDisplayable()) {
            adminLoginFrame.toFront();
            adminLoginFrame.requestFocus();
            return;
        }
        JFrame f = new JFrame("Admin Login");
        adminLoginFrame = f;
        setupFrame(f, 420, 240);

        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel l1 = new JLabel("User ID:");
        JLabel l2 = new JLabel("Password:");
        JTextField id = new JTextField(); id.setColumns(20);
        JPasswordField pass = new JPasswordField(); pass.setColumns(20);
        JButton login = new JButton("Login");
        styleButton(login, PRIMARY);

        // show-password checkbox
        JCheckBox show = new JCheckBox("Show");
        show.setOpaque(false);
        final char echo = pass.getEchoChar();
        show.addItemListener(ev -> pass.setEchoChar(show.isSelected() ? (char)0 : echo));

        c.gridx = 0; c.gridy = 0; c.gridwidth = 1; p.add(l1, c);
        c.gridx = 1; c.gridy = 0; p.add(id, c);
        c.gridx = 0; c.gridy = 1; p.add(l2, c);
        c.gridx = 1; c.gridy = 1; p.add(pass, c);
        c.gridx = 2; c.gridy = 1; p.add(show, c);
        c.gridx = 0; c.gridy = 2; c.gridwidth = 3; p.add(login, c);

        login.addActionListener(e -> {
            if (id.getText().equals(adminId) &&
                new String(pass.getPassword()).equals(adminPass)) {
                f.dispose();
                adminLoginFrame = null;
                adminPanel();
            } else {
                JOptionPane.showMessageDialog(f, "Wrong ID or Password");
            }
        });

        f.add(p);
        f.setVisible(true);
        attachCloseListener(f, () -> adminLoginFrame = null);
    }

    // ---------------- ADMIN PANEL ----------------
    static void adminPanel() {
        if (adminFrame != null && adminFrame.isDisplayable()) {
            adminFrame.toFront();
            adminFrame.requestFocus();
            return;
        }
        JFrame f = new JFrame("Admin Panel");
        adminFrame = f;
        setupFrame(f, 800, 500);
        f.setLayout(new BorderLayout(8, 8));

        String[] col = {"Name", "Father", "DOB", "CNIC", "Voted"};
        DefaultTableModel model = new DefaultTableModel(col, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        for (Voter v : voters) {
            model.addRow(new Object[]{v.name, v.father, v.dob, v.cnic, v.voted ? "YES" : "NO"});
        }
        voterTableModel = model;
        voterTable = new JTable(model);
        JScrollPane sp = new JScrollPane(voterTable);

        JButton addBtn = new JButton("Add Voter");
        JButton delBtn = new JButton("Delete Voter");
        styleButton(addBtn, PRIMARY);
        styleButton(delBtn, DANGER);

        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        p.add(addBtn);
        p.add(delBtn);

        addBtn.addActionListener(e -> addVoter());
        delBtn.addActionListener(e -> deleteVoter());

        f.add(sp, BorderLayout.CENTER);
        f.add(p, BorderLayout.SOUTH);
        f.setVisible(true);
        attachCloseListener(f, () -> adminFrame = null);
    }

    // ---------------- ADD VOTER ----------------
    static void addVoter() {
        JFrame f = new JFrame("Add Voter");
        setupFrame(f, 420, 320);

        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        JTextField name = new JTextField(); name.setColumns(22);
        JTextField father = new JTextField(); father.setColumns(22);
        JTextField dob = new JTextField(); dob.setColumns(22);
        JTextField cnic = new JTextField(); cnic.setColumns(22);
        JButton save = new JButton("Save");
        styleButton(save, PRIMARY);

        c.gridx = 0; c.gridy = 0; p.add(new JLabel("Name:"), c);
        c.gridx = 1; c.gridy = 0; p.add(name, c);
        c.gridx = 0; c.gridy = 1; p.add(new JLabel("Father:"), c);
        c.gridx = 1; c.gridy = 1; p.add(father, c);
        c.gridx = 0; c.gridy = 2; p.add(new JLabel("DOB:"), c);
        c.gridx = 1; c.gridy = 2; p.add(dob, c);
        c.gridx = 0; c.gridy = 3; p.add(new JLabel("CNIC:"), c);
        c.gridx = 1; c.gridy = 3; p.add(cnic, c);
        c.gridx = 0; c.gridy = 4; c.gridwidth = 2; p.add(save, c);

        save.addActionListener(e -> {
            String nm = name.getText().trim().toLowerCase();
            String fa = father.getText().trim().toLowerCase();
            String dobText = dob.getText().trim();
            String cnicText = cnic.getText().trim();
            voters.add(new Voter(
                nm,
                fa,
                dobText,
                cnicText
            ));
            saveVotersToFile();
            // update admin table immediately if open
            if (voterTableModel != null) {
                voterTableModel.addRow(new Object[]{nm, fa, dobText, cnicText, "NO"});
            }
            JOptionPane.showMessageDialog(f, "Voter Added");
            f.dispose();
        });

        f.add(p);
        f.setVisible(true);
    }

    // ---------------- DELETE VOTER ----------------
    static void deleteVoter() {
        String cnic = JOptionPane.showInputDialog("Enter CNIC");
        for (int i = 0; i < voters.size(); i++) {
            if (voters.get(i).cnic.equals(cnic)) {
                voters.remove(i);
                saveVotersToFile();
                // update admin table immediately if open
                if (voterTableModel != null) {
                    // find row with matching CNIC (column 3)
                    for (int r = 0; r < voterTableModel.getRowCount(); r++) {
                        Object cell = voterTableModel.getValueAt(r, 3);
                        if (cell != null && cnic.equals(cell.toString())) {
                            voterTableModel.removeRow(r);
                            break;
                        }
                    }
                }
                JOptionPane.showMessageDialog(null, "Voter Deleted");
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Voter Not Found");
    }

    // ---------------- VOTER FORM ----------------
    static void voterForm() {
        if (voterFrame != null && voterFrame.isDisplayable()) {
            voterFrame.toFront();
            voterFrame.requestFocus();
            return;
        }
        JFrame f = new JFrame("Voter Form");
        voterFrame = f;
        setupFrame(f, 460, 340);

        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        JTextField name = new JTextField(); name.setColumns(22);
        JTextField father = new JTextField(); father.setColumns(22);
        JTextField dob = new JTextField(); dob.setColumns(22);
        JTextField cnic = new JTextField(); cnic.setColumns(22);
        JButton verify = new JButton("Verify");
        styleButton(verify, PRIMARY);

        c.gridx = 0; c.gridy = 0; p.add(new JLabel("Name:"), c);
        c.gridx = 1; c.gridy = 0; p.add(name, c);
        c.gridx = 0; c.gridy = 1; p.add(new JLabel("Father:"), c);
        c.gridx = 1; c.gridy = 1; p.add(father, c);
        c.gridx = 0; c.gridy = 2; p.add(new JLabel("DOB:"), c);
        c.gridx = 1; c.gridy = 2; p.add(dob, c);
        c.gridx = 0; c.gridy = 3; p.add(new JLabel("CNIC:"), c);
        c.gridx = 1; c.gridy = 3; p.add(cnic, c);
        c.gridx = 0; c.gridy = 4; c.gridwidth = 2; p.add(verify, c);

        verify.addActionListener(e -> {
            String inName = name.getText().trim().toLowerCase();
            String inFather = father.getText().trim().toLowerCase();
            String inDob = dob.getText().trim();
            String inCnic = cnic.getText().trim();
            for (Voter v : voters) {
                if (v.name.equals(inName) &&
                    v.father.equals(inFather) &&
                    v.dob.equals(inDob) &&
                    v.cnic.equals(inCnic)) {

                    if (v.voted) {
                        JOptionPane.showMessageDialog(f, "Already Voted");
                        return;
                    }
                    f.dispose();
                    voteCasting(v);
                    return;
                }
            }
            JOptionPane.showMessageDialog(f, "Voter Not Verified");
        });

        f.add(p);
        f.setVisible(true);
        attachCloseListener(f, () -> voterFrame = null);
    }

    // ---------------- VOTE CASTING ----------------
    static void voteCasting(Voter v) {
        JFrame f = new JFrame("Vote Casting");
        setupFrame(f, 360, 320);
        f.setLayout(new GridLayout(4, 1, 8, 8));

        JButton b1 = new JButton("PTI");
        JButton b2 = new JButton("PMLN");
        JButton b3 = new JButton("MQM");
        JButton b4 = new JButton("JMS");

        styleButton(b1, PRIMARY);
        styleButton(b2, SUCCESS);
        styleButton(b3, new Color(255, 140, 0));
        styleButton(b4, DANGER);

        ActionListener vote = e -> {
            if (e.getSource() == b1) pti++;
            if (e.getSource() == b2) pmln++;
            if (e.getSource() == b3) mqm++;
            if (e.getSource() == b4) jms++;
            v.voted = true;
            saveVotersToFile();
            // update table voted status if open
            if (voterTableModel != null && voterTable != null) {
                for (int r = 0; r < voterTableModel.getRowCount(); r++) {
                    Object cell = voterTableModel.getValueAt(r, 3);
                    if (cell != null && cell.toString().equals(v.cnic)) {
                        voterTableModel.setValueAt("YES", r, 4);
                        break;
                    }
                }
            }
            JOptionPane.showMessageDialog(f, "Thank You For Voting");
            f.dispose();
        };

        b1.addActionListener(vote);
        b2.addActionListener(vote);
        b3.addActionListener(vote);
        b4.addActionListener(vote);

        f.add(b1); f.add(b2); f.add(b3); f.add(b4);
        f.setVisible(true);
    }

    // ---------------- CALCULATION LOGIN ----------------
    static void calculationLogin() {
        if (calcFrame != null && calcFrame.isDisplayable()) {
            calcFrame.toFront();
            calcFrame.requestFocus();
            return;
        }
        JFrame f = new JFrame("Result Login");
        calcFrame = f;
        setupFrame(f, 420, 240);

        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel l1 = new JLabel("Admin ID:");
        JLabel l2 = new JLabel("Password:");
        JTextField id = new JTextField(); id.setColumns(20);
        JPasswordField pass = new JPasswordField(); pass.setColumns(20);
        JButton login = new JButton("View Result");
        styleButton(login, PRIMARY);

        JCheckBox show = new JCheckBox("Show");
        show.setOpaque(false);
        final char echo = pass.getEchoChar();
        show.addItemListener(ev -> pass.setEchoChar(show.isSelected() ? (char)0 : echo));

        c.gridx = 0; c.gridy = 0; c.gridwidth = 1; p.add(l1, c);
        c.gridx = 1; c.gridy = 0; p.add(id, c);
        c.gridx = 0; c.gridy = 1; p.add(l2, c);
        c.gridx = 1; c.gridy = 1; p.add(pass, c);
        c.gridx = 2; c.gridy = 1; p.add(show, c);
        c.gridx = 0; c.gridy = 2; c.gridwidth = 3; p.add(login, c);

        login.addActionListener(e -> {
            if (id.getText().equals(adminId) &&
                new String(pass.getPassword()).equals(adminPass)) {
                showResult();
                f.dispose();
                calcFrame = null;
            } else {
                JOptionPane.showMessageDialog(f, "Invalid Login");
            }
        });

        f.add(p);
        f.setVisible(true);
        attachCloseListener(f, () -> calcFrame = null);
    }

    // ---------------- RESULT ----------------
    static void showResult() {
        JOptionPane.showMessageDialog(null,
            "FINAL RESULT\n\n" +
            "PTI: " + pti + "\n" +
            "PMLN: " + pmln + "\n" +
            "MQM: " + mqm + "\n" +
            "JMS: " + jms
        );
    }
}
 