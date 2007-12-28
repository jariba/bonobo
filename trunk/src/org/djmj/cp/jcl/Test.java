/*
 * Test.java
 *
 * Created on November 12, 2003, 4:31 PM
 */

package org.djmj.cp.jcl;

import java.lang.StringBuffer;
import java.io.StringReader;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author  Javier
 */
public class Test 
    extends JFrame
    implements JCLParserOutObserver,ActionListener
{
    JTextArea inputArea_;
    JTextArea outputArea_;
    JCLParserContext pcontext_;
    
    public static void main(String args[])
    {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            Test t = new Test();
            if (args.length > 1)
                t.includeFile(args[1]);
            t.setVisible(true);
        }
        catch (Exception e) {
            System.out.println("Caught parser error:"+e);
            e.printStackTrace();
        }
    }
    
    public Test()
    {
        setVisible(false);
        layoutUI();
        setJMenuBar(makeMenuBar());
        pcontext_ = new JCLParserContext(new JCLSymbolTable(),new JCLParserOut());
        pcontext_.getOut().addObserver(new JCLParserOut.JCLPOConsoleObserver());
        pcontext_.getOut().addObserver(this);
    }

    private void layoutUI()
    {
        // instantiate main frame component: position it in the middle of the
        // screen, size to be 80% of screen, and listen for window close events
        setTitle("JCL Listener");

        Dimension d = getToolkit().getScreenSize();
        setBounds((int)(d.width*.1), (int)(d.height*.1),
                  (int)(d.width*.8), (int)(d.height*.8));

        JComponent mainPanel = makeMainPanel();
        getContentPane().add(mainPanel);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });
    }

    private JComponent makeMainPanel()
    {
        inputArea_ = new JTextArea();
        outputArea_ = new JTextArea();        
        JSplitPane p = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        JPanel buttonPane = new JPanel(new FlowLayout());
        JButton b = new JButton("Parse");
        b.setActionCommand("Parse");
        b.addActionListener(this);
        buttonPane.add(b);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(buttonPane,BorderLayout.WEST);
        topPanel.add(new JScrollPane(inputArea_),BorderLayout.CENTER);
        p.add(JSplitPane.TOP,topPanel);

        p.add(JSplitPane.BOTTOM,new JScrollPane(outputArea_));
        p.setDividerLocation(0.5);

        return p;
    }

    private JMenuBar makeMenuBar()
    {
        JMenuBar mb = new JMenuBar();

        JMenu fileMenu = new JMenu("File");

        fileMenu.add(new AbstractAction("Exit") {
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });
        mb.add(fileMenu);

        return mb;
    }


    public void includeFile(String filename)
        throws Exception
    {
        StringBuffer code = new StringBuffer();
        code.append("include \""+filename+"\"\n");
        parse(code.toString());        
    }    
    
    protected void parseInputArea()
        throws Exception
    {
        String txt = inputArea_.getText();
        outputArea_.append("\n"+txt+"\n");
        parse(txt);
        inputArea_.setText(null);
    }
    
    protected void parse(String code)
        throws Exception
    {        
        StringReader input = new StringReader(code);        
        JCLLexer lexer = new JCLLexer(input); // attach lexer to the input stream        
        JCLParser parser = new JCLParser(lexer); // Create parser attached to lexer
        parser.context_ = pcontext_;
        // start up the parser by calling the rule at which you want to begin parsing.
        parser.program();                        
    }
    
    public void onPrintln(String s) 
    {
        outputArea_.append(s+"\n");        
    }    
    
    /** Invoked when an action occurs.
     *
     */
    public void actionPerformed(ActionEvent ev) 
    {
        if (ev.getActionCommand().equals("Parse")) {
            try {
                parseInputArea();
            }
            catch (Exception e) {
                outputArea_.append("ERROR:"+e);
                e.printStackTrace();
            }
        }
    }
}
