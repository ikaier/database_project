import java.sql.* ;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class VaccineApp {
    public static Connection con;
    public static Statement statement;
    public static int sqlCode=0;      // Variable to hold SQLCODE
    public static String sqlState="00000";  // Variable to hold SQLSTATE

    /*
    Check if the hinsurnum is already in the TABLE RECEIVER
    retrun true if the hinsurnum exist, otherwise return false
     */
    private static boolean check_hinsurnum(String hinsurnum) throws SQLException {
        boolean return_result=false;
        try {
            String querySQL = "SELECT COUNT(*) from RECEIVER WHERE HINSURNUM = '" + hinsurnum + "'";
            java.sql.ResultSet rs = statement.executeQuery(querySQL);
            if (rs.next()) {
                if (rs.getInt(1) >= 1) {
                    return_result = true;
                }
            }
        }catch (SQLException e)
        {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }
        return return_result;
    }

    /*
    update TABLE RECEIVER and RECEIVER_PRIORITY if the user choose to update the existed hinsurnum
     */
    private static void update_person(String hinsurnum, String name, String phone, String postal_code,String city,String street_address,String gender,String birthday,String critical_category) throws SQLException {
        try {
            String updateSQL = "UPDATE RECEIVER SET name = '" +name+"', " +
                    "phone = '" + phone+"',"+
                    "postal_code = '" + postal_code+"',"+
                    "city = '" + city+"',"+
                    "street_address = '" + street_address+"',"+
                    "birthday = '" + birthday+"',"+
                    "gender = '" + gender+"' "+
                    "WHERE hinsurnum = '"+hinsurnum+"'";
            //System.out.println(updateSQL);
            statement.executeUpdate(updateSQL);
            System.out.println("*UPDATE TABLE RECEIVER successfully");
            updateSQL = "UPDATE RECEIVER_PRIORITY SET critical_category ='"+critical_category+"' WHERE hinsurnum = '"+hinsurnum+"'";
            statement.executeUpdate(updateSQL);
            System.out.println("*UPDATE TABLE RECEIVER_PRIORITY successfully");
        }catch (SQLException e)
        {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }
    }
    /*
    INSERT TABLE RECEIVER and RECEIVER_PRIORITY if the hinsurnum is not in the database
     */
    private static void insert_person(String hinsurnum, String name, String phone, String postal_code,String city,String street_address,String gender,String birthday,String critical_category) throws SQLException {
        try {
            String insertSQL ="INSERT INTO RECEIVER ( hinsurnum, name, phone, postal_code, city, street_address, gender, birthday ) VALUES " +
                    "('"+hinsurnum+"'," +
                    "'"+name+"'," +
                    "'"+phone+"'," +
                    "'"+postal_code+"'," +
                    "'"+city+"'," +
                    "'"+street_address+"'," +
                    "'"+gender+"'," +
                    "'"+birthday+"')";
            //System.out.println(insertSQL);
            statement.executeUpdate(insertSQL);
            System.out.println("*INSERT table RECEIVER successfully");
            insertSQL="INSERT INTO RECEIVER_PRIORITY (hinsurnum,critical_category) VALUES " +
                    "('"+hinsurnum+"','"+ critical_category+"')";
            //System.out.println(insertSQL);
            statement.executeUpdate(insertSQL);
            System.out.println("*INSERT table RECEIVER_PRIORITY successfully");
        }catch (SQLException e)
        {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }
    }

        /*
        Main function to handle the function of adding a person option,
        Ask user for useful information and store them in a hashmap
        Check validation of user's input
        Call insert or update function if all information is complete and valid
        */
    private static void add_a_person() throws SQLException {
        Map<String, String> person = new HashMap<String, String>() {{
            put("hinsurnum", null);
            put("name", null);
            put("phone", null);
            put("postal_code", null);
            put("city", null);
            put("street_address", null);
            put("gender", null);
            put("birthday", null);
            put("critical_category", null);
        }};

        for (String person_key : person.keySet()) {
            if (person_key.equals("hinsurnum")) continue;
            Scanner person_option = new Scanner(System.in);  // Create a Scanner object
            System.out.println(
                    "| Please Enter "+person_key+":");
            switch (person_key) {
                case "phone" -> System.out.println("(format: 936-714-9838 )");
                case "postal_code" -> System.out.println("(format: H8W 3F2 )");
                case "gender" -> System.out.println("(format: male/female/other )");
                case "birthday" -> System.out.println("(format: YYYY-MM-DD )");
                case "critical_category" -> System.out.println("(format: HCW/ELD/IC/TEA/KID/PPTF/ESW/PPTS/NONE)");
            }
            String person_menu = person_option.nextLine();  // Read user input
            person.put(person_key, person_menu);
        }
            Scanner ask_hinsurnum = new Scanner(System.in);  // Create a Scanner object
            //check hinsurance
            System.out.println("Please enter the person's health insurance number");
            String ans_hinsurnum = ask_hinsurnum.nextLine();
            person.put("hinsurnum",ans_hinsurnum);

        if (VaccineApp.check_hinsurnum(ans_hinsurnum)){
            Scanner ask_update = new Scanner(System.in);  // Create a Scanner object
            System.out.println("This insurance number has already registered, would you like to update the person's information?" +
                    "\n Answer \"Y\" for YES or other key for NO");
            String res_ask_update = ask_update.nextLine();
            if (res_ask_update.equals("Y")){
                VaccineApp.update_person(person.get("hinsurnum"),person.get("name"),person.get("phone"),person.get("postal_code"),person.get("city"),person.get("street_address"),person.get("gender"),person.get("birthday"),person.get("critical_category"));
            }else{
                System.out.println("Back to Main Menu");
                return;
            }
        }else{
            VaccineApp.insert_person(person.get("hinsurnum"),person.get("name"),person.get("phone"),person.get("postal_code"),person.get("city"),person.get("street_address"),person.get("gender"),person.get("birthday"),person.get("critical_category"));
        }
        //System.out.println(person);
    }

    /*
    Check if the slot user asked to assign is exist in table SLOT
    return true if the slot exists
     */
    private static boolean check_slot(String place_name, String date, String slot_time) {
        boolean return_result=false;
        try {
            String querySQL = "SELECT COUNT(*) from SLOT t1 WHERE SLOT_TIME = '" + slot_time + "'AND PLACE_NAME = '"+place_name+"'AND DATE ='"+date+"' AND NOT EXISTS( SELECT 1 FROM ALLOCATE_SLOT t2 WHERE t1.SLOT_TIME=t2.SLOT_TIME AND t1.PLACE_NAME=t2.PLACE_NAME AND t1.DATE=t2.DATE)";
            //System.out.println(querySQL);
            java.sql.ResultSet rs = statement.executeQuery(querySQL);
            if (rs.next()) {
                if (rs.getInt(1) >= 1) {
                    return_result = true;
                }
            }
        }catch (SQLException e)
        {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE

            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }
        return return_result;
    }

    /*
    Check if the person with hunsurnum has enough shot before
    return true if the person take as many shot as required.
    */
    private static boolean check_enough(String hinsurnum) {
        int got_slot=0;
        int require_slot=0;
        try {
            String querySQL = "SELECT COUNT(*) FROM VACCINE_DOES WHERE HINSURNUM = '" + hinsurnum + "'";
            java.sql.ResultSet rs = statement.executeQuery(querySQL);
            if (rs.next()) {
                if (rs.getInt(1) == 0) {
                    return false;
                }
                got_slot=rs.getInt(1);
            }
            querySQL = "SELECT REQUIRED_DOES_QUANTITY FROM VACCINE_BATCH WHERE BATCH_NUMBER IN (SELECT BATCH_NUMBER FROM VACCINE_DOES WHERE HINSURNUM='"+hinsurnum+"')";
            rs = statement.executeQuery(querySQL);
            if (rs.next()) {
                require_slot=rs.getInt(1);
            }
            if (got_slot==require_slot){
                return true;
            }
        }catch (SQLException e)
        {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }
        return false;
    }

    /*
    Check if the slot the person is try to be assigned is in the past
    if the date of slot to be assigned is in the past, return true. Otherwise, return false.
    */
    private static boolean check_slot_exp(String slot_time, String date) {
        if (LocalDate.parse(date).isAfter(LocalDate.now())){
            return false;
        }else if(LocalDate.parse(date).isEqual(LocalDate.now())){
            return (LocalTime.parse(slot_time).isBefore(LocalTime.now()));
        }
        return true;
    }

    /*
    Insert slot into TABLE ALLOCATE_SLOT
    */
    private static void insert_slot(String place_name, String date, String slot_time, String hinsurnum, String allocation_date) {
        try {
            String insertSQL ="INSERT INTO ALLOCATE_SLOT ( place_name, date, slot_time, ALLCATION_DATE, hinsurnum ) VALUES " +
                    "('"+place_name+"'," +
                    "'"+date+"'," +
                    "'"+slot_time+"'," +
                    "'"+allocation_date+"'," +
                    "'"+hinsurnum+"')";
            //System.out.println(insertSQL);
            statement.executeUpdate(insertSQL);
            //System.out.println("DONE");
        }catch (SQLException e)
        {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE

            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }
    }

    /*
    Find all possible location that has free slot in the future
    */
    private static void find_location() {
        String now_date=LocalDate.now().toString();
        //String now_time=LocalTime.now().toString();
        System.out.println(
                "The future available locations from today are:\n" +
                "+------------------------------+");
        try {
            String querySQL = "SELECT PLACE_NAME from SLOT t1 WHERE DATE >= '"+now_date +"' AND NOT EXISTS( SELECT 1 FROM ALLOCATE_SLOT t2 WHERE t1.SLOT_TIME=t2.SLOT_TIME AND t1.PLACE_NAME=t2.PLACE_NAME AND t1.DATE=t2.DATE) GROUP BY PLACE_NAME ";
            //System.out.println(querySQL);
            java.sql.ResultSet rs = statement.executeQuery(querySQL);
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
        }catch (SQLException e)
        {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }
    }

    /*
    After knowing the location, print all possible date that this locatio has free slot in the future
    */
    private static void find_slot_date(String place_name) {
        String now_date=LocalDate.now().toString();
        System.out.println(
                "The available dates in "+place_name+" are:\n" +
                "+------------------------------+");
        try {
            String querySQL = "SELECT DATE from SLOT t1 WHERE DATE >= '"+now_date +"' AND PLACE_NAME= '"+place_name+"' AND NOT EXISTS( SELECT 1 FROM ALLOCATE_SLOT t2 WHERE t1.SLOT_TIME=t2.SLOT_TIME AND t1.PLACE_NAME=t2.PLACE_NAME AND t1.DATE=t2.DATE) GROUP BY DATE";
            //System.out.println(querySQL);
            java.sql.ResultSet rs = statement.executeQuery(querySQL);
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
        }catch (SQLException e)
        {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }
    }

    /*
    After knowing the location and date, print all the available time slot for this day at this location
    */
    private static void find_slot_time(String place_name, String date) {
        String now_date=LocalDate.now().toString();
        System.out.println(
                "The available time slot in "+place_name+" on "+date+" are:\n" +
                "+------------------------------+");
        try {
            String querySQL = "SELECT SLOT_TIME from SLOT t1 WHERE DATE = '"+date +"' AND PLACE_NAME= '"+place_name+"' AND NOT EXISTS( SELECT 1 FROM ALLOCATE_SLOT t2 WHERE t1.SLOT_TIME=t2.SLOT_TIME AND t1.PLACE_NAME=t2.PLACE_NAME AND t1.DATE=t2.DATE) ";
            //System.out.println(querySQL);
            java.sql.ResultSet rs = statement.executeQuery(querySQL);
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
        }catch (SQLException e)
        {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }
    }

    /*
    Main function to handle the functionalities of assign a slot option
    */
    private static void assign_a_slot() throws SQLException {
        Map<String, String> slot = new HashMap<String, String>() {
            {
                put("hinsurnum", null);
                put("place_name", null);
                put("date", null);
                put("slot_time", null);
            }};
        Scanner assign_slot = new Scanner(System.in);  // Create a Scanner object
        System.out.println("+------------------------------+\n" +
                "| You are assigning a slot      |\n" +
                "+------------------------------+\n" +
                "Please Enter Your Health Insurance Number:");
        String ans_slot = assign_slot.nextLine();  // Read user input
        slot.put("hinsurnum",ans_slot);
        if (!VaccineApp.check_hinsurnum(ans_slot)){
            Scanner err = new Scanner(System.in);  // Create a Scanner object
            System.out.println("+------------------------------+\n" +
                    "| The person is not registered, would you like to register now?\n" +
                    "+------------------------------+\n" +
                    "Please enter Y for yes or other key for no");
            String ans_err = err.nextLine();  // Read user input
            if (ans_err.equals("Y")){
                VaccineApp.add_a_person();
                System.out.println("Person added, continue to assign slot");
            }else{
                System.out.println("Return back to main menu");
                return;
            }
        }
        VaccineApp.find_location();
        assign_slot = new Scanner(System.in);  // Create a Scanner object
        System.out.println("+------------------------------+\n" +
                "| Please enter the preferred location      " +
                "");
        ans_slot = assign_slot.nextLine();  // Read user input
        slot.put("place_name",ans_slot);

        VaccineApp.find_slot_date(ans_slot);
        assign_slot = new Scanner(System.in);  // Create a Scanner object
        System.out.println("+------------------------------+\n" +
                "| Please enter the preferred date      " +
                "");
        ans_slot = assign_slot.nextLine();  // Read user input
        slot.put("date",ans_slot);
        VaccineApp.find_slot_time(slot.get("place_name"),ans_slot);

        assign_slot = new Scanner(System.in);  // Create a Scanner object
        System.out.println("+------------------------------+\n" +
                "| Please enter the preferred time      " +
                "");
        ans_slot = assign_slot.nextLine();  // Read user input
        slot.put("slot_time",ans_slot);

        if (VaccineApp.check_slot_exp(slot.get("slot_time"),slot.get("date"))){
            System.out.println(
                    "| The slot is in the past, return back to main menu\n" +
                    "********************************");
            return;
        }

        if (!VaccineApp.check_slot(slot.get("place_name"),slot.get("date"),slot.get("slot_time"))){
            System.out.println(
                    "| This slot is not available, return back to main menu\n" +
                    "********************************");
            return;
        }
        if (VaccineApp.check_enough(slot.get("hinsurnum"))){
            System.out.println(
                    "| This person got all the required shot, return back to main menu\n" +
                    "********************************");
            return;
        }
        VaccineApp.insert_slot(slot.get("place_name"),slot.get("date"),slot.get("slot_time"),slot.get("hinsurnum"),LocalDate.now().toString());
        System.out.println(
                "| Slot assigned!      |\n" +
                "Return back to the main menu\n" +
                "********************************");
    }



    /*
    CHECK if the lisence is in the table nurse.
    Not used
    */
    private static boolean check_lisence(String license_number) {
        try {
            String querySQL = "SELECT COUNT(*) from NURSE WHERE license_number = '" + license_number + "'";
            java.sql.ResultSet rs = statement.executeQuery(querySQL);
            if (rs.next()) {
                if (rs.getInt(1) >= 1) {
                    return true;
                }
            }
        }catch (SQLException e)
        {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE

            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }
        return false;
    }

    /*
    CHECK if the lisence is in the table nurse.
    */
    private static void add_a_vaccination(String batch_number, String vaccine_name, String vial_number, String license_number, String hinsurnum, String does_date, String does_time, String place_name) {
        try {
            String insertSQL ="INSERT INTO VACCINE_DOES ( batch_number, vaccine_name, vial_number, license_number, hinsurnum,does_date,does_time ) VALUES " +
                    "('"+batch_number+"'," +
                    "'"+vaccine_name+"'," +
                    "'"+vial_number+"'," +
                    "'"+license_number+"'," +
                    "'"+hinsurnum+"'," +
                    "'"+does_date+"'," +
                    "'"+does_time+"')";
            System.out.println("*INSERT TABLE VACCINE_DOES successfully");
            statement.executeUpdate(insertSQL);
            insertSQL ="INSERT INTO ASSIGN_DOSE_TO_SLOT (SLOT_TIME, PLACE_NAME, DATE, BATCH_NUMBER, VACCINE_NAME, VIAL_NUMBER) VALUES " +
                    "('"+does_time+"'," +
                    "'" +place_name+"',"+
                    "'"+does_date+"'," +
                    "'" +batch_number+"'," +
                    "'" +vaccine_name+"'," +
                    "'" +vial_number+"')";
            //System.out.println(insertSQL);
            statement.executeUpdate(insertSQL);
            System.out.println("*INSERT TABLE ASSIGN_DOSE_TO_SLOT successfully");
        }catch (SQLException e)
        {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE

            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }
    }


    /*
    Print all possible date for take vaccination
    */
    private static Boolean GetVaDate(String hinsurnum) {
        System.out.println("+------------------------------+\n"+
                "The booked date with " + hinsurnum + " are:");
        try {
            String querySQL = "SELECT DATE from ALLOCATE_SLOT WHERE HINSURNUM='" + hinsurnum + "'";
            //System.out.println(querySQL);
            java.sql.ResultSet rs = statement.executeQuery(querySQL);
            if (rs.next()){
                System.out.println(rs.getString(1));
            }else{
                return false;
            }
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
            return true;
        } catch (SQLException e) {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE

            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }
        return false;
    }

    /*
    Print all possible time of a date to take vaccination
    */
    private static boolean get_va_time(String hinsurnum, String does_date) {
        System.out.println("+------------------------------+\n"+
                "The booked time_slot with " + hinsurnum + " on "+does_date+" are:");
        try {
            String querySQL = "SELECT SLOT_TIME from ALLOCATE_SLOT WHERE HINSURNUM='"+hinsurnum+"' AND DATE='"+does_date+"'";
            //System.out.println(querySQL);
            java.sql.ResultSet rs = statement.executeQuery(querySQL);
            if (rs.next()){
                System.out.println(rs.getString(1));
            }else{
                return false;
            }
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
            return true;
        }catch (SQLException e)
        {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE

            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }
        return false;
    }

    /*
    Print all avalibale nurse lincense number that on that day in that location
     */
    private static ArrayList<String> get_nurse(String batch_number, String vaccine_name) {
        ArrayList<String> ava_nurse = new ArrayList<String>();
        System.out.println("+------------------------------+\n"+
                "The available nurses for this slot are");
        try {
            String querySQL = "SELECT LICENSE_NUMBER from NURSE_WORK_RECORD INNER JOIN VACCINE_BATCH ON NURSE_WORK_RECORD.DATE=VACCINE_BATCH.DATE AND NURSE_WORK_RECORD.PLACE_NAME=VACCINE_BATCH.PLACE_NAME WHERE BATCH_NUMBER = '"+batch_number+"' AND VACCINE_NAME='"+vaccine_name+"'";
            //System.out.println(querySQL);
            java.sql.ResultSet rs = statement.executeQuery(querySQL);
            while (rs.next()) {
                System.out.println(rs.getString(1));
                ava_nurse.add(rs.getString(1));
            }
        }catch (SQLException e)
        {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE

            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }
        return ava_nurse;
    }

        /*
    Check if the brand the person is try to take is same as he took before
    */
    private static Boolean get_name_check(String vaccine_name, String hinsurnum) {
        try {
            String querySQL = "SELECT vaccine_name from VACCINE_DOES WHERE HINSURNUM='"+hinsurnum+"'";
            //System.out.println(querySQL);
            java.sql.ResultSet rs = statement.executeQuery(querySQL);
            if (!rs.next()) {
                 return true;
            }
            return vaccine_name.equals(rs.getString(1));
        }catch (SQLException e)
        {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE

            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }
        return null;
    }

    /*
        Return all possible place name
        */
    private static boolean check_place_name(String hinsurnum, String does_date, String does_time) {
        System.out.println("+------------------------------+\n"+
                "The avaliable place to " + hinsurnum + " are:");
        try {
            String querySQL = "SELECT PLACE_NAME from ALLOCATE_SLOT WHERE HINSURNUM='"+hinsurnum+"' AND DATE='"+does_date+"' AND SLOT_TIME='"+does_time+"'";
            //System.out.println(querySQL);
            java.sql.ResultSet rs = statement.executeQuery(querySQL);
            if (rs.next()){
                System.out.println(rs.getString(1));
            }else{
                return false;
            }
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
            return true;
        }catch (SQLException e)
        {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE

            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }
        return false;
    }
    /*
            Return all possible batch number
            */
    private static boolean check_batch_number(String does_date, String place_name) {
        System.out.println("+------------------------------+\n"+
                "The possible batch number are:");
        try {
            String querySQL = "SELECT BATCH_NUMBER from VACCINE_BATCH WHERE DATE='"+does_date+"' AND PLACE_NAME='"+place_name+"' GROUP BY BATCH_NUMBER";
            //System.out.println(querySQL);
            java.sql.ResultSet rs = statement.executeQuery(querySQL);
            if (rs.next()){
                System.out.println(rs.getString(1));
            }else{
                return false;
            }
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
            return true;
        }catch (SQLException e)
        {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE

            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }
        return false;
    }

            /*
        Return all possible vaccine name
        */
    private static boolean check_vacc_name(String does_date, String place_name, String batch_number) {
        System.out.println("+------------------------------+\n"+
                "The possible vaccine are:");
        try {
            String querySQL = "SELECT VACCINE_NAME from VACCINE_BATCH WHERE DATE='"+does_date+"' AND PLACE_NAME='"+place_name+"' AND BATCH_NUMBER='"+batch_number+"'";
            //System.out.println(querySQL);
            java.sql.ResultSet rs = statement.executeQuery(querySQL);
            if (rs.next()){
                System.out.println(rs.getString(1));
            }else{
                return false;
            }
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
            return true;
        }catch (SQLException e)
        {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE

            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }
        return false;
    }

        /*
    Main method to handle functionalities of taking a vaccination
    */
    private static void vaccination() throws SQLException {
        Map<String, String> vaccination = new HashMap<String, String>() {{
            put("batch_number", null);
            put("vaccine_name", null);
            put("vial_number", null);
            put("nurse's license number", null);
            put("does_date", null);
            put("does_time", null);
            put("hinsurnum", null);
        }};
        Scanner assign_slot = new Scanner(System.in);  // Create a Scanner object
        System.out.println(
                "Please Enter Your Health Insurance Number:");
        String ans_slot = assign_slot.nextLine();  // Read user input
        vaccination.put("hinsurnum",ans_slot);
        if (!VaccineApp.check_hinsurnum(ans_slot)){
            System.out.println("+------------------------------+\n" +
                    "| The person is not registered, Return back to the main menu\n" +
                    "*************************************");
            return;
        }

        assign_slot = new Scanner(System.in);  // Create a Scanner object
        System.out.println("+------------------------------+\n" +
                "| Please enter the vial number      " +
                "");
        ans_slot = assign_slot.nextLine();  // Read user input
        vaccination.put("vial_number",ans_slot);

        if (VaccineApp.check_enough(vaccination.get("hinsurnum"))){
            System.out.println("+------------------------------+\n" +
                    "| This person had enough shot. Return back to main menu.\n" +
                    "*************************************");
            return;
        }

        if(!VaccineApp.GetVaDate(vaccination.get("hinsurnum"))){
            System.out.println("+------------------------------+\n" +
                    "| The Vaccine is not exist, Return back to the main menu\n" +
                    "*************************************");
            return;
        }
        assign_slot = new Scanner(System.in);  // Create a Scanner object
        System.out.println("" +
                "| Please enter the vaccination date      " +
                "");
        ans_slot = assign_slot.nextLine();  // Read user input
        vaccination.put("does_date",ans_slot);

        if(!VaccineApp.get_va_time(vaccination.get("hinsurnum"),vaccination.get("does_date"))){
            System.out.println("+------------------------------+\n" +
                    "| The time slot is not exist, Return back to the main menu\n" +
                    "*************************************");
            return;
        }
        assign_slot = new Scanner(System.in);  // Create a Scanner object
        System.out.println("" +
                "| Please enter the vaccination time slot      " +
                "");
        ans_slot = assign_slot.nextLine();  // Read user input
        vaccination.put("does_time",ans_slot);

        if(!VaccineApp.check_place_name(vaccination.get("hinsurnum"),vaccination.get("does_date"),vaccination.get("does_time"))){
            System.out.println("+------------------------------+\n" +
                    "| The time slot is not exist, Return back to the main menu\n" +
                    "*************************************");
            return;
        }
        assign_slot = new Scanner(System.in);  // Create a Scanner object
        System.out.println("" +
                "| Please enter the place_name      " +
                "");
        ans_slot = assign_slot.nextLine();  // Read user input
        vaccination.put("place_name",ans_slot);

        if(!VaccineApp.check_batch_number(vaccination.get("does_date"),vaccination.get("place_name"))){
            System.out.println("+------------------------------+\n" +
                    "| The batch is not exist, Return back to the main menu\n" +
                    "*************************************");
            return;
        }
        assign_slot = new Scanner(System.in);  // Create a Scanner object
        System.out.println("" +
                "| Please enter the batch number      " +
                "");
        ans_slot = assign_slot.nextLine();  // Read user input
        vaccination.put("batch_number",ans_slot);
        
        if(!VaccineApp.check_vacc_name(vaccination.get("does_date"),vaccination.get("place_name"),vaccination.get("batch_number"))){
            System.out.println("+------------------------------+\n" +
                    "| The vaccine is not exist, Return back to the main menu\n" +
                    "*************************************");
            return;
        }
        assign_slot = new Scanner(System.in);  // Create a Scanner object
        System.out.println("" +
                "| Please enter the vaccine_name      " +
                "");
        ans_slot = assign_slot.nextLine();  // Read user input
        vaccination.put("vaccine_name",ans_slot);

        Boolean va_name_valid=VaccineApp.get_name_check(vaccination.get("vaccine_name"),vaccination.get("hinsurnum"));
        if (va_name_valid==null) System.out.println("get_name _check error");
        if (!va_name_valid){
            System.out.println("+------------------------------+\n" +
                    "| Vaccine brand that is different from previously applied vaccine brand. Return back\n" +
                    "*************************************");
            return;
        }

        ArrayList<String> license_number= VaccineApp.get_nurse(vaccination.get("batch_number"),vaccination.get("vaccine_name"));
        assign_slot = new Scanner(System.in);  // Create a Scanner object
        System.out.println("" +
                "| Please enter the Nurse License Number      " +
                "");
        ans_slot = assign_slot.nextLine();  // Read user input
        if(!license_number.contains(ans_slot)){
            System.out.println("+------------------------------+\n" +
                    "| This nurse is unavaliable. \n" +
                    "+------------------------------+\n" +
                    "Return to the main menu");
            return;
        }
        vaccination.put("nurse's license number",ans_slot);
        VaccineApp.add_a_vaccination(vaccination.get("batch_number"),vaccination.get("vaccine_name"),vaccination.get("vial_number"),vaccination.get("nurse's license number"),vaccination.get("hinsurnum"),vaccination.get("does_date"),vaccination.get("does_time"),vaccination.get("place_name"));
        System.out.println("" +
                "| The Vaccination is recorded successfully! Return back. \n" +
                "*************************************");
    }




    public static void main ( String [ ] args ) throws SQLException{
        try { DriverManager.registerDriver ( new com.ibm.db2.jcc.DB2Driver() ) ; }
        catch (Exception cnfe){ System.out.println("Class not found"); }

        // This is the url you must use for DB2.
        //Note: This url may not valid now !
        String url = "jdbc:db2://winter2021-comp421.cs.mcgill.ca:50000/cs421";

        //REMEMBER to remove your user id and password before submitting your code!!
        String your_userid = "";
        String your_password = "";
        //AS AN ALTERNATIVE, you can just set your password in the shell environment in the Unix (as shown below) and read it from there.
        //$  export SOCSPASSWD=yoursocspasswd
        if(your_userid == null && (your_userid = System.getenv("SOCSUSER")) == null)
        {
            System.err.println("Error!! do not have a password to connect to the database!");
            System.exit(1);
        }
        if(your_password == null && (your_password = System.getenv("SOCSPASSWD")) == null)
        {
            System.err.println("Error!! do not have a password to connect to the database!");
            System.exit(1);
        }
        Connection con = DriverManager.getConnection (url,your_userid,your_password) ;
        Statement statement = con.createStatement ( ) ;
        VaccineApp.statement=statement;
        VaccineApp.con=con;
        System.out.println(" _    _                _           _  _        _____                           _       \n" +
                "| |  | |              | |         (_)( )      / ____|                         | |      \n" +
                "| |__| |  ___   _   _ | | __ __ _  _ |/ ___  | |      ___   _ __   ___   ___  | |  ___ \n" +
                "|  __  | / _ \\ | | | || |/ // _` || |  / __| | |     / _ \\ | '_ \\ / __| / _ \\ | | / _ \\\n" +
                "| |  | || (_) || |_| ||   <| (_| || |  \\__ \\ | |____| (_) || | | |\\__ \\| (_) || ||  __/\n" +
                "|_|  |_| \\___/  \\__,_||_|\\_\\\\__,_||_|  |___/  \\_____|\\___/ |_| |_||___/ \\___/ |_| \\___|\n");
        while (true){
            Scanner menu_number = new Scanner(System.in);  // Create a Scanner object
            System.out.println(
                    "+------------------------------------------+\n" +
                    "| Welcome to Houkai's VaccineApp Main Menu |\n" +
                    "+==========================================+\n" +
                    "|     1. Add a Person                      |\n" +
                    "|     2. Assign a slot to a Person         |\n" +
                    "|     3. Enter Vaccination information     |\n" +
                    "|     4. Exit Application                  |\n" +
                    "+------------------------------------------+\n" +
                    "|     Please Enter Your Option:            |\n" +
                    "+------------------------------------------+");
            String main_menu = menu_number.nextLine();  // Read user input
            switch(main_menu){
                case "1":
                    VaccineApp.add_a_person();
                    break;
                case "2":
                    VaccineApp.assign_a_slot();
                    break;
                case "3":
                    VaccineApp.vaccination();
                    break;
                case "4":
                    System.out.println("ByeBye!");
                    statement.close ( ) ;
                    con.close ( ) ;
                    return;
                default:
                    System.out.println("Please enter a valid option!");
            }
        }
    }
}
