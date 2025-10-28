import java.io.File; //Used for working with files and directories.
import java.io.FileNotFoundException; //Handles missing files errors.
import java.util.ArrayList; //Used to create lists.
import java.util.List; //Interface implemented by ArrayList.
import java.util.Scanner; //Used to read user input.
import java.security.MessageDigest; //Used to generate MD5 hash for files.
import java.nio.file.*; //Used for moving, copying, and deleting files.

/* 
 * This class is a basic antivirus scanner. It can read known malware
 * file hashes from "signatures.txt", scan a folder, compare file hashes
 * to known signatures, and quarantine any infected files.
 *
 * @Author Joncarlo Ferrante 
 */
public class AntiVirusScanner {

    public static void main(String[] args) {
        // Load malware signatures
        List<String> signatures = new ArrayList<>();

        try {
            File file = new File("signatures.txt");
            Scanner reader = new Scanner(file);

            while (reader.hasNextLine()) {
                String hash = reader.nextLine().trim();
                signatures.add(hash);
            }
            reader.close();

            System.out.println("Loaded " + signatures.size() + " malware signatures.");
            System.out.println("Signatures: " + signatures);

        } catch (FileNotFoundException e) {
            System.out.println("Error: signatures.txt not found.");
        }

        Scanner input = new Scanner(System.in);
        while (true) {
            System.out.println("\n----Antivirus Menu----");
            System.out.println("1. Scan a folder");
            System.out.println("2. View scan log");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            String choice = input.nextLine();

            if (choice.equals("1")) {
                System.out.println("Enter a folder path to scan: ");
                String folderpath = input.nextLine();
                scanFolder(folderpath, signatures);
            } 
            else if (choice.equals("2")) {
                viewScanLog();
            } 
            else if (choice.equals("3")) {
                System.out.println("Exiting Antivirus.");
                System.out.println();
                System.out.println("Project made by Joncarlo Ferrante.");
                break;
            } 
            else {
                System.out.println("Invalid option. Try again.");
            }
        }
        input.close();
    }

    public static void scanFolder(String folderpath, List<String> signatures) {
        File folder = new File(folderpath);

        if (folder.isDirectory()) {
            File[] files = folder.listFiles();

            File quarantineFolder = new File("Quarantine");
            if (!quarantineFolder.exists()) {
                quarantineFolder.mkdir();
                System.out.println("Created Quarantine Folder.");
            }

            System.out.println("Scanning Files...");
            for (File fileToScan : files) {
                if (fileToScan.isFile()) {
                    String fileHash = getFileHash(fileToScan);

                    if (fileHash == null) {
                        System.out.println(fileToScan.getName() + " [Error: Could not read]");
                    } else if (signatures.contains(fileHash)) {
                        System.out.println(fileToScan.getName() + " [INFECTED]");
                        try {
                            Path sourcePath = fileToScan.toPath();
                            Path targetPath = quarantineFolder.toPath().resolve(fileToScan.getName());
                            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("Moved to Quarantine: " + fileToScan.getName());
                        } catch (Exception e) {
                            System.out.println("Could not move " + fileToScan.getName() + " to Quarantine.");
                        }
                    } else {
                        System.out.println(fileToScan.getName() + " [CLEAN]");
                    }
                }
            }

            try {
                java.io.FileWriter writer = new java.io.FileWriter("scan_log.txt", true);
                writer.write("Scan Report:\n");
                writer.write("Scanned folder: " + folderpath + "\n");
                writer.write("Total signatures loaded: " + signatures.size() + "\n");
                writer.write("Scan date: " + java.time.LocalDateTime.now() + "\n\n");

                for (File fileToScan : files) {
                    if (fileToScan.isFile()) {
                        String fileHash = getFileHash(fileToScan);
                        String result = "[CLEAN]";
                        if (fileHash == null) result = "[ERROR]";
                        else if (signatures.contains(fileHash)) result = "[INFECTED]";
                        writer.write(fileToScan.getName() + " " + result + "\n");
                    }
                }

                writer.write("\n------------------------------------------\n");
                writer.close();
                System.out.println("Scan results saved to scan_log.txt");

            } catch (Exception e) {
                System.out.println("Error writing scan log: " + e.getMessage());
            }

        } else {
            System.out.println("That path is not a folder.");
        }
    }
    
    public static void viewScanLog() {
        try {
            File logFile = new File("scan_log.txt");
            if (!logFile.exists()) {
                System.out.println("No scan log found yet.");
                return;
            }

            Scanner logReader = new Scanner(logFile);
            System.out.println("\n--- Scan Log ---");
            while (logReader.hasNextLine()) {
                System.out.println(logReader.nextLine());
            }
            logReader.close();
            System.out.println("--------------------------\n");

        } catch (Exception e) {
            System.out.println("Error reading scan log: " + e.getMessage());
        }
    }

    public static String getFileHash(File file) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            java.io.InputStream is = new java.io.FileInputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = is.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
            is.close();

            StringBuilder sb = new StringBuilder();
            for (byte b : md.digest()) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}