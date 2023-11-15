import java.io.*;
import java.net.*;
import java.util.*;

public class CalcClient {
    public static void main(String[] args) {
        try {
            // 서버 정보를 저장한 파일에서 데이터를 읽어오는 부분
            FileInputStream inner = new FileInputStream("C:\\Users\\ssh11\\OneDrive\\Desktop\\HW1_src_신승훈\\server_info.dat");
            String OutputString = "";
            byte arr[] = new byte[16];
            while (true) {
                int num = inner.read(arr);
                if (num < 0)
                    break;
                for (int cnt = 0; cnt < num; cnt++){
                    int value = arr[cnt] & 0xff; // 바이트를 int로 변환
                    char charact = (char) value;
                    OutputString += Character.toString(charact);
                }

            }

            // 서버 정보를 공백으로 구분하여 배열로 분리
            String[] array = OutputString.split(" ");
            array[0]=array[0].substring(1); // 첫 번째 요소에서 첫 글자 제거
        
        // 소켓 통신을 위한 입출력 스트림 및 소켓 객체 초기화
        BufferedReader in = null;
        BufferedWriter out = null;
        Socket socket = null;
        Scanner scanner = new Scanner(System.in);
        try {
            // 서버에 연결
            socket = new Socket(array[0], Integer.parseInt(array[2]));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            while (true) {
                System.out.print("계산식(빈칸으로 띄어 입력,예:24 + 42)>>"); // 프롬프트
                String outputMessage = scanner.nextLine(); // 키보드에서 수식 읽기
                if (outputMessage.equalsIgnoreCase("bye")) {
                    out.write(outputMessage + "\n"); // "bye" 문자열 전송
                    out.flush();
                    break; // 사용자가 "bye"를 입력한 경우 서버로 전송 후 연결 종료
                }
                out.write(outputMessage + "\n"); // 키보드에서 읽은 수식 문자열 전송
                out.flush();
                String inputMessage = in.readLine(); // 서버로부터 계산 결과 수신
                System.out.println("계산 결과: " + inputMessage);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                scanner.close();
                if (socket != null)
                    socket.close(); // 클라이언트 소켓 닫기
            } catch (IOException e) {
                System.out.println("서버와 채팅 중 오류가 발생했습니다.");
            }
        }
        inner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
