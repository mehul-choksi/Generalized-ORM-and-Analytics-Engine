public class Runner {

    public static void main(String args[]){
        String PATH = "/home/ash/workspace/result-data/";
        String pdfFile = "BE 2015PAT_EXTC.pdf";
        String readIndexes = "be_extc_2015";
        String subjectDescriptor = "be_extc_2015_desc";

        GeneralizedFileReader fileReader = new GeneralizedFileReader(PATH,pdfFile,readIndexes);
        fileReader.init();
        fileReader.readFile();
        fileReader.check();

        GeneralizedORM orm = new GeneralizedORM(PATH,subjectDescriptor,1);
        orm.useDatabase();
        //The below 2 functions are for one time use only
        orm.generateDDLQueries();
        orm.initializeSchema();

        orm.setStudentMap(fileReader.getStudents());
        orm.initializeScoreTables();
        orm.write();

    }
}
