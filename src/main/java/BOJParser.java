import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BOJParser {

    public static void main(String[] args) {

        String url = "https://www.acmicpc.net/status?option-status-pid=on&problem_id=&user_id=yslim37&language_id=-1&result_id=-1"; // 가져올 웹 페이지의 URL
        try {
            // Jsoup을 사용하여 GET 요청을 보내고 HTML 문서를 파싱합니다.
            Document doc = Jsoup.connect(url).get();

            // 원하는 HTML 태그를 선택합니다.
            Elements problems = doc.select("#status-table>tbody>tr");

            // 선택된 태그에서 원하는 정보를 추출합니다.
            for (Element problem : problems) {
                String result = problem.getElementsByClass("result-text").get(0).text();
                if (result.equals("틀렸습니다")) {
                    continue;
                }

                Elements titleElement = problem.getElementsByClass("problem_title");
                Elements dateElement = problem.getElementsByClass("show-date");

                String title = titleElement.get(0).attr("title");
                System.out.println(title);

                String datetimeStr = dateElement.get(0).attr("title");
                System.out.println(datetimeStr);

                // datetime
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime datetime = LocalDateTime.parse(datetimeStr, formatter);

                LocalDateTime now = LocalDateTime.now();

                // 각 LocalDateTime 객체의 날짜 부분 추출
                LocalDate date1 = datetime.toLocalDate();
                LocalDate date2 = now.toLocalDate();

                // 날짜 부분만 비교하여 일수 차이 계산
                long daysDifference = ChronoUnit.DAYS.between(date1, date2);

                // 현재 날짜와 비교
                if (daysDifference == 0) {
                    System.out.println("오늘도 풀었네");
                    continue;
                } else if (daysDifference == 1) {
                    System.out.println("어제 풀었네");
                } else {
                    System.out.println("안풀었네. 커피사야겠다");
                }

                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
