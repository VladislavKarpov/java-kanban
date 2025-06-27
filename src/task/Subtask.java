package task;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    //добавил еще один конструктор с возможностью установки статуса исходя из рекомендаций
    public Subtask(String name, String description, int epicId, Status status) {
        super(name, description, status);
        this.epicId = epicId;
    }



    public int getEpicId() {
        return epicId;
    }

    //добавил и сеттер, но изменил поле epicId на private(убрав final)
    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    //добавил обращение через super
    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + super.getId() +
                ", epicId=" + epicId +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() +
                '}';
    }
}
