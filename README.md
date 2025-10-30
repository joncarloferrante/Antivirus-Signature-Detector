# Signature Antivirus Scanner
@Auther Joncarlo Ferrante
An antivirus program made with Java that uses MD5 signature matching to detect malware. This project demonstrates
how antivirus software performs signature-based detection, an essential technique used in threat analysis.

# Key Features
- Loads known malware signatures from `signatures.txt`
- Scans any selected folder for files
- Generates and compares MD5 hashes of files to known malware signatures
- Moves infected files to a safe `Quarantine` folder
- Logs every scan to `scan_log.txt` with timestamps
- Includes an interactive console menu (Scan, View Log, Exit)

# Run the code
- Open in a java IDE
-  Ensure the following files exist in your project:
   - `signatures.txt`
   - `scan_log.txt` (created after first scan)
-   When prompted, enter a folder path to scan
-   Review `scan_log.txt` and the `Quarantine` folder
