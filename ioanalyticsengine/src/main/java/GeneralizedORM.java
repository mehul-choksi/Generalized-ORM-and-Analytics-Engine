import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.*;

public class GeneralizedORM {

    private HashMap<Integer, String> indexToSubject;
    private HashMap<String, Student> studentMap;
    private String dbName;
    private String createSubjectTable = "create table %s (score int (3), student_count int);";
    private String createStudentTable = "create table students (roll_no varchar(10) primary key, %s int(3), %s int(3), %s int(3), %s int(3), " +
            "%s int(3), %s int(3), %s int(3), %s int(3))";

    private ArrayList<String> ddl;
    private ArrayList<String> dml;

    private Connection connection;

    private String path;
    private String subjectDesc;

    public GeneralizedORM(String path, String subjectDesc){
        this.path = path;
        this.subjectDesc = subjectDesc;

        indexToSubject = new HashMap<Integer, String>();
        ddl = new ArrayList<String>();
        dml = new ArrayList<String>();
        readSubjectData();
        initConnection();
        useDatabase();

    }

    public GeneralizedORM(String path, String subjectDesc,int val){
        this.path = path;
        this.subjectDesc = subjectDesc;

        indexToSubject = new HashMap<Integer, String>();
        ddl = new ArrayList<String>();
        dml = new ArrayList<String>();
        readSubjectData();
        createDatabase();
        initConnection();
        useDatabase();

    }

    public void initConnection(){
        try{
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/" +dbName , "root", "root");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void createDatabase(){
        try{
            String query = "create database " + dbName;
            Connection rootConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "root");
            Statement statement = rootConnection.createStatement();
            statement.executeUpdate(query);
            statement.close();
            rootConnection.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void useDatabase(){
        try{
            String useQuery = "use " + dbName;
            Connection rootConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/" , "root", "root");
            Statement statement = rootConnection.createStatement();
            statement.execute(useQuery);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void readSubjectData(){
        try{
            BufferedReader br = new BufferedReader(new FileReader(new File(path + subjectDesc)));
            String line;
            dbName = br.readLine();
            System.out.println("Database name: " + dbName);
            while((line = br.readLine()) != null){
                String tokens[] = line.split("\\s+");
                indexToSubject.put(Integer.parseInt(tokens[0]), tokens[1]);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void generateDDLQueries(){
        try{
            String subject;
            for(int i = 0; i < 8; i++){
                subject = indexToSubject.get(i);
                String subjectTable = String.format(createSubjectTable, subject);
                ddl.add(subjectTable);
            }
            String studentTable = String.format(createStudentTable, indexToSubject.get(0),indexToSubject.get(1),
                    indexToSubject.get(2),indexToSubject.get(3),indexToSubject.get(4),indexToSubject.get(5), indexToSubject.get(6),
                    indexToSubject.get(7), indexToSubject.get(8));
            ddl.add(studentTable);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void initializeSchema(){
        try{
            Statement statement = connection.createStatement();
            for(String query : ddl){
                statement.executeUpdate(query);
                System.out.println("Ddl query: " + query);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //Mode can either be truncate or drop
    public void cleanDatabase(String mode){
        String query = mode + " table students";
        try{
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            query = mode + " table %s";
            for(int i = 0; i < 5; i++){
                statement.executeUpdate(String.format(query, indexToSubject.get(i)));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public HashMap<String, Student> getStudentMap() {
        return studentMap;
    }

    public void setStudentMap(HashMap<String, Student> studentMap) {
        this.studentMap = studentMap;
    }

    public void write(){
        try{
            Statement statement = connection.createStatement();
            String s = "insert into students values ";
            StringBuilder query = new StringBuilder(s);
            ArrayList<HashMap<Integer, Integer> > subjectCount = new ArrayList<HashMap<Integer, Integer>>();
            for(int i = 0; i < 8; i++){
                HashMap<Integer, Integer> subject = new HashMap<Integer, Integer>();
                subjectCount.add(subject);
            }

            for(String rollNo : studentMap.keySet()){
                Student student = studentMap.get(rollNo);
                query.append("('" + student.rollNo + "'");
                ArrayList<Integer> theory = student.theory;
                ArrayList<Integer> practical = student.practical;

                int tBound = theory.size();
                int pBound = practical.size();
                for(int i = 0; i < tBound; i++){
                    int tScore = theory.get(i);
                    query.append( ", "+ theory.get(i));
                    HashMap<Integer, Integer> subject = subjectCount.get(i);
                    if(subject.containsKey(tScore)){
                        int tCount = subject.get(tScore);
                        tCount++;
                        subject.put(tScore, tCount);
                    }
                    else{
                        subject.put(tScore, 1);
                    }
                }
                for(int i = 0; i < pBound; i++){
                    query.append(", " + practical.get(i));
                }
                query.append("), ");
            }
            String exec = query.substring(0, query.length() - 2);
            System.out.println(exec);
            statement.executeUpdate(exec);

            for(int i = 0; i < 8; i++){
                HashMap<Integer, Integer> map = subjectCount.get(i);

                String prefix = "update " + indexToSubject.get(i)+ " set student_count = %d where score = %d";
                for(Integer key : map.keySet()){
                    statement.executeUpdate(String.format(prefix, map.get(key), key));
                }
            }

            System.out.println("Done.");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void initializeScoreTables(){
        String prefix = "insert into %s values ";
        try{
            Statement statement = connection.createStatement();
            for(int i = 0; i < 8; i++){
                String subject = indexToSubject.get(i);
                StringBuilder query = new StringBuilder(String.format(prefix,subject));

                for(int j = 0; j <= 100; j++){
                    query.append("(" + j + ",0 ), ");
                }
                statement.executeUpdate(new String(query.substring(0,query.length() - 2)));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }



}
