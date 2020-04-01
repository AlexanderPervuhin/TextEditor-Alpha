package editor;
 
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
 
 
 
interface SaveLoadListener {
    enum Actions {
        SAVE_FILE,
        LOAD_FILE
    }
 
    void onClick(String fileName, final Actions action) throws IOException; 
}

//class which implements SaveLoadListener can react to actions of observed object (in this case it's SaveLoadControl fileSelection).

 
public class TextEditor extends JFrame implements SaveLoadListener { 
    JTextArea textArea;

    public TextEditor() {
        setTitle("Text Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        textArea = new JTextArea(200, 200);
        textArea.setName("TextArea");
        JScrollPane scrollableTextArea = new JScrollPane(textArea);
        scrollableTextArea.setName("ScrollPane");
        scrollableTextArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollableTextArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        BorderFactory.setMargin(scrollableTextArea, 10, 10, 10, 10);
        add(scrollableTextArea, BorderLayout.CENTER);
        SaveLoadControl fileSelection = new SaveLoadControl();
        fileSelection.addSaveLoadListener(this);                  //this links Text editor and fileSelection panel
        BorderFactory.setMargin(fileSelection, 5, 0, 0, 5);
        add(fileSelection, BorderLayout.NORTH);
        setVisible(true);
    }
 
    @Override
    
     //this method perfoms an action depending on what button was clicked at observed object 
     
    public void onClick(String fileName, Actions action) throws IOException {
        File file = new File(fileName);
        switch (action) {
            case LOAD_FILE:
                if (file.exists()) loadTextFromFile(file);  
                else textArea.setText("");
                break;
            case SAVE_FILE:
                saveTextToFile(file);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + action);
        }
    }
    
    
    //auxilary methods to save/load text into textArea

    void loadTextFromFile(File file) throws IOException {
        Path path = Paths.get(file.getPath());
        String text = new String(Files.readAllBytes(path));
        textArea.setText(text);
    }
 
    void saveTextToFile(File file) {
 
        String text = textArea.getText();
        Path path = Paths.get(file.getPath());
        try {
            Files.write(path, text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
 
 
//class to make borders for JPanels and other stuff  

class BorderFactory {
    public static void setMargin(JComponent component, int top, int right, int bottom, int left) {
        Border border = component.getBorder();
        Border marginBorder = new EmptyBorder(new Insets(top, left, bottom, right));
        component.setBorder(border == null ? marginBorder
                : new CompoundBorder(marginBorder, border));
    }
}
 
 
 
 
//panel which contains save and load buttons
 
class SaveLoadControl extends JPanel {

    ArrayList<SaveLoadListener> buttonListeners = new ArrayList<>(); 
    //^ contains refences to Listeneres, so that they get notified when a certain action is performed 
        
    private JTextField fileNameField; 
 
    SaveLoadControl() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        fileNameField = new JTextField();
        fileNameField.setName("FilenameField");
        fileNameField.setColumns(30);
        JButton saveButton = new JButton();
        saveButton.setName("SaveButton");
        saveButton.setText("Save");
        
        saveButton.addActionListener(actionEvent -> {
            try {
                saveFileButtonAction();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        //^method to do something when button is pressed (save to file )
 
        JButton loadButton = new JButton();
        loadButton.setName("LoadButton");
        loadButton.setText("Load");
        
        loadButton.addActionListener(actionEvent -> {
            try {
                loadFileButtonAction();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        this.add(fileNameField);
        this.add(saveButton);
        this.add(loadButton);
    }
 
 
 
    void addSaveLoadListener(SaveLoadListener listener) {
        buttonListeners.add(listener);
    }
  //^adds new listener references;  
  
 
    private void loadFileButtonAction() throws IOException {
        String fileName = fileNameField.getText();
        if (fileName.isEmpty()) {
            JOptionPane.showMessageDialog(null, "File name is empty!");    //shows message box if file name field is empty
        } else {
            for (SaveLoadListener listener : buttonListeners) {           // goes through all the listeners and notifies them that load button is pressed
                listener.onClick(fileName, SaveLoadListener.Actions.LOAD_FILE);
            }
        }
    }
 
    private void saveFileButtonAction() throws IOException {
        String fileName = fileNameField.getText();
        if (fileName.isEmpty()) {
            JOptionPane.showMessageDialog(null, "File name is empty!");  //shows message box if file name field is empty
        } else
            for (SaveLoadListener listener : buttonListeners)           // goes through all the listeners and notifies them that load button is pressed
                listener.onClick(fileName, SaveLoadListener.Actions.SAVE_FILE);
            }
    }
}
