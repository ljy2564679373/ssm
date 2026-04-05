package edu.java.eams.comm;

public class RetJson<T> {
    private int code; // 状态码
    private String msg; // 消息
    private T data; // 返回数据

    public RetJson() {}
    public RetJson(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    public static <T> RetJson<T> success(T data) {
        return new RetJson<>(200, "success", data);
    }
    public static <T> RetJson<T> error(String msg) {
        return new RetJson<>(500, msg, null);
    }
    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }
} 