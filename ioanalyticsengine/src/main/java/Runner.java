public class Runner {

    public static void main(String args[]){
        String PATH = "/{your_path}";
        String pdfFile = "{result_pdf}";
        String readIndexes = "{pdf_index_file}";
        String subjectDescriptor = "{pdf_descriptor_file}";

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
