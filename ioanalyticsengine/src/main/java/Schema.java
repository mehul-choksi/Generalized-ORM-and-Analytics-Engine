import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;


public class Schema {

    private String startLine;
    private String endLine;

    private int rollLineGap;
    private int semesters;

    private int subjectLineGap;

    private String path;
    private String readIndexFile;

    private ArrayList<Pair> subjectPositions;

    public Schema(String path, String readIndexFile){
        this.path = path;
        this.readIndexFile = readIndexFile;

        startLine = "B150";
        endLine = "SGPA1 :";
        rollLineGap = 0;
        semesters = 1;
        subjectLineGap = 6;
        subjectPositions = new ArrayList<Pair>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(new File(path + readIndexFile)));

            String line;
            while((line = br.readLine()) != null){
                String data[] = line.split("\\s");
                int r,c;
                for(int i = 0; i < data.length; i+=2){
                    r = Integer.parseInt(data[i]);
                    c = Integer.parseInt(data[i+1]);
                    Pair p = new Pair(r,c);
                    subjectPositions.add(p);
                }

            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }



    public String getStartLine() {
        return startLine;
    }

    public void setStartLine(String startLine) {
        this.startLine = startLine;
    }

    public String getEndLine() {
        return endLine;
    }

    public void setEndLine(String endLine) {
        this.endLine = endLine;
    }

    public int getRollLineGap() {
        return rollLineGap;
    }

    public void setRollLineGap(int rollLineGap) {
        this.rollLineGap = rollLineGap;
    }

    public int getSemesters() {
        return semesters;
    }

    public void setSemesters(int semesters) {
        this.semesters = semesters;
    }

    public ArrayList<Pair> getSubjectPositions() {
        return subjectPositions;
    }

    public void setSubjectPositions(ArrayList<Pair> subjectPositions) {
        this.subjectPositions = subjectPositions;
    }

    public int getSubjectLineGap() {
        return subjectLineGap;
    }

    public void setSubjectLineGap(int subjectLineGap) {
        this.subjectLineGap = subjectLineGap;
    }
}
