

public class Main {
    public static StringBuilder logBuilder = new StringBuilder();

    public static void main(String[] args) {

        // Директория папки для "Игры"
        String installDirPath = "/Users/shubinvaleriy/Desktop/netology/Games";
        Game game = new Game(installDirPath);
        // Задание 1
        game.install();
        // Задание 2
        game.save(3);
        // Задание 3
        game.loading();
    }
}
