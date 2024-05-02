import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@ToString
public class Problem {

    private String title;
    private String number;
    private String difficulty;
    private String difficultyEmoji;
    private String difficultyImageURL; // 현재 image url 을 받아오기는 하지만, 띄우는 데 한계가 있고 emoji 로 대체가능하여 사용 X
    private String solvedTime;

    // 난이도와 이모지를 관리하는 Map
    private static final Map<String, String> difficultyEmojiMap = new HashMap<>();
    static {
//        difficultyEmojiMap.put("Unrated", ":boj_unrated:");
//        difficultyEmojiMap.put("Bronze V", ":bronze_5:");
//        difficultyEmojiMap.put("Bronze IV", ":bronze_4:");
//        difficultyEmojiMap.put("Bronze III", ":bronze_3:");
//        difficultyEmojiMap.put("Bronze II", ":bronze_2:");
//        difficultyEmojiMap.put("Bronze I", ":bronze_1:");
//        difficultyEmojiMap.put("Silver V", ":silver_5:");
//        difficultyEmojiMap.put("Silver IV", ":silver_4:");
//        difficultyEmojiMap.put("Silver III", ":silver_3:");
        difficultyEmojiMap.put("Silver II", ":silver_2:");
        difficultyEmojiMap.put("Silver I", ":silver_1:");
        difficultyEmojiMap.put("Gold V", ":gold_5:");
        difficultyEmojiMap.put("Gold IV", ":gold_4:");
        difficultyEmojiMap.put("Gold III", ":gold_3:");
        difficultyEmojiMap.put("Gold II", ":gold_2:");
        difficultyEmojiMap.put("Gold I", ":gold_1:");
        difficultyEmojiMap.put("Platinum V", ":platinum_5:");
        difficultyEmojiMap.put("Platinum IV", ":platinum_4:");
        difficultyEmojiMap.put("Platinum III", ":platinum_3:");
        difficultyEmojiMap.put("Platinum II", ":platinum_2:");
        difficultyEmojiMap.put("Platinum I", ":platinum_1:");
        difficultyEmojiMap.put("Diamond V", ":dia5:");
        difficultyEmojiMap.put("Diamond IV", ":dia4:");
        difficultyEmojiMap.put("Diamond III", ":dia3:");
        difficultyEmojiMap.put("Diamond II", ":dia2:");
        difficultyEmojiMap.put("Diamond I", ":dia1:");
        difficultyEmojiMap.put("Ruby V", ":ruby5:");
        difficultyEmojiMap.put("Ruby IV", ":ruby4:");
        difficultyEmojiMap.put("Ruby III", ":ruby3:");
        difficultyEmojiMap.put("Ruby II", ":ruby2:");
        difficultyEmojiMap.put("Ruby I", ":ruby1:");
    }
    
    // 실버 2 이상의 문제인지
    public boolean validateSilver2() {
        return difficultyEmoji != null;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
        if (difficultyEmojiMap.get(difficulty) != null)
            difficultyEmoji = difficultyEmojiMap.get(difficulty);
    }
}
