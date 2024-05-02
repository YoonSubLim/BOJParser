import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import com.amazonaws.services.lambda.runtime.Context;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BOJParser {

    public static void start(Context context) {

        String url = "https://www.acmicpc.net/status?option-status-pid=on&problem_id=&user_id=yslim37&language_id=-1&result_id=-1"; // 가져올 웹 페이지의 URL
        ArrayList<String[]> solvedProblems = new ArrayList<>();

        try {
            // Jsoup을 사용하여 GET 요청을 보내고 HTML 문서를 파싱합니다.
            Document doc = Jsoup.connect(url).get();

            //　제출　단위로 Elements 파싱
            Elements submits = doc.select("#status-table>tbody>tr");

            // 선택된 태그에서 원하는 정보를 추출합니다.
            for (Element submit : submits) {
                // 결과 text
                String result = submit.getElementsByClass("result-text").get(0).text();
                if (result.equals("틀렸습니다")) {
                    continue;
                }

                // 문제 정보 (제목/번호)
                Elements titleElement = submit.getElementsByClass("problem_title");
                String problem_title = titleElement.get(0).attr("title");
                String problem_number = titleElement.get(0).text();

                // 제출 시간
                Elements dateElement = submit.getElementsByClass("show-date");
                String datetimeStr = dateElement.get(0).attr("title");

                // datetime
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime datetime = LocalDateTime.parse(datetimeStr, formatter);

                LocalDateTime now = LocalDateTime.now();

                // 각 LocalDateTime 객체의 날짜 부분 추출
                LocalDate date1 = datetime.toLocalDate();
                LocalDate date2 = now.toLocalDate();

                // 날짜 부분만 비교하여 일수 차이 계산
                long daysDifference = ChronoUnit.DAYS.between(date1, date2);

                // 1일 전 푼 문제가 있다면, List 에 저장
                if (daysDifference == 1) {
                    String solve_info = problem_title + " " + problem_number + " " + getDifficulty(problem_number) + " " + datetimeStr.split("\\s+")[1];
                    sendToMatterMost(solve_info); // TODO: move to outer
                    solvedProblems.add(solve_info.split("\\s+"));
                } else if (daysDifference >= 2) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println();
        for (String[] str: solvedProblems) {
            for (String s: str) {
                System.out.print(s + " ");
            }
            System.out.println();
        }
    }

    private static String getDifficulty(String problem_number) throws Exception {

        String solvedacBaseUrl = "https://solved.ac/search?query="; // + problem number
        System.out.println("REQEUST URL : " + solvedacBaseUrl + problem_number);

        Document doc = Jsoup.connect(solvedacBaseUrl + problem_number).get();

        // (난이도이미지 + 문제번호) 가 포함된 <a> 인 첫번째 Element 를 가져온다.
        Element problem = doc.selectFirst(".css-q9j30p");
        if (problem == null) throw new Exception();

        System.out.println(problem);

        System.out.println();
        System.out.println("검색 결과 첫 문제번호 : " + problem.text());

        // 일치하는 문제가 없음 (
        if (!problem_number.equals(problem.text())) {
            throw new Exception();
        }

        Element difficulty_image_tag = problem.getElementsByClass("css-1vnxcg0").get(0);
        String difficulty = difficulty_image_tag.attr("alt");
        String image_url = difficulty_image_tag.attr("src");
        System.out.println("검색 결과 첫 문제난이도 : " + difficulty); // Unrated
        System.out.println("검색 결과 첫 문제난이도 이미지 : " + image_url);

        return difficulty + " " + image_url;
    }

    // TODO : User 별로 정보 얻어, requestBody 에 add 한 뒤 Send Request
    private static boolean sendToMatterMost(String message) {

        try {
            // 요청을 보낼 URL 설정
            URL url = new URL("https://meeting.ssafy.com/hooks/x4j366d1y78njkegfpygggtqce");

            // HttpURLConnection 객체 생성
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 요청 메소드 설정
            connection.setRequestMethod("POST");

            // 요청 헤더 설정
            connection.setRequestProperty("Content-Type", "application/json");

            // 요청 바디 작성
            String text = String.format("""
                    |   Coffee   |   이름   |   푼 문제   |   푼 시간   |
                    |:----------:|:-------:|:----------:|:----------:|
                    |            |  임윤섭  |    1234    |  23:43:44  |
                    |            |         |    4321    |  23:43:44  |
                    |            |         |   240430   |  23:43:44  |
                    |  :coffee:  |  이승원  |            |            |
                    """);

            String requestBody = "{\"text\": \"" + text + "\"}";

            // 요청 바디 전송 설정
            connection.setDoOutput(true);

            // 요청 바디를 OutputStream을 통해 전송
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 응답 코드 확인
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // 연결 종료
            connection.disconnect();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
