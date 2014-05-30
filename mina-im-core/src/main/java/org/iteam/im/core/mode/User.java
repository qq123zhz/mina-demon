package org.iteam.im.core.mode;

import java.io.Serializable;

/**
 * 用户
 * 
 * <pre>
 * 		String	u_id	U_ID
 * 		String	u_name	姓名
 * 		String	u_create_time
 * </pre>
 * 
 * @author wenke
 * 
 */
public class User implements Serializable {
	public static final String tableName = "USER";

	/**
	 * 
	 */
	private static final long serialVersionUID = 5136678713419465308L;
	// U_ID,姓名，创建时间，密码，登录帐号，状态
	public String _id, u_name, u_create_time, u_pwd, u_account, u_status;

}
