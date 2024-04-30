import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BOJParser {

    public static void main(String[] args) {

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
                    // TODO : 난이도 추가 저장
                    solvedProblems.add(new String[] {problem_title, problem_number});
                } else if (daysDifference >= 2) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String[] str: solvedProblems) {
            System.out.println(str[0] + " " + str[1]);
        }
    }
}
