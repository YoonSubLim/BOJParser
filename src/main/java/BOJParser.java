import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Locale;

import com.amazonaws.services.lambda.runtime.Context;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BOJParser {

    public static void start(Context context) {
//    public static void main(String[] args) {

        StringBuilder resultMsg = new StringBuilder();

        for (UserInfo user : UserInfo.values()) {
            resultMsg.append(getUserResult(user));
        }
        sendToMatterMost(resultMsg.toString());
    }

    private static String getUserResult(UserInfo user) {

        String boj_url = "https://www.acmicpc.net/status?option-status-pid=on&problem_id=&user_id=" + user.getUserId() +"&language_id=-1&result_id=-1"; // 가져올 웹 페이지의 URL
        ArrayList<Problem> solvedProblems = new ArrayList<>();

        try {
            // Jsoup을 사용하여 GET 요청을 보내고 HTML 문서를 파싱합니다.
            Document doc = Jsoup.connect(boj_url).get();

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

                LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

                // 각 LocalDateTime 객체의 날짜 부분 추출
                LocalDate date1 = datetime.toLocalDate();
                LocalDate date2 = now.toLocalDate();

                // 날짜 부분만 비교하여 일수 차이 계산
                long daysDifference = ChronoUnit.DAYS.between(date1, date2);

                // 1일 전 푼 문제가 있다면, List 에 저장
                if (daysDifference == 1) {
                    Problem problem = Problem.builder()
                            .title(problem_title)
                            .number(problem_number)
                            .solvedTime(datetimeStr.split("\\s+")[1])
                            .build();
                    setDifficulty(problem); // problem 객체에 난이도 정보 set

                    if (problem.validateSilver2()) {
                        solvedProblems.add(problem);
                    }
                } else if (daysDifference >= 2) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return makeMessage(user, solvedProblems);
    }

    private static void setDifficulty(Problem problem) throws Exception {

        String solvedacBaseUrl = "https://solved.ac/search?query="; // + problem number

        Document doc = Jsoup.connect(solvedacBaseUrl + problem.getNumber()).get();

        // (난이도이미지 + 문제번호) 가 포함된 <a> 인 첫번째 Element 를 가져온다.
        Element problem_element = doc.selectFirst(".css-q9j30p");
        if (problem_element == null) throw new Exception();

        // 일치하는 문제가 없음 (Solved AC 에 등록되지 않은 문제 등)
        if (!problem.getNumber().equals(problem_element.text())) {
            throw new Exception();
        }

        Element difficulty_image_tag = problem_element.getElementsByClass("css-1vnxcg0").get(0);
        String difficulty = difficulty_image_tag.attr("alt");
        String image_url = difficulty_image_tag.attr("src");

        problem.setDifficulty(difficulty);
        problem.setDifficultyImageURL(image_url);
    }

    // 시간의 11:42:55 등의 예시에서, :42: 가 custom emoji 로 인식되기 때문에 `백틱`을 추가하여 변형
    private static String makeMessage(UserInfo user, ArrayList<Problem> solvedProblems) {
        StringBuilder sb = new StringBuilder();
        
        // 안 푼 경우
        if (solvedProblems.isEmpty()) {
            sb.append(String.format("| :coffee: | **%s** (%s) | | |\n",
                    user.getUserName(),
                    user.getUserId()));
        } else {
            sb.append(String.format("| | **%s** (%s) | %s %s. %s | `%s` |\n",
                    user.getUserName(),
                    user.getUserId(),
                    solvedProblems.get(0).getDifficultyEmoji(),
                    solvedProblems.get(0).getNumber(),
                    solvedProblems.get(0).getTitle(),
                    solvedProblems.get(0).getSolvedTime()));
            for (int i = 1; i < solvedProblems.size(); i++) {
                sb.append(String.format("| | | %s %s. %s | `%s` |\n",
                        solvedProblems.get(i).getDifficultyEmoji(),
                        solvedProblems.get(i).getNumber(),
                        solvedProblems.get(i).getTitle(),
                        solvedProblems.get(i).getSolvedTime()));
            }
        }
        return sb.toString();
    }

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
            StringBuilder sb = new StringBuilder();
            LocalDate yesterday = LocalDate.now().minusDays(1);
            // 원하는 형식의 날짜 포맷터 생성
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd'th', yyyy", Locale.ENGLISH);
            // 현재 날짜와 시간을 원하는 형식의 문자열로 변환
            String formattedDate = yesterday.format(formatter);

            sb.append("#### :rotating_light: 스트릭 챌린지 결과 for ").append(formattedDate).append("\\n");
            sb.append("""
                    |   Coffee   |   이름   |   맞은 문제   |   푼 시간   |
                    |:----------:|:-------|:------------|:----------:|
                    """);

            String requestBody = "{\"text\": \"" + sb.toString() + message + "\"}";

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
