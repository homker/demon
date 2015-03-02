package ecjtunet.com.demon;

import java.io.Serializable;


/**
 * Created by homker on 2015/1/19.
 */
public class UserEntity implements Serializable {
    private static final long serialVersionUID = -5683263669918171030L;

    private String userName;  //用户名
    private String studentID; //学号
    private String password;  //密码
    private String headImage; //头像图片

    public UserEntity() {

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }


}

