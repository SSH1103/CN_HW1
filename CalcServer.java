import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

public class CalcServer {

    // 계산을 수행하는 메서드
    public static String calc(String exp) {
        StringTokenizer st = new StringTokenizer(exp, " ");
        if (st.countTokens() != 3)
            return "Too many arguments";
        String res = "";
        int op1 = Integer.parseInt(st.nextToken());
        String opcode = st.nextToken();
        int op2 = Integer.parseInt(st.nextToken());
        switch (opcode) {
            case "+":
                res = Integer.toString(op1 + op2);
                break;
            case "-":
                res = Integer.toString(op1 - op2);
                break;
            case "*":
                res = Integer.toString(op1 * op2);
                break;
            case "/":
                if (op2 == 0) {
                    res = "divided by zero";
                    break;
                }
                res = Integer.toString(op1 / op2);
                break;
            default:
                res = "wrong operator";
        }
        return res;
    }

    // 클라이언트 처리를 담당하는 내부 클래스
    static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        // 생성자
        ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        // 스레드에서 실행될 로직
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {

                while (true) {
                    // 클라이언트로부터 메세지 수신
                    String inputMessage = in.readLine();

                    // 클라이언트 종료 확인
                    if (inputMessage.equalsIgnoreCase("bye")) {
                        System.out.println("Client disconnected");
                        break;
                    }

                    // 서버 콘솔에 수신된 메세지 출력
                    System.out.println(inputMessage);

                    // 계산 메서드 호출
                    String res = calc(inputMessage);

                    // 클라이언트에게 결과 전송
                    out.write(res + "\n");
                    out.flush();
                }
            } catch (IOException e) {
                System.out.println("Error handling client: " + e.getMessage());
            } finally {
                try {
                    // 클라이언트 소켓 닫기
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 메인 메서드
    public static void main(String[] args) {
        try (ServerSocket listener = new ServerSocket(9999)) {
            System.out.println("Server is running. Waiting for connections...");

            while (true) {
                // 클라이언트 연결 대기
                Socket clientSocket = listener.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // 새로운 스레드 생성 및 클라이언트 처리
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            System.out.println("Error starting the server: " + e.getMessage());
        }
    }
}
