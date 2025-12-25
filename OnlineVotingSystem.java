import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

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

    static int pti = 0, pmln = 0, mqm = 0, jms = 0;

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

    // ---------------- MAIN ----------------
    public static void main(String[] args) {
        voters.add(new Voter("Ali", "Ahmed", "01-01-2000", "42101"));
        voters.add(new Voter("Sara", "Khan", "02-02-2000", "42102"));
        voters.add(new Voter("Usman", "Raza", "03-03-2000", "42103"));
        voters.add(new Voter("Huzaifa", "Raza", "03-03-2000", "412042529"));
        voters.add(new Voter("Rehan", "Sabir", "03-03-2000", "42103"));
        voters.add(new Voter("Rafai", "Kashif", "03-03-2000", "42103"));
        voters.add(new Voter("Aftab", "Raza", "03-03-2000", "42103"));
        voters.add(new Voter("Kashif", "Sommoro", "03-03-2000", "42103"));
        voters.add(new Voter("Mohid", "Amir", "03-03-2000", "42103"));
        voters.add(new Voter("Yasir", "Naveed", "03-03-2000", "42103"));
        voters.add(new Voter("Ibad", "Nasar", "03-03-2000", "42103"));
        voters.add(new Voter("Adnan", "Zafar", "03-03-2000", "42103"));
        showMainMenu();
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

        JButton adminBtn = new JButton("ADMIN PANEL");
        JButton voterBtn = new JButton("VOTER PANEL");
        JButton calcBtn = new JButton("CALCULATION");

        styleButton(adminBtn, PRIMARY);
        styleButton(voterBtn, SUCCESS);
        styleButton(calcBtn, DANGER);

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
        JFrame f = new JFrame("Admin Login");
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
                adminPanel();
            } else {
                JOptionPane.showMessageDialog(f, "Wrong ID or Password");
            }
        });

        f.add(p);
        f.setVisible(true);
    }

    // ---------------- ADMIN PANEL ----------------
    static void adminPanel() {
        JFrame f = new JFrame("Admin Panel");
        setupFrame(f, 800, 500);
        f.setLayout(new BorderLayout(8, 8));

        String[] col = {"Name", "Father", "DOB", "CNIC", "Voted"};
        String[][] data = new String[voters.size()][5];

        for (int i = 0; i < voters.size(); i++) {
            Voter v = voters.get(i);
            data[i][0] = v.name;
            data[i][1] = v.father;
            data[i][2] = v.dob;
            data[i][3] = v.cnic;
            data[i][4] = v.voted ? "YES" : "NO";
        }

        JTable table = new JTable(data, col);
        JScrollPane sp = new JScrollPane(table);

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
            voters.add(new Voter(
                name.getText(),
                father.getText(),
                dob.getText(),
                cnic.getText()
            ));
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
                JOptionPane.showMessageDialog(null, "Voter Deleted");
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Voter Not Found");
    }

    // ---------------- VOTER FORM ----------------
    static void voterForm() {
        JFrame f = new JFrame("Voter Form");
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
            for (Voter v : voters) {
                if (v.name.equalsIgnoreCase(name.getText()) &&
                    v.father.equalsIgnoreCase(father.getText()) &&
                    v.dob.equals(dob.getText()) &&
                    v.cnic.equals(cnic.getText())) {

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
        JFrame f = new JFrame("Result Login");
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
            } else {
                JOptionPane.showMessageDialog(f, "Invalid Login");
            }
        });

        f.add(p);
        f.setVisible(true);
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
