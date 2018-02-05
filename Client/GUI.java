import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.regex.Pattern;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GUI extends JFrame 
                implements ItemListener, ActionListener {

    /** Services */
    final private MessageService MessageService = new MessageService();

    /** Private Variables */
    final private String[] actionStrings = { "SUBMIT", "GET", "GET ALL", "UPDATE", "REMOVE" };
    final private String[] getStrings = { "ISBN", "TITLE", "AUTHOR", "PUBLISHER", "YEAR" };
    final private String[] updateStrings = { "TITLE", "AUTHOR", "PUBLISHER", "YEAR" };
    
    final private int SUBMIT = 0;
    final private int GET = 1;
    final private int GETALL = 2;
    final private int UPDATE = 3;
    final private int REMOVE = 4;

    final private int ISBN_LENGTH = 13;
    final private int FIRST_ISBN_FIELD = 3;
    final private int SECOND_ISBN_FIELD = 1;
    final private int THIRD_ISBN_FIELD = 2;
    final private int FOURTH_ISBN_FIELD = 6;
    final private int FIFTH_ISBN_FIELD = 1;
    final private int TEXT_FIELD = 80;

    /** Layouts */
    private FlowLayout actionLayout = new FlowLayout();
    private FlowLayout getLayout = new FlowLayout();
    private FlowLayout updateLayout = new FlowLayout();
    private FlowLayout removeLayout = new FlowLayout();
    private FlowLayout rowLayout = new FlowLayout();

    /** Card panel */
    private JPanel cards = new JPanel(new CardLayout());

    /** Combo boxs */
    private JComboBox<String> actionList = new JComboBox<String>(actionStrings);
    private JComboBox<String> updateList = new JComboBox<String>(updateStrings);
    private JComboBox<String> getList = new JComboBox<String>(getStrings);
    private JComboBox<String> removeList = new JComboBox<String>(getStrings);

    /** Buttons */
    private JButton actionButton = new JButton("Send Request");
    private JRadioButton bibtexButton = new JRadioButton("Bibtex Format");

    /** Submit Text Fields */
    private JTextField submitAuthorField = new JTextField();
    private JTextField submitTitleField = new JTextField();
    private JTextField submitPublisherField = new JTextField();
    private JTextField submitYearField = new JTextField();

    /** Other Text Fields */
    private JTextField getField = new JTextField();
    private JTextField updateField = new JTextField();
    private JTextField removeField = new JTextField();

    /** Results area */
    private JTextArea resultTextArea = new JTextArea(25, 0);

    /** Submit ISBN Fields */
    private JTextField submitFirstField;
    private JTextField submitSecondField;
    private JTextField submitThirdField;
    private JTextField submitFourthField;
    private JTextField submitFifthField;

    /** Get ISBN Fields */
    private JTextField getFirstField;
    private JTextField getSecondField;
    private JTextField getThirdField;
    private JTextField getFourthField;
    private JTextField getFifthField;

    /** Update ISBN Fields */
    private JTextField updateFirstField;
    private JTextField updateSecondField;
    private JTextField updateThirdField;
    private JTextField updateFourthField;
    private JTextField updateFifthField;

    /** Remove ISBN Fields */
    private JTextField removeFirstField;
    private JTextField removeSecondField;
    private JTextField removeThirdField;
    private JTextField removeFourthField;
    private JTextField removeFifthField;

    /** String constants */
    final private String ISBN = "ISBN: ";
    final private String TITLE = "Title: ";
    final private String AUTHOR = "Author(s): ";
    final private String PUBLISHER = "Publisher: ";
    final private String YEAR = "Year: ";

    /** Card state */
    private String state = actionStrings[SUBMIT];

    /** Socket variables */
    private Scanner in;
    private PrintWriter out;
    
    // Constructor to setup the GUI components
    public GUI(String name, Scanner input, PrintWriter output) {

        super(name);

        this.in = input;
        this.out = output;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);     // create and set up the window.
        addComponentsToPane(getContentPane());              // set up the content pane.                               // display the window.
        pack();                                             // pack the content pane
        setVisible(true);                                   // show gui
    }
   
    public void addComponentsToPane(Container pane) {
        
        getRootPane().setDefaultButton(actionButton);       // set action button as default action

        /** Create panels */
        JPanel actionPanel = new JPanel();
        JPanel resultPanel = new JPanel();
        JPanel actionButtonPanel = new JPanel();

        /** Set panel layouts */
        actionPanel.setLayout(actionLayout);
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        actionButtonPanel.setLayout(rowLayout);

        actionButton.addActionListener(this);               // add listener to action button

        /** Set action list defaults */
        actionList.setSelectedIndex(SUBMIT);
        actionList.setEditable(false);
        actionList.addItemListener(this);
        actionPanel.add(actionList);
        actionPanel.setComponentOrientation(
            ComponentOrientation.LEFT_TO_RIGHT);

        /** Create action "cards" */
        JPanel submitCard = createSubmitCard();
        JPanel removeCard = createRemoveCard();
        JPanel getAllCard = createGetAllCard();
        JPanel updateCard = createUpdateCard();
        JPanel getCard    = createGetCard();

        /** Add action cards to cards panel */
        cards.add(submitCard, actionStrings[SUBMIT]);
        cards.add(removeCard, actionStrings[REMOVE]);
        cards.add(getAllCard, actionStrings[GETALL]);
        cards.add(updateCard, actionStrings[UPDATE]);
        cards.add(getCard, actionStrings[GET]);

        /** Create result(s) field */
        JScrollPane resultScrollPane = new JScrollPane(resultTextArea);
        actionButtonPanel.add(actionButton);
        actionButtonPanel.add(bibtexButton);
        resultPanel.add(actionButtonPanel, BorderLayout.PAGE_START);
        resultPanel.add(resultScrollPane, BorderLayout.CENTER);

        /** Add panels to pane */
        pane.add(actionPanel, BorderLayout.PAGE_START);
        pane.add(cards, BorderLayout.CENTER);
        pane.add(resultPanel, BorderLayout.PAGE_END);

    }

    /** Listens to the action combo box. */
    public void itemStateChanged(ItemEvent e) {
        CardLayout cardLayout = (CardLayout) (cards.getLayout());
        String action = (String) e.getItem();
        state = action;
        cardLayout.show(cards, (String) action);
    }

    /** Listens to the action button. */
    public void actionPerformed(ActionEvent e) {

        String message, response, formattedResponse;
        String action, selection;
        String get, update, remove;
        String isbn, author, title, publisher, year;
        String output = "";

        boolean allFieldsComplete, isValidISBN, shouldFormat;
        
        switch (state) {

            case "SUBMIT":
                action = state;
                isbn = getISBN(state);
                author = submitAuthorField.getText();
                title = submitTitleField.getText();
                year = submitYearField.getText();
                publisher = submitPublisherField.getText();
                allFieldsComplete = checkSubmitFields(isbn, author, title, year, publisher);
                if (!allFieldsComplete) {
                    JOptionPane.showMessageDialog(
                        null, "Please ensure all fields are complete.");
                    break;
                }
                isValidISBN = checkISBN(isbn);
                if (!isValidISBN) {
                    JOptionPane.showMessageDialog(
                        null, "Please enter a valid ISBN.");
                    break;
                }
                message = MessageService.makeSubmitMessage(
                    action, isbn, title, author, year, publisher);
                out.println(message);
                while (!(response = in.nextLine()).equals(""))
                    resultTextArea.setText(response);
                break;

            case "GET":
                action = state;
                selection = getList.getSelectedItem().toString();
                shouldFormat = bibtexButton.isSelected();
                if (selection.equals("ISBN")) {
                    isbn = getISBN(state);
                    if (isbn.isEmpty()) {
                        JOptionPane.showMessageDialog(
                            null, "Please ensure ISBN fields are complete.");
                        return;
                    }
                    isValidISBN = checkISBN(isbn);
                    if (!isValidISBN) {
                        JOptionPane.showMessageDialog(
                            null, "Please enter a valid ISBN.");
                        return;
                    }
                    message = action + " ISBN " + isbn;
                } else {
                    get = getField.getText();
                    if (get.isEmpty()) {
                        JOptionPane.showMessageDialog(
                            null, "Please ensure the " + selection.toLowerCase() + " field is complete.");
                        return;
                    }
                    message = action + " " + selection + " " + get;
                }
                out.println(message);
                while (!(response = in.nextLine()).equals("")) {
                    if (shouldFormat)
                        response = MessageService.toBibtex(response);
                    output = output + response + "\n";
                }
                resultTextArea.setText(output);
                break;

            case "GET ALL":
                out.println(state);
                shouldFormat = bibtexButton.isSelected();
                while (!(response = in.nextLine()).equals("")) {
                    if (shouldFormat)
                        response = MessageService.toBibtex(response);
                    output = output + response + "\n";
                }
                resultTextArea.setText(output);
                break;

            case "UPDATE":
                action = state;
                isbn = getISBN(state);
                selection = updateList.getSelectedItem().toString();
                update = updateField.getText();
                allFieldsComplete = checkUpdateFields(isbn, update);
                if (!allFieldsComplete) {
                    JOptionPane.showMessageDialog(null, "Please ensure all fields are complete.");
                    break;
                }
                isValidISBN = checkISBN(isbn);
                if (!isValidISBN) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid ISBN.");
                    break;
                }
                message = MessageService.makeUpdateMessage(
                    action, selection, isbn, update);
                out.println(message);
                while (!(response = in.nextLine()).equals(""))
                    resultTextArea.setText(response);
                break;

            case "REMOVE":
                action = state;
                selection = removeList.getSelectedItem().toString();
                remove = removeField.getText();
                if (selection.equals("ISBN")) {
                    isbn = getISBN(state);
                    if (isbn.isEmpty()) {
                        JOptionPane.showMessageDialog(
                            null, "Please ensure ISBN fields are complete.");
                        return;
                    }
                    isValidISBN = checkISBN(isbn);
                    if (!isValidISBN) {
                        JOptionPane.showMessageDialog(
                            null, "Please enter a valid ISBN.");
                        return;
                    }
                    message = action + " ISBN " + isbn;
                } else {
                    remove = removeField.getText();
                    if (remove.isEmpty()) {
                        JOptionPane.showMessageDialog(
                            null, "Please ensure the " + selection.toLowerCase() + " field is complete.");
                        return;
                    }
                    message = action + " " + selection + " " + remove;
                }
                out.println(message);
                while (!(response = in.nextLine()).equals(""))
                    resultTextArea.setText(response);
                break;

            default:
                break;                
        }
    }

    /**
     * Builds the SUBMIT card for GUI.
     * @param Void.
     * @return SUBMIT card as JPanel.
     */
    public JPanel createSubmitCard() {

        /** Submit Card */
        JPanel submitCard = new JPanel();
        submitCard.setLayout(new BoxLayout(submitCard, BoxLayout.Y_AXIS));

        /** Panels */
        JPanel isbnPanel = getISBNPanel(actionStrings[SUBMIT]);
        JPanel titlePanel = new JPanel();
        JPanel authorPanel = new JPanel();
        JPanel publisherPanel = new JPanel();
        JPanel yearPanel = new JPanel();

        /** Panel Layouts */
        isbnPanel.setLayout(rowLayout);
        titlePanel.setLayout(rowLayout);
        authorPanel.setLayout(rowLayout);
        publisherPanel.setLayout(rowLayout);
        yearPanel.setLayout(rowLayout);

        /** Labels */
        JLabel isbnLabel = new JLabel(ISBN);
        JLabel titleLabel = new JLabel(TITLE);
        JLabel authorLabel = new JLabel(AUTHOR);
        JLabel publisherLabel = new JLabel(PUBLISHER);
        JLabel yearLabel = new JLabel(YEAR);

        /** Text Column Widths */
        submitAuthorField.setColumns(TEXT_FIELD);
        submitTitleField.setColumns(TEXT_FIELD);
        submitPublisherField.setColumns(TEXT_FIELD);
        submitYearField.setColumns(TEXT_FIELD);

        /** Text Input Filters */
        MessageService.textFilter(submitAuthorField, TEXT_FIELD);
        MessageService.textFilter(submitTitleField, TEXT_FIELD);
        MessageService.textFilter(submitPublisherField, TEXT_FIELD);

        /** Year Filter */
        MessageService.numberFilter(submitYearField, TEXT_FIELD);

        /** Add Fields to Panels */
        titlePanel.add(titleLabel);
        titlePanel.add(submitTitleField);
        authorPanel.add(authorLabel);
        authorPanel.add(submitAuthorField);
        publisherPanel.add(publisherLabel);
        publisherPanel.add(submitPublisherField);
        yearPanel.add(yearLabel);
        yearPanel.add(submitYearField);

        /** Add Panels */
        submitCard.add(isbnPanel);
        submitCard.add(authorPanel);
        submitCard.add(titlePanel);
        submitCard.add(publisherPanel);
        submitCard.add(yearPanel);

        return submitCard;
    }

    /**
     * Builds the GET card for GUI.
     * @param Void.
     * @return GET card as JPanel.
     */
    public JPanel createGetCard() {

        /** Get Card */
        JPanel getCard = new JPanel();
        JPanel getListPanel = new JPanel();
        JPanel getPanel = new JPanel();

        getCard.setLayout(new BoxLayout(getCard, BoxLayout.Y_AXIS));
        getListPanel.setLayout(getLayout);

        JPanel isbnPanel = getISBNPanel(actionStrings[GET]);

        getListPanel.add(getList);

        getField.setColumns(TEXT_FIELD);                  // Text column width
        getPanel.add(getField);                           // Add field to panel

        getCard.add(isbnPanel);
        getCard.add(getListPanel);
        getCard.add(getPanel);

        return getCard;
    }

    /**
     * Builds the GET ALL card for GUI.
     * @param Void.
     * @return GET ALL card as JPanel.
     */
    public JPanel createGetAllCard() {

        /** Get All Card */
        JPanel getAllCard = new JPanel();
        getAllCard.setLayout(new BoxLayout(getAllCard, BoxLayout.Y_AXIS));

        return getAllCard;
    }

    /**
     * Builds the UPDATE card for GUI.
     * @param Void.
     * @return UPDATE card as JPanel.
     */
    public JPanel createUpdateCard() {

        /** Update Card */
        JPanel updateCard = new JPanel();
        JPanel updateListPanel = new JPanel();
        JPanel updatePanel = new JPanel();

        updateCard.setLayout(new BoxLayout(updateCard, BoxLayout.Y_AXIS));
        updateListPanel.setLayout(updateLayout);

        JPanel isbnPanel = getISBNPanel(actionStrings[UPDATE]);

        updateListPanel.add(updateList);

        updateField.setColumns(TEXT_FIELD);                  // Text column width
        updatePanel.add(updateField);                        // Add field to panel

        updateCard.add(isbnPanel);
        updateCard.add(updateListPanel);
        updateCard.add(updatePanel);

        return updateCard;
    }

    /**
     * Builds the REMOVE card for GUI.
     * @param Void.
     * @return REMOVE card as JPanel.
     */
    public JPanel createRemoveCard() {

        /** Remove Card */
        JPanel removeCard = new JPanel();
        JPanel removeListPanel = new JPanel();
        JPanel removePanel = new JPanel();

        removeCard.setLayout(new BoxLayout(removeCard, BoxLayout.Y_AXIS));
        removeListPanel.setLayout(removeLayout);

        JPanel isbnPanel = getISBNPanel(actionStrings[REMOVE]);

        removeListPanel.add(removeList);

        removeField.setColumns(TEXT_FIELD);                     // Text column width
        removePanel.add(removeField);                           // Add field to panel

        removeCard.add(isbnPanel);
        removeCard.add(removeListPanel);
        removeCard.add(removePanel);

        return removeCard;
    }

    /**
     * Builds the ISBN panel for GUI for given state.
     * @param Current state of action combo box.
     * @return ISBN Panel.
     */
    public JPanel getISBNPanel(String state) {

        JPanel isbnPanel = new JPanel();                    // ISBN Panel 
        isbnPanel.setLayout(rowLayout);                     // ISBN Panel Layout
        JLabel isbnLabel = new JLabel(ISBN);                // ISBN Labels

        /** ISBN Fields */
        JTextField firstField = new JTextField();
        JTextField secondField = new JTextField();
        JTextField thirdField = new JTextField();
        JTextField fourthField = new JTextField();
        JTextField fifthField = new JTextField();

        /** ISBN Column Widths */
        firstField.setColumns(FIRST_ISBN_FIELD);
        secondField.setColumns(SECOND_ISBN_FIELD);
        thirdField.setColumns(THIRD_ISBN_FIELD);
        fourthField.setColumns(FOURTH_ISBN_FIELD);
        fifthField.setColumns(FIFTH_ISBN_FIELD);

        /** ISBN Filters */
        MessageService.numberFilter(firstField, FIRST_ISBN_FIELD);
        MessageService.numberFilter(secondField, SECOND_ISBN_FIELD);
        MessageService.numberFilter(thirdField, THIRD_ISBN_FIELD);
        MessageService.numberFilter(fourthField, FOURTH_ISBN_FIELD);
        MessageService.numberFilter(fifthField, FIFTH_ISBN_FIELD);

        switch (state) {

            case "SUBMIT":
                setSubmitISBNFields(
                    firstField, secondField, thirdField, fourthField, fifthField);
                break;

            case "GET":
                setGetISBNFields(
                    firstField, secondField, thirdField, fourthField, fifthField);
                break;

            case "UPDATE":
                setUpdateISBNFields(
                    firstField, secondField, thirdField, fourthField, fifthField);
                break;

            case "REMOVE":
                setRemoveISBNFields(
                    firstField, secondField, thirdField, fourthField, fifthField);
                break;
        }
        
        /** Add ISBN Input Fields to ISBN Panel */
        isbnPanel.add(isbnLabel);
        isbnPanel.add(firstField);
        isbnPanel.add(new JLabel("-"));
        isbnPanel.add(secondField);
        isbnPanel.add(new JLabel("-"));
        isbnPanel.add(thirdField);
        isbnPanel.add(new JLabel("-"));
        isbnPanel.add(fourthField);
        isbnPanel.add(new JLabel("-"));
        isbnPanel.add(fifthField);

        return isbnPanel;
    }

    /**
     * Check whether the 13-digits number is valid as 13-digits ISBN.
     * @param number 13-digits number which you want to check. This must not include hyphens
     * @return true if the 13-digits number is valid as ISBN, otherwise false
     */
    public boolean checkISBN(String isbn) {
        if (!Pattern.matches("^\\d{" + ISBN_LENGTH + "}$", isbn)) return false;

        char[] digits = isbn.toCharArray();
        final int myDigit = computeIsbn13CheckDigit(digits);
        int checkDigit = digits[ISBN_LENGTH - 1] - '0';
        return myDigit == 10 && checkDigit == 0 || myDigit == checkDigit;
    }   

    /**
     * Compute the check digits of 13-digits ISBN.
     * Both full 13-digits and check-digit-less 12-digits are allowed as the argument.
     * @param digits the array of each digit in ISBN.
     * @return check digit
     */
    public int computeIsbn13CheckDigit(char[] digits) {
        int[] weights = {1, 3};
        int sum = 0;
        for (int i = 0; i < ISBN_LENGTH - 1; ++i) 
            sum += (digits[i] - '0') * weights[i % 2];
        return 10 - sum % 10;
    }

    /**
     * Checks to see if the given fields are complete.
     * @param String values of fields to check.
     * @return True if strings are non-empty, false otherwise.
     */
    public boolean checkSubmitFields(
        String isbn, 
        String author, 
        String title, 
        String year, 
        String publisher) {
        return (!isbn.isEmpty() && 
            !author.isEmpty() && 
            !title.isEmpty() && 
            !publisher.isEmpty() && 
            !year.isEmpty());
    }

    /**
     * Checks to see if the given fields are complete.
     * @param String values of fields to check.
     * @return True if strings are non-empty, false otherwise.
     */
    public boolean checkUpdateFields(
        String isbn, 
        String update) {
        return (!isbn.isEmpty() &&
            !update.isEmpty());
    }

    /**
     * Gets the ISBN numbers from the ISBN fields.
     * @param Current state of action combo box.
     * @return String representation of the ISBN number.
     */
    public String getISBN(String state) {

        String isbn = null;

        switch (state) {

            case "SUBMIT":
                isbn = submitFirstField.getText() + 
                    submitSecondField.getText() + 
                    submitThirdField.getText() + 
                    submitFourthField.getText() + 
                    submitFifthField.getText();
                break;

            case "GET":
                isbn = getFirstField.getText() + 
                    getSecondField.getText() + 
                    getThirdField.getText() + 
                    getFourthField.getText() + 
                    getFifthField.getText();
                break;

            case "UPDATE":
                isbn = updateFirstField.getText() + 
                    updateSecondField.getText() + 
                    updateThirdField.getText() + 
                    updateFourthField.getText() + 
                    updateFifthField.getText();
                break;

            case "REMOVE":
                isbn = removeFirstField.getText() + 
                    removeSecondField.getText() + 
                    removeThirdField.getText() + 
                    removeFourthField.getText() + 
                    removeFifthField.getText();
                break;
        }

        return isbn;
    }

    /**
     * Sets the ISBN fields for SUBMIT state.
     * @param JTextFields to bind to.
     * @return Void.
     */
    public void setSubmitISBNFields(
        JTextField a,
        JTextField b,
        JTextField c, 
        JTextField d, 
        JTextField e) {
        this.submitFirstField = a;
        this.submitSecondField = b;
        this.submitThirdField = c;
        this.submitFourthField = d;
        this.submitFifthField = e;
    }

    /**
     * Sets the ISBN fields for GET state.
     * @param JTextFields to bind to.
     * @return Void.
     */
    public void setGetISBNFields(
        JTextField a,
        JTextField b,
        JTextField c, 
        JTextField d, 
        JTextField e) {
        this.getFirstField = a;
        this.getSecondField = b;
        this.getThirdField = c;
        this.getFourthField = d;
        this.getFifthField = e;
    }

    /**
     * Sets the ISBN fields for UPDATE state.
     * @param JTextFields to bind to.
     * @return Void.
     */
    public void setUpdateISBNFields(
        JTextField a,
        JTextField b,
        JTextField c, 
        JTextField d, 
        JTextField e) {
        this.updateFirstField = a;
        this.updateSecondField = b;
        this.updateThirdField = c;
        this.updateFourthField = d;
        this.updateFifthField = e;
    }

    /**
     * Sets the ISBN fields for REMOVE state.
     * @param JTextFields to bind to.
     * @return Void.
     */
    public void setRemoveISBNFields(
        JTextField a,
        JTextField b,
        JTextField c, 
        JTextField d, 
        JTextField e) {
        this.removeFirstField = a;
        this.removeSecondField = b;
        this.removeThirdField = c;
        this.removeFourthField = d;
        this.removeFifthField = e;
    }

}
