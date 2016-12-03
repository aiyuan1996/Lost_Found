package cn.aiyuan1996.lost_found.bean;

import cn.bmob.v3.BmobObject;

/**
 * @author aiyuan
 *
 */
public class Lost extends BmobObject{
	private String title;//标题
	private String describe;//描述
	private String phone;//联系手机
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}

}
