package edu.eci.arsw.highlandersim;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControlFrame extends JFrame {

    private static final int DEFAULT_IMMORTAL_HEALTH = 100;
    private static final int DEFAULT_DAMAGE_VALUE = 10;

    private JPanel contentPane;

    private List<Immortal> immortals;

    private JTextArea output;
    private JTextField numOfImmortals;
    
    private static Object lock = new Object();

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ControlFrame frame = new ControlFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public ControlFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 647, 248);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        JToolBar toolBar = new JToolBar();
        contentPane.add(toolBar, BorderLayout.NORTH);

        final JButton btnStart = new JButton("Start");
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                immortals = setupInmortals();

                if (immortals != null) {
                    for (Immortal im : immortals) {
                        im.start();
                    }
                }

                btnStart.setEnabled(false);

            }
        });
        toolBar.add(btnStart);

        JButton btnPauseAndCheck = new JButton("Pause and check");
        btnPauseAndCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if(immortals != null){
                    
                    synchronized(lock){
                        int sum = 0;
                        // pausa a todos los inmortales para poder sumar los puntos 
                        for(Immortal im: immortals){
                            im.pause();
                        }
                        //Validar que todos esten pausados
                        int i=0;
                        while(i<immortals.size()){
                            System.out.println("Estado: "+immortals.get(i).getState() + "Posición: "+i);
                            if(immortals.get(i).getState()!=Thread.State.RUNNABLE){
                                i++;
                            }
                        }
                        // Suma todos los punto de vida 
                        for (Immortal im : immortals) {
                            sum += im.getHealth();
                        }
                        output.setText(immortals.toString() + ". Sum:" + sum);
                    }
                }
            }
        });
        toolBar.add(btnPauseAndCheck);

        JButton btnResume = new JButton("Resume");

        btnResume.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                //aqui todos se despiertan 
                   for(Immortal im: immortals){
                   im.cont();
                   }
            }
           
        });

        toolBar.add(btnResume);

        JLabel lblNumOfImmortals = new JLabel("num. of immortals:");
        toolBar.add(lblNumOfImmortals);

        numOfImmortals = new JTextField();
        //Con los valores 100, 1000 y 10000 funcionan correctamente.
        numOfImmortals.setText("3");
        toolBar.add(numOfImmortals);
        numOfImmortals.setColumns(10);
        
        //Implementacion del Boton STOP
        JButton btnStop = new JButton("STOP");
        btnStop.setForeground(Color.RED);
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                   for(Immortal im: immortals){
                       im.finishGame();
                   }
                   btnStart.setEnabled(false);
                   btnPauseAndCheck.setEnabled(false);
                   btnResume.setEnabled(false);
                   btnStop.setEnabled(false);
            }
           
        });
        toolBar.add(btnStop);

        JScrollPane scrollPane = new JScrollPane();
        contentPane.add(scrollPane, BorderLayout.CENTER);

        output = new JTextArea();
        output.setEditable(false);
        scrollPane.setViewportView(output);
        
        

    }

    public List<Immortal> setupInmortals() {

        try {
            int ni = Integer.parseInt(numOfImmortals.getText());

            List<Immortal> il = new LinkedList<Immortal>();

            for (int i = 0; i < ni; i++) {
                Immortal i1 = new Immortal("im" + i, il, DEFAULT_IMMORTAL_HEALTH, DEFAULT_DAMAGE_VALUE,lock);
                il.add(i1);
            }
            return il;
        } catch (NumberFormatException e) {
            JOptionPane.showConfirmDialog(null, "Número inválido.");
            return null;
        }

    }

}
