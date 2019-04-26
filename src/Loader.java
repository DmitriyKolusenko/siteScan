import downloader.Data;
import downloader.State;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

/**
 * Created by HerrSergio on 31.07.2016.
 */
public class Loader implements Runnable, ActionListener {

    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);

        String url = scanner.nextLine().trim();

        Data data = new Data(Runtime.getRuntime().availableProcessors() * 2, url);

        SwingUtilities.invokeLater(new Loader(data));

        while (true) {
            String command = scanner.nextLine().trim();
            switch (command) {
                case "pause":
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            data.setPaused(true);
                        }
                    });
                    System.out.println("Paused!");
                    break;
                case "resume":
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            data.setPaused(false);
                        }
                    });
                    System.out.println("Resumed!");
                    break;
                case "stop":
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            data.close();
                            System.out.println("Stopped!");
                            System.exit(0);
                        }
                    });
                    return;
                default:
                    System.out.println("Unknown command!");
            }
        }

    }

    private Timer timer;
    private Data data;

    public Loader(Data data) {
        this.data = data;
    }

    @Override
    public void run() {
        timer = new Timer(15000, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        State state = data.getState();
        System.out.println("-------------------------");
        System.out.println("Найдено ссылок: " + state.getLinksDetectedCount());
        System.out.println("Ссылок обрабатывается: " + state.getLinksInProcessCount());
        System.out.println("Ссылок обработано: " + (state.getLinksDetectedCount() - state.getLinksInProcessCount()));
        System.out.println("Остановлено: " + (state.isPaused() ? "да" : "нет"));
        System.out.println("Времени в работе: " + state.getTimeElapsed()/1000.0 + " сек." );
        System.out.println("*************************");
        if(state.getLinksInProcessCount() == 0) {
            System.out.println("ЗАВЕРШЕНО!!!");
            data.close();
            System.exit(0);
        }
    }
}
