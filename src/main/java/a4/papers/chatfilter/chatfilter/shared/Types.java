package a4.papers.chatfilter.chatfilter.shared;

public enum Types {

    SWEAR("SWEAR"), IP_DNS("IP_DNS"), IP_SWEAR("IP_SWEAR"), FONT("FONT"), URL("URL"), NOTYPE("NOTYPE");

    public String id;

    Types(String id) {
        this.id = id;
    }

}
