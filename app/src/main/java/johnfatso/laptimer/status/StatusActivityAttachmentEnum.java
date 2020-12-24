package johnfatso.laptimer.status;

public enum StatusActivityAttachmentEnum {
    ACTIVITY_ATTACHED(0x01),
    ACTIVITY_DETACHED(0x02);

    public int id;

    StatusActivityAttachmentEnum(int id){
        this.id = id;
    }

}
