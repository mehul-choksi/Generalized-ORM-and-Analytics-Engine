import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GeneralizedFileReader implements  ReaderInterface{

    private Schema fileSchema;

    private ArrayList<Pair>  subjectPositions;

    private HashMap<String, Student> students;
    private String path;
    private String pdfFile;
    private String readIndexFile;
    public GeneralizedFileReader(String path, String pdfFile, String readIndexFile){
        this.path = path;
        this.pdfFile = pdfFile;
        this.readIndexFile = readIndexFile;
        students = new HashMap<String, Student>();
        fileSchema = new Schema(path, readIndexFile);
    }

    public void loadFileSchema() {
        fileSchema = new Schema(path, readIndexFile);
    }

    public void init(){
        subjectPositions = fileSchema.getSubjectPositions();
    }

    public void readFile() {
        try {
            PDDocument document = PDDocument.load(new File(path+pdfFile));
            document.getClass();

            if (!document.isEncrypted()) {
                PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                stripper.setSortByPosition(true);
                PDFTextStripper tStripper = new PDFTextStripper();
                String pdfFileInText = tStripper.getText(document);

                String lines[] = pdfFileInText.split("\\r?\\n");
                int lineLen = lines.length;

                for (int i = 0; i < lineLen; i++) {
                  while (!lines[i].startsWith(fileSchema.getStartLine()) && i < lineLen){
                        i++;
                    }
                    i += fileSchema.getRollLineGap();
                    String rollNo = lines[i].split("\\s+")[0].trim();
//                    System.out.println("Roll no.: " + rollNo);
                    i += fileSchema.getSubjectLineGap();
                    int count = 0;
                    ArrayList<Integer> currentTheory = new ArrayList<Integer>();
                    ArrayList<Integer> currentPractical = new ArrayList<Integer>();

                    int score;
                    for(Pair p : subjectPositions){
                        String tokens[] = lines[i + p.r - 1].split("\\s+");
                        try{
                            score = Integer.parseInt(tokens[p.c].split("\\/")[0]);
                        }
                        catch (Exception e){
                            score = -1;
                        }
                        if(count < 5){
                            currentTheory.add(score);
                        }
                        else{
                            currentPractical.add(score);
                        }
                        count++;
                    }
                    //May change according to req
                    currentPractical.set(0, currentPractical.get(0) + currentPractical.get(1));
                    currentPractical.set(2, currentPractical.get(2) + currentPractical.get(3));
                    currentPractical.remove(1);
                    currentPractical.remove(3);
                    Student s = new Student(rollNo);
                    s.theory = currentTheory;
                    s.practical = currentPractical;
                    students.put(rollNo, s);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void check(){
        int x = 0;
        for(String roll : students.keySet()){
            if(x >= 5){
                break;
            }
            System.out.println("Roll no. : " + roll);
            Student s = students.get(roll);
            System.out.println(s.theory);
            System.out.println(s.practical);
            x++;
        }
    }

    public HashMap<String, Student> getStudents(){
        return students;
    }
}
