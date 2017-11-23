package me.jcala.pact.user.domain;

/**
 * @author zhipeng.zuo
 * Created on 17-11-23.
 */
public class User {

    private Long id;

    private String name;

    private String pass;

    public User() {
    }

    public User(String name) {
        this.name = name;
        this.pass = "";
    }

    public User(String name, String pass) {
        this.name = name;
        this.pass = pass;
    }

    public User(Long id, String name, String pass) {
        this.id = id;
        this.name = name;
        this.pass = pass;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
