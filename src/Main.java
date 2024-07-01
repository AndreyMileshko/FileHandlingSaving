import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class Main {
    private static final File saveGamesDir = new File("C:\\Users\\andre\\IdeaProjects\\FileHandlingSaving\\games\\savegames");

    public static void main(String[] args) {
        GameProgress progress = new GameProgress(100, 12, 80, 78.3);
        GameProgress progress1 = new GameProgress(31, 6, 3, 12.48);
        GameProgress progress2 = new GameProgress(58, 32, 160, 99.9);

        saveGame(saveGamesDir, "save.dat", progress);
        saveGame(saveGamesDir, "save1.dat", progress1);
        saveGame(saveGamesDir, "save2.dat", progress2);
        delimiter();

        zipFiles(saveGamesDir, "zipSaveFiles.zip", saveGamesDir.listFiles());
        delimiter();

        openZip(saveGamesDir, "zipSaveFiles.zip");
        delimiter();

        System.out.println(openProgress(saveGamesDir, "save1.dat"));
        delimiter();
    }

    public static void saveGame(File path, String nameFile, GameProgress progress) {
        File saveFile = new File(path, nameFile);
        try {
            if (saveFile.createNewFile()) System.out.printf("Файл %s создан.\n", saveFile.getName());
        } catch (IOException exc) {
            System.out.printf("Файл %s не создан.\n", saveFile.getName());
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile))) {
            oos.writeObject(progress);
            System.out.printf("Запись файла %s успешна.\n", saveFile.getName());
        } catch (IOException exc) {
            System.out.printf("Запись файла %s не успешна.\n", saveFile.getName());
        }
    }

    public static void zipFiles(File path, String nameZipFile, File[] saveFiles) {
        File zipSaveFile = new File(path, nameZipFile);
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipSaveFile))) {
            for (File saveFile : saveFiles) {
                try (FileInputStream fis = new FileInputStream(saveFile)) {
                    ZipEntry zipEntry = new ZipEntry(saveFile.getName());
                    zos.putNextEntry(zipEntry);
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    zos.write(buffer);
                    zos.closeEntry();
                    System.out.printf("Файл %s записан в архив.\n", saveFile.getName());
                } catch (IOException exc) {
                    System.out.printf("Файл %s не записан в архив.\n", saveFile.getName());
                }
            }
            System.out.printf("Архив %s создан.\n", nameZipFile);
        } catch (IOException exc) {
            System.out.printf("Архив %s не создан.\n", nameZipFile);
        }

        for (File file : saveFiles) {
            if (file.delete()) System.out.printf("%s удалён.\n", file.getName());
        }
    }

    public static void openZip(File unpackingDirectory, String pathZipFile) {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(new File(unpackingDirectory, pathZipFile)))) {
            ZipEntry zipEntry;
            File newSaveFile = null;
            while ((zipEntry = zis.getNextEntry()) != null) {
                newSaveFile = new File(unpackingDirectory, zipEntry.getName());
                FileOutputStream fos = new FileOutputStream(newSaveFile);
                for (int i = zis.read(); i != -1; i = zis.read()) {
                    fos.write(i);
                }
                fos.flush();
                zis.closeEntry();
                fos.close();
            }
            newSaveFile.createNewFile();
            System.out.println("Распаковка удалась.");

        } catch (IOException exc) {
            System.out.println("Распаковка не удалась.");
        }
    }

    public static GameProgress openProgress(File dir, String nameFile) {
        File progressFile = new File(dir, nameFile);
        GameProgress result = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(progressFile))) {
            result = (GameProgress) ois.readObject();
            System.out.println("Считывание сохраненного объекта произошло:");
        }catch (IOException | ClassNotFoundException exc) {
            System.out.println("Считывание сохраненного объекта не произошло.");
        }
        return result;
    }

    public static void delimiter() {
        System.out.println("================================================================");
    }
}