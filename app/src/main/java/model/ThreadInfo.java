package model;

/**
 * Created by Reiky on 2015/7/1.
 */
public class ThreadInfo {
    private int id, start, end, finished;
    private String url;

    public ThreadInfo() {
        super();
    }

    public ThreadInfo(int end, int finished, int id, int start, String url) {
        this.end = end;
        this.finished = finished;
        this.id = id;
        this.start = start;
        this.url = url;
    }

    @Override
    public String toString() {
        return "ThreadInfo{" +
                "end=" + end +
                ", id=" + id +
                ", start=" + start +
                ", finished=" + finished +
                ", url='" + url + '\'' +
                '}';
    }


    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
