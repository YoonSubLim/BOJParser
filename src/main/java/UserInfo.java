public enum UserInfo {
    // 이름을 지정하지 않고 정의
    USER1("강창우", "kangcw0107"),
    USER2("김두열", "enduf768640"),
    USER3("김지원", "gokim0928"),
    USER4("박지훈", "java_java"),
    USER5("유재광", "dbworhkd97"),
    USER6("이승원", "wlk256032");
//    USER7("임윤섭", "yslim37");

    private final String userName;
    private final String userId;

    UserInfo(String userName, String userId) {
        this.userName = userName;
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserId() {
        return userId;
    }
}