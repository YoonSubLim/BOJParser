import lombok.Builder;
import lombok.Data;
import lombok.ToString;

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

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
        setDifficultyEmoji(difficulty);
    }

    // Mattermost 내의 Custom Emoji 명령어를 사용하기 위한, :?: 꼴의 String 으로 Set
    private void setDifficultyEmoji(String difficulty) {
        if (difficulty.equals("Unrated")) {
            this.difficultyEmoji = ":boj_unrated:";
        } else if (difficulty.equals("Bronze V")) {
            this.difficultyEmoji = ":bronze_5:";
        } else if (difficulty.equals("Bronze IV")) {
            this.difficultyEmoji = ":bronze_4:";
        } else if (difficulty.equals("Bronze III")) {
            this.difficultyEmoji = ":bronze_3:";
        } else if (difficulty.equals("Bronze II")) {
            this.difficultyEmoji = ":bronze_2:";
        } else if (difficulty.equals("Bronze I")) {
            this.difficultyEmoji = ":bronze_1:";
        } else if (difficulty.equals("Silver V")) {
            this.difficultyEmoji = ":silver_5:";
        } else if (difficulty.equals("Silver IV")) {
            this.difficultyEmoji = ":silver_4:";
        } else if (difficulty.equals("Silver III")) {
            this.difficultyEmoji = ":silver_3:";
        } else if (difficulty.equals("Silver II")) {
            this.difficultyEmoji = ":silver_2:";
        } else if (difficulty.equals("Silver I")) {
            this.difficultyEmoji = ":silver_1:";
        } else if (difficulty.equals("Gold V")) {
            this.difficultyEmoji = ":gold_5:";
        } else if (difficulty.equals("Gold IV")) {
            this.difficultyEmoji = ":gold_4:";
        } else if (difficulty.equals("Gold III")) {
            this.difficultyEmoji = ":gold_3:";
        } else if (difficulty.equals("Gold II")) {
            this.difficultyEmoji = ":gold_2:";
        } else if (difficulty.equals("Gold I")) {
            this.difficultyEmoji = ":gold_1:";
        } else if (difficulty.equals("Platinum V")) {
            this.difficultyEmoji = ":platinum_5:";
        } else if (difficulty.equals("Platinum IV")) {
            this.difficultyEmoji = ":platinum_4:";
        } else if (difficulty.equals("Platinum III")) {
            this.difficultyEmoji = ":platinum_3:";
        } else if (difficulty.equals("Platinum II")) {
            this.difficultyEmoji = ":platinum_2:";
        } else if (difficulty.equals("Platinum I")) {
            this.difficultyEmoji = ":platinum_1:";
        } else if (difficulty.equals("Diamond V")) {
            this.difficultyEmoji = ":dia5:";
        } else if (difficulty.equals("Diamond IV")) {
            this.difficultyEmoji = ":dia4:";
        } else if (difficulty.equals("Diamond III")) {
            this.difficultyEmoji = ":dia3:";
        } else if (difficulty.equals("Diamond II")) {
            this.difficultyEmoji = ":dia2:";
        } else if (difficulty.equals("Diamond I")) {
            this.difficultyEmoji = ":dia1:";
        } else if (difficulty.equals("Ruby V")) {
            this.difficultyEmoji = ":ruby5:";
        } else if (difficulty.equals("Ruby IV")) {
            this.difficultyEmoji = ":ruby4:";
        } else if (difficulty.equals("Ruby III")) {
            this.difficultyEmoji = ":ruby3:";
        } else if (difficulty.equals("Ruby II")) {
            this.difficultyEmoji = ":ruby2:";
        } else if (difficulty.equals("Ruby I")) {
            this.difficultyEmoji = ":ruby1:";
        } else {
            this.difficultyEmoji = ":question:";
        }
    }
}
