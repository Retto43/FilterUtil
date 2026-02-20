import java.util.*;
import java.io.*;
import org.apache.commons.cli.*;

public class Program {



    public static class IntStatistics{

        long count= 0;
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        long sum = 0;
        double average = 0;

        void update(Long value){
            count++;
            min = Math.min(min, value);
            max = Math.max(max, value);
            sum += value;
            average = count > 0 ? (double) sum / count : 0;
        }

        void printShort() {
            System.out.printf("Целые числа: %d элементов%n", count);
        }

        void printFull() {
            if (count > 0) {
                System.out.printf("Целые числа: %d элементов, мин: %d, макс: %d, сумма: %d, среднее: %.2f%n",
                        count, min, max, sum, average);
            } else {
                System.out.println("Целые числа: 0 элементов");
            }
        }
    }



    public static class FloatStatistics {
        int count = 0;
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        double sum = 0;
        double average = 0;

        void update(float value) {
            count++;
            min = Math.min(min, value);
            max = Math.max(max, value);
            sum += value;
            average = count > 0 ? sum / count : 0;
        }

        void printShort() {
            System.out.printf("Вещественные числа: %d элементов%n", count);
        }

        void printFull() {
            if (count > 0) {
                System.out.printf("Вещественные числа: %d элементов, мин: %.3f, макс: %.3f, сумма: %.3f, среднее: %.3f%n",
                        count, min, max, sum, average);
            } else {
                System.out.println("Вещественные числа: 0 элементов");
            }
        }
    }

public static class StringStatistics {
    int count = 0;
    int minLength = Integer.MAX_VALUE;
    int maxLength = 0;

    void update(String value) {
        count++;
        int length = value.length();
        minLength = Math.min(minLength, length);
        maxLength = Math.max(maxLength, length);
    }

    void printShort() {
        System.out.printf("Строки: %d элементов%n", count);
    }

    void printFull() {
        if (count > 0) {
            System.out.printf("Строки: %d элементов, мин. длина: %d, макс. длина: %d%n",
                    count, minLength, maxLength);
        } else {
            System.out.println("Строки: 0 элементов");
        }
    }
}

    IntStatistics intStats = new IntStatistics();
    FloatStatistics floatStats = new FloatStatistics();
    StringStatistics stringStats = new StringStatistics();

    public static class Flags {
       private boolean append = false;
       private boolean shortStatistic = false;
       private boolean fullStatistic = false;
       private String output;
       private String prefix;
       private CommandLine cmd = null;

        public Flags(String[] args) throws ParseException

        {   Options options = new Options();

            options.addOption("a", "append",false, null);
            options.addOption("o", "output", true, null);
            options.addOption("p",  "prefix",true, null);
            options.addOption("s", "short",false, null);
            options.addOption("f", "full",false, null);

            CommandLineParser parser = new DefaultParser();

            try {
                cmd = parser.parse(options, args);
            } catch (ParseException pe) {
                pe.printStackTrace();
            }



            append = cmd.hasOption("a");
            shortStatistic = cmd.hasOption("s");
            fullStatistic = cmd.hasOption("f");

            prefix = cmd.getOptionValue("p");
            output = cmd.getOptionValue("o");



        }

        public String[] getFiles() {
            return cmd.getArgs();
        }

        public  boolean isAppend(){

            return append;
        }

        public  boolean isShort(){

            return shortStatistic;
        }

        public  boolean isFull(){

            return fullStatistic;
        }

        public String output(){

            return  output;
        }

        public String prefix(){

            return prefix;
        }
    }


    public boolean ParseInt(String word){

        try{
            Long.parseLong(word);

            return true;
        } catch (NumberFormatException e){

            return false;
        }

    }

    public  boolean ParseFloat(String word){

        try {
            Float.parseFloat(word);

            return true;
        }   catch (NumberFormatException e){

            return false;
        }
    }


    public void WriteFile(BufferedWriter writer, String word) throws IOException {

            writer.write(word);
            writer.newLine();

    }
    public void closeWriters(BufferedWriter... writers) {
        for (BufferedWriter writer : writers) {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.out.println("Ошибка закрытия файла: " + e.getMessage());
                }
            }
        }
    }



public void Filter(String[] files, Flags flags) {
    String prefix = flags.prefix() != null ? flags.prefix() : "";
    String output = flags.output() != null ? flags.output() : "";
    boolean append = flags.isAppend();

    BufferedWriter intWriter = null;
    BufferedWriter doubleWriter = null;
    BufferedWriter stringWriter = null;

    try {
        if (!output.isEmpty()) {
            File dir = new File(output);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    System.out.println("Не удалось создать директорию: " + output);
                    return;
                }
            }
        }

        String basePath = output.isEmpty() ? "" :
                output.endsWith(File.separator) ? output : output + File.separator;

        for (String file : files) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String word;

                while ((word = br.readLine()) != null) {
                    if (ParseInt(word)) {
                        long intValue = Long.parseLong(word);
                        intStats.update(intValue);

                        if (intWriter == null) {
                            intWriter = new BufferedWriter(
                                    new FileWriter(basePath + prefix + "integers.txt", append)
                            );
                        }
                        WriteFile(intWriter, word);

                    } else if (ParseFloat(word)) {
                        float floatValue = Float.parseFloat(word);
                        floatStats.update(floatValue);

                        if (doubleWriter == null) {
                            doubleWriter = new BufferedWriter(
                                    new FileWriter(basePath + prefix + "floats.txt", append)
                            );
                        }
                        WriteFile(doubleWriter, word);

                    } else {
                        stringStats.update(word);

                        if (stringWriter == null) {
                            stringWriter = new BufferedWriter(
                                    new FileWriter(basePath + prefix + "strings.txt", append)
                            );
                        }
                        WriteFile(stringWriter, word);
                    }
                }
            } catch (IOException ex) {
                System.out.println("Ошибка чтения файла " + file + ": " + ex.getMessage());
            }
        }
    } finally {
        closeWriters(intWriter, doubleWriter, stringWriter);
    }
}
    public void printStatistic(Flags flags) {
        if (flags.isFull()) {
            System.out.println("\n=== Полная статистика ===");
            intStats.printFull();
            floatStats.printFull();
            stringStats.printFull();
        } else if (flags.isShort()) {
            System.out.println("\n=== Краткая статистика ===");
            intStats.printShort();
            floatStats.printShort();
            stringStats.printShort();
        }
    }


   public static void main(String[] args) {
        Program program = new Program();
        Flags flags;

        try {

           flags = new Flags(args);
       } catch (ParseException e) {
           System.out.println("Ошибка, такого флага нет: " + e.getMessage());
           return;
       }

        String[] files = flags.getFiles();

        if (files.length == 0 ){
            System.out.println("Не указаны входные файлы");
            return;
        }
       program.Filter(files, flags);
       program.printStatistic(flags);


    }


}