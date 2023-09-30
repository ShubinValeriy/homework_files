import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class Game {
    private final String installDirPath;

    public Game(String installDirPath) {
        this.installDirPath = installDirPath;
    }

    // Задача номер 1 Установка
    public void install() {
        // В папке Games создайте несколько директорий: src, res, savegames, temp.
        File res = makeDir(installDirPath, "res");
        File src = makeDir(installDirPath, "src");
        File savegames = makeDir(installDirPath, "savegames");
        File temp = makeDir(installDirPath, "temp");
        // В каталоге src создайте две директории: main, test.
        File main = makeDir(src.getPath(), "main");
        File test = makeDir(src.getPath(), "test");
        // В подкаталоге main создайте два файла: Main.java, Utils.java
        File mainJava = makeFile(main.getPath(), "Main.java");
        File utilsJava = makeFile(main.getPath(), "Utils.java");
        // В каталог res создайте три директории: drawables, vectors, icons
        File drawables = makeDir(res.getPath(), "drawables");
        File vectors = makeDir(res.getPath(), "vectors");
        File icons = makeDir(res.getPath(), "icons");
        //В директории temp создайте файл temp.txt
        File tempTxt = makeFile(temp.getPath(), "temp.txt");
        // Запись лога в файл temp.txt
        writeLog(tempTxt);
    }


    private File makeDir(String dirPath, String nameDir) {
        String newDirPath = dirPath + "/" + nameDir;
        File newDir = new File(newDirPath);
        if (newDir.mkdir())
            Main.logBuilder.append(makeLog(newDir, " is create"));
        else
            Main.logBuilder.append(makeLog(newDir, " is not create"));
        return newDir;
    }

    private File makeFile(String dirPath, String nameFile) {
        File newFile = new File(dirPath, nameFile);
        try {
            if (newFile.createNewFile())
                Main.logBuilder.append(makeLog(newFile, " is create"));
            else
                Main.logBuilder.append(makeLog(newFile, " is not create"));
        } catch (IOException ex) {
            Main.logBuilder.append(makeLog(newFile, " is not create because " + ex.getMessage()));
        }
        return newFile;
    }

    private String makeLog(File file, String msg) {
        Date date = new Date();
        String typeFile = file.isDirectory() ? " directory " : " file ";
        return date + " - " + typeFile + "\"" + file.getName() + "\" " + msg + " in directory " +
                file.getPath() + "\n";
    }

    private void writeLog(File file) {
        try (FileWriter writer = new FileWriter(file, false)) {
            writer.write(Main.logBuilder.toString());
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }


    // Задача номер 2 Сохранение

    public void save(int countSave) {
        // Создать int countSave экземпляра класса GameProgress.
        GameProgress[] gameProgresses = new GameProgress[countSave];
        for (int i = 0; i < countSave; i++) {
            int health = (int) (Math.random() * 100);
            int weapons = (int) (Math.random() * 200);
            int lvl = (int) (Math.random() * 15);
            double distance = Math.random() * 1000;
            gameProgresses[i] = new GameProgress(health, weapons, lvl, distance);
        }

        String saveDir = installDirPath + "/savegames";

        //Сохранить сериализованные объекты GameProgress в папку savegames из предыдущей задачи
        //используя реализованный метод saveGame()
        ArrayList<String> savePath = new ArrayList();
        for (int i = 0; i < countSave; i++) {
            savePath.add(saveDir + "/save" + (i + 1) + ".dat");
            saveGame(savePath.get(i), gameProgresses[i]);
        }

        // Запаковывание файлов сохранения используя реализованный метод zipFiles()
        String zipFilePath = saveDir + "/zip.zip";
        zipFiles(zipFilePath, savePath);
    }

    // Реализуйте метод saveGame()
    private void saveGame(String savePath, GameProgress gameProgress) {
        try (FileOutputStream fos = new FileOutputStream(savePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(gameProgress);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    //Реализуйте метод zipFiles()
    private void zipFiles(String zipPath, ArrayList<String> saveFilesPath) {
        try (ZipOutputStream zout = new ZipOutputStream(new
                FileOutputStream(zipPath))) {
            for (String saveFilePath : saveFilesPath) {
                File saveFile = new File(saveFilePath);
                try (FileInputStream fis = new FileInputStream(saveFile)) {
                    ZipEntry zipEntry = new ZipEntry(saveFile.getName());
                    zout.putNextEntry(zipEntry);
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    zout.write(buffer);
                    zout.closeEntry();
                } catch (Exception exception) {
                    System.out.println(exception.getMessage());
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    //Очистка папки от всех файлов кроме .zip и каталогов
    public void cleanDir(String dirPath) {
        File dir = new File(dirPath);
        for (File file : dir.listFiles()) {
            if (!file.isDirectory()) {
                if (!file.getName().contains(".zip")) {
                    file.delete();
                }
            }
        }
    }

    // Задача номер 3 Загрузка
    public void loading() {
        String saveDir = installDirPath + "/savegames";
        String zipPath = saveDir + "/zip.zip";
        // распаковка zip архива
        openZip(zipPath, saveDir);
        // вывод в консоль всех распакованных файлов сохранений
        for (File file : new File(saveDir).listFiles()) {
            if (!file.isDirectory()) {
                if (!file.getName().contains(".zip")) {
                    System.out.println(openProgress(file.getPath()));
                }
            }
        }
    }

    // Реализуйте метод openZip()
    private void openZip(String zipPath, String dirPathForUnpack) {
        try (ZipInputStream zin = new ZipInputStream(new FileInputStream(zipPath))) {
            ZipEntry zipEntry;
            String fileName;
            while ((zipEntry = zin.getNextEntry()) != null) {
                fileName = zipEntry.getName();
                String filePath = dirPathForUnpack + "/" + fileName;
                FileOutputStream fout = new FileOutputStream(filePath);
                for (int i = zin.read(); i != -1; i = zin.read()) {
                    fout.write(i);
                }
                fout.flush();
                zin.closeEntry();
                fout.close();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    // Реализуйте метод openProgress()
    private GameProgress openProgress(String filePath) {
        GameProgress gameProgress = null;

        try (FileInputStream fis = new FileInputStream(filePath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            gameProgress = (GameProgress) ois.readObject();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return gameProgress;
    }


}

