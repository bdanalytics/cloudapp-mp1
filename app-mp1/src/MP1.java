import java.io.*;
//import java.io.File;
//import java.io.FileWriter;
import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class MP1 {
    Random generator;
    String userName;
    String inputFileName;
    String delimiters = " \t,;.?!-:@[](){}_*/";
    String[] stopWordsArray = {"i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours",
            "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its",
            "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that",
            "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having",
            "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while",
            "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before",
            "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again",
            "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each",
            "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than",
            "too", "very", "s", "t", "can", "will", "just", "don", "should", "now"};
    List<String> stopWordsList = new ArrayList<String>(Arrays.asList(stopWordsArray));

    void initialRandomGenerator(String seed) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA");
        messageDigest.update(seed.toLowerCase().trim().getBytes());
        byte[] seedMD5 = messageDigest.digest();

        long longSeed = 0;
        for (int i = 0; i < seedMD5.length; i++) {
            longSeed += ((long) seedMD5[i] & 0xffL) << (8 * i);
        }

        this.generator = new Random(longSeed);
    }

    Integer[] getIndexes() throws NoSuchAlgorithmException {
        Integer n = 10000;
        Integer number_of_lines = 50000;
        Integer[] ret = new Integer[n];
        this.initialRandomGenerator(this.userName);
        for (int i = 0; i < n; i++) {
            ret[i] = generator.nextInt(number_of_lines);
        }
        return ret;
    }
    
    public MP1(String userName, String inputFileName) {
        this.userName = userName;
        this.inputFileName = inputFileName;
    }

    public String[] process() throws Exception {
        String[] ret = new String[20];

        // TODO
        
        StringTokenizer tokenizer;
        int nTokens;
        
        // Check if file can be opened
        try {
            new File(this.inputFileName);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
         
        // Read file, process lines & collect tokens
        File inpFile = new File(this.inputFileName);
        Scanner inpf = new Scanner(inpFile);            
        System.out.println("Reading from " + this.inputFileName + "...");
            
        int nReadLines = 0; int nProcLines = 0; int freqLine = 0;
        List<Integer> procLinesList = new ArrayList<Integer>(Arrays.asList(getIndexes()));
        List<String> tokens = new ArrayList<String>();        
        
        while (inpf.hasNextLine()) {
            nReadLines++;
            if ((nReadLines % 10_000) == 0) {
                System.out.println("Read lines:" + String.format("%,d", nReadLines) + 
                        "; Proc lines:" + String.format("%,d", nProcLines) + 
                        "; Collected tokens:" + String.format("%,d", tokens.size()));                
            }    

            String thisLine = inpf.nextLine();
            //System.out.println("Processing nReadLines:" + Integer.toString(nReadLines));
            
            freqLine = Collections.frequency(procLinesList, nReadLines - 1);
//            if (freqLine > 1)
//                System.out.println("lineNum:" + Integer.toString(nReadLines) + 
//                                    " freq:" + Integer.toString(freqLine));

//if (freqLine  > 0) freqLine = 1;
            for (int ixProc = 0; ixProc < freqLine; ixProc++) {
                nProcLines++;
    //            if (nReadLines == 4)
    //                System.out.println("Processing line:" + thisLine);                
                tokenizer = new StringTokenizer(thisLine, delimiters);
                nTokens = tokenizer.countTokens();    
                for (int ixToken = 0; ixToken < nTokens; ixToken++) {
                    String token = tokenizer.nextToken()
                                        .toLowerCase()
                                        .trim();
    //                if (nReadLines == 4)
    //                    System.out.println("Checking presence in stopWordsList of token:" + token);

                    if (stopWordsList.indexOf(token) < 0)
                        tokens.add(token); 
                }    
            }
        }

        System.out.println("Total: Read lines:" + String.format("%,d", nReadLines) + 
                "; Proc lines:" + String.format("%,d", nProcLines) + 
                "; Coll tokens:" + String.format("%,d", tokens.size()));
        inpf.close();

        // Count frequencies
        Map<String, Integer> mapTokens = new HashMap<String, Integer>();

        for (String token : tokens) {
            Integer freq = mapTokens.get(token);
            mapTokens.put(token, (freq == null) ? 1 : freq + 1);
        }

        System.out.println("Mapped distinct tokens:" + String.format("%,d", mapTokens.size()));
        System.out.println(mapTokens);
        
        // Sort by descending freq
        SortedSet<Map.Entry<String, Integer>> srtTokens = 
                new TreeSet<Map.Entry<String, Integer>>(
                new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
                int bValue = e2.getValue().compareTo(e1.getValue());
                return bValue != 0 ? bValue : e1.getKey().compareTo(e2.getKey());
        }
        });
        
        srtTokens.addAll(mapTokens.entrySet());

        System.out.println("Descending Sorted distinct tokens:" + 
                String.format("%,d", srtTokens.size()));
        System.out.println(srtTokens);        
        
        int ixToken = 0;
        for (Map.Entry<String, Integer> entry : srtTokens) {
            if (ixToken >= 20)
                break;
            
            ret[ixToken++] = entry.getKey();
        }    

        // Output top tokens into file
        String otputFileName = "./output_user_" + this.userName + ".txt";
        try {
            new FileWriter(otputFileName);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
        
        BufferedWriter otp = new BufferedWriter(new FileWriter(otputFileName));
        System.out.println("Writing top tokens to " + otputFileName + "...");
        for (ixToken = 0; ixToken < 20; ixToken++)
            otp.write(ret[ixToken] + "\n");
        
        otp.close();
        System.out.println("Wrote tokens total: " + Integer.toString(ixToken));

        return ret;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1){
            System.out.println("MP1 <User ID>");
        }
        else {
            String userName = args[0];
            String inputFileName = "./input.txt";
            MP1 mp = new MP1(userName, inputFileName);
            String[] topItems = mp.process();
            for (String item: topItems){
                System.out.println(item);
            }
        }
    }
}
